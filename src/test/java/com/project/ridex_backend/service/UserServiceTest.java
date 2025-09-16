package com.project.ridex_backend.service;

import com.project.ridex_backend.dto.request.UserRegisterRequest;
import com.project.ridex_backend.dto.response.UserResponse;
import com.project.ridex_backend.entity.User;
import com.project.ridex_backend.enums.UserRole;
import com.project.ridex_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    public void existsByEmail_delegatesToRepository() {
        //Arrange
        when(userRepository.existsByEmail("username@example.com")).thenReturn(true);

        //Act
        boolean exists = userRepository.existsByEmail("username@example.com");

        //Assert
        assertThat(exists).isEqualTo(true);
        verify(userRepository).existsByEmail("username@example.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void registerNewUser_encodesPassword_andSaves() {
        //Arrange
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserRegisterRequest mockUser = UserRegisterRequest.builder()
                .username("fathima")
                .email("fathima@gmail.com")
                .password("password")
                .role(UserRole.RIDER)
                .build();

        when(userRepository.save(any())).thenAnswer(invocationOnMock -> {
            User arg = invocationOnMock.getArgument(0);
            arg.setId(1L);
            return arg;
        });

        //Act
        UserResponse resp = userService.registerNewUser(mockUser);

        //Assert
        assertThat(resp.getId()).isEqualTo(1L);
        assertThat(resp.getUsername()).isEqualTo("fathima");
        assertThat(resp.getEmail()).isEqualTo("fathima@gmail.com");
        assertThat(resp.getRole()).isEqualTo(UserRole.RIDER);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        User captured = captor.getValue();
        assertThat(captured.getPassword()).isEqualTo("encodedPassword");

        verifyNoMoreInteractions(userRepository, bCryptPasswordEncoder);

    }

}
