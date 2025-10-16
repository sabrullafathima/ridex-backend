package com.project.ridex_backend.cache;

import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.exception.RideNotFoundException;
import com.project.ridex_backend.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class RideCacheInMemory {
    private static final Logger logger = LoggerFactory.getLogger(RideCacheInMemory.class);
    private final RideRepository rideRepository;

    private final Map<Long, Ride> cache = new ConcurrentHashMap<>();

    public Ride getRide(Long rideId) {
        Ride ride = cache.get(rideId);
        if (ride != null) {
            logger.info("Ride {} fetched from cache", rideId);
            return ride;
        }
        return cache.computeIfAbsent(rideId, id -> {
            logger.info("Ride {} not in cache → loading from DB", id);
            return rideRepository.findById(id).orElseThrow(
                    () -> new RideNotFoundException("Ride Not Found | rideId: " + id));
        });
    }

    public void removeRide(Long rideId) {
        cache.remove(rideId);
        logger.info("successfully cleared cache memory. cacheSize: {}", cache.size());
    }
}
