import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { loginAPI } from '../../api/authentication';
import { saveToken, saveUser } from '../../utils/jwt-helper';
import './AuthModal.css';

const LoginModal = ({ isOpen, onClose, onSwitchToRegister, onSwitchToForgotPassword }) => {
    const [formData, setFormData] = useState({
        userAccount: '',
        userPassword: ''
    });
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value.trim() // Trim whitespace
        });
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        console.log('LoginModal - Form data:', formData);

        try {
            const response = await loginAPI(formData);
            console.log("LoginModal - Login response:", response);
            
            if (response && response.data) {
                const { accessToken, ...userInfo } = response.data;
                saveToken(accessToken);
                saveUser(userInfo);
                
                onClose();
                
                // Redirect based on role
                if (userInfo.roleName === 'Administrator') {
                    navigate('/admin');
                } else if (userInfo.roleName === 'Staff') {
                    navigate('/employee');
                } else {
                    // Customer - maybe reload to update UI
                    window.location.reload();
                }
            }
        } catch (err) {
            console.error("LoginModal - Login failed", err);
            console.error("LoginModal - Error response:", err.response?.data);
            
            // Show more specific error message
            if (err.response?.data?.message) {
                if (err.response.data.message.includes('Invalid credentials')) {
                    setError('Tên đăng nhập hoặc mật khẩu không đúng. Vui lòng thử lại.');
                } else {
                    setError(err.response.data.message);
                }
            } else if (err.response?.status === 400) {
                setError('Tên đăng nhập hoặc mật khẩu không đúng. Vui lòng kiểm tra lại.');
            } else if (err.response?.status === 401) {
                setError('Tên đăng nhập hoặc mật khẩu không đúng.');
            } else {
                setError('Đăng nhập thất bại. Vui lòng thử lại sau.');
            }
        } finally {
            setIsLoading(false);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="modal-close" onClick={onClose}>
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
                
                <div className="modal-header">
                    <h2>Đăng Nhập</h2>
                    <p>Chào mừng trở lại!</p>
                </div>

                {error && <div className="error-message" style={{color: 'red', marginBottom: '10px', textAlign: 'center'}}>{error}</div>}

                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="form-group">
                        <label htmlFor="userAccount">Tên đăng nhập</label>
                        <input
                            type="text"
                            id="userAccount"
                            name="userAccount"
                            value={formData.userAccount}
                            onChange={handleChange}
                            placeholder="Nhập tên đăng nhập"
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
                                placeholder="Nhập mật khẩu"
                                required
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

                    <div className="form-footer">
                        <label className="remember-me">
                            <input type="checkbox" />
                            <span>Ghi nhớ đăng nhập</span>
                        </label>
                        <button type="button" className="forgot-password" onClick={onSwitchToForgotPassword}>Quên mật khẩu?</button>
                    </div>

                    <button type="submit" className="btn-submit" disabled={isLoading}>
                        {isLoading ? 'Đang xử lý...' : 'Đăng Nhập'}
                    </button>
                </form>

                <div className="modal-switch">
                    <p>Chưa có tài khoản? 
                        <button onClick={onSwitchToRegister} className="switch-link">
                            Đăng ký ngay
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default LoginModal;
