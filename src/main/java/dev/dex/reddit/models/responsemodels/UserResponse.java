package dev.dex.reddit.models.responsemodels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserResponse {
    private int id;
    private String username;
    private String img;
}
