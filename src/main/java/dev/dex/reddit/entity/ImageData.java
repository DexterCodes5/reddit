package dev.dex.reddit.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "image_data")
@Data
@NoArgsConstructor
public class ImageData {
    @Id
    @SequenceGenerator(name = "image_data_seq", sequenceName = "image_data_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_data_seq")
    private Integer id;
    private String name;
    private String type;
    private String filePath;

    public ImageData(String name, String type, String filePath) {
        this.name = name;
        this.type = type;
        this.filePath = filePath;
    }
}
