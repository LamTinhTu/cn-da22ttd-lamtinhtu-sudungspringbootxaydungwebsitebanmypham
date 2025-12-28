package com.oceanbutterflyshop.backend.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.oceanbutterflyshop.backend.entities.Brand;
import com.oceanbutterflyshop.backend.entities.Image;
import com.oceanbutterflyshop.backend.entities.Order;
import com.oceanbutterflyshop.backend.entities.OrderItem;
import com.oceanbutterflyshop.backend.entities.Product;
import com.oceanbutterflyshop.backend.entities.Role;
import com.oceanbutterflyshop.backend.entities.User;
import com.oceanbutterflyshop.backend.enums.Gender;
import com.oceanbutterflyshop.backend.enums.OrderStatus;
import com.oceanbutterflyshop.backend.enums.PaymentMethod;
import com.oceanbutterflyshop.backend.enums.ProductStatus;
import com.oceanbutterflyshop.backend.repositories.BrandRepository;
import com.oceanbutterflyshop.backend.repositories.ImageRepository;
import com.oceanbutterflyshop.backend.repositories.OrderItemRepository;
import com.oceanbutterflyshop.backend.repositories.OrderRepository;
import com.oceanbutterflyshop.backend.repositories.ProductRepository;
import com.oceanbutterflyshop.backend.repositories.RoleRepository;
import com.oceanbutterflyshop.backend.repositories.UserRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Data seeder for Cosmetics Store application.
 * Uses net.datafaker with Vietnamese locale (vi).
 * Execution order: Role -> User -> Brand -> Product -> Order -> OrderItem.
 * * Requirements:
 * - Roles: 3 fixed roles (ADM, STF, CUS)
 * - Users: 1 Admin, 1 Staff, 5 Customers with BCrypt hashed passwords
 * - Brands: Famous Cosmetic brands
 * - Products: 20 cosmetic items (Lipstick, Cream, Serum...)
 * - Orders: Random orders with snapshot logic for address/phone
 */
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

    private final Random random = new Random();
    
    // BCrypt password encoder
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        log.info("========================================");
        log.info("DATA SEEDER: Checking database status...");
        log.info("========================================");
        
        long roleCount = roleRepository.count();
        long userCount = userRepository.count();
        
        log.info("Current database state: {} roles, {} users", roleCount, userCount);
        
        createRoles();
        createAdminAndStaff();
        createCustomers();
        createBrands();
        createProducts();
        createImages();
        createOrders();

        log.info("========================================");
        log.info("DATA SEEDER: Data seeding process completed.");
        log.info("========================================");
    }

    // Step 1: Create Roles (Giữ nguyên logic)
    private void createRoles() {
        log.info("Step 1: Creating roles...");

        if(roleRepository.count() > 0) {
            log.warn("⚠ Roles already exist. Skipping role creation.");
            return;
        }
        
        Role adminRole = new Role();
        adminRole.setRoleCode("ADM");
        adminRole.setRoleName("Administrator");
        roleRepository.save(adminRole);

        Role staffRole = new Role();
        staffRole.setRoleCode("STF");
        staffRole.setRoleName("Staff");
        roleRepository.save(staffRole);

        Role customerRole = new Role();
        customerRole.setRoleCode("CUS");
        customerRole.setRoleName("Customer");
        roleRepository.save(customerRole);

        log.info("✓ Created 3 roles: Administrator (ADM), Staff (STF), Customer (CUS)");
    }

    // Step 2: Create Admin & Staff (Giữ nguyên logic)
    private void createAdminAndStaff() {
        log.info("Step 2: Creating Admin & Staff accounts...");
        
        if(userRepository.count() > 0) {
            log.warn("⚠ Users already exist. Skipping Admin & Staff creation.");
            return;
        }

        Role adminRole = roleRepository.findByRoleCode("ADM")
            .orElseThrow(() -> new RuntimeException("Admin role not found. Ensure Step 1 completed successfully."));
        Role staffRole = roleRepository.findByRoleCode("STF")
            .orElseThrow(() -> new RuntimeException("Staff role not found. Ensure Step 1 completed successfully."));

        // Create 1 Admin
        User admin = createUser(
            "Nguyễn Văn Quản Trị",
            Gender.MALE,
            adminRole,
            "admin",
            "password"
        );
        userRepository.save(admin);
        log.info("  ✓ Created Admin: {} (Code: {}, Username: admin)", admin.getUserName(), admin.getUserCode());

        // Create 1 Staff
        User staff = createUser(
            "Trần Thị Thu Ngân",
            Gender.FEMALE,
            staffRole,
            "staff",
            "password"
        );
        userRepository.save(staff);
        log.info("  ✓ Created Staff: {} (Code: {}, Username: staff)", staff.getUserName(), staff.getUserCode());

        log.info("✓ Step 2 complete: Created {} privileged users", userRepository.count());
    }
    
    // Step 3A: Create Customers (Giữ nguyên logic tạo user, đổi tên cho nữ tính hơn một chút vì shop mỹ phẩm)
    private void createCustomers() {
        log.info("Step 3: Creating Customer accounts...");
        
        if(userRepository.count() > 2) {
            log.warn("⚠ More than 2 users exist. Skipping Customer creation.");
            return;
        }

        Role customerRole = roleRepository.findByRoleCode("CUS")
            .orElseThrow(() -> new RuntimeException("Customer role not found. Ensure Step 1 completed successfully."));

        String[] customerNames = {
            "Lê Thị Hồng Hạnh",
            "Phạm Minh Trang",
            "Hoàng Văn Thái", // Nam cũng mua mỹ phẩm/quà tặng
            "Võ Thu Thảo",
            "Đặng Ngọc Mai"
        };
        
        Gender[] genders = {Gender.FEMALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.FEMALE};
        
        for (int i = 0; i < 5; i++) {
            String username = "customer" + (i + 1);
            User customer = createUser(
                customerNames[i],
                genders[i],
                customerRole,
                username,
                "password"
            );
            userRepository.save(customer);
            log.info("  ✓ Created Customer: {} (Username: {})", customer.getUserName(), username);
        }

        log.info("✓ Step 3A complete: Created 5 customers");
    }

    /**
     * Step 3B: Create Brands (Cosmetic Brands)
     */
    private void createBrands() {
        log.info("Step 3B: Creating brands...");

        if(brandRepository.count() > 0) {
            log.warn("⚠ Brands already exist. Skipping brand creation.");
            return;
        }
        
        // Dữ liệu Brand Mỹ Phẩm
        String[][] brandData = {
            {"L'Oréal", "Tập đoàn mỹ phẩm hàng đầu thế giới từ Pháp với đa dạng sản phẩm"},
            {"MAC", "Thương hiệu trang điểm chuyên nghiệp đẳng cấp quốc tế"},
            {"Innisfree", "Mỹ phẩm thiên nhiên đến từ đảo Jeju, Hàn Quốc"},
            {"Laneige", "Thương hiệu dưỡng da cao cấp tập trung vào độ ẩm và sự rạng rỡ"},
            {"Maybelline", "Thương hiệu trang điểm số 1 tại Mỹ, trẻ trung và phong cách"},
            {"Estée Lauder", "Biểu tượng của sự sang trọng và công nghệ dưỡng da tiên tiến"},
            {"La Roche-Posay", "Dược mỹ phẩm Pháp chuyên sâu cho da nhạy cảm"},
            {"Vichy", "Thương hiệu dược mỹ phẩm sử dụng nước khoáng núi lửa"},
            {"Shiseido", "Sự kết hợp giữa khoa học phương Tây và bí quyết làm đẹp phương Đông"},
            {"Kiehl's", "Mỹ phẩm dưỡng da tự nhiên có nguồn gốc từ hiệu thuốc cũ ở New York"}
        };

        for (String[] data : brandData) {
            Brand brand = new Brand();
            
            String brandCode = generateUniqueCode("TH", code -> 
                brandRepository.findByBrandCode(code).isPresent()
            );
            
            brand.setBrandCode(brandCode);
            brand.setBrandName(data[0]);
            brand.setBrandDescription(data[1]);
            
            brandRepository.save(brand);
        }

        log.info("✓ Step 3B complete: Created {} brands", brandRepository.count());
    }

    /**
     * Step 4: Create Products (Cosmetic Items)
     */
    private void createProducts() {
        log.info("Step 4: Creating products...");

        if(productRepository.count() > 0) {
            log.warn("⚠ Products already exist. Skipping product creation.");
            return;
        }
        
        List<Brand> brands = brandRepository.findAll();
        
        if (brands.isEmpty()) {
            throw new RuntimeException("No brands found. Ensure Step 3B completed successfully.");
        }
        
        // Loại sản phẩm mỹ phẩm
        String[] productTypes = {
            "Son lì Matte", "Son dưỡng môi", "Kem nền Foundation", "Phấn nước Cushion", 
            "Mascara chống nước", "Kẻ mắt Eyeliner", "Kem dưỡng ẩm", "Serum Vitamin C", 
            "Toner cân bằng da", "Sữa rửa mặt", "Kem chống nắng", "Mặt nạ ngủ",
            "Tẩy trang Micellar", "Kem mắt Anti-aging", "Phấn má hồng"
        };
        
        // Tính từ mô tả
        String[] adjectives = {"Cao cấp", "Dưỡng trắng", "Phục hồi da", "Kiềm dầu", "Cấp ẩm sâu", "Tự nhiên"};

        for (int i = 0; i < 20; i++) {
            Product product = new Product();
            
            String productCode = generateUniqueCode("SP", code -> 
                productRepository.findByProductCode(code).isPresent()
            );
            
            Brand randomBrand = brands.get(random.nextInt(brands.size()));
            String productType = productTypes[random.nextInt(productTypes.length)];
            String adjective = adjectives[random.nextInt(adjectives.length)];
            
            product.setProductCode(productCode);
            product.setProductName(randomBrand.getBrandName() + " " + productType + " " + adjective);
            
            product.setProductDescription(
                String.format("Sản phẩm %s thuộc dòng %s của thương hiệu %s. " +
                    "Công thức %s giúp mang lại vẻ đẹp tự nhiên và nuôi dưỡng làn da khỏe mạnh từ bên trong.", 
                    productType, adjective, randomBrand.getBrandName(), adjective.toLowerCase())
            );
            
            // Generate realistic prices based on brand positioning
            BigDecimal basePrice = generateRealisticPrice(randomBrand.getBrandName());
            product.setProductPrice(basePrice);
            
            product.setQuantityStock(ThreadLocalRandom.current().nextInt(10, 100));
            
            // Set ProductStatus
            int statusRoll = random.nextInt(100);
            if (statusRoll < 80) { // 80% Selling
                product.setProductStatus(ProductStatus.SELLING);
            } else if (statusRoll < 95) { // 15% Not Sold (New arrival/Coming soon)
                product.setProductStatus(ProductStatus.NOT_SOLD);
            } else { // 5% Out of Stock
                product.setProductStatus(ProductStatus.OUT_OF_STOCK);
            }
            
            product.setBrand(randomBrand);
            
            productRepository.save(product);
        }

        log.info("✓ Step 4 complete: Created {} cosmetic products", productRepository.count());
    }

    // Step 4B: Create Images (Sử dụng local uploads)
    private void createImages() {
        log.info("Step 4B: Creating product images...");

        // Xóa images cũ nếu có URL external
        long oldImageCount = imageRepository.count();
        if(oldImageCount > 0) {
            // Kiểm tra xem có images với URL external không
            List<Image> oldImages = imageRepository.findAll();
            boolean hasExternalUrls = oldImages.stream()
                .anyMatch(img -> img.getImageURL() != null && 
                         img.getImageURL().startsWith("https://images.oceanbutterfly.com"));
            
            if (hasExternalUrls) {
                log.info("Found {} images with external URLs. Deleting and recreating with local paths...", oldImageCount);
                imageRepository.deleteAll();
            } else {
                log.warn("⚠ Images already exist with local URLs. Skipping image creation.");
                return;
            }
        }
        
        // Tạo sample placeholder images
        createSampleImages();
        
        List<Product> products = productRepository.findAll();
        
        int totalImages = 0;
        
        for (Product product : products) {
            int imageCount = ThreadLocalRandom.current().nextInt(1, 3);
            
            for (int i = 0; i < imageCount; i++) {
                Image image = new Image();
                String filename = product.getProductCode() + "_" + (i + 1) + ".jpg";
                image.setImageName(filename);
                // Sử dụng local uploads path
                image.setImageURL("/uploads/" + filename);
                image.setProduct(product);
                
                imageRepository.save(image);
                totalImages++;
            }
        }

        log.info("✓ Step 4B complete: Created {} product images", totalImages);
    }
    
    /**
     * Tạo sample placeholder images trong thư mục uploads
     */
    private void createSampleImages() {
        try {
            Path uploadsDir = Paths.get("uploads").toAbsolutePath().normalize();
            
            // Tạo thư mục uploads nếu chưa tồn tại
            if (!Files.exists(uploadsDir)) {
                Files.createDirectories(uploadsDir);
                log.info("Created uploads directory: {}", uploadsDir);
            }
            
            // Tạo sample images cho tất cả products
            List<Product> products = productRepository.findAll();
            int createdImages = 0;
            
            for (Product product : products) {
                int imageCount = ThreadLocalRandom.current().nextInt(1, 3);
                
                for (int i = 0; i < imageCount; i++) {
                    String filename = product.getProductCode() + "_" + (i + 1) + ".jpg";
                    Path imagePath = uploadsDir.resolve(filename);
                    
                    // Chỉ tạo nếu file chưa tồn tại
                    if (!Files.exists(imagePath)) {
                        createPlaceholderImage(imagePath, product.getProductName(), 400, 400);
                        createdImages++;
                    }
                }
            }
            
            log.info("✓ Created {} sample placeholder images in uploads directory", createdImages);
            
        } catch (IOException e) {
            log.error("Failed to create sample images", e);
        }
    }
    
    /**
     * Tạo một placeholder image đơn giản
     */
    private void createPlaceholderImage(Path imagePath, String productName, int width, int height) throws IOException {
        // Tạo một BufferedImage đơn giản với màu gradient
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            width, height, java.awt.image.BufferedImage.TYPE_INT_RGB
        );
        
        java.awt.Graphics2D g2d = image.createGraphics();
        
        // Background gradient
        java.awt.GradientPaint gradient = new java.awt.GradientPaint(
            0, 0, new java.awt.Color(255, 192, 203), // Light pink
            width, height, new java.awt.Color(255, 218, 224) // Lighter pink
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // Draw border
        g2d.setColor(new java.awt.Color(255, 182, 193));
        g2d.setStroke(new java.awt.BasicStroke(3));
        g2d.drawRect(2, 2, width - 4, height - 4);
        
        // Draw product name text
        g2d.setColor(new java.awt.Color(139, 69, 19));
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        
        // Wrap text if too long
        String displayText = productName.length() > 30 
            ? productName.substring(0, 27) + "..." 
            : productName;
            
        java.awt.FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(displayText);
        int textX = (width - textWidth) / 2;
        int textY = height / 2;
        
        g2d.drawString(displayText, textX, textY);
        
        // Draw "SAMPLE" watermark
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 12));
        g2d.setColor(new java.awt.Color(200, 100, 100, 128));
        String watermark = "SAMPLE IMAGE";
        int wmWidth = g2d.getFontMetrics().stringWidth(watermark);
        g2d.drawString(watermark, (width - wmWidth) / 2, height - 20);
        
        g2d.dispose();
        
        // Save as JPEG
        javax.imageio.ImageIO.write(image, "jpg", imagePath.toFile());
    }

    // Step 5: Create Orders (Logic giữ nguyên)
    private void createOrders() {
        log.info("Step 5: Creating orders with order items...");

        if(orderRepository.count() > 0) {
            log.warn("⚠ Orders already exist. Skipping order creation.");
            return;
        }
        
        List<User> customers = userRepository.findByRole_RoleCode("CUS");
        List<Product> products = productRepository.findAll();
        
        if (customers.isEmpty() || products.isEmpty()) return;
        
        int orderCount = ThreadLocalRandom.current().nextInt(10, 16);
        
        for (int i = 0; i < orderCount; i++) {
            User customer = customers.get(random.nextInt(customers.size()));
            
            Order order = new Order();
            String orderCode = generateUniqueCode("DH", code -> 
                orderRepository.findByOrderCode(code).isPresent()
            );
            
            order.setOrderCode(orderCode);
            order.setUser(customer);
            order.setOrderDate(generateRandomDate());
            order.setOrderStatus(getRandomOrderStatus());
            
            order.setShippingAddress(customer.getUserAddress());
            order.setShippingPhone(customer.getUserPhone());
            order.setPaymentMethod(getRandomPaymentMethod());
            
            if (order.getOrderStatus() != OrderStatus.NEW) {
                order.setPaymentDate(order.getOrderDate().plusDays(1));
            }
            
            order.setOrderAmount(BigDecimal.ZERO);
            orderRepository.save(order);
            
            BigDecimal totalAmount = createOrderItems(order, products);
            order.setOrderAmount(totalAmount);
            orderRepository.save(order);
        }

        log.info("✓ Step 5 complete: Created {} orders", orderRepository.count());
    }

    private BigDecimal createOrderItems(Order order, List<Product> products) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        int itemTypeCount = ThreadLocalRandom.current().nextInt(1, 6); // Mua mỹ phẩm thường mua nhiều món hơn
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
            
            int quantity = ThreadLocalRandom.current().nextInt(1, 3);
            orderItem.setItemQuantity(quantity);
            orderItem.setItemPrice(product.getProductPrice());
            orderItem.setUnitPrice(product.getProductPrice().doubleValue());
            
            orderItemRepository.save(orderItem);
            
            totalAmount = totalAmount.add(
                product.getProductPrice().multiply(BigDecimal.valueOf(quantity))
            );
        }
        
        return totalAmount;
    }

    // Helper: Create User (Giữ nguyên)
    private User createUser(String name, Gender gender, Role role, String account, String plainPassword) {
        User user = new User();
        String prefix = getUserCodePrefix(role.getRoleCode());
        String userCode = generateUniqueCode(prefix, code -> 
            userRepository.findByUserCode(code).isPresent()
        );
        
        user.setUserCode(userCode);
        user.setUserName(name);
        user.setUserGender(gender);
        user.setUserBirthDate(generateRandomBirthDate());
        user.setUserAddress(generateVietnameseAddress());
        user.setUserPhone(generateVietnamesePhone());
        user.setUserAccount(account);
        user.setUserPassword(passwordEncoder.encode(plainPassword));
        user.setRole(role);
        
        return user;
    }

    private String getUserCodePrefix(String roleCode) {
        switch (roleCode) {
            case "ADM": return "AD";
            case "STF": return "NV";
            case "CUS": return "KH";
            default: throw new RuntimeException("Unknown role code: " + roleCode);
        }
    }

    private String generateUniqueCode(String prefix, java.util.function.Predicate<String> existsChecker) {
        String code;
        int attempts = 0;
        do {
            code = codeGeneratorUtils.generateCode(prefix);
            attempts++;
            if (attempts > 100) throw new RuntimeException("Failed to generate unique code");
        } while (existsChecker.test(code));
        return code;
    }

    /**
     * Helper: Generate realistic price based on Cosmetic Brand Tier
     */
    private BigDecimal generateRealisticPrice(String brandName) {
        BigDecimal basePrice;
        
        switch (brandName) {
            case "Estée Lauder":
            case "Shiseido":
            case "Kiehl's":
                // High-end: 1M - 4M VND
                basePrice = BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(1000000, 4000001));
                break;
            case "MAC":
            case "Laneige":
            case "La Roche-Posay":
            case "Vichy":
                // Mid-range: 400k - 1.2M VND
                basePrice = BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(400000, 1200001));
                break;
            default: // Maybelline, L'Oréal, Innisfree
                // Mass market: 150k - 500k VND
                basePrice = BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(150000, 500001));
        }
        
        // Làm tròn đến hàng nghìn (1000)
        return basePrice.divide(BigDecimal.valueOf(1000), 0, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(1000));
    }

    private LocalDate generateRandomBirthDate() {
        int year = ThreadLocalRandom.current().nextInt(1985, 2004); // Khách hàng trẻ hơn chút (1985-2003)
        int month = ThreadLocalRandom.current().nextInt(1, 13);
        int day = ThreadLocalRandom.current().nextInt(1, 29);
        return LocalDate.of(year, month, day);
    }

    private String generateVietnameseAddress() {
        String[] streets = {
            "Nguyễn Trãi", "Cầu Giấy", "Xuân Thủy", "Kim Mã", "Phố Huế", // Thêm phố Hà Nội cho đa dạng
            "Lê Lợi", "Nguyễn Huệ", "Đồng Khởi", "Hai Bà Trưng", "Cách Mạng Tháng 8"
        };
        String[] districts = {"Quận 1", "Quận 3", "Quận Cầu Giấy", "Quận Hoàn Kiếm", "Quận Ba Đình", "Quận 7"};
        
        int number = ThreadLocalRandom.current().nextInt(1, 200);
        String street = streets[random.nextInt(streets.length)];
        String district = districts[random.nextInt(districts.length)];
        
        return String.format("%d %s, %s, Việt Nam", number, street, district);
    }

    private String generateVietnamesePhone() {
        String[] prefixes = {"090", "091", "093", "094", "096", "097", "098", "032", "033", "070", "079"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String suffix = String.format("%07d", ThreadLocalRandom.current().nextInt(0, 10000000));
        return prefix + suffix;
    }

    private LocalDate generateRandomDate() {
        return LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(0, 60));
    }

    private OrderStatus getRandomOrderStatus() {
        int roll = random.nextInt(100);
        if (roll < 20) return OrderStatus.NEW;
        if (roll < 40) return OrderStatus.PROCESSING;
        if (roll < 90) return OrderStatus.DELIVERED;
        return OrderStatus.CANCELLED;
    }

    private PaymentMethod getRandomPaymentMethod() {
        int roll = random.nextInt(100);
        if (roll < 30) return PaymentMethod.CASH; // Mua mỹ phẩm hay COD
        if (roll < 70) return PaymentMethod.BANK_TRANSFER;
        return PaymentMethod.CARD;
    }
}