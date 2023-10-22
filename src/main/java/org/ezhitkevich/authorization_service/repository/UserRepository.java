package org.ezhitkevich.authorization_service.repository;

import org.ezhitkevich.authorization_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

   @Query("from User u join fetch u.roles where u.login =:login")
   Optional<User> findByLogin(String login);

   boolean existsByLogin(String login);
}
