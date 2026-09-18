package com.geodevai.integration.buildium;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BuildiumApiClient {

    private static final String BASE_URL = "https://api.buildium.com/v1/";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public <T> List<T> getList(String endpoint, String apiKey, Class<T> elementType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + endpoint, HttpMethod.GET, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to fetch " + endpoint + ": " + response.getStatusCode());
        }

        try {
            Map<String, Object> body = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
            Object data = body.get("data");
            if (data instanceof List) {
                return objectMapper.convertValue(data, new TypeReference<List<T>>() {});
            }
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse " + endpoint + " response", e);
        }
    }
}
