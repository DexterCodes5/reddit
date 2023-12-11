package dev.dex.reddit.service;

import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void uploadUserImage(int id, MultipartFile image) throws IOException {
        String fileName = storageService.uploadUserImage(id, image);
        userRepository.updateImgById(id, "http://localhost:8080/api/v1/image-data/user/" + fileName);
    }
}
