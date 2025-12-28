import React, { useEffect, useState } from 'react';
import EmployeeHeader from '../../components/Employee/EmployeeHeader';
import { getDashboardStats, getRecentOrders, getTopProducts } from '../../api/dashboard';
import { API_BASE_URL } from '../../api/constant';

const StatCard = ({ title, value, icon, color, trend, loading }) => (
    <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
        <div className="flex items-center justify-between mb-4">
            <div className={`p-3 rounded-lg ${color} bg-opacity-10`}>
                <span className={`text-2xl ${color.replace('bg-', 'text-')}`}>{icon}</span>
            </div>
            {!loading && trend !== undefined && (
                <span className={`text-sm font-medium ${trend >= 0 ? 'text-green-500' : 'text-red-500'}`}>
                    {trend >= 0 ? '+' : ''}{trend.toFixed(1)}%
                </span>
            )}
        </div>
        <h3 className="text-gray-500 text-sm font-medium">{title}</h3>
        {loading ? (
            <div className="h-8 bg-gray-200 rounded animate-pulse mt-1"></div>
        ) : (
            <p className="text-2xl font-bold text-gray-800 mt-1">{value}</p>
        )}
    </div>
);

const DashboardHome = () => {
    const [stats, setStats] = useState(null);
    const [recentOrders, setRecentOrders] = useState([]);
    const [topProducts, setTopProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                setLoading(true);
                
                // Fetch all data in parallel
                const [statsResponse, ordersResponse, productsResponse] = await Promise.all([
                    getDashboardStats(),
                    getRecentOrders(5),
                    getTopProducts(5)
                ]);

                setStats(statsResponse.data);
                setRecentOrders(ordersResponse.data);
                setTopProducts(productsResponse.data);
            } catch (error) {
                console.error('Failed to fetch dashboard data:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchDashboardData();
    }, []);

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('vi-VN');
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'DELIVERED': return 'bg-green-100 text-green-700';
            case 'NEW': return 'bg-yellow-100 text-yellow-700';
            case 'PROCESSING': return 'bg-blue-100 text-blue-700';
            case 'CANCELLED': return 'bg-red-100 text-red-700';
            default: return 'bg-gray-100 text-gray-700';
        }
    };

    const getStatusLabel = (status) => {
        const labels = {
            'NEW': 'Mới',
            'PROCESSING': 'Đang xử lý',
            'DELIVERED': 'Đã giao',
            'CANCELLED': 'Đã hủy'
        };
        return labels[status] || status;
    };

    // Modified statsCards to exclude Revenue
    const statsCards = stats ? [
        { 
            title: 'Đơn hàng mới', 
            value: stats.newOrders.toString(), 
            icon: '🛍️', 
            color: 'bg-purple-500', 
            trend: stats.ordersTrend 
        },
        { 
            title: 'Khách hàng', 
            value: stats.totalCustomers.toLocaleString('vi-VN'), 
            icon: '👥', 
            color: 'bg-green-500', 
            trend: stats.customersTrend 
        },
        { 
            title: 'Sản phẩm', 
            value: stats.totalProducts.toString(), 
            icon: '📦', 
            color: 'bg-orange-500', 
            trend: stats.productsTrend 
        },
    ] : [];

    return (
        <div className="min-h-screen bg-gray-50">
            <EmployeeHeader title="Tổng quan" />
            
            <div className="p-8">
                {/* Stats Grid - Adjusted for 3 items */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                    {loading ? (
                        // Loading skeleton
                        [1, 2, 3].map((i) => (
                            <StatCard key={i} title="..." value="..." icon="⏳" color="bg-gray-500" loading={true} />
                        ))
                    ) : (
                        statsCards.map((stat, index) => (
                            <StatCard key={index} {...stat} loading={false} />
                        ))
                    )}
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Recent Orders */}
                    <div className="lg:col-span-2 bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                        <div className="flex items-center justify-between mb-6">
                            <h3 className="text-lg font-bold text-gray-800">Đơn hàng gần đây</h3>
                        </div>
                        {loading ? (
                            <div className="space-y-4">
                                {[1, 2, 3, 4, 5].map((i) => (
                                    <div key={i} className="h-12 bg-gray-200 rounded animate-pulse"></div>
                                ))}
                            </div>
                        ) : (
                            <div className="overflow-x-auto">
                                <table className="w-full">
                                    <thead>
                                        <tr className="text-left border-b border-gray-100">
                                            <th className="pb-4 text-sm font-semibold text-gray-500">Mã đơn</th>
                                            <th className="pb-4 text-sm font-semibold text-gray-500">Khách hàng</th>
                                            <th className="pb-4 text-sm font-semibold text-gray-500">Ngày đặt</th>
                                            <th className="pb-4 text-sm font-semibold text-gray-500">Tổng tiền</th>
                                            <th className="pb-4 text-sm font-semibold text-gray-500">Trạng thái</th>
                                        </tr>
                                    </thead>
                                    <tbody className="text-sm">
                                        {recentOrders.map((order) => (
                                            <tr key={order.orderId} className="border-b border-gray-50 last:border-0">
                                                <td className="py-4 font-medium text-gray-800">{order.orderCode}</td>
                                                <td className="py-4 text-gray-600">{order.customerName}</td>
                                                <td className="py-4 text-gray-500">{formatDate(order.orderDate)}</td>
                                                <td className="py-4 font-medium text-gray-800">{formatCurrency(order.totalAmount)}</td>
                                                <td className="py-4">
                                                    <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(order.orderStatus)}`}>
                                                        {getStatusLabel(order.orderStatus)}
                                                    </span>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>

                    {/* Top Products */}
                    <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
                        <h3 className="text-lg font-bold text-gray-800 mb-6">Sản phẩm bán chạy</h3>
                        {loading ? (
                            <div className="space-y-6">
                                {[1, 2, 3, 4, 5].map((i) => (
                                    <div key={i} className="flex items-center gap-4">
                                        <div className="w-12 h-12 bg-gray-200 rounded-lg animate-pulse"></div>
                                        <div className="flex-1 space-y-2">
                                            <div className="h-4 bg-gray-200 rounded animate-pulse"></div>
                                            <div className="h-3 bg-gray-200 rounded w-1/2 animate-pulse"></div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="space-y-6">
                                {topProducts.map((product) => {
                                    let imageUrl = product.imageUrl;
                                    if (imageUrl && !imageUrl.startsWith('http')) {
                                        imageUrl = `${API_BASE_URL}${imageUrl}`;
                                    }

                                    return (
                                        <div key={product.productId} className="flex items-center gap-4">
                                            <div className="w-12 h-12 bg-gray-100 rounded-lg flex-shrink-0 overflow-hidden">
                                                {imageUrl ? (
                                                    <img 
                                                        src={imageUrl} 
                                                        alt={product.productName}
                                                        className="w-full h-full object-cover"
                                                    />
                                                ) : (
                                                    <div className="w-full h-full flex items-center justify-center text-gray-400">
                                                        📦
                                                    </div>
                                                )}
                                            </div>
                                            <div className="flex-1 min-w-0">
                                                <h4 className="text-sm font-medium text-gray-800 truncate">{product.productName}</h4>
                                                <p className="text-xs text-gray-500">{product.totalSold} đã bán</p>
                                            </div>
                                            <span className="text-sm font-bold text-gray-800">{formatCurrency(product.productPrice)}</span>
                                        </div>
                                    );
                                })}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DashboardHome;
