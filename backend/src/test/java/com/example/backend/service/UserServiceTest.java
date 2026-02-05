package com.example.backend.service;

import com.example.backend.dto.UserDto;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnUsers() {

        when(userRepository.findAll())
                .thenReturn(List.of(new User(), new User()));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void getUserById_shouldReturnUser() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void createUser_shouldEncodePasswordAndSave() {

        UserDto dto = new UserDto();
        dto.setFirstName("Ali");
        dto.setLastName("Test");
        dto.setEmail("ali@test.com");
        dto.setPassword("1234");

        when(passwordEncoder.encode("1234"))
                .thenReturn("hashed");

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        User saved = userService.createUser(dto);

        assertEquals("hashed", saved.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void emailExists_shouldReturnTrue() {

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(new User()));

        boolean exists = userService.emailExists("test@test.com");

        assertTrue(exists);
    }

    @Test
    void updateUser_shouldUpdateAndSave() {

        User user = new User();
        user.setId(1L);

        UserDto dto = new UserDto();
        dto.setFirstName("New");
        dto.setLastName("Name");
        dto.setEmail("new@test.com");
        dto.setPassword("pass");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("pass"))
                .thenReturn("hashed");

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        Optional<User> result = userService.updateUser(1L, dto);

        assertTrue(result.isPresent());
        assertEquals("New", result.get().getFirstName());
    }

    @Test
    void deleteUser_shouldCallRepository() {

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}