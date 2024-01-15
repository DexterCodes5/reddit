package dev.dex.reddit.models.responsemodels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {
    private int id;
    private String title;
    private String content;
    private Timestamp postTimestamp;
    private Integer rating;
    private UserResponse user;
}
