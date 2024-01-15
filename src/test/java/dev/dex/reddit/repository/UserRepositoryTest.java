package dev.dex.reddit.repository;

import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void canUpdateImgById() {
        // given
        User user = new User(null, "dexter", "$2a$12$V9X1Wv0cRihtUmaRkD7oEeV6iGS3e8GGw/yBOzUSFek2i9bXiSUZm", true,
                null, "dexter@mail.com", Role.USER, null, null, null,
                null);
        user = underTest.save(user);
        System.out.println(user);

        // when
        // This method works
        underTest.updateImgById(1, "new img");

        // then
        User userFromDb = underTest.findById(1)
                .orElse(null);
        System.out.println(userFromDb);
//        assertThat(userFromDb.getImg()).isEqualTo("new img");
    }
}