package ru.practicum.shareit.user.userStorage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceprions.WrongEmailException;
import ru.practicum.shareit.exceprions.WrongIdException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private List<User> users = new ArrayList<>();
    private static long lastUsedId = 1;

    @SneakyThrows
    @Override
    public User userAdd(User user) {
        if (user.getEmail() == null) throw new WrongEmailException("Неверный формат email");
        if(isNotDuplicateEmail(user.getEmail())) throw new WrongEmailException("Email уже существует");
        user.setId(getNextId());
        log.info("Размер хранилища аккаунтов до добавления: {}", users.size());
        users.add(user);
        log.info("Размер хранилища аккаунтов после добавления: {}", users.size());
        return getUserFromId(user.getId());
    }

    @Override
    public boolean userDelete(User user) {
        return users.remove(user);
    }

    @SneakyThrows
    @Override
    public User userRefresh(Long id, User user) {
        User refreshingUser = getUserFromId(id);
        if (refreshingUser == null) throw new WrongIdException("Пользователь не найден");
        log.info("Размер хранилища аккаунтов до обновления: {}", users.size());
        refreshingUser.setEmail(user.getEmail());
        refreshingUser.setName(user.getName());
        log.info("Размер хранилища аккаунтов после обновления: {}", users.size());
        return getUserFromId(id);
    }

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public User getUserFromId(long id) throws WrongIdException {
        User user = null;
        for (User findUser : users) {
            if (findUser.getId() == id) {
                user = findUser;
            }
        }
        if (user == null) throw new WrongIdException("Пользователь не найден");
        return user;
    }

    private long getNextId() {
        return lastUsedId++;
    }

    private boolean isNotDuplicateEmail(String email) {
        boolean isDuplicate = false;
        for (User user : users) {
            if(user.getEmail() == email) isDuplicate = true;
        }
        return isDuplicate;
    }
}

