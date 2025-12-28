package com.oceanbutterflyshop.backend.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import com.oceanbutterflyshop.backend.entities.Product;
import com.oceanbutterflyshop.backend.enums.ProductStatus;
import com.oceanbutterflyshop.backend.enums.ProductCategory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification cho thực thể Product.
 * Cung cấp khả năng xây dựng truy vấn động cho việc tìm kiếm và lọc sản phẩm.
 *
 * Ví dụ sử dụng:
 * <pre>
 * Specification<Product> spec = ProductSpecification.builder()
 *     .keyword("Rolex")
 *     .minPrice(BigDecimal.valueOf(1000))
 *     .maxPrice(BigDecimal.valueOf(5000))
 *     .brandId(1)
 *     .status(ProductStatus.SELLING)
 *     .build();
 * Page<Product> products = productRepository.findAll(spec, pageable);
 * </pre>
 */
public class ProductSpecification {

    /**
     * Tạo một specification để lọc sản phẩm theo nhiều tiêu chí
     *
     * @param keyword Từ khóa tìm kiếm trong tên hoặc mô tả sản phẩm
     * @param minPrice Giá sản phẩm tối thiểu (bao gồm)
     * @param maxPrice Giá sản phẩm tối đa (bao gồm)
     * @param brandId ID thương hiệu để lọc
     * @param status Trạng thái sản phẩm để lọc
     * @param category Loại sản phẩm để lọc (MAKEUP, SKINCARE, HAIRCARE)
     * @return Specification cho thực thể Product
     */
    public static Specification<Product> filterProducts(
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer brandId,
            ProductStatus status,
            ProductCategory category
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm kiếm từ khóa trong tên hoặc mô tả sản phẩm (không phân biệt chữ hoa chữ thường)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("productName")),
                    searchPattern
                );
                Predicate descriptionPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("productDescription")),
                    searchPattern
                );
                predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
            }

            // Lọc theo giá tối thiểu
            if (minPrice != null) {
                predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("productPrice"), minPrice)
                );
            }

            // Lọc theo giá tối đa
            if (maxPrice != null) {
                predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(root.get("productPrice"), maxPrice)
                );
            }

            // Lọc theo ID thương hiệu
            if (brandId != null) {
                predicates.add(
                    criteriaBuilder.equal(root.get("brand").get("brandId"), brandId)
                );
            }

            // Lọc theo trạng thái sản phẩm
            if (status != null) {
                predicates.add(
                    criteriaBuilder.equal(root.get("productStatus"), status)
                );
            }

            // Lọc theo loại sản phẩm
            if (category != null) {
                predicates.add(
                    criteriaBuilder.equal(root.get("productCategory"), category)
                );
            }

            // Kết hợp tất cả các điều kiện với AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Tạo một specification để tìm kiếm sản phẩm chỉ bằng từ khóa
     *
     * @param keyword Từ khóa tìm kiếm trong tên hoặc mô tả sản phẩm
     * @return Specification cho thực thể Product
     */
    public static Specification<Product> searchByKeyword(String keyword) {
        return filterProducts(keyword, null, null, null, null, null);
    }

    /**
     * Tạo một specification để lọc sản phẩm theo khoảng giá
     *
     * @param minPrice Giá sản phẩm tối thiểu
     * @param maxPrice Giá sản phẩm tối đa
     * @return Specification cho thực thể Product
     */
    public static Specification<Product> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return filterProducts(null, minPrice, maxPrice, null, null, null);
    }

    /**
     * Tạo một specification để lọc sản phẩm theo thương hiệu
     *
     * @param brandId ID thương hiệu
     * @return Specification cho thực thể Product
     */
    public static Specification<Product> filterByBrand(Integer brandId) {
        return filterProducts(null, null, null, brandId, null, null);
    }

    /**
     * Tạo một specification để lọc sản phẩm theo trạng thái
     *
     * @param status Trạng thái sản phẩm
     * @return Specification cho thực thể Product
     */
    public static Specification<Product> filterByStatus(ProductStatus status) {
        return filterProducts(null, null, null, null, status, null);
    }
}
