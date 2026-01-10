import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';

const PaymentTransferPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { order } = location.state || {};

    if (!order) {
        return (
            <div className="container mx-auto px-4 py-8 text-center">
                <h2 className="text-2xl font-bold mb-4">Không tìm thấy thông tin đơn hàng</h2>
                <Link to="/" className="text-blue-600 hover:underline">Quay về trang chủ</Link>
            </div>
        );
    }

    const bankInfo = {
        bankName: "MB Bank (Ngân hàng Quân Đội)",
        accountNumber: "123456789999",
        accountName: "OCEAN BUTTERFLY SHOP",
        branch: "Chi nhánh TP.HCM"
    };

    const transferContent = `THANHTOAN ${order.orderCode}`;

    return (
        <div className="container mx-auto px-4 py-8">
            <div className="max-w-2xl mx-auto bg-white shadow-lg rounded-lg overflow-hidden">
                <div className="bg-blue-600 text-white py-4 px-6">
                    <h2 className="text-2xl font-bold text-center">Thông tin chuyển khoản</h2>
                </div>
                
                <div className="p-6">
                    <div className="mb-6 text-center">
                        <p className="text-gray-600 mb-2">Cảm ơn bạn đã đặt hàng! Vui lòng chuyển khoản theo thông tin dưới đây để hoàn tất đơn hàng.</p>
                        <p className="text-red-500 font-bold text-xl">Tổng tiền: {order.orderAmount?.toLocaleString('vi-VN')} đ</p>
                    </div>

                    <div className="border-t border-b border-gray-200 py-4 mb-6">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <p className="text-gray-500 text-sm">Ngân hàng</p>
                                <p className="font-semibold text-lg">{bankInfo.bankName}</p>
                            </div>
                            <div>
                                <p className="text-gray-500 text-sm">Số tài khoản</p>
                                <p className="font-semibold text-lg text-blue-600">{bankInfo.accountNumber}</p>
                            </div>
                            <div>
                                <p className="text-gray-500 text-sm">Chủ tài khoản</p>
                                <p className="font-semibold text-lg uppercase">{bankInfo.accountName}</p>
                            </div>
                            <div>
                                <p className="text-gray-500 text-sm">Nội dung chuyển khoản</p>
                                <p className="font-semibold text-lg text-red-600">{transferContent}</p>
                            </div>
                        </div>
                    </div>

                    <div className="text-center mb-6">
                        <p className="text-sm text-gray-500 mb-2">Quét mã QR để chuyển khoản nhanh</p>
                        {/* Placeholder for QR Code - In a real app, generate this dynamically */}
                        <div className="inline-block p-2 border border-gray-200 rounded">
                            <img 
                                src={`https://img.vietqr.io/image/MB-123456789999-compact2.jpg?amount=${order.orderAmount}&addInfo=${transferContent}&accountName=${encodeURIComponent(bankInfo.accountName)}`}
                                alt="QR Code Chuyển khoản" 
                                className="w-64 h-64 object-contain"
                            />
                        </div>
                    </div>

                    <div className="bg-yellow-50 border border-yellow-200 rounded p-4 mb-6">
                        <h4 className="font-bold text-yellow-800 mb-2">Lưu ý:</h4>
                        <ul className="list-disc list-inside text-sm text-yellow-700 space-y-1">
                            <li>Vui lòng ghi đúng nội dung chuyển khoản để hệ thống tự động xác nhận.</li>
                            <li>Đơn hàng sẽ được xử lý sau khi chúng tôi nhận được thanh toán.</li>
                            <li>Nếu cần hỗ trợ, vui lòng liên hệ hotline: 1900 xxxx.</li>
                        </ul>
                    </div>

                    <div className="text-center space-x-4">
                        <button 
                            onClick={() => navigate('/my-orders')}
                            className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 transition duration-200"
                        >
                            Đã chuyển khoản
                        </button>
                        <button 
                            onClick={() => navigate('/')}
                            className="bg-gray-200 text-gray-800 px-6 py-2 rounded hover:bg-gray-300 transition duration-200"
                        >
                            Về trang chủ
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PaymentTransferPage;
