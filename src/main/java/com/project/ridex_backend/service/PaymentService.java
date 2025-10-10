package com.project.ridex_backend.service;

import com.project.ridex_backend.dto.request.PaymentRequest;
import com.project.ridex_backend.entity.Payment;
import com.project.ridex_backend.entity.Ride;
import com.project.ridex_backend.enums.PaymentMethod;
import com.project.ridex_backend.enums.PaymentStatus;
import com.project.ridex_backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    public void processPayment(Ride ride, PaymentRequest paymentRequest) {
        logger.info("Processing Payment | rideId: {}", ride.getId());
        //TODO:  verify payment method and recalculate , Confirm Payment

        savePayment(ride.getPayment(), PaymentStatus.SUCCESS, ride.getPayment().getAmount(), paymentRequest.getPaymentMethod());
    }

    public Payment createPayment(Ride ride) {
        logger.info("Creating Payment | rideId: {}", ride.getId());

        double fare = calculateEstimatedFareForDemo(ride.getPickup(), ride.getDestination());

        Payment payment = Payment.builder()
                .ride(ride)
                .build();

        return savePayment(payment, PaymentStatus.PENDING, fare, PaymentMethod.CARD);
    }

    private Payment savePayment(Payment payment, PaymentStatus status, double amount, PaymentMethod method) {
        payment.setStatus(status);
        payment.setAmount(amount);
        payment.setPaymentMethod(method);
        paymentRepository.save(payment);
        return payment;
    }

    /**
     * Estimates a demo fare for the ride.
     * This method simulates a distance-based fare calculation for demonstration purposes only.
     * In a real-world scenario, you would calculate the actual distance between
     * pickup and destination using map services (e.g., Google Maps API).
     */
    public double calculateEstimatedFareForDemo(String pickup, String destination) {
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
