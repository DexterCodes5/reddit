package dev.dex.reddit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dex.reddit.entity.Answer;
import dev.dex.reddit.entity.AnswerRating;
import dev.dex.reddit.entity.Question;
import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.repository.AnswerRatingRepository;
import dev.dex.reddit.repository.AnswerRepository;
import dev.dex.reddit.repository.QuestionRepository;
import dev.dex.reddit.repository.UserRepository;
import dev.dex.reddit.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-it.properties")
@AutoConfigureMockMvc
class AnswerRatingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ITUtils itUtils;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private AnswerRatingRepository answerRatingRepository;

    @BeforeEach
    void setUp() {
        itUtils.deleteAll();
    }


    @Test
    void canGetAnswerRating() throws Exception {
        // given
        String username = "dexter";
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        User user = new User(null,"dexter", "$2a$12$V9X1Wv0cRihtUmaRkD7oEeV6iGS3e8GGw/yBOzUSFek2i9bXiSUZm",
                true, null, "dexter@mail.com", Role.USER, null, accessToken, refreshToken,
                null);
        Question question = new Question(null, "title", "content", new Timestamp(System.currentTimeMillis()), user);
        questionRepository.save(question);
        Answer answer = new Answer(null, "content", new Timestamp(System.currentTimeMillis()), question.getId(), user);
        answerRepository.save(answer);
        AnswerRating answerRating = new AnswerRating(null, true, false, answer.getId(), user.getId());
        answerRatingRepository.save(answerRating);

        // when
        ResultActions resultActions = mockMvc
                .perform(get("/api/v1/answer-ratings/" + answer.getId())
                        .header("Authorization", "Bearer " + accessToken));

        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        AnswerRating res = objectMapper.readValue(responseBody, AnswerRating.class);
        assertThat(res)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(answerRating);
    }

    @Test
    void postAnswerRating() throws Exception {
        // given
        String username = "dexter";
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        User user = new User(null, "dexter", "$2a$12$V9X1Wv0cRihtUmaRkD7oEeV6iGS3e8GGw/yBOzUSFek2i9bXiSUZm",
                true, null, "dexter@mail.com", Role.USER, null, accessToken, refreshToken,
                null);
        Question question = new Question(null, "title", "content", new Timestamp(System.currentTimeMillis()), user);
        questionRepository.save(question);
        Answer answer = new Answer(null, "content", new Timestamp(System.currentTimeMillis()), question.getId(), user);
        answerRepository.save(answer);
        AnswerRating answerRating = new AnswerRating(null, true, false, answer.getId(), user.getId());

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/answer-ratings")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRating)));

        // then
        resultActions.andExpect(status().isOk());
        List<AnswerRating> res = answerRatingRepository.findAll();
        assertThat(res)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(answerRating);
    }
}