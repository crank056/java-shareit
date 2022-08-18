package ru.practicum.shareit.user.userStorage;

import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    User userAdd(User user);

    boolean userDelete(Long id) throws WrongIdException;

    User userRefresh(Long id, User user);

    List<User> getAllUsers();

    User getUserFromId(long id) throws WrongIdException;
}
