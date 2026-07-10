package com.gmail.detection.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Simple sliding-window rate limiter for the auth endpoints most attractive to
 * brute-force/credential-stuffing (login, register, refresh). Keyed by
 * client IP + path.
 *
 * NOTE: in-memory only, like TokenBlacklistService - fine for a single
 * instance, but a multi-instance deployment behind a load balancer should use
 * a shared store (e.g. Redis) so limits apply across all instances.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_WINDOW = 10;
    private static final long WINDOW_MILLIS = 60_000; // 1 minute

    private static final List<String> LIMITED_PATHS = List.of(
            "/api/auth/login", "/api/auth/register", "/api/auth/refresh");

    private final ConcurrentHashMap<String, Deque<Long>> requestLog = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if (LIMITED_PATHS.stream().noneMatch(path::equals)) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = clientIp(request) + ":" + path;
        long now = System.currentTimeMillis();

        Deque<Long> timestamps = requestLog.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());

        // Drop timestamps outside the current window.
        while (!timestamps.isEmpty() && now - timestamps.peekFirst() > WINDOW_MILLIS) {
            timestamps.pollFirst();
        }

        if (timestamps.size() >= MAX_REQUESTS_PER_WINDOW) {
            response.setStatus(429); // 429 Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Too many requests. Please try again in a minute.\"}");
            return;
        }

        timestamps.addLast(now);

        filterChain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
