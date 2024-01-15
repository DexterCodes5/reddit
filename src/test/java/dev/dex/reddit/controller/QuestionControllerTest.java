package dev.dex.reddit.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dex.reddit.entity.Question;
import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.models.requestmodels.QuestionRequest;
import dev.dex.reddit.models.responsemodels.MyQuestionResponse;
import dev.dex.reddit.models.responsemodels.QuestionResponse;
import dev.dex.reddit.models.responsemodels.UserResponse;
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
class QuestionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ITUtils itUtils;

    @BeforeEach
    void setUp() {
        itUtils.deleteAll();
    }

    @Test
    void postQuestion() throws Exception {
        // given
        String username = "dexter";
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        User user = new User(null,"dexter", "$2a$12$V9X1Wv0cRihtUmaRkD7oEeV6iGS3e8GGw/yBOzUSFek2i9bXiSUZm",
                true, null, "dexter@mail.com", Role.USER, null, accessToken, refreshToken,
                null);
        userRepository.save(user);
        QuestionRequest questionRequest = new QuestionRequest("title", "content", user.getId());

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/questions")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionRequest)));

        // then
        resultActions.andExpect(status().isCreated());
        List<Question> questions = questionRepository.findAll();
        Question expected = new Question(null, questionRequest.getTitle(), questionRequest.getContent(), null,
                user);
        assertThat(questions)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "postTimestamp")
                .contains(expected);
    }

    @Test
    void canGetQuestions() throws Exception {
        // given
        String username = "dexter";
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        User user = new User(null,"dexter", "$2a$12$V9X1Wv0cRihtUmaRkD7oEeV6iGS3e8GGw/yBOzUSFek2i9bXiSUZm",
                true, null, "dexter@mail.com", Role.USER, null, accessToken, refreshToken,
                null);

        Question question = new Question(null, "title", "content", new Timestamp(System.currentTimeMillis()), user);
        questionRepository.save(question);

        // when
        ResultActions resultActions = mockMvc
                .perform(get("/api/v1/questions/get-questions?page=0&size=1"));

        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
    }

    @Test
    void canGetQuestionById() throws Exception {
        // given
        String username = "dexter";
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        User user = new User(null,"dexter", "$2a$12$V9X1Wv0cRihtUmaRkD7oEeV6iGS3e8GGw/yBOzUSFek2i9bXiSUZm",
                true, null, "dexter@mail.com", Role.USER, null, accessToken, refreshToken,
                null);

        Question question = new Question(null, "title", "content", new Timestamp(System.currentTimeMillis()), user);
        questionRepository.save(question);

        // when
        ResultActions resultActions = mockMvc
                .perform(get("/api/v1/questions/" + question.getId()));

        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        QuestionResponse res = objectMapper.readValue(responseBody, QuestionResponse.class);
        QuestionResponse expected = new QuestionResponse(question.getId(), question.getTitle(), question.getContent(),
                question.getPostTimestamp(), 0, new UserResponse(user.getId(), user.getUsername(), user.getImg()));
        assertThat(res).isEqualTo(expected);
    }

    @Test
    void canGetQuestionsByUser() throws Exception {
        // given
        String username = "dexter";
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        User user = new User(null,"dexter", "$2a$12$V9X1Wv0cRihtUmaRkD7oEeV6iGS3e8GGw/yBOzUSFek2i9bXiSUZm",
                true, null, "dexter@mail.com", Role.USER, null, accessToken, refreshToken,
                null);

        Question question = new Question(null, "title", "content", new Timestamp(System.currentTimeMillis()), user);
        questionRepository.save(question);

        // when
        ResultActions resultActions = mockMvc
                .perform(get("/api/v1/questions/get-questions-by-user/" + username));

        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        List<MyQuestionResponse> res = objectMapper.readValue(responseBody, new TypeReference<List<MyQuestionResponse>>() {});
        MyQuestionResponse expected = new MyQuestionResponse(question.getId(), question.getTitle(), 0, 0);
        assertThat(res.get(0)).isEqualTo(expected);
    }
}