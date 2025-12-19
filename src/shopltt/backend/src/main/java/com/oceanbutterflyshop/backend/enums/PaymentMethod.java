package com.oceanbutterflyshop.backend.enums;

public enum PaymentMethod {
    CASH("Tiền mặt"),
    BANK_TRANSFER("Chuyển khoản"),
    CARD("Thẻ tín dụng");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentMethod fromDisplayName(String displayName) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.displayName.equals(displayName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid payment method: " + displayName);
    }
}