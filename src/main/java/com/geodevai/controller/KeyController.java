package com.geodevai.controller;

import com.geodevai.data.model.McpKey;
import com.geodevai.data.repository.McpKeyRepository;
import com.geodevai.mcp.service.McpKeyService;
import com.geodevai.mcp.util.KeyMaskUtil;
import com.geodevai.data.model.User;
import com.geodevai.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/services/keys")
@RequiredArgsConstructor
public class KeyController {

    private final McpKeyService keyService;
    private final McpKeyRepository keyRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listKeys() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        List<McpKey> keys = keyRepository.findAllByUserAndActiveIsTrue(user);

        List<Map<String, Object>> result = keys.stream().map(key -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", key.getMcpKeyId());
            map.put("name", key.getName());
            map.put("maskedKey", KeyMaskUtil.mask(key.getKeyHash()));
            map.put("createdAt", key.getAuditFields().getCreated());
            map.put("active", key.isActive());
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createKey(@RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();
        String name = request.get("name");

        McpKeyService.McpKeyResult result = keyService.createKey(user, name);
        McpKey key = result.key();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("id", key.getMcpKeyId());
        responseMap.put("name", key.getName());
        responseMap.put("rawKey", result.rawKey());
        responseMap.put("createdAt", key.getAuditFields().getCreated());
        responseMap.put("active", key.isActive());

        return ResponseEntity.ok(responseMap);
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> deactivateKey(@PathVariable UUID keyId) {
        keyService.deactivateKey(keyId);
        return ResponseEntity.ok().build();
    }
}
