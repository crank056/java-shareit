package ru.practicum.shareit.user.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.userStorage.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        List<UserDto> dtoUsers = new ArrayList<>();
        for (User user : users) {
            dtoUsers.add(UserMapper.toUserDto(user));
        }
        return dtoUsers;
    }

    @Override
    @SneakyThrows
    public UserDto findById(long userId) {
        User user = null;
        if (userRepository.findById(userId).isPresent()) {
            user = userRepository.findById(userId).get();
        } else throw new WrongIdException("Нет пользователя с таким email");
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    @SneakyThrows
    public UserDto update(long userId, UserDto userDto) {
        if (!userRepository.existsById(userId)) throw new WrongIdException("Нет такого пользователя");
        User user = UserMapper.toUser(findById(userId));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @SneakyThrows
    public boolean delete(long userId) {
        if (!userRepository.existsById(userId)) throw new WrongIdException("Нет такого пользователя");
        userRepository.deleteById(userId);
        return userRepository.existsById(userId);
    }
}

