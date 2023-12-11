package dev.dex.reddit.models.responsemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAndAnswersResponse {
    private int id;
    private String title;
    private String content;
    private Date postTimestamp;
    private UserResponse user;
    private List<AnswerResponse> answers;
}
