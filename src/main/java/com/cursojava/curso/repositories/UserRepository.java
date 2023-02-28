package com.cursojava.curso.repositories;
import com.cursojava.curso.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByUsername(String username);
    User findOneById(Long userId);

    User findByUsernameOrEmail(String username, String email);
}
