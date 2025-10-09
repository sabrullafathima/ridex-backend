package com.project.ridex_backend.controller;

import com.project.ridex_backend.dto.request.PaymentRequest;
import com.project.ridex_backend.dto.response.PaymentResponse;
import com.project.ridex_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

//    @PostMapping("/{rideId}")
//    public ResponseEntity<PaymentResponse> processPayment(@PathVariable Long rideId, @RequestBody PaymentRequest paymentRequest) {
//        logger.info("Processing payment for rideId: {}", rideId);
//        PaymentResponse paymentResponse = paymentService.processPayment(rideId, paymentRequest);
//        return ResponseEntity.ok(paymentResponse);
//    }
}
