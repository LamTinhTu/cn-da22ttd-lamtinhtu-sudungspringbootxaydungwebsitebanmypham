import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../../components/Auth/AuthModal.css';

const ForgotPasswordPage = () => {
    const [email, setEmail] = useState('');
    const [isSubmitted, setIsSubmitted] = useState(false);

    const handleSubmit = (e) => {
        e.preventDefault();
        // Xử lý gửi email reset password ở đây
        console.log('Reset password for:', email);
        setIsSubmitted(true);
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 font-sans">
            <div className="modal-content" style={{ maxWidth: '400px', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)' }}>
                <div className="modal-header">
                    <h2>Quên mật khẩu?</h2>
                    <p>
                        {!isSubmitted 
                            ? "Nhập email để đặt lại mật khẩu"
                            : "Vui lòng kiểm tra email của bạn"}
                    </p>
                </div>
                
                {!isSubmitted ? (
                    <form className="auth-form" onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="email-address">Email</label>
                            <input
                                id="email-address"
                                name="email"
                                type="email"
                                autoComplete="email"
                                required
                                placeholder="Nhập địa chỉ email của bạn"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </div>

                        <button
                            type="submit"
                            className="btn-submit"
                        >
                            Gửi hướng dẫn
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
                                        Đã gửi email thành công
                                    </h3>
                                    <div className="mt-2 text-sm text-green-700">
                                        <p>
                                            Nếu email tồn tại trong hệ thống, bạn sẽ nhận được hướng dẫn trong vài phút.
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
                        <Link to="/" className="switch-link">
                            Trang chủ
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default ForgotPasswordPage;
