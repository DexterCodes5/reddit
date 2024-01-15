package dev.dex.reddit.models.requestmodels;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnswerRequest {
    private Integer userId;
    private Integer questionId;
    @Size(min = 20)
    private String answer;
}
