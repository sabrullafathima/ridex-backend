package com.project.ridex_backend.repository;

import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findRideByStatus(RideStatus rideStatus);

    Ride findRideByStatusAndDriverId(RideStatus rideStatus, Long driverId);
}
