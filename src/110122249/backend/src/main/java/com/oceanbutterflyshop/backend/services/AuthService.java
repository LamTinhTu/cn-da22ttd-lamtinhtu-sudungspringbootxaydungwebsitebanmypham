package com.oceanbutterflyshop.backend.services;

import com.oceanbutterflyshop.backend.dtos.request.LoginRequestDTO;
import com.oceanbutterflyshop.backend.dtos.request.RegisterRequestDTO;
import com.oceanbutterflyshop.backend.dtos.response.AuthResponseDTO;

/**
 * Interface dịch vụ cho các thao tác xác thực
 * Theo PROJECT_SPEC.md Mục 4.7
 */
public interface AuthService {
    
    /**
     * Đăng ký người dùng mới
     * Quy trình:
     * 1. Kiểm tra tên đăng nhập đã tồn tại chưa
     * 2. Kiểm tra số điện thoại đã tồn tại chưa
     * 3. Mã hóa mật khẩu bằng BCrypt
     * 4. Gán vai trò mặc định (Customer - CUS)
     * 5. Tạo mã người dùng với tiền tố "KH"
     * 6. Lưu người dùng vào database
     * 
     * @param request Yêu cầu đăng ký chứa thông tin người dùng
     * @return AuthResponseDTO với thông tin người dùng
     * @throws BadRequestException nếu tên đăng nhập hoặc số điện thoại đã tồn tại
     */
    AuthResponseDTO register(RegisterRequestDTO request);
    
    /**
     * Xác thực người dùng và trả về thông tin người dùng
     * Quy trình:
     * 1. Tìm người dùng theo tên đăng nhập
     * 2. Xác minh hash mật khẩu khớp
     * 3. Trả về thông tin người dùng nếu thành công
     * 
     * @param request Yêu cầu đăng nhập chứa tên đăng nhập và mật khẩu
     * @return AuthResponseDTO với thông tin người dùng và token placeholder
     * @throws BadRequestException nếu thông tin đăng nhập không hợp lệ
     */
    AuthResponseDTO login(LoginRequestDTO request);

    /**
     * Xử lý yêu cầu quên mật khẩu
     * @param request Yêu cầu chứa số điện thoại
     */
    void forgotPassword(com.oceanbutterflyshop.backend.dtos.request.ForgotPasswordRequestDTO request);
}
