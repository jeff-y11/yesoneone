package com.geodevai.mcp.service;

import com.geodevai.data.model.McpKey;
import com.geodevai.data.repository.McpKeyRepository;
import com.geodevai.data.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class McpKeyService {

    private static final String KEY_PREFIX = "gvai_";
    private static final int KEY_BYTES = 24;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final McpKeyRepository keyRepository;

    public McpKeyResult createKey(User user, String name) {
        String rawKey = generateRawKey();
        String keyHash = hashKey(rawKey);

        McpKey key = new McpKey();
        key.setName(name);
        key.setKeyHash(keyHash);
        key.setKeyPrefix(rawKey.substring(0, 13));
        key.setUser(user);
        key.setActive(true);
        keyRepository.save(key);

        return new McpKeyResult(key, rawKey);
    }

    public boolean validateKey(String rawKey) {
        String keyHash = hashKey(rawKey);
        return keyRepository.findByKeyHash(keyHash).isPresent();
    }

    public Optional<McpKey> getKeyByHash(String keyHash) {
        return keyRepository.findByKeyHash(keyHash);
    }

    public void deactivateKey(UUID keyId) {
        McpKey key = keyRepository.findById(keyId).orElseThrow();
        key.setActive(false);
        keyRepository.save(key);
    }

    public String hashKey(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawKey.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash key", e);
        }
    }

    private String generateRawKey() {
        byte[] randomBytes = new byte[KEY_BYTES];
        SECURE_RANDOM.nextBytes(randomBytes);
        StringBuilder hex = new StringBuilder();
        for (byte b : randomBytes) {
            hex.append(String.format("%02x", b));
        }
        return KEY_PREFIX + hex;
    }

    public record McpKeyResult(McpKey key, String rawKey) {}
}
