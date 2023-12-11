package dev.dex.reddit.models.responsemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private int id;
    private String title;
    private String content;
    private Date postTimestamp;
    private UserResponse user;
}
