package com.oceanbutterflyshop.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oceanbutterflyshop.backend.dtos.ApiResponse;
import com.oceanbutterflyshop.backend.dtos.request.OrderRequest;
import com.oceanbutterflyshop.backend.dtos.response.OrderResponse;
import com.oceanbutterflyshop.backend.dtos.response.PageResponseWrapper;
import com.oceanbutterflyshop.backend.services.OrderService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(
        summary = "Get orders with pagination",
        description = """
            Retrieve orders with pagination and sorting support. Requires ADMIN or STAFF role.
            
            **Default Behavior:** Returns page 0 with 10 items, sorted by orderId descending.
            
            **Pagination Parameters:**
            - page: Page number (0-indexed, default: 0)
            - size: Number of items per page (default: 10)
            - sort: Sort field and direction (default: orderId,desc)
            
            **Sortable Fields:** orderId, orderDate, totalPrice, orderStatus
            
            **Examples:**
            - GET /api/v1/orders → Returns page 0, size 10 (default)
            - GET /api/v1/orders?page=1&size=5 → Returns page 1, size 5
            - GET /api/v1/orders?sort=orderDate,desc → Sorted by date descending
            """)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<PageResponseWrapper<OrderResponse>>> getAllOrders(
            @Parameter(hidden = true) @PageableDefault(size = 10, sort = "orderId", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(required = false, defaultValue = "10") Integer size,
            @Parameter(description = "Sort field and direction (format: field,direction)", example = "orderId,desc") @RequestParam(required = false, defaultValue = "orderId,desc") String sort
    ) {
        // Phân tích tham số sắp xếp thủ công
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageableRequest = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<OrderResponse> orderPage = orderService.getAllOrdersPaginated(pageableRequest);
        PageResponseWrapper<OrderResponse> response = PageResponseWrapper.of(orderPage);
        
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", response));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Customers can only view their own orders. Admin/Staff can view all orders.")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        OrderResponse order = orderService.getOrderById(orderId, username);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
    }

    @GetMapping("/code/{orderCode}")
    @Operation(summary = "Get order by code", description = "Customers can only view their own orders. Admin/Staff can view all orders.")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByCode(
            @PathVariable String orderCode,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        OrderResponse order = orderService.getOrderByCode(orderCode, username);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user ID")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByUserId(@PathVariable Integer userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("User orders retrieved successfully", orders));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByStatus(@PathVariable String status) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Orders by status retrieved successfully", orders));
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Get the logged-in user's username from SecurityContext
        String username = userDetails.getUsername();
        OrderResponse createdOrder = orderService.createOrder(orderRequest, username);
        return new ResponseEntity<>(
            ApiResponse.success("Order created successfully", createdOrder),
            HttpStatus.CREATED
        );
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam String status) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", updatedOrder));
    }

    @PutMapping("/{orderId}/payment")
    @Operation(summary = "Update order payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<OrderResponse>> updatePayment(
            @PathVariable Integer orderId,
            @RequestParam String paymentMethod) {
        OrderResponse updatedOrder = orderService.updatePayment(orderId, paymentMethod);
        return ResponseEntity.ok(ApiResponse.success("Order payment updated successfully", updatedOrder));
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Customers can only cancel their own orders. Admin/Staff can cancel any order.")
    public ResponseEntity<ApiResponse<Object>> cancelOrder(
            @PathVariable Integer orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        orderService.cancelOrder(orderId, username);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", null));
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{orderId}")
    @Operation(summary = "Delete order", description = "Hard delete an order. Allowed if order status is CANCELLED or PROCESSING.")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteOrder(@PathVariable Integer orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        orderService.deleteOrder(orderId, username);
        return ResponseEntity.ok(ApiResponse.success("Order deleted successfully", null));
    }

    @GetMapping("/calculate-amount")
    @Operation(summary = "Calculate order amount")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateOrderAmount(
            @RequestParam List<Integer> productIds,
            @RequestParam List<Integer> quantities) {
        BigDecimal amount = orderService.calculateOrderAmount(productIds, quantities);
        return ResponseEntity.ok(ApiResponse.success("Order amount calculated", amount));
    }
}