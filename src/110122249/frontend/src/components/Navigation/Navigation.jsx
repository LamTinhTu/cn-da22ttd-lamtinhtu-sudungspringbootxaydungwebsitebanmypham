import React, { useState, useEffect } from 'react'
import { useSelector } from 'react-redux';
import { selectCartCount } from '../../store/features/cartSlice';
import { Wishlist } from '../common/Wishlist';
import { AccountIcon } from '../common/AccountIcon';
import { CartIcon } from '../common/CartIcon';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import LoginModal from '../Auth/LoginModal';
import RegisterModal from '../Auth/RegisterModal';
import ForgotPasswordModal from '../Auth/ForgotPasswordModal';
import { getUser, logOut } from '../../utils/jwt-helper';
import './Navigation.css';

const Navigation = () => {
    const navigate = useNavigate();
    const [showLoginModal, setShowLoginModal] = useState(false);
    const [showRegisterModal, setShowRegisterModal] = useState(false);
    const [showForgotPasswordModal, setShowForgotPasswordModal] = useState(false);
    const [showUserMenu, setShowUserMenu] = useState(false);
    const user = getUser();
    const [searchTerm, setSearchTerm] = useState('');

    const handleSearch = (e) => {
        if (e.key === 'Enter' || e.type === 'click') {
            if (searchTerm.trim()) {
                navigate(`/search?q=${encodeURIComponent(searchTerm)}`);
                setSearchTerm('');
            }
        }
    };
    const cartCount = useSelector(selectCartCount);

    const handleLogout = () => {
        logOut();
        window.location.href = '/';
    };

    const handleSwitchToRegister = () => {
        setShowLoginModal(false);
        setShowRegisterModal(true);
        setShowForgotPasswordModal(false);
    };

    const handleSwitchToLogin = () => {
        setShowRegisterModal(false);
        setShowForgotPasswordModal(false);
        setShowLoginModal(true);
    };

    const handleSwitchToForgotPassword = () => {
        setShowLoginModal(false);
        setShowForgotPasswordModal(true);
    };

    const toggleUserMenu = () => {
        setShowUserMenu(!showUserMenu);
    };

    // Close menu when clicking outside
    React.useEffect(() => {
        const handleClickOutside = (event) => {
            if (showUserMenu && !event.target.closest('.user-menu-container')) {
                setShowUserMenu(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [showUserMenu]);

    return (
        <nav className='fixed top-0 left-0 right-0 z-50 bg-white shadow-md flex items-center py-2 px-8 gap-8'>
            <div className='flex items-center gap-6'>
                {/* Logo */}
                <Link className='text-3xl text-black font-bold gap-8' to='/about-us'>
                    <img src="/logo-ocean-butterfly.png" alt="Logo" className="h-14 w-auto object-contain" />
                </Link>
            </div>
            <div className='flex flex-wrap items-center gap-8 flex-1'>
                {/* Nav items */}
                <ul className='flex gap-8 text-gray-600 hover:text-black whitespace-nowrap font-medium'>
                    <li><NavLink to="/" className={({isActive})=> isActive ? 'active-link':''}>Trang chủ</NavLink></li>
                    <li><NavLink to="/makeup" className={({isActive})=> isActive ? 'active-link':''}>Trang Điểm</NavLink></li>
                    <li><NavLink to="/skincare" className={({isActive})=> isActive ? 'active-link':''}>Dưỡng Da</NavLink></li>
                    <li><NavLink to="/haircare" className={({isActive})=> isActive ? 'active-link':''}>Chăm Sóc Tóc</NavLink></li>
                </ul>
            </div>

            <div className='flex justify-center'>
                {/* search bar */}
                <div className='border rounded flex owerflow-hidden'>
                    <div className="flex items-center justify-center px-4 border-1">
                        <button onClick={handleSearch}>
                            <svg className="h-4 w-4 text-grey-dark" fill="currentColor" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M16.32 14.9l5.39 5.4a1 1 0 0 1-1.42 1.4l-5.38-5.38a8 8 0 1 1 1.41-1.41zM10 16a6 6 0 1 0 0-12 6 6 0 0 0 0 12z"/></svg>
                        </button>
                        <input 
                            type="text" 
                            className="px-4 py-2 outline-none" 
                            placeholder="Tìm kiếm"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            onKeyDown={handleSearch}
                        />
                    </div> 
                </div>
            </div>

            <div className='flex flex-wrap items-center gap-4'>
                {/* action itéms - icon */}
                <ul className='flex items-center gap-8'>
                    {/* <li><button><Wishlist/></button></li> */}
                    <li>
                        {user ? (
                            <div className="relative user-menu-container">
                                <button 
                                    onClick={toggleUserMenu}
                                    className="flex items-center gap-2 hover:opacity-80 transition-opacity"
                                >
                                    <AccountIcon/>
                                    <svg 
                                        className={`w-4 h-4 transition-transform ${showUserMenu ? 'rotate-180' : ''}`} 
                                        fill="none" 
                                        stroke="currentColor" 
                                        viewBox="0 0 24 24"
                                    >
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                    </svg>
                                </button>
                                {showUserMenu && (
                                    <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50 border border-gray-200">
                                        <div className="px-4 py-2 text-sm text-gray-500 border-b border-gray-200">
                                            <div className="font-medium text-gray-900">{user.userName}</div>
                                            <div className="text-xs">{user.roleName}</div>
                                        </div>
                                        {user.roleName === 'Administrator' && (
                                            <Link 
                                                to="/admin" 
                                                className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                                onClick={() => setShowUserMenu(false)}
                                            >
                                                <span className="flex items-center gap-2">
                                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                                    </svg>
                                                    Admin Dashboard
                                                </span>
                                            </Link>
                                        )}
                                        {user.roleName === 'Staff' && (
                                            <Link 
                                                to="/employee" 
                                                className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                                onClick={() => setShowUserMenu(false)}
                                            >
                                                <span className="flex items-center gap-2">
                                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                                                    </svg>
                                                    Employee Dashboard
                                                </span>
                                            </Link>
                                        )}
                                        {user.roleName === 'Customer' && (
                                            <Link 
                                                to="/my-orders" 
                                                className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                                onClick={() => setShowUserMenu(false)}
                                            >
                                                <span className="flex items-center gap-2">
                                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
                                                    </svg>
                                                    Đơn hàng của tôi
                                                </span>
                                            </Link>
                                        )}
                                        <button 
                                            onClick={handleLogout} 
                                            className="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 border-t border-gray-200"
                                        >
                                            <span className="flex items-center gap-2">
                                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                                                </svg>
                                                Đăng xuất
                                            </span>
                                        </button>
                                    </div>
                                )}
                            </div>
                        ) : (
                            <button onClick={() => setShowLoginModal(true)}><AccountIcon/></button>
                        )}
                    </li>
                    <li>
                        <Link to='/cart' className="relative block">
                            <CartIcon/>
                            {cartCount > 0 && (
                                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
                                    {cartCount}
                                </span>
                            )}
                        </Link>
                    </li>
                </ul>
            </div>

            {/* Auth Modals */}
            <LoginModal 
                isOpen={showLoginModal} 
                onClose={() => setShowLoginModal(false)}
                onSwitchToRegister={handleSwitchToRegister}
                onSwitchToForgotPassword={handleSwitchToForgotPassword}
            />
            <RegisterModal 
                isOpen={showRegisterModal} 
                onClose={() => setShowRegisterModal(false)}
                onSwitchToLogin={handleSwitchToLogin}
            />
            <ForgotPasswordModal 
                isOpen={showForgotPasswordModal} 
                onClose={() => setShowForgotPasswordModal(false)}
                onSwitchToLogin={handleSwitchToLogin}
            />
        </nav>
    )
}

export default Navigation;