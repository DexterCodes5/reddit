package dev.dex.reddit.models.responsemodels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private int id;
    private String username;
    private String email;
    private String img;
    private String accessToken;
    private String refreshToken;
}
