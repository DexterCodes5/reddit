package dev.dex.reddit.models.requestmodels;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @Size(min = 5, max = 50)
    private String username;
    @Size(min = 5)
    private String password;
    @Email
    private String email;
}
