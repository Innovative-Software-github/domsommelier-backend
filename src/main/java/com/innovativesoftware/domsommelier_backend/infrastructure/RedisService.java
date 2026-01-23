package com.innovativesoftware.domsommelier_backend.infrastructure;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisService {
    private static final String OBJECT = "object";
    private static final String CREATED = "created";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(@Qualifier("dataRedis") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public boolean hasKey(@NonNull String redisKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }

    @Nullable
    public String save(byte[] data) {
        try {
            String fileData = Base64.getEncoder().encodeToString(data);
            String redisKey = DigestUtils.sha256Hex(fileData);
            redisTemplate.opsForHash().put(redisKey, OBJECT, fileData);
            redisTemplate.opsForHash().put(redisKey, CREATED, LocalDateTime.now().toString());
            return redisKey;
        } catch (Exception ex) {
            log.warn("Не удалось записать данные в Redis: {}", ex.getMessage());
            return null;
        }
    }

    public void save(@NonNull String redisKey, @NonNull Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            redisTemplate.opsForHash().put(redisKey, OBJECT, json);
            redisTemplate.opsForHash().put(redisKey, CREATED, LocalDateTime.now().toString());
        } catch (Exception ex) {
            log.warn("Не удалось записать данные в Redis: {}", ex.getMessage());
        }
    }

    public Object getObject(@NonNull String redisKey) {
        if (!hasKey(redisKey)) {
            log.warn("Данные по ключу '{}' не найдены в Redis", redisKey);
            return null;
        }

        Object value = redisTemplate.opsForHash().get(redisKey, OBJECT);

        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            try {
                String strValue = (String) value;
                if (strValue.startsWith("{") && strValue.endsWith("}")) {
                    return objectMapper.readValue(strValue, Map.class);
                }
            } catch (Exception e) {
                log.debug("Значение не является JSON строкой: {}", e.getMessage());
            }
        }

        return value;
    }

    public <T> T getObject(@NonNull String redisKey, Class<T> clazz) {
        if (!hasKey(redisKey)) {
            log.warn("Данные по ключу '{}' не найдены в Redis", redisKey);
            return null;
        }

        Object value = redisTemplate.opsForHash().get(redisKey, OBJECT);

        if (value == null) {
            return null;
        }

        try {
            if (value instanceof String) {
                String strValue = (String) value;
                if (strValue.startsWith("{") && strValue.endsWith("}")) {
                    return objectMapper.readValue(strValue, clazz);
                }
            }

            if (value instanceof Map) {
                return objectMapper.convertValue(value, clazz);
            }

            return clazz.cast(value);

        } catch (Exception e) {
            log.warn("Не удалось получить объект типа {} по ключу '{}': {}",
                    clazz.getSimpleName(), redisKey, e.getMessage());
            return null;
        }
    }

    public Set<String> getAllKeys(@NonNull String prefix) {
        Set<String> keys = new HashSet<>();

        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection()
                .scan(ScanOptions.scanOptions().match(String.format("*%s*", prefix)).build())) {
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
        } catch (Exception ex) {
            log.warn("Ошибка при сканировании ключей: {}", ex.getMessage());
        }
        return keys;
    }

    @Nullable
    public Long getExpire(@NonNull String redisKey) {
        if (hasKey(redisKey)) {
            return redisTemplate.getExpire(redisKey);
        }
        return null;
    }

    public byte[] get(@NonNull String redisKey) {
        if (!hasKey(redisKey)) {
            log.warn("Данные по ключу '{}' не найдены в Redis", redisKey);
            return new byte[0];
        }
        try {
            Object value = redisTemplate.opsForHash().get(redisKey, OBJECT);
            if (value == null) {
                throw new RuntimeException("Не удалось прочитать объект: получен null");
            }

            String objectB64;
            if (value instanceof String) {
                objectB64 = (String) value;
            } else {
                objectB64 = objectMapper.writeValueAsString(value);
            }

            String digest = DigestUtils.sha256Hex(objectB64);
            if (!redisKey.equals(digest)) {
                throw new RuntimeException("Объект в Redis поврежден: дайджесты не совпадают.");
            }
            return Base64.getDecoder().decode(objectB64);
        } catch (Exception ex) {
            log.warn("Не удалось прочитать данные по ключу '{}' из Redis: {}", redisKey, ex.getMessage());
            return new byte[0];
        }
    }

    public byte[] getAndRemove(@NonNull String redisKey) {
        byte[] result = get(redisKey);
        if (hasKey(redisKey)) {
            remove(redisKey);
        }
        return result;
    }

    public void remove(@NonNull String redisKey) {
        try {
            if (hasKey(redisKey)) {
                redisTemplate.delete(redisKey);
            } else {
                log.warn("Данные по ключу '{}' не найдены в Redis", redisKey);
            }
        } catch (Exception ex) {
            log.warn("Ошибка при удалении ключа '{}': {}", redisKey, ex.getMessage());
        }
    }

    public void removeOlderData(@NonNull LocalDateTime beforeDate) {
        try (Cursor<byte[]> cursor = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection()
                .scan(ScanOptions.scanOptions().match("*").build())) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                Object dateTimeObj = redisTemplate.opsForHash().get(key, CREATED);
                if (dateTimeObj instanceof String) {
                    try {
                        LocalDateTime createdTime = LocalDateTime.parse((String) dateTimeObj);
                        if (createdTime.isBefore(beforeDate)) {
                            redisTemplate.delete(key);
                            log.info("Ключ: {}, дата создания: {} удален.", key, dateTimeObj);
                        }
                    } catch (Exception e) {
                        log.warn("Не удалось распарсить дату для ключа {}: {}", key, dateTimeObj);
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Ошибка при удалении старых данных: {}", ex.getMessage());
        }
    }

    public void setExpire(@NonNull String redisKey, long timeout, @NonNull TimeUnit unit) {
        try {
            redisTemplate.expire(redisKey, timeout, unit);
        } catch (Exception ex) {
            log.warn("Ошибка при установке expiration для ключа '{}': {}", redisKey, ex.getMessage());
        }
    }
}