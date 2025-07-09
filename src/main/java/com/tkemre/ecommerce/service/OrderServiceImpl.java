package com.tkemre.ecommerce.service;

import com.tkemre.ecommerce.exception.OutOfStockException;
import com.tkemre.ecommerce.dto.AddressDto;
import com.tkemre.ecommerce.dto.CreateOrderRequest;
import com.tkemre.ecommerce.dto.CreateOrderItemRequest; // Yeni import
import com.tkemre.ecommerce.dto.OrderDto;
import com.tkemre.ecommerce.dto.OrderItemDto;
import com.tkemre.ecommerce.dto.UpdateOrderStatusRequest;
import com.tkemre.ecommerce.exception.AddressNotFoundException;
import com.tkemre.ecommerce.exception.OrderNotFoundException;
import com.tkemre.ecommerce.exception.ProductNotFoundException;
import com.tkemre.ecommerce.exception.UserNotFoundException;
import com.tkemre.ecommerce.model.*;
import com.tkemre.ecommerce.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository,
                            AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional
    public OrderDto createOrder(String username, CreateOrderRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));


        if (request.items().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }


        Address shippingAddress = addressRepository.findByIdAndUser(request.shippingAddressId(), user)
                .orElseThrow(() -> new AddressNotFoundException("Shipping address not found for user with id: " + request.shippingAddressId()));

        // Yeni sipariş objesini oluştur
        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING) // Yeni sipariş varsayılan olarak PENDING durumundadır
                .build();


        BigDecimal totalOrderPrice = BigDecimal.ZERO;
        Set<OrderItem> orderItems = new HashSet<>();

        for (CreateOrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + itemRequest.productId()));
            Integer quantity = itemRequest.quantity();

            // Stok kontrolü yap
            if (product.getStock() < quantity) {
                throw new OutOfStockException(product.getName(), quantity, product.getStock());
            }


            // OrderItem oluştur
            OrderItem orderItem = OrderItem.builder()
                    .order(order) // Sipariş objesini bağla
                    .product(product)
                    .quantity(quantity)
                    .priceAtOrder(product.getPrice()) // Ürünün sipariş anındaki fiyatını kaydet
                    .build();
            orderItems.add(orderItem);
            totalOrderPrice = totalOrderPrice.add(orderItem.getTotalPrice());

            // Ürün stoğunu güncelle
            product.setStock(product.getStock() - quantity);
            productRepository.save(product); // Güncellenen ürünü kaydet
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalOrderPrice);

        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);



        return toOrderDto(savedOrder);
    }

    @Override
    public OrderDto getOrderById(Long orderId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));


        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId + " for user " + username));

        return toOrderDto(order);
    }

    @Override
    public Page<OrderDto> getUserOrders(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        return orderRepository.findAllByUser(user, pageable)
                .map(this::toOrderDto);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));


        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalArgumentException("Cannot change status of a " + order.getStatus() + " order.");
        }

        order.setStatus(request.newStatus());
        Order updatedOrder = orderRepository.save(order);
        return toOrderDto(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        // Kullanıcının sadece kendi siparişini iptal etmesini sağla
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId + " for user " + username));

        // Zaten iptal edilmiş veya teslim edilmiş siparişler iptal edilemez
        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalArgumentException("Order cannot be canceled in " + order.getStatus() + " status.");
        }

        order.setStatus(OrderStatus.CANCELED); // Sipariş durumunu İPTAL EDİLDİ olarak ayarla
        orderRepository.save(order);

        // İptal edilen siparişin stoklarını geri ekle
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product); // Güncellenen ürünü kaydet
        }
    }

    @Override
    @Transactional // Sadece ADMIN için tam silme
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));



        orderRepository.delete(order);
    }


    // Order entity'sinden OrderDto'ya dönüşüm metodu
    private OrderDto toOrderDto(Order order) {
        Set<OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(this::toOrderItemDto)
                .collect(Collectors.toSet());

        // AddressDto'ya dönüştür
        AddressDto addressDto = AddressDto.builder()
                .id(order.getShippingAddress().getId())
                .street(order.getShippingAddress().getStreet())
                .city(order.getShippingAddress().getCity())
                .state(order.getShippingAddress().getState())
                .postalCode(order.getShippingAddress().getPostalCode())
                .country(order.getShippingAddress().getCountry())
                .isDefault(order.getShippingAddress().getIsDefault())
                .build();

        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .username(order.getUser().getUsername())
                .shippingAddress(addressDto)
                .orderItems(itemDtos)
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .build();
    }

    // OrderItem entity'sinden OrderItemDto'ya dönüşüm metodu
    private OrderItemDto toOrderItemDto(OrderItem orderItem) {
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .productCategory(orderItem.getProduct().getCategory())
                .quantity(orderItem.getQuantity())
                .priceAtOrder(orderItem.getPriceAtOrder())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }
}
