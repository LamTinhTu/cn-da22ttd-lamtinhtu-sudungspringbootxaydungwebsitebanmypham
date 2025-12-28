import React, { useState } from 'react';
import { registerAPI } from '../../api/authentication';
import { toast } from 'react-toastify';
import './AuthModal.css';

const RegisterModal = ({ isOpen, onClose, onSwitchToLogin }) => {
    const [formData, setFormData] = useState({
        userName: '',
        userAccount: '',
        userPassword: '',
        confirmPassword: '',
        userPhone: '',
        userGender: 'MALE',
        userAddress: ''
    });
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (formData.userPassword !== formData.confirmPassword) {
            setError('Mật khẩu xác nhận không khớp!');
            return;
        }

        if (formData.userPassword.length < 6) {
            setError('Mật khẩu phải có ít nhất 6 ký tự!');
            return;
        }

        setIsLoading(true);
        setError('');

        try {
            const registerData = {
                userName: formData.userName,
                userAccount: formData.userAccount,
                userPassword: formData.userPassword,
                userPhone: formData.userPhone,
                userGender: formData.userGender,
                userAddress: formData.userAddress
            };

            console.log('Sending register data:', registerData);
            const response = await registerAPI(registerData);
            console.log('Register response:', response);
            
            if (response && response.status === 200) {
                toast.success('Đăng ký thành công! Vui lòng đăng nhập.');
                setFormData({
                    userName: '',
                    userAccount: '',
                    userPassword: '',
                    confirmPassword: '',
                    userPhone: '',
                    userGender: 'MALE',
                    userAddress: ''
                });
                onSwitchToLogin();
            }
        } catch (err) {
            console.error('Register failed:', err);
            console.error('Error response:', err.response);
            
            // Extract error message from backend
            let errorMessage = 'Đăng ký thất bại. Vui lòng thử lại.';
            
            if (err.response?.data?.message) {
                const backendMessage = err.response.data.message;
                
                // Translate common backend messages to Vietnamese
                if (backendMessage.includes('Username already exists')) {
                    errorMessage = 'Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.';
                } else if (backendMessage.includes('Phone number already exists')) {
                    errorMessage = 'Số điện thoại đã được sử dụng.';
                } else if (backendMessage.includes('RBHT')) {
                    errorMessage = 'Họ tên chỉ được chứa chữ cái và khoảng trắng.';
                } else if (backendMessage.includes('RBSDT')) {
                    errorMessage = 'Số điện thoại phải có đúng 10 hoặc 11 chữ số.';
                } else {
                    errorMessage = backendMessage;
                }
            } else if (err.response?.data?.data) {
                // Handle validation errors
                const validationErrors = err.response.data.data;
                const firstError = Object.values(validationErrors)[0];
                if (firstError) {
                    errorMessage = firstError;
                }
            } else if (err.message) {
                errorMessage = err.message;
            }
            
            setError(errorMessage);
        } finally {
            setIsLoading(false);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content register-modal" onClick={(e) => e.stopPropagation()}>
                <button className="modal-close" onClick={onClose}>
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
                
                <div className="modal-header">
                    <h2>Đăng Ký</h2>
                    <p>Tạo tài khoản mới</p>
                </div>

                {error && <div className="error-message" style={{color: 'red', marginBottom: '10px', textAlign: 'center'}}>{error}</div>}

                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="form-group">
                        <label htmlFor="userName">Họ và tên</label>
                        <input
                            type="text"
                            id="userName"
                            name="userName"
                            value={formData.userName}
                            onChange={handleChange}
                            placeholder="Nhập họ và tên"
                            required
                            pattern="^[a-zA-ZÀ-ỹ\s]+$"
                            title="Họ tên chỉ được chứa chữ cái và khoảng trắng"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="userAccount">Tên đăng nhập</label>
                        <input
                            type="text"
                            id="userAccount"
                            name="userAccount"
                            value={formData.userAccount}
                            onChange={handleChange}
                            placeholder="Nhập tên đăng nhập (3-50 ký tự)"
                            required
                            minLength="3"
                            maxLength="50"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="userPhone">Số điện thoại</label>
                        <input
                            type="tel"
                            id="userPhone"
                            name="userPhone"
                            value={formData.userPhone}
                            onChange={handleChange}
                            placeholder="Nhập số điện thoại (10-11 số)"
                            required
                            pattern="[0-9]{10,11}"
                            title="Số điện thoại phải có 10 hoặc 11 chữ số"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="userGender">Giới tính</label>
                        <select
                            id="userGender"
                            name="userGender"
                            value={formData.userGender}
                            onChange={handleChange}
                            className="form-control"
                            style={{
                                width: '100%',
                                padding: '10px',
                                border: '1px solid #ddd',
                                borderRadius: '4px',
                                fontSize: '14px'
                            }}
                        >
                            <option value="MALE">Nam</option>
                            <option value="FEMALE">Nữ</option>
                            <option value="OTHER">Khác</option>
                        </select>
                    </div>

                    <div className="form-group full-width">
                        <label htmlFor="userAddress">Địa chỉ</label>
                        <input
                            type="text"
                            id="userAddress"
                            name="userAddress"
                            value={formData.userAddress}
                            onChange={handleChange}
                            placeholder="Nhập địa chỉ của bạn"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="userPassword">Mật khẩu</label>
                        <div style={{ position: 'relative' }}>
                            <input
                                type={showPassword ? "text" : "password"}
                                id="userPassword"
                                name="userPassword"
                                value={formData.userPassword}
                                onChange={handleChange}
                                placeholder="Nhập mật khẩu (tối thiểu 6 ký tự)"
                                required
                                minLength="6"
                                style={{ width: '100%', paddingRight: '40px' }}
                            />
                            <button
                                type="button"
                                onClick={() => setShowPassword(!showPassword)}
                                style={{
                                    position: 'absolute',
                                    right: '10px',
                                    top: '50%',
                                    transform: 'translateY(-50%)',
                                    background: 'none',
                                    border: 'none',
                                    cursor: 'pointer',
                                    color: '#666',
                                    padding: 0,
                                    display: 'flex',
                                    alignItems: 'center'
                                }}
                            >
                                {showPassword ? (
                                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                                        <line x1="1" y1="1" x2="23" y2="23"></line>
                                    </svg>
                                ) : (
                                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                        <circle cx="12" cy="12" r="3"></circle>
                                    </svg>
                                )}
                            </button>
                        </div>
                    </div>

                    <div className="form-group">
                        <label htmlFor="confirmPassword">Xác nhận mật khẩu</label>
                        <div style={{ position: 'relative' }}>
                            <input
                                type={showConfirmPassword ? "text" : "password"}
                                id="confirmPassword"
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                placeholder="Nhập lại mật khẩu"
                                required
                                style={{ width: '100%', paddingRight: '40px' }}
                            />
                            <button
                                type="button"
                                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                style={{
                                    position: 'absolute',
                                    right: '10px',
                                    top: '50%',
                                    transform: 'translateY(-50%)',
                                    background: 'none',
                                    border: 'none',
                                    cursor: 'pointer',
                                    color: '#666',
                                    padding: 0,
                                    display: 'flex',
                                    alignItems: 'center'
                                }}
                            >
                                {showConfirmPassword ? (
                                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                                        <line x1="1" y1="1" x2="23" y2="23"></line>
                                    </svg>
                                ) : (
                                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                        <circle cx="12" cy="12" r="3"></circle>
                                    </svg>
                                )}
                            </button>
                        </div>
                    </div>

                    <div className="form-footer">
                        <label className="remember-me">
                            <input type="checkbox" required />
                            <span>Tôi đồng ý với điều khoản và điều kiện</span>
                        </label>
                    </div>

                    <button type="submit" className="btn-submit" disabled={isLoading}>
                        {isLoading ? 'Đang xử lý...' : 'Đăng Ký'}
                    </button>
                </form>

                <div className="modal-switch">
                    <p>Đã có tài khoản? 
                        <button onClick={onSwitchToLogin} className="switch-link">
                            Đăng nhập ngay
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default RegisterModal;
