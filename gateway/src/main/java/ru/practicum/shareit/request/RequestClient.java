package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(Long userId, RequestDtoValid requestDto) {
        return post("/", userId, requestDto);
    }

    public ResponseEntity<Object> getAllRequestByUserId(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllWithPage(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequestFromId(Long userId, Long id) {
        return get("/" + id, userId);
    }
}