package com.project.ridex_backend.dto.response;

import com.project.ridex_backend.enums.PaymentMethod;
import com.project.ridex_backend.enums.PaymentStatus;
import lombok.*;

@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PaymentResponse {
    private PaymentMethod paymentMethod;
    private double amount;
    private PaymentStatus status;
}
