package site.leesoyeon.probabilityrewardsystem.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.leesoyeon.probabilityrewardsystem.user.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
