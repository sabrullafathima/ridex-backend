package com.project.ridex_backend.service;

import com.project.ridex_backend.dto.request.PaymentRequest;
import com.project.ridex_backend.entity.Payment;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.enums.PaymentStatus;
import com.project.ridex_backend.repository.PaymentRepository;
import com.project.ridex_backend.repository.RideRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final RideRepository rideRepository;
    private final PaymentRepository paymentRepository;

    public void processPayment(Ride ride, PaymentRequest paymentRequest) {

//        Long mockAmount = (long) 222.070; //TODO: need to calculate amount
        Payment payment = ride.getPayment();
        payment.setAmount(900.00);
        payment.setStatus(PaymentStatus.SUCCESS);

        paymentRepository.save(payment);
        logger.info("successfully save the Payment to DB");

    }

    public Payment createPayment(Ride ride) {
        logger.info("Start to create Payment for rideId: {}", ride.getId());
        return Payment.builder()
                .ride(ride)
                .amount(222.070)
                .status(PaymentStatus.PENDING)
                .build();
    }

    /**
     * Estimates a demo fare for the ride.
     * This method simulates a distance-based fare calculation for demonstration purposes only.
     * In a real-world scenario, you would calculate the actual distance between
     * pickup and destination using map services (e.g., Google Maps API).
     */
    public double calculateEstimatedFareForDemo(String pickup,String destination) {
        logger.debug("Simulating fare estimation (demo mode) | pickup: {} | destination: {}", pickup, destination);

        // Simulate a pseudo "distance" using the string length difference (for demo purposes)
        int fakeDistanceKm = Math.abs(pickup.length() - destination.length()) + 5; // base 5 km

        double baseFare = 50.0;     // base charge in LKR
        double ratePerKm = 75.0;    // cost per km in LKR

        double estimatedFare = baseFare + (fakeDistanceKm * ratePerKm);
        logger.debug("Estimated demo fare calculated | fakeDistance: {} km | fare: {}", fakeDistanceKm, estimatedFare);
        return estimatedFare;
    }
}
