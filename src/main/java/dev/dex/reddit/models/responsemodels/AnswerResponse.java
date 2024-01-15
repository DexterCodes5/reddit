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
public class AnswerResponse {
    private Integer id;
    private String content;
    private Timestamp postTimestamp;
    private UserResponse user;
    private int rating;
}
