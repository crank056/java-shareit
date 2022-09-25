package ru.practicum.shareit.item.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
@EnableJpaRepositories
@Component
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerOrderByIdAsc(User owner, Pageable page);

    List<Item> findAllByRequestId(Long requestId);

    @Query("SELECT i FROM Item i WHERE i.isAvailable = TRUE AND " +
            "(UPPER(i.name) LIKE UPPER(CONCAT('%',:text,'%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%',:text,'%'))) ")
    List<Item> findFromKeyWord(@Param("text") String text, Pageable page);
}