package com.oceanbutterflyshop.backend.enums;

public enum ProductStatus {
    NOT_SOLD("Chưa bán"),
    SELLING("Đang bán"),
    OUT_OF_STOCK("Hết hàng"),
    DISCONTINUED("Ngừng kinh doanh");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ProductStatus fromDisplayName(String displayName) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid product status: " + displayName);
    }
}
