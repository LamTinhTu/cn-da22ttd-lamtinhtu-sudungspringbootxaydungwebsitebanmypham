import React, { useState } from 'react';
import { forgotPasswordAPI } from '../../api/authentication';
import './AuthModal.css';

const ForgotPasswordModal = ({ isOpen, onClose, onSwitchToLogin }) => {
    const [phoneNumber, setPhoneNumber] = useState('');
    const [isSubmitted, setIsSubmitted] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');
        
        try {
            await forgotPasswordAPI({ phoneNumber });
            setIsSubmitted(true);
        } catch (err) {
            console.error("Forgot password error:", err);
            setError('Có lỗi xảy ra. Vui lòng thử lại sau.');
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
                    <h2>Quên mật khẩu?</h2>
                    <p>
                        {!isSubmitted 
                            ? "Nhập số điện thoại để đặt lại mật khẩu"
                            : "Vui lòng kiểm tra tin nhắn của bạn"}
                    </p>
                </div>
                
                {error && <div className="error-message" style={{color: 'red', marginBottom: '10px', textAlign: 'center'}}>{error}</div>}
                
                {!isSubmitted ? (
                    <form className="auth-form" onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="phone-number">Số điện thoại</label>
                            <input
                                id="phone-number"
                                name="phoneNumber"
                                type="tel"
                                required
                                placeholder="Nhập số điện thoại của bạn"
                                value={phoneNumber}
                                onChange={(e) => setPhoneNumber(e.target.value)}
                                pattern="[0-9]{10}"
                                title="Vui lòng nhập số điện thoại 10 chữ số"
                            />
                        </div>

                        <button
                            type="submit"
                            className="btn-submit"
                            disabled={isLoading}
                        >
                            {isLoading ? 'Đang xử lý...' : 'Gửi'}
                        </button>
                    </form>
                ) : (
                    <div className="auth-form">
                        <div className="rounded-md bg-green-50 p-4 border border-green-100">
                            <div className="flex">
                                <div className="flex-shrink-0">
                                    <svg className="h-5 w-5 text-green-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                                    </svg>
                                </div>
                                <div className="ml-3">
                                    <h3 className="text-sm font-medium text-green-800">
                                        Đã gửi yêu cầu thành công
                                    </h3>
                                    <div className="mt-2 text-sm text-green-700">
                                        <p>
                                            Nếu số điện thoại chính xác, bạn sẽ nhận được mã xác nhận trong giây lát.
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                <div className="modal-switch">
                    <p>
                        Quay lại? 
                        <button onClick={onSwitchToLogin} className="switch-link">
                            Đăng nhập
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default ForgotPasswordModal;
