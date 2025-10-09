package com.project.ridex_backend.dto.request;

import com.project.ridex_backend.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class PaymentRequest {
    @NotBlank
    private String pickupLocation;
    @NotBlank
    private String dropLocation;
    @NotNull
    private PaymentMethod paymentMethod;
}
