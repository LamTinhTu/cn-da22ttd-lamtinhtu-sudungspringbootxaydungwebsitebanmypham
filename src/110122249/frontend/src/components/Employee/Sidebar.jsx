import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { logOut } from '../../utils/jwt-helper';

const Sidebar = () => {
    const navigate = useNavigate();
    const menuItems = [
        { path: '/employee', label: 'Tổng quan' },
        { path: '/employee/orders', label: 'Đơn hàng' },
        { path: '/employee/products', label: 'Sản phẩm' },
        { path: '/employee/customer-lookup', label: 'Tra cứu khách hàng' },
    ];

    const handleLogout = () => {
        logOut();
        navigate('/');
    };

    return (
        <div className="w-64 bg-[#ffe6f2] min-h-screen text-gray-800 flex flex-col font-sans border-r border-pink-200">
            <div className="h-20 flex items-center justify-center border-b border-pink-200">
                <h1 className="text-2xl font-bold text-gray-800">Nhân viên</h1>
            </div>
            <nav className="flex-1 py-6">
                <ul className="space-y-2">
                    {menuItems.map((item) => (
                        <li key={item.path}>
                            <NavLink
                                to={item.path}
                                end={item.path === '/employee'}
                                className={({ isActive }) =>
                                    `flex items-center px-6 py-3 text-gray-600 hover:bg-white/50 hover:text-gray-900 transition-colors ${
                                        isActive ? 'bg-white text-black shadow-sm border-r-4 border-pink-400' : ''
                                    }`
                                }
                            >
                                <span className="font-medium">{item.label}</span>
                            </NavLink>
                        </li>
                    ))}
                </ul>
            </nav>
            <div className="p-4 border-t border-pink-200">
                <button 
                    onClick={handleLogout}
                    className="flex items-center w-full px-4 py-2 text-gray-600 hover:text-gray-900 hover:bg-white/50 rounded transition-colors"
                >
                    <span>Đăng xuất</span>
                </button>
            </div>
        </div>
    );
};

export default Sidebar;
