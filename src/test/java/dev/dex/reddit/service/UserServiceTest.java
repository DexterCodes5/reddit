package dev.dex.reddit.service;

import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.models.responsemodels.AuthResponse;
import dev.dex.reddit.models.responsemodels.UserResponse;
import dev.dex.reddit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private StorageService storageService;
    @Mock
    private JwtService jwtService;
    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository, passwordEncoder, storageService, jwtService);
    }

    @Test
    void canUploadUserImage() throws IOException {
        // given
        int id = 1;
        MultipartFile file = mock(MultipartFile.class);

        String fileName = "fileName";
        given(storageService.uploadUserImage(anyInt(), any(MultipartFile.class)))
                .willReturn(fileName);

        // when
        underTest.uploadUserImage(id, file);

        // then
        verify(userRepository).updateImgById(anyInt(), anyString());
    }

    @Test
    void canFindByUsername() {
        // given
        String username = "dexter";

        User user = new User(1, "dexter", "test123", true,
                null, "dexter@mail.com", Role.USER, null, null, null,
                null);
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(user));

        // when
        UserResponse res = underTest.findByUsername(username);

        // then
        assertThat(res).isEqualTo(new UserResponse(user.getId(), user.getUsername(), user.getImg()));
    }

    @Test
    void canUpdateUser() {
        // given
        MultipartFile image = mock(MultipartFile.class);
        String username = "dexter1";
        User user = new User(1, "dexter", "test123", true,
                null, "dexter@mail.com", Role.USER, null, null, null,
                null);
        Principal principal = new UsernamePasswordAuthenticationToken(user, null);

        String accessToken = "accessToken";
        given(jwtService.generateToken(anyString()))
                .willReturn(accessToken);

        String refreshToken = "refreshToken";
        given(jwtService.generateRefreshToken(anyString()))
                .willReturn(refreshToken);

        User user2 = new User(1, "dexter1", "test123", true,
                null, "dexter@mail.com", Role.USER, "null/api/v1/image-data/user/null", accessToken, refreshToken,
                null);
        given(userRepository.save(user2))
                .willReturn(user2);

        // when
        AuthResponse res = underTest.updateUser(image, username, principal);

        // then
        assertThat(res).isEqualTo(AuthResponse.builder()
                        .id(user2.getId())
                        .username(user2.getUsername())
                        .email(user2.getEmail())
                        .img(user2.getImg())
                        .accessToken(user2.getAccessToken())
                        .refreshToken(user2.getRefreshToken())
                .build());
    }
}