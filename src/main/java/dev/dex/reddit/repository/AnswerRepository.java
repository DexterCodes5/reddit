package dev.dex.reddit.repository;

import dev.dex.reddit.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    List<Answer> findByQuestionId(int questionId);

    int countByQuestionId(int id);
}
