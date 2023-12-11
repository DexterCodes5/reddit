package dev.dex.reddit.models.responsemodel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private int id;
    private String username;
    private String img;
}
