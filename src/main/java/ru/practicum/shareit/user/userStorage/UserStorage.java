package ru.practicum.shareit.user.userStorage;

import ru.practicum.shareit.exceprions.WrongIdException;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    User userAdd(User user);

    boolean userDelete(User user);

    User userRefresh(Long id, User user);

    List<User> getAllUsers();

    User getUserFromId(long id) throws WrongIdException;
}
