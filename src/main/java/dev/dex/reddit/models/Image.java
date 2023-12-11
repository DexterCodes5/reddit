package dev.dex.reddit.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Image {
    private byte[] img;
    private String type;
}
