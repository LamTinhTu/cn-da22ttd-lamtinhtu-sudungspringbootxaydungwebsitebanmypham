import React, { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import ConfirmModal from '../common/ConfirmModal';

const Sidebar = () => {
    const navigate = useNavigate();
    const [showConfirm, setShowConfirm] = useState(false);

    const menuItems = [
        { path: '/admin', label: 'Tổng quan' },
        { path: '/admin/manufacturers', label: 'Nhà sản xuất' },
        { path: '/admin/products', label: 'Sản phẩm' },
        { path: '/admin/orders', label: 'Đơn hàng' },
        { path: '/admin/users', label: 'Tài khoản người dùng' },
        { path: '/admin/reviews', label: 'Bài viết đánh giá' },
    ];

    const handleLogout = () => {
        setShowConfirm(true);
    };

    const confirmLogout = () => {
        // Xóa token và thông tin user khỏi localStorage
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        
        // Redirect về trang chủ
        navigate('/');
        
        // Reload trang để reset state
        window.location.reload();
    };

    return (
        <div className="w-64 bg-gray-900 min-h-screen text-white flex flex-col">
            <div className="h-20 flex items-center justify-center border-b border-gray-800 gap-2">
                <span className="text-2xl">👋</span>
                <h1 className="text-xl font-bold text-white">Xin chào quản trị</h1>
            </div>
            <nav className="flex-1 py-6">
                <ul className="space-y-2">
                    {menuItems.map((item) => (
                        <li key={item.path}>
                            <NavLink
                                to={item.path}
                                end={item.path === '/admin'}
                                className={({ isActive }) =>
                                    `flex items-center px-6 py-3 text-gray-300 hover:bg-gray-800 hover:text-white transition-colors ${
                                        isActive ? 'bg-gray-800 text-white border-r-4 border-pink-500' : ''
                                    }`
                                }
                            >
                                <span className="font-medium">{item.label}</span>
                            </NavLink>
                        </li>
                    ))}
                </ul>
            </nav>
            <div className="p-4 border-t border-gray-800">
                <button 
                    onClick={handleLogout}
                    className="flex items-center w-full px-4 py-2 text-gray-300 hover:text-white hover:bg-gray-800 rounded transition-colors"
                >
                    <span className="mr-2">🚪</span>
                    <span>Đăng xuất</span>
                </button>
            </div>
            <ConfirmModal
                isOpen={showConfirm}
                onClose={() => setShowConfirm(false)}
                onConfirm={confirmLogout}
                title="Xác nhận đăng xuất"
                message="Bạn có chắc chắn muốn đăng xuất?"
                confirmText="Đăng xuất"
            />
        </div>
    );
};

export default Sidebar;
