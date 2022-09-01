package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(long userId) throws WrongIdException;

    UserDto create(UserDto user);

    UserDto update(long userId, UserDto userDto);

    void delete(long userId);
}
