package com.oceanbutterflyshop.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendOTPRequestDTO {
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[0-9]{9,10}|\\+1[0-9]{10})$", message = "Số điện thoại phải là VN (0XXXXXXXXX) hoặc US (+1XXXXXXXXXX)")
    private String phoneNumber;
}
