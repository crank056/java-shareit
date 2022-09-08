package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
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
    public UserDto findById(long userId) throws WrongIdException {
        User user = null;
        if (userRepository.findById(userId).isPresent()) {
            user = userRepository.findById(userId).get();
        } else throw new WrongIdException("Нет пользователя с таким email");
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) throws ValidationException {
        validateUser(userDto);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) throws WrongIdException, ValidationException {
        User user = UserMapper.toUser(findById(userId));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        validateUser(UserMapper.toUserDto(user));
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public boolean delete(long userId) {
        userRepository.deleteById(userId);
        return userRepository.existsById(userId);
    }

    private void validateUser(UserDto userDto) throws ValidationException {
        if (userDto == null) {
            throw new ValidationException("");
        }
        if (userDto.getName() == null || userDto.getName().isEmpty()) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty() || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Неверный формат email");
        }
    }
}

