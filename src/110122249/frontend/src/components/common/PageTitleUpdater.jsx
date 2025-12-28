import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

const PageTitleUpdater = () => {
    const location = useLocation();

    useEffect(() => {
        const path = location.pathname;
        let title = 'Ocean & Butterfly';

        if (path === '/') {
            title = 'Ocean & Butterfly';
        } else if (path === '/about-us') {
            title = 'Về Chúng Tôi | Ocean & Butterfly';
        } else if (path === '/cart') {
            title = 'Giỏ Hàng | Ocean & Butterfly';
        } else if (path === '/checkout') {
            title = 'Thanh Toán | Ocean & Butterfly';
        } else if (path === '/my-orders') {
            title = 'Đơn Hàng Của Tôi | Ocean & Butterfly';
        } else if (path.startsWith('/makeup')) {
            title = 'Trang Điểm | Ocean & Butterfly';
        } else if (path.startsWith('/skincare')) {
            title = 'Dưỡng Da | Ocean & Butterfly';
        } else if (path.startsWith('/haircare')) {
            title = 'Chăm Sóc Tóc | Ocean & Butterfly';
        } else if (path.startsWith('/search')) {
            title = 'Tìm Kiếm | Ocean & Butterfly';
        } else if (path.startsWith('/product/')) {
            // Ideally we would want the product name here, but that requires data
            title = 'Chi Tiết Sản Phẩm | Ocean & Butterfly';
        } else if (path.startsWith('/admin')) {
            title = 'Quản Trị Viên | Ocean & Butterfly';
        } else if (path.startsWith('/employee')) {
            title = 'Nhân Viên | Ocean & Butterfly';
        }

        document.title = title;
    }, [location]);

    return null;
};

export default PageTitleUpdater;
