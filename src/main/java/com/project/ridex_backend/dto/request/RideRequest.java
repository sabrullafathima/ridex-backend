package com.project.ridex_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RideRequest {
    @NotBlank(message = "pickup is required")
    private String pickup;
    @NotBlank(message = "destination is required")
    private String destination;
}
