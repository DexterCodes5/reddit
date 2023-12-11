package dev.dex.reddit.models.responsemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private int id;
    private String username;
    private String email;
    private String img;
    private String accessToken;
}
