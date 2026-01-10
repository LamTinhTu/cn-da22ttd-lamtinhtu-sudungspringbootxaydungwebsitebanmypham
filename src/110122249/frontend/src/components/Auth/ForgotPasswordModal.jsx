import React, { useState } from 'react';
import { resetPasswordAPI, checkPhoneExistsAPI } from '../../api/authentication';
import { toast } from 'react-toastify';
import './AuthModal.css';

const ForgotPasswordModal = ({ isOpen, onClose, onSwitchToLogin }) => {
    const [step, setStep] = useState(1); // 1: nhập SĐT, 2: nhập mật khẩu mới
    const [formData, setFormData] = useState({
        phoneNumber: '',
        newPassword: '',
        confirmPassword: ''
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

    const validatePhoneNumber = (phone) => {
        // Kiểm tra định dạng số điện thoại Việt Nam
        const vnPhoneRegex = /^0[0-9]{9,10}$/;
        return vnPhoneRegex.test(phone);
    };

    const handleCheckPhone = async (e) => {
        e.preventDefault();
        
        if (!validatePhoneNumber(formData.phoneNumber)) {
            setError('Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (bắt đầu bằng 0, 10-11 số)');
            return;
        }

        setIsLoading(true);
        setError('');

        try {
            // Kiểm tra số điện thoại có tồn tại không bằng cách gọi API
            const response = await checkPhoneExistsAPI({ phoneNumber: formData.phoneNumber });
            console.log('Check phone response:', response);
            
            if (response && response.data && response.data.data) {
                if (response.data.data.exists) {
                    // Số điện thoại tồn tại, chuyển sang bước 2
                    setStep(2);
                    toast.success('Số điện thoại hợp lệ. Vui lòng nhập mật khẩu mới.');
                } else {
                    // Số điện thoại không tồn tại
                    setError('Số điện thoại không tồn tại trong hệ thống.');
                }
            } else {
                setError('Không thể kiểm tra số điện thoại. Vui lòng thử lại.');
            }
        } catch (err) {
            console.error('Check phone failed:', err);
            if (err.response?.data?.message) {
                setError(err.response.data.message);
            } else {
                setError('Số điện thoại không tồn tại trong hệ thống.');
            }
        } finally {
            setIsLoading(false);
        }
    };

    const handleResetPassword = async (e) => {
        e.preventDefault();
        
        if (formData.newPassword !== formData.confirmPassword) {
            setError('Mật khẩu xác nhận không khớp!');
            return;
        }

        if (formData.newPassword.length < 6) {
            setError('Mật khẩu phải có ít nhất 6 ký tự!');
            return;
        }

        setIsLoading(true);
        setError('');

        try {
            const resetData = {
                phoneNumber: formData.phoneNumber,
                newPassword: formData.newPassword
            };

            console.log('Sending reset password data:', resetData);
            const response = await resetPasswordAPI(resetData);
            console.log('Reset password response:', response);
            
            if (response && (response.status === 200 || response.data?.status === 200)) {
                toast.success('Đặt lại mật khẩu thành công! Vui lòng đăng nhập.');
                
                // Reset form
                setFormData({
                    phoneNumber: '',
                    newPassword: '',
                    confirmPassword: ''
                });
                setStep(1);
                
                // Chuyển về màn hình đăng nhập
                onClose();
                if (onSwitchToLogin) {
                    onSwitchToLogin();
                }
            }
        } catch (err) {
            console.error('Reset password failed:', err);
            
            if (err.response?.data?.message) {
                setError(err.response.data.message);
            } else if (err.response?.status === 400) {
                setError('Số điện thoại không tồn tại trong hệ thống.');
            } else {
                setError('Đặt lại mật khẩu thất bại. Vui lòng thử lại sau.');
            }
        } finally {
            setIsLoading(false);
        }
    };

    const handleBack = () => {
        setStep(1);
        setError('');
    };

    const handleCloseModal = () => {
        setFormData({
            phoneNumber: '',
            newPassword: '',
            confirmPassword: ''
        });
        setStep(1);
        setError('');
        onClose();
    };

    if (!isOpen) return null;

    return (
        <div className="modal-overlay" onClick={handleCloseModal}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="modal-close" onClick={handleCloseModal}>
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>

                <div className="modal-header">
                    <h2>Quên Mật Khẩu</h2>
                    <p>{step === 1 ? 'Nhập số điện thoại của bạn' : 'Nhập mật khẩu mới'}</p>
                </div>
                
                {error && <div className="error-message" style={{color: 'red', marginBottom: '10px', textAlign: 'center'}}>{error}</div>}
                
                {step === 1 ? (
                    <form onSubmit={handleCheckPhone} className="auth-form">
                        <div className="form-group">
                            <label htmlFor="phoneNumber">Số điện thoại</label>
                            <input
                                type="tel"
                                id="phoneNumber"
                                name="phoneNumber"
                                value={formData.phoneNumber}
                                onChange={handleChange}
                                placeholder="Nhập số điện thoại (VD: 0123456789)"
                                required
                            />
                        </div>

                        {error ? (
                            <div className="btn-group" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginTop: '10px' }}>
                                <button 
                                    type="button"
                                    onClick={onSwitchToLogin}
                                    className="btn-submit"
                                    style={{ 
                                        background: '#fff',
                                        color: '#000',
                                        border: '1px solid #000',
                                        marginTop: 0
                                    }}
                                >
                                    Quay lại
                                </button>
                                <button 
                                    type="submit" 
                                    className="btn-submit"
                                    style={{ marginTop: 0 }}
                                    disabled={isLoading}
                                >
                                    {isLoading ? '...' : 'Gửi'}
                                </button>
                            </div>
                        ) : (
                            <button 
                                type="submit" 
                                className="btn-submit"
                                disabled={isLoading}
                            >
                                {isLoading ? 'Đang kiểm tra...' : 'Gửi'}
                            </button>
                        )}

                        <div className="modal-switch" style={{ marginTop: '20px', borderTop: '1px solid #eee', paddingTop: '20px' }}>
                            <p>
                                Đã nhớ mật khẩu?{' '}
                                <button type="button" onClick={onSwitchToLogin} className="switch-link">
                                    Đăng nhập
                                </button>
                            </p>
                        </div>
                    </form>
                ) : (
                    <form onSubmit={handleResetPassword} className="auth-form">
                        <div className="form-group">
                            <label htmlFor="newPassword">Mật khẩu mới</label>
                            <div style={{ position: 'relative' }}>
                                <input
                                    type={showPassword ? "text" : "password"}
                                    id="newPassword"
                                    name="newPassword"
                                    value={formData.newPassword}
                                    onChange={handleChange}
                                    placeholder="Nhập mật khẩu mới (tối thiểu 6 ký tự)"
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

                        <div className="form-group">
                            <label htmlFor="confirmPassword">Xác nhận mật khẩu</label>
                            <div style={{ position: 'relative' }}>
                                <input
                                    type={showConfirmPassword ? "text" : "password"}
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    value={formData.confirmPassword}
                                    onChange={handleChange}
                                    placeholder="Nhập lại mật khẩu mới"
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

                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginTop: '10px' }}>
                            <button 
                                type="button"
                                onClick={handleBack}
                                className="btn-submit"
                                style={{ 
                                    background: '#fff',
                                    color: '#000',
                                    border: '1px solid #000',
                                    marginTop: 0
                                }}
                                disabled={isLoading}
                            >
                                Quay lại
                            </button>
                            <button 
                                type="submit" 
                                className="btn-submit"
                                style={{ marginTop: 0 }}
                                disabled={isLoading}
                            >
                                {isLoading ? '...' : 'Gửi'}
                            </button>
                        </div>
                    </form>
                )}
            </div>
        </div>
    );
};

export default ForgotPasswordModal;
