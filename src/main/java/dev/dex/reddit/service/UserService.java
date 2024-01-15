package dev.dex.reddit.service;

import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.models.responsemodels.AuthResponse;
import dev.dex.reddit.models.responsemodels.UserResponse;
import dev.dex.reddit.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;
    private final JwtService jwtService;

    @Value("${application.base-url}")
    private String baseUrl;

    @Transactional
    public void uploadUserImage(int id, MultipartFile image) throws IOException {
        String fileName = storageService.uploadUserImage(id, image);
        userRepository.updateImgById(id, baseUrl + "/api/v1/image-data/user/" + fileName);
    }

    public UserResponse findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getImg()))
                .orElseThrow(() -> new RuntimeException("Invalid username"));
    }

    @Transactional
    public AuthResponse updateUser(MultipartFile image, String username, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        try {
            String fileName = storageService.uploadUserImage(user.getId(), image);
            user.setImg(baseUrl + "/api/v1/image-data/user/" + fileName);
        } catch (IOException ex) {
            throw new RuntimeException("Upload User image failed");
        }
        user.setUsername(username);
        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        user = userRepository.save(user);
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .img(user.getImg())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
