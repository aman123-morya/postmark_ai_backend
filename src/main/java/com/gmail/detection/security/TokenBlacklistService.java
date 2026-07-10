package com.gmail.detection.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks tokens that have been explicitly logged out before their natural
 * expiration. Since JWTs are stateless by design, this is the standard
 * workaround for supporting a "logout" action.
 *
 * NOTE: this is in-memory only, so it resets on restart and does not share
 * state across multiple server instances. That's fine for a single-instance
 * deployment; a multi-instance production deployment should back this with
 * Redis (or similar) instead.
 */
@Component
public class TokenBlacklistService {

    private final Map<String, Date> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String token, Date expiry) {
        blacklist.put(token, expiry);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    // Periodically sweep out entries whose original expiry has already passed,
    // so the blacklist doesn't grow unbounded.
    @Scheduled(fixedRate = 3_600_000) // hourly
    public void purgeExpired() {
        Date now = new Date();
        blacklist.entrySet().removeIf(entry -> entry.getValue().before(now));
    }
}
