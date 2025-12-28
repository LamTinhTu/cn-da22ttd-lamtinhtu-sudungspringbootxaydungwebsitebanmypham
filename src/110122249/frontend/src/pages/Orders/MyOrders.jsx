import React, { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getUser, isTokenValid } from '../../utils/jwt-helper';
import { toast } from 'react-toastify';
import axios from 'axios';
import { API_BASE_URL, getHeaders } from '../../api/constant';
import { deleteOrder } from '../../api/order';
import ConfirmModal from '../../components/common/ConfirmModal';

const MyOrders = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showConfirm, setShowConfirm] = useState(false);
    const [confirmAction, setConfirmAction] = useState(null);
    const [confirmMessage, setConfirmMessage] = useState('');
    const navigate = useNavigate();
    const user = getUser();
    const isAdmin = user?.roleName === 'Administrator';

    const fetchOrders = useCallback(async () => {
        if (!user?.userId) return;
        try {
            setLoading(true);
            const response = await axios.get(`${API_BASE_URL}/api/v1/orders/user/${user.userId}`, {
                headers: getHeaders()
            });
            
            if (response.data && response.data.data) {
                setOrders(response.data.data);
            }
        } catch (error) {
            console.error("Error fetching orders:", error);
            // toast.error("Không thể tải danh sách đơn hàng");
        } finally {
            setLoading(false);
        }
    }, [user?.userId]);

    useEffect(() => {
        if (!isTokenValid()) {
            toast.error("Vui lòng đăng nhập để xem đơn hàng!");
            navigate('/');
            return;
        }

        fetchOrders();
    }, [navigate, fetchOrders]);

    const getStatusColor = (status) => {
        switch (status) {
            case 'NEW':
                return 'bg-blue-100 text-blue-700';
            case 'PROCESSING':
                return 'bg-yellow-100 text-yellow-700';
            case 'DELIVERED':
                return 'bg-green-100 text-green-700';
            case 'CANCELLED':
                return 'bg-red-100 text-red-700';
            default:
                return 'bg-gray-100 text-gray-700';
        }
    };

    const getStatusText = (status) => {
        switch (status) {
            case 'NEW':
                return 'Đã đặt';
            case 'PROCESSING':
                return 'Đang xử lý';
            case 'DELIVERED':
                return 'Đã giao';
            case 'CANCELLED':
                return 'Đã hủy';
            default:
                return status;
        }
    };

    const handleDeleteOrder = (orderId, orderCode) => {
        setConfirmMessage(`Bạn có chắc chắn muốn xóa đơn hàng ${orderCode}?`);
        setConfirmAction(() => async () => {
            try {
                await deleteOrder(orderId);
                toast.success('Xóa đơn hàng thành công!');
                // Reload orders
                fetchOrders();
            } catch (error) {
                console.error("Error deleting order:", error);
                toast.error(error.response?.data?.message || "Không thể xóa đơn hàng");
            }
        });
        setShowConfirm(true);
    };

    const canDeleteOrder = (status) => {
        return status === 'CANCELLED' || status === 'PROCESSING';
    };

    if (loading) {
        return (
            <div className="container mx-auto px-4 py-16 text-center">
                <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900"></div>
                <p className="mt-4 text-gray-600">Đang tải...</p>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-8 font-sans">
            <h1 className="text-3xl font-bold mb-8">Đơn hàng của tôi</h1>

            {orders.length === 0 ? (
                <div className="text-center py-16">
                    <h2 className="text-2xl font-bold mb-4">Bạn chưa có đơn hàng nào</h2>
                    <Link to="/" className="inline-block bg-black text-white px-8 py-3 rounded-full hover:bg-gray-800 transition-colors">
                        Tiếp tục mua sắm
                    </Link>
                </div>
            ) : (
                <div className="space-y-4">
                    {orders.map((order) => (
                        <div key={order.orderId} className="bg-white rounded-lg shadow-sm border border-gray-100 p-6">
                            <div className="flex justify-between items-start mb-4">
                                <div>
                                    <h3 className="font-bold text-lg">Đơn hàng #{order.orderCode}</h3>
                                    <p className="text-sm text-gray-500">
                                        Ngày đặt: {new Date(order.orderDate).toLocaleDateString('vi-VN')}
                                    </p>
                                </div>
                                <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(order.orderStatus)}`}>
                                    {getStatusText(order.orderStatus)}
                                </span>
                            </div>

                            <div className="border-t border-gray-100 pt-4 mb-4">
                                <div className="grid grid-cols-2 gap-4 text-sm">
                                    <div>
                                        <p className="text-gray-500">Địa chỉ giao hàng:</p>
                                        <p className="font-medium">{order.shippingAddress}</p>
                                    </div>
                                    <div>
                                        <p className="text-gray-500">Số điện thoại:</p>
                                        <p className="font-medium">{order.shippingPhone}</p>
                                    </div>
                                    <div>
                                        <p className="text-gray-500">Phương thức thanh toán:</p>
                                        <p className="font-medium">
                                            {order.paymentMethod === 'CASH' ? 'Tiền mặt' :
                                             order.paymentMethod === 'BANK_TRANSFER' ? 'Chuyển khoản' :
                                             order.paymentMethod === 'CARD' ? 'Thẻ' : order.paymentMethod}
                                        </p>
                                    </div>
                                    <div>
                                        <p className="text-gray-500">Tổng tiền:</p>
                                        <p className="font-bold text-lg text-black">
                                            {parseFloat(order.orderAmount).toLocaleString('vi-VN')} ₫
                                        </p>
                                    </div>
                                </div>
                            </div>

                            {order.orderItems && order.orderItems.length > 0 && (
                                <div className="border-t border-gray-100 pt-4">
                                    <p className="text-sm font-medium text-gray-700 mb-2">Sản phẩm:</p>
                                    <div className="space-y-2">
                                        {order.orderItems.map((item, index) => (
                                            <div key={index} className="flex justify-between text-sm">
                                                <span className="text-gray-600">
                                                    {item.productName || `Sản phẩm #${item.productId}`} x {item.itemQuantity}
                                                </span>
                                                <span className="font-medium">
                                                    {parseFloat(item.itemPrice).toLocaleString('vi-VN')} ₫
                                                </span>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            {isAdmin && canDeleteOrder(order.orderStatus) && (
                                <div className="border-t border-gray-100 pt-4 mt-4">
                                    <button
                                        onClick={() => handleDeleteOrder(order.orderId, order.orderCode)}
                                        className="w-full sm:w-auto px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                                    >
                                        Xóa đơn hàng
                                    </button>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            )}
            <ConfirmModal
                isOpen={showConfirm}
                onClose={() => setShowConfirm(false)}
                onConfirm={confirmAction}
                title="Xác nhận xóa"
                message={confirmMessage}
            />
        </div>
    );
};

export default MyOrders;
