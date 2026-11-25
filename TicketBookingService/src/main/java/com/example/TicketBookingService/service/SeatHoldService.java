package com.example.TicketBookingService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatHoldService {

    private static final DefaultRedisScript<Long> ACQUIRE_ALL_SCRIPT = new DefaultRedisScript<>(
            """
            for i = 1, #KEYS do
                if redis.call('exists', KEYS[i]) == 1 then
                    return 0
                end
            end
            for i = 1, #KEYS do
                redis.call('set', KEYS[i], ARGV[1], 'NX', 'EX', ARGV[2])
            end
            return 1
            """,
            Long.class
    );

    private static final DefaultRedisScript<Long> RELEASE_OWNED_SCRIPT = new DefaultRedisScript<>(
            """
            for i = 1, #KEYS do
                if redis.call('get', KEYS[i]) == ARGV[1] then
                    redis.call('del', KEYS[i])
                end
            end
            return 1
            """,
            Long.class
    );

    private final StringRedisTemplate redisTemplate;
    private final long holdTtlSeconds;

    public SeatHoldService(
            StringRedisTemplate redisTemplate,
            @Value("${app.booking.hold-ttl-seconds:300}") long holdTtlSeconds
    ) {
        this.redisTemplate = redisTemplate;
        this.holdTtlSeconds = holdTtlSeconds;
    }

    public boolean acquireHolds(Long showId, List<String> seatLabels, Long holderId) {
        List<String> keys = seatLabels.stream()
                .map(label -> key(showId, label))
                .toList();
        Long result = redisTemplate.execute(
                ACQUIRE_ALL_SCRIPT,
                keys,
                String.valueOf(holderId),
                String.valueOf(holdTtlSeconds)
        );
        return result != null && result == 1L;
    }

    public void releaseHolds(Long showId, List<String> seatLabels, Long holderId) {
        List<String> keys = seatLabels.stream()
                .map(label -> key(showId, label))
                .toList();
        redisTemplate.execute(RELEASE_OWNED_SCRIPT, keys, String.valueOf(holderId));
    }

    private String key(Long showId, String seatLabel) {
        return "seat_hold:" + showId + ":" + seatLabel;
    }
}
