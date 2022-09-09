package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(long userId) throws WrongIdException;

    UserDto create(UserDto user) throws ValidationException;

    UserDto update(long userId, UserDto userDto) throws WrongIdException, ValidationException;

    boolean delete(long userId);
}
