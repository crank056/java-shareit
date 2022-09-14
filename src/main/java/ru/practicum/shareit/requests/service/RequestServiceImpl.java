package ru.practicum.shareit.requests.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Repository.ItemRepository;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.RequestRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.RequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.userStorage.UserRepository;

import java.util.List;

@Service
public class RequestServiceImpl implements RequestService{
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        validateRequest(itemRequestDto);
        User requester = userRepository.getReferenceById(userId);
        ItemRequest itemRequest = RequestMapper.toRequest(itemRequestDto, requester);
        return RequestMapper.ToDto(requestRepository.save(itemRequest), requester);
    }

    @Override
    public List<ItemRequestDto> getRequest(Long userId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllWithPagination(Integer from, Integer size) {
        return null;
    }

    @Override
    public ItemRequestDto getRequestFromId(Long requestId) {
        return null;
    }
    @SneakyThrows
    private void validateRequest(ItemRequestDto itemRequestDto) {
        if(itemRequestDto.equals(null)) throw new ValidationException("Объекта нет");
        if(itemRequestDto.getDescription().isEmpty() ||
                itemRequestDto.getDescription().isBlank() ||
                itemRequestDto.getDescription().equals(null))
            throw new ValidationException("Нет описания");
    }
}
