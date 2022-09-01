package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exceptions.WrongEmailException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.userStorage.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        List<UserDto> dtoUsers = new ArrayList<>();
        for(User user: users) {
            dtoUsers.add(UserMapper.toUserDto(user));
        }
        return dtoUsers;
    }

    @Override
    public UserDto findById(long userId) throws WrongIdException {
        User user = null;
        if(userRepository.findById(userId).isPresent()) {
            user = userRepository.findById(userId).get();
        } else throw new WrongIdException("Нет пользователя с таким email");
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto user) {
        return null;
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        return null;
    }

    @Override
    public void delete(long userId) {

    }
}
