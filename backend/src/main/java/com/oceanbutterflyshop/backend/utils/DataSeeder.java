package com.oceanbutterflyshop.backend.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.oceanbutterflyshop.backend.entities.*;
import com.oceanbutterflyshop.backend.enums.Gender;
import com.oceanbutterflyshop.backend.enums.OrderStatus;
import com.oceanbutterflyshop.backend.enums.PaymentMethod;
import com.oceanbutterflyshop.backend.repositories.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "demo"}) // Only run in dev or demo profiles
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CodeGeneratorUtils codeGeneratorUtils;

    private final Faker faker = new Faker(new Locale("vi"));
    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data seeding with Vietnamese locale...");
        
        // Execute in proper order to satisfy FK constraints
        createRoles();
        createUsers();
        createBrands();
        createProducts();
        createImages();
        createOrders();
        
        log.info("Data seeding completed successfully!");
    }

    /**
     * Step 1: Create Roles (Fixed data)
     */
    private void createRoles() {
        if (roleRepository.count() == 0) {
            // Fixed roles as per specification
            Role adminRole = new Role();
            adminRole.setRoleCode("AD");
            adminRole.setRoleName("Administrator");
            roleRepository.save(adminRole);

            Role staffRole = new Role();
            staffRole.setRoleCode("NV");
            staffRole.setRoleName("Staff");
            roleRepository.save(staffRole);

            Role customerRole = new Role();
            customerRole.setRoleCode("KH");
            customerRole.setRoleName("Customer");
            roleRepository.save(customerRole);

            log.info("Created 3 roles: Administrator (AD), Staff (NV), Customer (KH)");
        }
    }

    /**
     * Step 2: Create Users (1 Admin, 1 Staff, 5 Customers)
     */
    private void createUsers() {
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByRoleCode("AD").orElse(null);
            Role staffRole = roleRepository.findByRoleCode("NV").orElse(null);
            Role customerRole = roleRepository.findByRoleCode("KH").orElse(null);

            // Create 1 Admin
            if (adminRole != null) {
                User admin = createUser("Admin", Gender.NAM, adminRole, "admin", "admin123");
                userRepository.save(admin);
                log.info("Created admin user: {}", admin.getUserName());
            }

            // Create 1 Staff
            if (staffRole != null) {
                User staff = createUser("Test Staff", Gender.NU, staffRole, "staff", "staff123");
                userRepository.save(staff);
                log.info("Created staff user: {}", staff.getUserName());
            }

            // Create 5 Customers
            if (customerRole != null) {
                for (int i = 0; i < 5; i++) {
                    Gender gender = random.nextBoolean() ? Gender.NAM : Gender.NU;
                    String name = gender == Gender.NAM ? 
                        faker.name().fullName() : 
                        faker.name().fullName();
                    
                    User customer = createUser(name, gender, customerRole, 
                        generateUsername(name), "password123");
                    userRepository.save(customer);
                    log.info("Created customer: {}", customer.getUserName());
                }
            }

            log.info("Created {} users total", userRepository.count());
        }
    }

    /**
     * Step 3: Create Brands (Real watch brands)
     */
    /**
     * Step 3: Create Brands (Thương hiệu Mỹ phẩm)
     */
    private void createBrands() {
        if (brandRepository.count() == 0) {
            String[] brandNames = {
                "L'Oréal Paris", "MAC Cosmetics", "Innisfree", "La Roche-Posay", "Laneige", 
                "Maybelline", "Estée Lauder", "Paula's Choice", "Vichy", "3CE"
            };
            
            String[] descriptions = {
                "Tập đoàn mỹ phẩm hàng đầu thế giới với các sản phẩm đa dạng từ trang điểm đến chăm sóc da.",
                "Thương hiệu trang điểm chuyên nghiệp đẳng cấp quốc tế, nổi tiếng với các dòng son thỏi.",
                "Thương hiệu mỹ phẩm thiên nhiên từ đảo Jeju, Hàn Quốc, thân thiện với môi trường.",
                "Dược mỹ phẩm Pháp được chuyên gia da liễu khuyên dùng cho da nhạy cảm.",
                "Thương hiệu cao cấp Hàn Quốc, nổi tiếng với công nghệ cấp nước và dưỡng ẩm chuyên sâu.",
                "Thương hiệu trang điểm số 1 thế giới, mang phong cách New York trẻ trung và năng động.",
                "Biểu tượng của sự sang trọng trong thế giới làm đẹp với các sản phẩm chống lão hóa.",
                "Thương hiệu dược mỹ phẩm chú trọng vào thành phần an toàn và hiệu quả thực tế trên da.",
                "Dược mỹ phẩm sử dụng nước khoáng núi lửa, giúp củng cố và bảo vệ hàng rào bảo vệ da.",
                "Thương hiệu trang điểm Hàn Quốc dẫn đầu xu hướng với phong cách trẻ trung, sành điệu."
            };

            for (int i = 0; i < brandNames.length; i++) {
                Brand brand = new Brand();
                
                // Generate unique brand code
                String brandCode;
                do {
                    brandCode = codeGeneratorUtils.generateCode("TH");
                } while (brandRepository.existsByBrandCode(brandCode));
                
                brand.setBrandCode(brandCode);
                brand.setBrandName(brandNames[i]);
                brand.setBrandDescription(descriptions[i]);
                
                brandRepository.save(brand);
                log.info("Created brand: {} with code {}", brand.getBrandName(), brand.getBrandCode());
            }

            log.info("Created {} brands", brandRepository.count());
        }
    }

    /**
     * Step 4: Create Products (Mỹ phẩm)
     */
    private void createProducts() {
        if (productRepository.count() == 0) {
            List<Brand> brands = brandRepository.findAll();
            
            // Các loại mỹ phẩm phổ biến
            String[] productTypes = {
                "Lipstick", "Foundation", "Serum", "Toner", "Sunscreen", 
                "Moisturizer", "Mascara", "Cleanser", "Face Mask", "Cushion"
            };

            // Các tính năng/đặc điểm để ghép vào tên cho phong phú
            String[] features = {
                "Matte", "Glow", "Hydrating", "Oil-Free", "Long-lasting", 
                "Whitening", "Anti-aging", "Sensitive", "Natural", "Waterproof"
            };

            for (int i = 0; i < 20; i++) {
                Product product = new Product();
                
                String productCode;
                do {
                    productCode = codeGeneratorUtils.generateCode("SP");
                } while (productRepository.existsByProductCode(productCode));
                
                Brand randomBrand = brands.get(random.nextInt(brands.size()));
                String productType = productTypes[random.nextInt(productTypes.length)];
                String feature = features[random.nextInt(features.length)];
                
                // Tên sản phẩm: [Brand] [Type] [Feature] (VD: MAC Lipstick Matte)
                product.setProductCode(productCode);
                product.setProductName(randomBrand.getBrandName() + " " + productType + " " + feature);
                
                product.setProductDescription(
                    String.format("Sản phẩm %s dòng %s của %s giúp mang lại vẻ đẹp tự nhiên và rạng rỡ. " +
                        "Công thức %s độc đáo, an toàn cho da và độ bám màu vượt trội suốt cả ngày.", 
                        productType, feature, randomBrand.getBrandName(), feature)
                );
                
                // Giá mỹ phẩm (Giả lập từ 150.000 đến 2.000.000)
                // Lưu ý: Nếu bạn có hàm generateRealisticPrice riêng thì cần sửa lại hàm đó, 
                // hoặc dùng logic random đơn giản dưới đây:
                
                // Làm tròn về hàng nghìn
                BigDecimal finalPrice = this.generateRealisticPrice(randomBrand.getBrandName());
                product.setProductPrice(finalPrice);
                
                product.setQuantityStock(ThreadLocalRandom.current().nextInt(10, 100)); // Mỹ phẩm thường nhập nhiều hơn đồng hồ
                product.setBrand(randomBrand);
                
                productRepository.save(product);
                log.info("Created product: {} - Price: {}", product.getProductName(), product.getProductPrice());
            }

            log.info("Created {} products", productRepository.count());
        }
    }

    /**
     * Step 5: Create Images (URL)
     */
    private void createImages() {
        if (imageRepository.count() == 0) {
            List<Product> products = productRepository.findAll();
            
            for (Product product : products) {
                int imageCount = ThreadLocalRandom.current().nextInt(1, 4);
                
                for (int i = 0; i < imageCount; i++) {
                    Image image = new Image();
                    image.setImageName(product.getProductCode() + "_cosmetic_" + (i + 1) + ".jpg");
                    // Đổi đường dẫn URL cho hợp ngữ cảnh
                    image.setImageURL("https://example.com/images/cosmetics/" + product.getProductCode() + "_" + (i + 1) + ".jpg");
                    image.setProduct(product);
                    
                    imageRepository.save(image);
                }
            }

            log.info("Created {} product images", imageRepository.count());
        }
    }

    /**
     * Step 6: Create Orders with snapshot logic
     */
    private void createOrders() {
        if (orderRepository.count() == 0) {
            List<User> customers = userRepository.findByRoleRoleCode("KH");
            List<Product> products = productRepository.findAll();
            
            // Create 10-15 random orders
            int orderCount = ThreadLocalRandom.current().nextInt(10, 16);
            
            for (int i = 0; i < orderCount; i++) {
                User customer = customers.get(random.nextInt(customers.size()));
                
                Order order = new Order();
                
                // Generate unique order code
                String orderCode;
                do {
                    orderCode = codeGeneratorUtils.generateCode("DH");
                } while (orderRepository.existsByOrderCode(orderCode));
                
                order.setOrderCode(orderCode);
                order.setUser(customer);
                order.setOrderDate(generateRandomDate());
                order.setOrderStatus(getRandomOrderStatus());
                
                // SNAPSHOT LOGIC: Copy user's current address/phone to order
                order.setShippingAddress(customer.getUserAddress());
                order.setShippingPhone(customer.getUserPhone());
                
                order.setPaymentMethod(getRandomPaymentMethod());
                
                if (order.getOrderStatus() != OrderStatus.NEW) {
                    order.setPaymentDate(order.getOrderDate().plusDays(ThreadLocalRandom.current().nextInt(1, 8)));
                }
                
                orderRepository.save(order);
                
                // Create order items
                createOrderItems(order, products);
                
                log.info("Created order: {} for customer: {} - Status: {}", 
                    order.getOrderCode(), customer.getUserName(), order.getOrderStatus());
            }

            log.info("Created {} orders with order items", orderRepository.count());
        }
    }

    /**
     * Helper method to create order items for an order
     */
    private void createOrderItems(Order order, List<Product> products) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // Each order has 1-4 different products
        int itemTypeCount = ThreadLocalRandom.current().nextInt(1, 5);
        List<Product> selectedProducts = new ArrayList<>();
        
        for (int i = 0; i < itemTypeCount; i++) {
            Product product;
            do {
                product = products.get(random.nextInt(products.size()));
            } while (selectedProducts.contains(product));
            selectedProducts.add(product);
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            
            int quantity = ThreadLocalRandom.current().nextInt(1, 4); // 1-3 items
            orderItem.setItemQuantity(quantity);
            
            // Use current product price as item price (snapshot)
            orderItem.setItemPrice(product.getProductPrice());
            orderItem.setUnitPrice(product.getProductPrice().doubleValue()); // Legacy field
            
            orderItemRepository.save(orderItem);
            
            // Calculate total
            totalAmount = totalAmount.add(product.getProductPrice().multiply(BigDecimal.valueOf(quantity)));
        }
        
        // Update order total amount
        order.setOrderAmount(totalAmount);
        orderRepository.save(order);
    }

    /**
     * Helper methods
     */
    private User createUser(String name, Gender gender, Role role, String account, String password) {
        User user = new User();
        
        // Generate unique user code based on role
        String userCode;
        do {
            userCode = codeGeneratorUtils.generateCode(role.getRoleCode());
        } while (userRepository.existsByUserCode(userCode));
        
        user.setUserCode(userCode);
        user.setUserName(name);
        user.setUserGender(gender);
        user.setUserBirthDate(generateRandomBirthDate());
        user.setUserAddress(generateVietnameseAddress());
        user.setUserPhone(generateVietnamesePhone());
        user.setUserAccount(account);
        user.setUserPassword(password); // In production, should be hashed
        user.setRole(role);
        
        return user;
    }

    private String generateUsername(String fullName) {
        return fullName.toLowerCase()
            .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
            .replaceAll("[èéẹẻẽêềếệểễ]", "e")
            .replaceAll("[ìíịỉĩ]", "i")
            .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
            .replaceAll("[ùúụủũưừứựửữ]", "u")
            .replaceAll("[ỳýỵỷỹ]", "y")
            .replaceAll("[đ]", "d")
            .replaceAll("\\s+", "")
            .replaceAll("[^a-zA-Z0-9]", "");
    }

    private LocalDate generateRandomBirthDate() {
        int year = ThreadLocalRandom.current().nextInt(1970, 2000);
        int month = ThreadLocalRandom.current().nextInt(1, 13);
        int day = ThreadLocalRandom.current().nextInt(1, 29);
        return LocalDate.of(year, month, day);
    }

    private String generateVietnameseAddress() {
        String[] streets = {
            "Lê Lợi", "Nguyễn Huệ", "Đồng Khởi", "Hai Bà Trưng", "Trần Hưng Đạo",
            "Lý Tự Trọng", "Nam Kỳ Khởi Nghĩa", "Võ Văn Tần", "Cách Mạng Tháng 8"
        };
        String[] districts = {
            "Quận 1", "Quận 3", "Quận 5", "Quận 7", "Quận Bình Thạnh", "Quận Phú Nhuận"
        };
        
        int number = ThreadLocalRandom.current().nextInt(1, 500);
        String street = streets[random.nextInt(streets.length)];
        String district = districts[random.nextInt(districts.length)];
        
        return String.format("%d Đường %s, %s, TP.HCM", number, street, district);
    }

    private String generateVietnamesePhone() {
        String[] prefixes = {"090", "091", "092", "093", "094", "095", "096", "097", "098", "099",
                           "032", "033", "034", "035", "036", "037", "038", "039"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String suffix = String.format("%07d", ThreadLocalRandom.current().nextInt(0, 9999999));
        return prefix + suffix;
    }

    private LocalDate generateRandomDate() {
        return LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(0, 90));
    }

    private OrderStatus getRandomOrderStatus() {
        OrderStatus[] statuses = OrderStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    private PaymentMethod getRandomPaymentMethod() {
        PaymentMethod[] methods = PaymentMethod.values();
        return methods[random.nextInt(methods.length)];
    }

    private BigDecimal generateRealisticPrice(String brandName) {
        BigDecimal basePrice;
        
        // Set realistic price ranges based on brand prestige
        switch (brandName.toLowerCase()) {
            case "rolex":
            case "omega":
                basePrice = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(15000, 50000));
                break;
            case "tag heuer":
            case "tissot":
                basePrice = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(5000, 15000));
                break;
            case "seiko":
            case "citizen":
                basePrice = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1000, 5000));
                break;
            case "casio":
            case "timex":
                basePrice = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(200, 1000));
                break;
            default:
                basePrice = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(500, 3000));
        }
        
        // Round to 2 decimal places
        return basePrice.setScale(2, RoundingMode.HALF_UP);
    }
}