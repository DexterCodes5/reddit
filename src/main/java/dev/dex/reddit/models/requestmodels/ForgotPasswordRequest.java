package dev.dex.reddit.models.requestmodels;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
}
