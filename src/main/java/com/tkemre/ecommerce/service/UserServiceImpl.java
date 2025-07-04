package com.tkemre.ecommerce.service;

import com.tkemre.ecommerce.dto.AddressDto;
import com.tkemre.ecommerce.dto.AddressRequest;
import com.tkemre.ecommerce.dto.LoginRequest;
import com.tkemre.ecommerce.dto.RegisterRequest;
import com.tkemre.ecommerce.dto.UpdateUserRequest;
import com.tkemre.ecommerce.dto.UserDto;
import com.tkemre.ecommerce.exception.UserAlreadyExistsException;
import com.tkemre.ecommerce.exception.UserNotFoundException;
import com.tkemre.ecommerce.model.Address; // Address modelini import et
import com.tkemre.ecommerce.model.User;
import com.tkemre.ecommerce.model.UserRole;
import com.tkemre.ecommerce.repository.AddressRepository; // AddressRepository'yi import et
import com.tkemre.ecommerce.repository.UserRepository;
import com.tkemre.ecommerce.security.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository; // AddressRepository'yi enjekte et
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    // Constructor'a AddressRepository'yi de ekledik
    public UserServiceImpl(UserRepository userRepository,
                           AddressRepository addressRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("User with username " + request.username() + " already exists.");
        }

        // Yeni kullanıcı oluştur
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email()) // <<< BURAYA DİKKAT! request'ten email'i alıp user'a set etmelisiniz.
                .roles(new HashSet<>(Collections.singletonList(UserRole.USER)))
                .build();

        User savedUser = userRepository.save(user);
        return toUserDto(savedUser);
    }

    @Override
    public String login(LoginRequest request) {
        // Spring Security AuthenticationManager kullanarak kimlik doğrula
        // Bu adımda parolaların eşleşip eşleşmediği kontrol edilir
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // Kimlik doğrulama başarılı olursa, güvenlik bağlamını güncelle
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT token oluştur ve döndür
        return jwtTokenUtil.generateToken((User) authentication.getPrincipal());
    }

    @Override
    public UserDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return toUserDto(user);
    }

    @Override
    public UserDto updateUserProfile(String username, UpdateUserRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        // TODO: Eğer parola da güncellenecekse, buraya ekleyin ve şifrelemeyi unutmayın
        // if (request.password() != null && !request.password().isEmpty()) {
        //     user.setPassword(passwordEncoder.encode(request.password()));
        // }

        User updatedUser = userRepository.save(user);
        return toUserDto(updatedUser);
    }

    @Override
    public AddressDto addAddress(String username, AddressRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        // Address nesnesi oluştur
        Address address = Address.builder()
                .street(request.street())
                .city(request.city())
                .state(request.state()) // EKLENDİ - AddressRequest'ten state'i al
                .postalCode(request.postalCode())
                .country(request.country())
                .user(user)
                .build();

        Address savedAddress = addressRepository.save(address);

        return toAddressDto(savedAddress);
    }
    private AddressDto toAddressDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .build();
    }

    private UserDto toUserDto(User user) {
        List<AddressDto> addressDtos = user.getAddresses().stream()
                .map(this::toAddressDto)
                .collect(Collectors.toList());

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .addresses(addressDtos)
                .build();
    }
}