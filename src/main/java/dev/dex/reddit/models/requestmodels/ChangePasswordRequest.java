package dev.dex.reddit.models.requestmodels;

import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @Size(min = 64, max = 64)
        String forgotPasswordCode,
        @Size(min = 5)
        String newPassword
) {
}
