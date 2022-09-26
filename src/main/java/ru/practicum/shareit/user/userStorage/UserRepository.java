package ru.practicum.shareit.user.userStorage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;


public interface UserRepository extends JpaRepository<User, Long> {
}