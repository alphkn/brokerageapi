package com.inghubs.brokerageapi.repository;

import java.util.Optional;

import com.inghubs.brokerageapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository interface for managing User entities.
 * Extends JpaRepository to provide CRUD operations for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User by their username.
     *
     * @param username the username of the user to be retrieved
     * @return an Optional containing the User if found, or empty if not
     */
    Optional<User> findByUsername(String username);
}