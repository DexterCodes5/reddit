package dev.dex.reddit.repository;

import dev.dex.reddit.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.img = :img WHERE u.id = :id")
    void updateImgById(@Param("id") int id, @Param("img") String img);

    Optional<User> findByRefreshToken(String refreshToken);
}
