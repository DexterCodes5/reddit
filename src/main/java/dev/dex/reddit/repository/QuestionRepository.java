package dev.dex.reddit.repository;

import dev.dex.reddit.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByUserUsername(String username);
}
