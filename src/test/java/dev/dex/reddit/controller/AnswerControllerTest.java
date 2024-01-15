package dev.dex.reddit.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dex.reddit.entity.Answer;
import dev.dex.reddit.entity.Question;
import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.models.requestmodels.AnswerRequest;
import dev.dex.reddit.models.responsemodels.AnswerResponse;
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
class AnswerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private ITUtils itUtils;

    @BeforeEach
    void setUp() {
        itUtils.deleteAll();
    }

    @Test
    void canPostAnswer() throws Exception {
        // given
        String username = "dexter";
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        User user = new User(null,"dexter", "$2a$12$V9X1Wv0cRihtUmaRkD7oEeV6iGS3e8GGw/yBOzUSFek2i9bXiSUZm",
                true, null, "dexter@mail.com", Role.USER, null, accessToken, refreshToken,
                null);
        Question question = new Question(null, "title", "content", new Timestamp(System.currentTimeMillis()), user);
        questionRepository.save(question);

        AnswerRequest answerRequest = new AnswerRequest(user.getId(), question.getId(), "This is my answer. AAAAAAAAAAAAAAAAAAAA");

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(answerRequest)));

        // then
        resultActions.andExpect(status().isCreated());
        List<Answer> answers = answerRepository.findAll();
        Answer expected = new Answer(null, answerRequest.getAnswer(), null, answerRequest.getQuestionId(), user);
        assertThat(answers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "postTimestamp")
                .contains(expected);
    }

    @Test
    void getAnswers() throws Exception {
        // given
        String username = "dexter";
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        User user = new User(null,"dexter", "$2a$12$V9X1Wv0cRihtUmaRkD7oEeV6iGS3e8GGw/yBOzUSFek2i9bXiSUZm",
                true, null, "dexter@mail.com", Role.USER, null, accessToken, refreshToken,
                null);
        Question question = new Question(null, "title", "content", new Timestamp(System.currentTimeMillis()), user);
        questionRepository.save(question);

        Answer answer = new Answer(null, "This is my answer. AAAAAAAAAAAAAAAAAAAA", new Timestamp(System.currentTimeMillis()),
                question.getId(), user);
        answerRepository.save(answer);

        // when
        ResultActions resultActions = mockMvc
                .perform(get("/api/v1/answers/" + question.getId()));

        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        List<AnswerResponse> res = objectMapper.readValue(responseBody, new TypeReference<List<AnswerResponse>>() {});
        System.out.println(res);
        System.out.println(answer);
        assertThat(res.get(0).getId()).isEqualTo(answer.getId());
        assertThat(res.get(0).getContent()).isEqualTo(answer.getContent());
        assertThat(res.get(0).getPostTimestamp()).isEqualTo(answer.getPostTimestamp());
    }
}