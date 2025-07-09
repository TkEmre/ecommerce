package com.tkemre.ecommerce.service;

import com.tkemre.ecommerce.dto.*;
import com.tkemre.ecommerce.exception.UserAlreadyExistsException;
import com.tkemre.ecommerce.exception.UserNotFoundException;
import com.tkemre.ecommerce.model.*;
import com.tkemre.ecommerce.repository.AddressRepository;
import com.tkemre.ecommerce.repository.UserRepository;
import com.tkemre.ecommerce.security.FakeAuthentication;

import com.tkemre.ecommerce.security.FakeJwtTokenUtil;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;

    private UserServiceImpl userService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(
                userRepository,
                addressRepository,
                passwordEncoder,
                authenticationManager,
                new FakeJwtTokenUtil()
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void register_successful() {
        RegisterRequest request = new RegisterRequest(
                "emre@example.com", "123456", "emre@example.com",
                "Emre", "Tek", List.of("USER")
        );

        when(userRepository.existsByUsername("emre@example.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserDto dto = userService.register(request);

        assertThat(dto.username()).isEqualTo("emre@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_existingUser_throwsException() {
        RegisterRequest request = new RegisterRequest(
                "emre@example.com", "123456", "emre@example.com",
                "Emre", "Tek", List.of("USER")
        );

        when(userRepository.existsByUsername("emre@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void login_successful() {
        LoginRequest request = new LoginRequest("emre@example.com", "123456");
        Authentication auth = new FakeAuthentication(); // mock yerine elle oluşturduk

        when(authenticationManager.authenticate(any())).thenReturn(auth);

        String token = userService.login(request);

        assertThat(token).isEqualTo("fake-jwt-token");
    }


    @Test
    void getUserProfile_successful() {
        User user = User.builder()
                .id(1L)
                .username("emre@example.com")
                .email("emre@example.com")
                .roles(Set.of(UserRole.USER))
                .addresses(new HashSet<>())
                .build();

        when(userRepository.findByUsername("emre@example.com")).thenReturn(Optional.of(user));

        UserDto dto = userService.getUserProfile("emre@example.com");

        assertThat(dto.username()).isEqualTo("emre@example.com");
    }

    @Test
    void getUserProfile_userNotFound_throws() {
        when(userRepository.findByUsername("x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile("x"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateUserProfile_successful() {
        User user = User.builder()
                .id(1L)
                .username("emre@example.com")
                .firstName("Old")
                .lastName("Name")
                .roles(Set.of(UserRole.USER))
                .addresses(Set.of())
                .build();

        UpdateUserRequest request = new UpdateUserRequest("New", "Surname");

        when(userRepository.findByUsername("emre@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto dto = userService.updateUserProfile("emre@example.com", request);

        assertThat(dto.firstName()).isEqualTo("New");
    }

    @Test
    void addAddress_successful() {
        AddressRequest req = new AddressRequest("street", "city", "state", "00000", "Turkey");

        User user = User.builder()
                .id(1L)
                .username("emre@example.com")
                .roles(Set.of(UserRole.USER))
                .addresses(new HashSet<>())
                .build();

        when(userRepository.findByUsername("emre@example.com")).thenReturn(Optional.of(user));
        when(addressRepository.save(any())).thenAnswer(inv -> {
            Address address = inv.getArgument(0);
            address.setId(1L);
            return address;
        });

        AddressDto dto = userService.addAddress("emre@example.com", req);

        assertThat(dto.city()).isEqualTo("city");
        assertThat(dto.id()).isEqualTo(1L);
    }

    @Test
    void getAllUsers_paged_successful() {
        Pageable pageable = PageRequest.of(0, 2);
        User user = User.builder()
                .id(1L)
                .username("emre@example.com")
                .email("e@e.com")
                .roles(Set.of(UserRole.USER))
                .addresses(Set.of())
                .build();

        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserDto> result = userService.getAllUsers(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).username()).isEqualTo("emre@example.com");
    }
}
