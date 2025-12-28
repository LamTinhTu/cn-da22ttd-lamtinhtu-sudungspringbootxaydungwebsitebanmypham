import React, { useState, useEffect } from 'react';
import EmployeeHeader from '../../components/Employee/EmployeeHeader';
import { getAllOrders, updateOrderStatus } from '../../api/order';
import { toast } from 'react-toastify';

const Orders = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [showDetailModal, setShowDetailModal] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [showStatusModal, setShowStatusModal] = useState(false);
    const [newStatus, setNewStatus] = useState('');
    const [searchKeyword, setSearchKeyword] = useState('');

    const fetchOrders = async (keyword = '') => {
        try {
            setLoading(true);
            const params = { size: 100 };
            const response = await getAllOrders(params);
            console.log('Orders response:', response);
            if (response && response.data && response.data.items) {
                let filteredOrders = response.data.items;
                // Filter by keyword (order code or customer name) on client side
                if (keyword) {
                    const lowerKeyword = keyword.toLowerCase();
                    filteredOrders = response.data.items.filter(order => 
                        order.orderCode?.toLowerCase().includes(lowerKeyword) ||
                        order.userName?.toLowerCase().includes(lowerKeyword) ||
                        order.userPhone?.includes(keyword) ||
                        order.shippingPhone?.includes(keyword)
                    );
                }
                setOrders(filteredOrders);
            }
        } catch (err) {
            setError('Không thể tải danh sách đơn hàng');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchOrders();
    }, []);

    const handleViewDetail = (order) => {
        setSelectedOrder(order);
        setShowDetailModal(true);
    };

    const handleUpdateStatus = (order) => {
        setSelectedOrder(order);
        setNewStatus(getStatusText(order.orderStatus));
        setShowStatusModal(true);
    };

    // Helper functions to convert between enum names and display names
    const getEnumName = (displayName) => {
        const mapping = {
            'Đã đặt': 'NEW',
            'Đang xử lý': 'PROCESSING',
            'Đang giao': 'SHIPPING',
            'Đã giao': 'DELIVERED',
            'Đã hủy': 'CANCELLED'
        };
        return mapping[displayName] || displayName;
    };

    const handleStatusSubmit = async () => {
        if (!selectedOrder || !newStatus) return;
        
        try {
            // Convert display name to enum name before sending to backend
            const enumStatus = getEnumName(newStatus);
            await updateOrderStatus(selectedOrder.orderId, enumStatus);
            toast.success('Cập nhật trạng thái thành công!');
            setShowStatusModal(false);
            fetchOrders();
        } catch (err) {
            console.error(err);
            const errorMessage = err.response?.data?.message || 'Có lỗi xảy ra khi cập nhật trạng thái';
            toast.error(errorMessage);
        }
    };

    const getStatusColor = (status) => {
        switch(status) {
            case 'NEW': return 'bg-blue-100 text-blue-700';
            case 'PROCESSING': return 'bg-yellow-100 text-yellow-700';
            case 'SHIPPING': return 'bg-purple-100 text-purple-700';
            case 'DELIVERED': return 'bg-green-100 text-green-700';
            case 'CANCELLED': return 'bg-red-100 text-red-700';
            default: return 'bg-gray-100 text-gray-700';
        }
    };

    const getStatusText = (status) => {
        const mapping = {
            'NEW': 'Đã đặt',
            'PROCESSING': 'Đang xử lý',
            'SHIPPING': 'Đang giao',
            'DELIVERED': 'Đã giao',
            'CANCELLED': 'Đã hủy'
        };
        return mapping[status] || status;
    };

    const getPaymentMethodText = (method) => {
        switch(method) {
            case 'CASH': return 'Tiền mặt';
            case 'BANK_TRANSFER': return 'Chuyển khoản';
            case 'CARD': return 'Thẻ';
            default: return method;
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <EmployeeHeader title="Quản lý Đơn hàng" />
            
            <div className="p-8">
                <div className="sticky top-4 bg-gray-50 z-10 pb-6 flex justify-between items-center">
                    <h2 className="text-xl font-bold text-gray-800">Danh sách Đơn hàng</h2>
                    <input
                        type="text"
                        placeholder="Tìm kiếm"
                        className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:border-pink-500 w-80"
                        value={searchKeyword}
                        onChange={(e) => {
                            setSearchKeyword(e.target.value);
                            fetchOrders(e.target.value);
                        }}
                    />
                </div>

                {/* Table */}
                <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
                    {loading ? (
                        <div className="p-6 text-center text-gray-500">Đang tải...</div>
                    ) : error ? (
                        <div className="p-6 text-center text-red-500">{error}</div>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full text-left">
                                <thead className="bg-gray-50 border-b border-gray-100">
                                    <tr>
                                        <th className="px-6 py-4 font-semibold text-gray-600">Mã đơn</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600">Khách hàng</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600">Ngày đặt</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600">Tổng tiền</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600">Trạng thái</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600">Thanh toán</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600 whitespace-nowrap">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {orders.map((order) => (
                                        <tr key={order.orderId} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 font-medium text-blue-600">{order.orderCode}</td>
                                            <td className="px-6 py-4">
                                                <div className="text-gray-800 font-medium">{order.userName || 'N/A'}</div>
                                                <div className="text-sm text-gray-500">{order.userPhone || order.shippingPhone}</div>
                                            </td>
                                            <td className="px-6 py-4 text-gray-600">
                                                {new Date(order.orderDate).toLocaleDateString('vi-VN')}
                                            </td>
                                            <td className="px-6 py-4 font-medium text-gray-800">
                                                {parseFloat(order.orderAmount).toLocaleString('vi-VN')} ₫
                                            </td>
                                            <td className="px-6 py-4">
                                                <span className={`px-2 py-1 rounded-full text-xs ${getStatusColor(order.orderStatus)}`}>
                                                    {getStatusText(order.orderStatus)}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 text-gray-600">
                                                <div>{getPaymentMethodText(order.paymentMethod)}</div>
                                                {order.paymentDate && (
                                                    <div className="text-xs text-gray-400">
                                                        {new Date(order.paymentDate).toLocaleDateString('vi-VN')}
                                                    </div>
                                                )}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="flex items-center gap-2">
                                                    <button 
                                                        onClick={() => handleViewDetail(order)}
                                                        className="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700 transition-colors"
                                                    >
                                                        Chi tiết
                                                    </button>
                                                    <button 
                                                        onClick={() => handleUpdateStatus(order)}
                                                        className="bg-pink-600 text-white px-3 py-1 rounded hover:bg-pink-700 transition-colors"
                                                    >
                                                        Cập nhật
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>

                {/* Detail Modal */}
                {showDetailModal && selectedOrder && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                        <div className="bg-white rounded-xl p-6 w-full max-w-3xl max-h-[90vh] overflow-y-auto">
                            <h3 className="text-lg font-bold mb-4">Chi tiết đơn hàng {selectedOrder.orderCode}</h3>
                            
                            <div className="grid grid-cols-2 gap-4 mb-6">
                                <div>
                                    <p className="text-sm text-gray-600">Khách hàng</p>
                                    <p className="font-medium">{selectedOrder.userName}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-gray-600">Số điện thoại</p>
                                    <p className="font-medium">{selectedOrder.userPhone || selectedOrder.shippingPhone}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-gray-600">Ngày đặt</p>
                                    <p className="font-medium">{new Date(selectedOrder.orderDate).toLocaleDateString('vi-VN')}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-gray-600">Trạng thái</p>
                                    <span className={`inline-block px-2 py-1 rounded-full text-xs ${getStatusColor(selectedOrder.orderStatus)}`}>
                                        {getStatusText(selectedOrder.orderStatus)}
                                    </span>
                                </div>
                                <div className="col-span-2">
                                    <p className="text-sm text-gray-600">Địa chỉ giao hàng</p>
                                    <p className="font-medium">{selectedOrder.shippingAddress}</p>
                                </div>
                                <div>
                                    <p className="text-sm text-gray-600">Phương thức thanh toán</p>
                                    <p className="font-medium">{getPaymentMethodText(selectedOrder.paymentMethod)}</p>
                                </div>
                            </div>

                            {selectedOrder.orderItems && selectedOrder.orderItems.length > 0 && (
                                <div className="mb-4">
                                    <h4 className="font-semibold mb-3">Sản phẩm</h4>
                                    <table className="w-full text-sm">
                                        <thead className="bg-gray-50">
                                            <tr>
                                                <th className="px-3 py-2 text-left">Sản phẩm</th>
                                                <th className="px-3 py-2 text-right">Đơn giá</th>
                                                <th className="px-3 py-2 text-center">SL</th>
                                                <th className="px-3 py-2 text-right">Thành tiền</th>
                                            </tr>
                                        </thead>
                                        <tbody className="divide-y">
                                            {selectedOrder.orderItems.map((item, index) => (
                                                <tr key={index}>
                                                    <td className="px-3 py-2">{item.productName}</td>
                                                    <td className="px-3 py-2 text-right">
                                                        {parseFloat(item.itemPrice).toLocaleString('vi-VN')} ₫
                                                    </td>
                                                    <td className="px-3 py-2 text-center">{item.itemQuantity}</td>
                                                    <td className="px-3 py-2 text-right font-medium">
                                                        {(parseFloat(item.itemPrice) * item.itemQuantity).toLocaleString('vi-VN')} ₫
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                    <div className="flex justify-end mt-4 pt-4 border-t border-gray-200">
                                        <div className="text-right">
                                            <p className="text-sm text-gray-600 mb-1">Tổng tiền</p>
                                            <p className="font-bold text-xl text-blue-600">
                                                {parseFloat(selectedOrder.orderAmount).toLocaleString('vi-VN')} ₫
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            )}

                            <div className="flex justify-end">
                                <button
                                    onClick={() => setShowDetailModal(false)}
                                    className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300"
                                >
                                    Đóng
                                </button>
                            </div>
                        </div>
                    </div>
                )}

                {/* Status Update Modal */}
                {showStatusModal && selectedOrder && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                        <div className="bg-white rounded-xl p-6 w-full max-w-md">
                            <h3 className="text-lg font-bold mb-4">Cập nhật trạng thái đơn hàng</h3>
                            <p className="text-sm text-gray-600 mb-4">Đơn hàng: {selectedOrder.orderCode}</p>
                            
                            <div className="mb-6">
                                <label className="block text-sm font-medium text-gray-700 mb-2">Trạng thái</label>
                                <select
                                    className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                    value={newStatus}
                                    onChange={(e) => setNewStatus(e.target.value)}
                                >
                                    <option value="Đã đặt">Đã đặt</option>
                                    <option value="Đang xử lý">Đang xử lý</option>
                                    <option value="Đang giao">Đang giao</option>
                                    <option value="Đã giao">Đã giao</option>
                                    <option value="Đã hủy">Đã hủy</option>
                                </select>
                            </div>

                            <div className="flex justify-end gap-3">
                                <button
                                    onClick={() => setShowStatusModal(false)}
                                    className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
                                >
                                    Hủy
                                </button>
                                <button
                                    onClick={handleStatusSubmit}
                                    className="px-4 py-2 bg-pink-600 text-white rounded-lg hover:bg-pink-700"
                                >
                                    Cập nhật
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Orders;
