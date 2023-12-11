package dev.dex.reddit.models.responsemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {
    private Integer id;
    private String content;
    private Timestamp postTimestamp;
    private UserResponse user;
}
