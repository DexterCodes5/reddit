package dev.dex.reddit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "answer_rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRating {
    @Id
    @SequenceGenerator(name = "answer_rating_seq", sequenceName = "answer_rating_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_rating_seq")
    private Integer id;
    private boolean upvote;
    private boolean downvote;
    private int answerId;
    private int userId;
}
