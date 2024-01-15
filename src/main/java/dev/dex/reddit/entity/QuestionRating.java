package dev.dex.reddit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRating {
    @Id
    @SequenceGenerator(name = "question_rating_seq", sequenceName = "question_rating_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_rating_seq")
    private Integer id;
    private Boolean upvote;
    private Boolean downvote;
    @Positive
    private Integer userId;
    @Positive
    private Integer questionId;
}
