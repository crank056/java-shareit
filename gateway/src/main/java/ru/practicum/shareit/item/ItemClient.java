package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.item.dto.CommentDtoValid;
import ru.practicum.shareit.item.dto.ItemDtoValid;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(Long userId, ItemDtoValid itemDto) {
        return post("/", userId, itemDto);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDtoValid commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    public ResponseEntity<Object> refreshItem(Long userId, Long id, ItemDtoValid itemDto) {
        return patch("/" + id, userId, itemDto);
    }

    public ResponseEntity<Object> getItemFromId(Long userId, Long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getAllItemsFromUserId(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemsFromKeyWord(String searchText, long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", searchText,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }
}
