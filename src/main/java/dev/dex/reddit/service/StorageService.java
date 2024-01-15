package dev.dex.reddit.service;

import dev.dex.reddit.entity.ImageData;
import dev.dex.reddit.models.Image;
import dev.dex.reddit.repository.ImageDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final ImageDataRepository imageDataRepository;
    @Value("${application.file-system.images.user.path}")
    private String USER_IMAGES_PATH;

    public String uploadUserImage(int userId, MultipartFile file) throws IOException {
        String fileName = userId + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String filePath = USER_IMAGES_PATH + fileName;
        Optional<ImageData> imageDataFromDbOptional = imageDataRepository.findByUserId(userId);
        if (imageDataFromDbOptional.isPresent()) {
            ImageData imageDataFromDb = imageDataFromDbOptional.get();
            imageDataFromDb.setName(fileName);
            imageDataFromDb.setType(file.getContentType());
            imageDataFromDb.setFilePath(filePath);
            imageDataRepository.save(imageDataFromDb);
        } else {
            imageDataRepository.save(new ImageData(fileName, file.getContentType(), filePath, userId));
        }
        // Writes file to the file system
        file.transferTo(new File(filePath));
        return fileName;
    }

    public Image downloadUserImage(String fileName) throws IOException {
        ImageData imageData = imageDataRepository.findByName(fileName)
                .orElseThrow(() -> new RuntimeException("Invalid filename: " + fileName));
        String filePath = imageData.getFilePath();
        byte[] img = Files.readAllBytes(new File(filePath).toPath());
        return new Image(img, imageData.getType());
    }
}
