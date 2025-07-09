package com.tkemre.ecommerce.service;

import com.tkemre.ecommerce.dto.*;
import com.tkemre.ecommerce.exception.*;
import com.tkemre.ecommerce.model.*;
import com.tkemre.ecommerce.model.Order;
import com.tkemre.ecommerce.repository.*;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private AddressRepository addressRepository;

    @InjectMocks private OrderServiceImpl orderService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createOrder_successful() {
        String username = "testuser";

        User user = User.builder().id(1L).username(username).build();
        Address address = Address.builder().id(1L).user(user).build();
        Product product = Product.builder().id(10L).name("Laptop").price(BigDecimal.valueOf(1000)).stock(5).build();

        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest(10L, 2);
        CreateOrderRequest orderRequest = new CreateOrderRequest(1L, List.of(itemRequest));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(addressRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(address));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order order = inv.getArgument(0);
            order.setId(100L);
            return order;
        });

        OrderDto result = orderService.createOrder(username, orderRequest);

        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.totalPrice()).isEqualTo(BigDecimal.valueOf(2000));
        verify(productRepository).save(any(Product.class));
        verify(orderItemRepository).saveAll(anySet());
    }

    @Test
    void createOrder_withEmptyItems_throwsException() {
        String username = "testuser";
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> orderService.createOrder(username, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one item");
    }

    @Test
    void getOrderById_successful() {
        String username = "testuser";
        User user = User.builder().id(1L).username(username).build();
        Order order = Order.builder().id(1L).user(user).shippingAddress(Address.builder().id(1L).build()).orderItems(Set.of()).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));

        OrderDto result = orderService.getOrderById(1L, username);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void updateOrderStatus_successful() {
        Order order = Order.builder().id(1L).status(OrderStatus.PENDING).shippingAddress(Address.builder().id(1L).build()).orderItems(Set.of()).user(User.builder().id(1L).username("admin").build()).build();
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.SHIPPED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        OrderDto result = orderService.updateOrderStatus(1L, request);

        assertThat(result.status()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void updateOrderStatus_whenAlreadyDelivered_throwsException() {
        Order order = Order.builder().id(1L).status(OrderStatus.DELIVERED).build();
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.SHIPPED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cancelOrder_successful() {
        String username = "testuser";
        User user = User.builder().id(1L).username(username).build();
        Product product = Product.builder().id(1L).stock(3).build();
        OrderItem item = OrderItem.builder().product(product).quantity(2).build();
        Order order = Order.builder().id(1L).status(OrderStatus.PENDING).user(user).orderItems(Set.of(item)).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L, username);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(productRepository).save(product);
    }

    @Test
    void cancelOrder_invalidStatus_throwsException() {
        User user = User.builder().id(1L).username("test").build();
        Order order = Order.builder().id(1L).status(OrderStatus.DELIVERED).user(user).build();

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, "test"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteOrder_successful() {
        Order order = Order.builder().id(1L).user(User.builder().id(1L).username("admin").build()).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderRepository).delete(order);
    }

    @Test
    void getUserOrders_returnsPaged() {
        User user = User.builder().id(1L).username("user").build();
        Pageable pageable = PageRequest.of(0, 5);
        Page<Order> page = new PageImpl<>(List.of(Order.builder().id(1L).user(user).shippingAddress(Address.builder().id(1L).build()).orderItems(Set.of()).build()));

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(orderRepository.findAllByUser(user, pageable)).thenReturn(page);

        Page<OrderDto> result = orderService.getUserOrders("user", pageable);

        assertThat(result.getContent()).hasSize(1);
    }
}
