package dev.dex.reddit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.models.requestmodels.SignInRequest;
import dev.dex.reddit.models.requestmodels.SignUpRequest;
import dev.dex.reddit.models.responsemodels.AuthResponse;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-it.properties")
@AutoConfigureMockMvc
class AuthControllerTest {
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

    @BeforeEach
    void setUp() {
        itUtils.deleteAll();
    }

    @Test
    void signIn() throws Exception {
        // given
        String username = "dexter";
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        User user = new User(null,"dexter", "$2a$12$V9X1Wv0cRihtUmaRkD7oEeV6iGS3e8GGw/yBOzUSFek2i9bXiSUZm",
                true, null, "dexter@mail.com", Role.USER, "img", accessToken, refreshToken,
                null);
        userRepository.save(user);

        SignInRequest signInRequest = new SignInRequest(username, "test123");

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)));

        // then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        AuthResponse res = objectMapper.readValue(responseBody, AuthResponse.class);
        assertThat(res.getId()).isEqualTo(user.getId());
        assertThat(res.getUsername()).isEqualTo(username);
        assertThat(res.getEmail()).isEqualTo(user.getEmail());
        assertThat(res.getImg()).isEqualTo(user.getImg());
        assertThat(res.getAccessToken()).isNotBlank();
        assertThat(res.getRefreshToken()).isNotBlank();
    }

    @Test
    void signUp() throws Exception {
        // given
        SignUpRequest signUpRequest = new SignUpRequest("dexter", "test123", "dexter@mail.com");

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/auth/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)));

        // then
        resultActions.andExpect(status().isOk());

        List<User> users = userRepository.findAll();
        System.out.println(users);
        assertThat(users.get(0).getUsername()).isEqualTo(signUpRequest.getUsername());
        assertThat(users.get(0).getEmail()).isEqualTo(signUpRequest.getEmail());
        assertThat(users.get(0).getPassword()).isNotBlank();
        assertThat(users.get(0).getActive()).isFalse();
        assertThat(users.get(0).getVerificationCode()).isNotBlank();
        assertThat(users.get(0).getRole()).isEqualTo(Role.USER);
//        assertThat(users.get(0).getImg())
    }

    @Test
    void verify() {
    }

    @Test
    void refreshToken() {
    }

    @Test
    void forgotPassword() {
    }

    @Test
    void forgotPasswordRedirect() {
    }

    @Test
    void changePassword() {
    }
}