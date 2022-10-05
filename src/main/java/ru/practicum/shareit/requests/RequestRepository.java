package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long>{

    List<ItemRequest> findAllByRequesterOrderByCreatedDesc(User requester);

    Page<ItemRequest> findAllByRequesterNotOrderByCreatedDesc(Pageable page, User requester);


}
