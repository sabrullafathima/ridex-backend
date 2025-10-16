package com.project.ridex_backend.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.exception.RideNotFoundException;
import com.project.ridex_backend.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@RequiredArgsConstructor

public class RideCacheRedis {
    private static final Logger logger = LoggerFactory.getLogger(RideCacheRedis.class);
    private static final String RIDE_CACHE_PREFIX = "ride:";
    private final RideRepository rideRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public Ride getRide(Long rideId) {

        String key = RIDE_CACHE_PREFIX + rideId;
        Object cachedObj = redisTemplate.opsForValue().get(key);

        if (cachedObj != null) {
            Ride ride = objectMapper.convertValue(cachedObj, Ride.class);
            logger.info("Ride {} fetched from Redis cache", rideId);
            return ride;
        }

        logger.info("Ride {} not in Redis → loading from DB", rideId);
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride Not Found | rideId: " + rideId));

        redisTemplate.opsForValue().set(key, ride);
        logger.info("Ride {} saved to Redis cache", rideId);

        return ride;
    }

    public void removeRide(Long rideId) {
        String key = RIDE_CACHE_PREFIX + rideId;
        redisTemplate.delete(key);
        logger.info("Ride {} removed from Redis cache", rideId);
    }

    public void updateRide(Ride ride) {
        String key = RIDE_CACHE_PREFIX + ride.getId();
        redisTemplate.opsForValue().set(key, ride);
        logger.info("Ride {} updated in Redis cache", ride.getId());
    }
}
