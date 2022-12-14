package ru.practicum.shareit.requests.service;

import lombok.SneakyThrows;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.Repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.RequestRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.RequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.userStorage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public RequestServiceImpl(RequestRepository requestRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @SneakyThrows
    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        if (!userRepository.existsById(userId)) throw new WrongIdException("Пользователя не существует");
        User requester = getUserFromId(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = requestRepository.save(RequestMapper.toRequest(itemRequestDto, requester));
        return RequestMapper.toDto(requestRepository.save(itemRequest), getItems(itemRequest.getId()));
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        User requester = getUserFromId(userId);
        List<ItemRequest> requests = requestRepository.findAllByRequesterOrderByCreatedDesc(requester);
        List<ItemRequestDto> requestsDto = new ArrayList<>();
        for (ItemRequest itemRequest: requests) {
            requestsDto.add(RequestMapper.toDto(itemRequest, getItems(itemRequest.getId())));
        }
        return requestsDto;
    }

    @Override
    @SneakyThrows
    public List<ItemRequestDto> getAllWithPagination(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) throw new WrongIdException("Пользователя не существует");
        Pageable page = PageRequest.of(from / size, size, Sort.by("created").ascending());
        List<ItemRequestDto> itemRequestDto = new ArrayList<>();
        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterNotOrderByCreatedDesc(
                page, getUserFromId(userId)).getContent();
        for (ItemRequest itemRequest: itemRequests) {
            itemRequestDto.add(RequestMapper.toDto(itemRequest, getItems(itemRequest.getId())));
        }
        return itemRequestDto;
    }

    @Override
    public ItemRequestDto getRequestFromId(Long userId, Long requestId) throws WrongIdException {
        getUserFromId(userId);
        if (!requestRepository.existsById(requestId)) throw new WrongIdException("Запрос отсутствует");
        return RequestMapper.toDto(requestRepository.getReferenceById(requestId), getItems(requestId));
    }

    @SneakyThrows
    private List<ItemDto> getItems(Long requestId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        List<ItemDto> itemDto = new ArrayList<>();
        for (Item item: items) {
            itemDto.add(ItemMapper.toItemDto(item));
        }
        return itemDto;
    }

    @SneakyThrows
    private User getUserFromId(Long userId) {
        User requester = null;
        if (userRepository.existsById(userId)) {
            requester = userRepository.getReferenceById(userId);
        } else throw new WrongIdException("Пользователя с таким id не существует");
        return requester;
    }
}
