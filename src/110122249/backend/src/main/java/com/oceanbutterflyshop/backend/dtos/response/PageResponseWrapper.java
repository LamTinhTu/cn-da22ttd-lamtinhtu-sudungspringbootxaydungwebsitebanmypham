package com.oceanbutterflyshop.backend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Lớp bọc chung cho các phản hồi API phân trang.
 * Cung cấp cấu trúc nhất quán cho tất cả các endpoint phân trang.
 *
 * @param <T> Loại dữ liệu của các item trong trang
 *
 * Ví dụ sử dụng:
 * <pre>
 * Page<Product> productPage = productService.getAllProducts(pageable);
 * PageResponseWrapper<ProductResponse> response = PageResponseWrapper.of(
 *     productPage.map(productMapper::toResponse)
 * );
 * return ResponseEntity.ok(ApiResponse.success("Products retrieved", response));
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseWrapper<T> {
    
    /**
     * Danh sách các item trong trang hiện tại
     */
    private List<T> items;
    
    /**
     * Số trang hiện tại (bắt đầu từ 0)
     */
    private int pageNo;
    
    /**
     * Số lượng item trên mỗi trang
     */
    private int pageSize;
    
    /**
     * Tổng số trang
     */
    private int totalPages;
    
    /**
     * Tổng số phần tử trên tất cả các trang
     */
    private long totalElements;
    
    /**
     * Có phải là trang đầu tiên không
     */
    private boolean first;
    
    /**
     * Có phải là trang cuối cùng không
     */
    private boolean last;
    
    /**
     * Có trang tiếp theo không
     */
    private boolean hasNext;
    
    /**
     * Có trang trước đó không
     */
    private boolean hasPrevious;
    
    /**
     * Phương thức static factory để tạo PageResponseWrapper từ đối tượng Spring Data Page
     *
     * @param page Đối tượng Spring Data Page
     * @param <T> Loại của các item trong trang
     * @return PageResponseWrapper chứa dữ liệu trang
     */
    public static <T> PageResponseWrapper<T> of(Page<T> page) {
        return PageResponseWrapper.<T>builder()
                .items(page.getContent())
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
