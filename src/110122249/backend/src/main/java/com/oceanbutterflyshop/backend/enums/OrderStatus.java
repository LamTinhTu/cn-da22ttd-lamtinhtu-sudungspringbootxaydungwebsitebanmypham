package com.oceanbutterflyshop.backend.enums;

public enum OrderStatus {
    NEW("Đã đặt"),
    PROCESSING("Đang xử lý"),
    SHIPPING("Đang giao"),
    DELIVERED("Đã giao"),
    CANCELLED("Đã hủy");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OrderStatus fromDisplayName(String displayName) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status: " + displayName);
    }
}