import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { selectCartItems, selectCartTotal, clearCart } from '../../store/features/cartSlice';
import { placeOrderAPI } from '../../api/order';
import { toast } from 'react-toastify';
import { getUser, isTokenValid } from '../../utils/jwt-helper';

const CheckoutPage = () => {
    const cartItems = useSelector(selectCartItems);
    const subtotal = useSelector(selectCartTotal);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const user = getUser();

    const [formData, setFormData] = useState({
        fullName: '',
        email: '',
        phone: '',
        address: '',
        city: '',
        paymentMethod: 'CASH'
    });

    // Kiểm tra đăng nhập
    useEffect(() => {
        if (!isTokenValid()) {
            toast.error("Vui lòng đăng nhập để đặt hàng!");
            setTimeout(() => {
                navigate('/');
            }, 2000);
        }
    }, [navigate]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const shipping = 30000;
    const total = subtotal + shipping;

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Kiểm tra đăng nhập
        if (!isTokenValid()) {
            toast.error("Vui lòng đăng nhập để đặt hàng!");
            return;
        }

        if (cartItems.length === 0) {
            toast.error("Giỏ hàng trống!");
            return;
        }

        // Validate số điện thoại
        const phoneRegex = /^[0-9]{10,11}$/;
        if (!phoneRegex.test(formData.phone)) {
            toast.error("Số điện thoại phải có 10-11 chữ số!");
            return;
        }

        const orderData = {
            orderStatus: "NEW",
            orderAmount: parseFloat(total.toFixed(2)),
            shippingAddress: `${formData.address}, ${formData.city}`,
            shippingPhone: formData.phone,
            paymentMethod: formData.paymentMethod,
            orderItems: cartItems.map(item => ({
                productId: parseInt(item.id),
                itemQuantity: parseInt(item.quantity),
                itemPrice: parseFloat(item.price.toFixed(2)),
                unitPrice: parseFloat(item.price.toFixed(2))
            }))
        };

        try {
            console.log("Sending order data:", orderData);
            const response = await placeOrderAPI(orderData);
            console.log("Order response:", response);
            toast.success("Đặt hàng thành công!");
            dispatch(clearCart());
            navigate('/my-orders'); 
        } catch (error) {
            console.error("Order error:", error);
            console.error("Error response:", error.response?.data);
            
            // Hiển thị thông báo lỗi chi tiết
            const errorMessage = error.response?.data?.message || 
                               error.response?.data?.error ||
                               error.message || 
                               "Đặt hàng thất bại. Vui lòng thử lại.";
            toast.error(errorMessage);
        }
    };

    if (cartItems.length === 0) {
        return (
            <div className="container mx-auto px-4 py-16 text-center">
                <h2 className="text-2xl font-bold mb-4">Giỏ hàng của bạn đang trống</h2>
                <Link to="/" className="inline-block bg-black text-white px-8 py-3 rounded-full hover:bg-gray-800 transition-colors">
                    Tiếp tục mua sắm
                </Link>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-8 font-sans">
            <h1 className="text-3xl font-bold mb-8 text-center">Thanh toán</h1>

            <form onSubmit={handleSubmit} className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-8">
                {/* Shipping Information */}
                <div className="lg:col-span-7">
                    <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-100 mb-6">
                        <h2 className="text-xl font-bold mb-4">Thông tin giao hàng</h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div className="md:col-span-2">
                                <label className="block text-sm font-medium text-gray-700 mb-1">Họ và tên</label>
                                <input
                                    type="text"
                                    name="fullName"
                                    value={formData.fullName}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-black focus:border-transparent outline-none"
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                                <input
                                    type="email"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-black focus:border-transparent outline-none"
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Số điện thoại</label>
                                <input
                                    type="tel"
                                    name="phone"
                                    value={formData.phone}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-black focus:border-transparent outline-none"
                                    required
                                />
                            </div>
                            <div className="md:col-span-2">
                                <label className="block text-sm font-medium text-gray-700 mb-1">Địa chỉ</label>
                                <input
                                    type="text"
                                    name="address"
                                    value={formData.address}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-black focus:border-transparent outline-none"
                                    required
                                />
                            </div>
                            <div className="md:col-span-2">
                                <label className="block text-sm font-medium text-gray-700 mb-1">Tỉnh / Thành phố</label>
                                <select
                                    name="city"
                                    value={formData.city}
                                    onChange={handleChange}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-black focus:border-transparent outline-none"
                                    required
                                >
                                    <option value="">Chọn tỉnh / thành phố</option>
                                    <option value="Hà Nội">Hà Nội</option>
                                    <option value="TP. Hồ Chí Minh">TP. Hồ Chí Minh</option>
                                    <option value="Đà Nẵng">Đà Nẵng</option>
                                    <option value="Hải Phòng">Hải Phòng</option>
                                    <option value="Cần Thơ">Cần Thơ</option>
                                    <option value="An Giang">An Giang</option>
                                    <option value="Bà Rịa - Vũng Tàu">Bà Rịa - Vũng Tàu</option>
                                    <option value="Bắc Giang">Bắc Giang</option>
                                    <option value="Bắc Kạn">Bắc Kạn</option>
                                    <option value="Bạc Liêu">Bạc Liêu</option>
                                    <option value="Bắc Ninh">Bắc Ninh</option>
                                    <option value="Bến Tre">Bến Tre</option>
                                    <option value="Bình Định">Bình Định</option>
                                    <option value="Bình Dương">Bình Dương</option>
                                    <option value="Bình Phước">Bình Phước</option>
                                    <option value="Bình Thuận">Bình Thuận</option>
                                    <option value="Cà Mau">Cà Mau</option>
                                    <option value="Cao Bằng">Cao Bằng</option>
                                    <option value="Đắk Lắk">Đắk Lắk</option>
                                    <option value="Đắk Nông">Đắk Nông</option>
                                    <option value="Điện Biên">Điện Biên</option>
                                    <option value="Đồng Nai">Đồng Nai</option>
                                    <option value="Đồng Tháp">Đồng Tháp</option>
                                    <option value="Gia Lai">Gia Lai</option>
                                    <option value="Hà Giang">Hà Giang</option>
                                    <option value="Hà Nam">Hà Nam</option>
                                    <option value="Hà Tĩnh">Hà Tĩnh</option>
                                    <option value="Hải Dương">Hải Dương</option>
                                    <option value="Hậu Giang">Hậu Giang</option>
                                    <option value="Hòa Bình">Hòa Bình</option>
                                    <option value="Hưng Yên">Hưng Yên</option>
                                    <option value="Khánh Hòa">Khánh Hòa</option>
                                    <option value="Kiên Giang">Kiên Giang</option>
                                    <option value="Kon Tum">Kon Tum</option>
                                    <option value="Lai Châu">Lai Châu</option>
                                    <option value="Lâm Đồng">Lâm Đồng</option>
                                    <option value="Lạng Sơn">Lạng Sơn</option>
                                    <option value="Lào Cai">Lào Cai</option>
                                    <option value="Long An">Long An</option>
                                    <option value="Nam Định">Nam Định</option>
                                    <option value="Nghệ An">Nghệ An</option>
                                    <option value="Ninh Bình">Ninh Bình</option>
                                    <option value="Ninh Thuận">Ninh Thuận</option>
                                    <option value="Phú Thọ">Phú Thọ</option>
                                    <option value="Phú Yên">Phú Yên</option>
                                    <option value="Quảng Bình">Quảng Bình</option>
                                    <option value="Quảng Nam">Quảng Nam</option>
                                    <option value="Quảng Ngãi">Quảng Ngãi</option>
                                    <option value="Quảng Ninh">Quảng Ninh</option>
                                    <option value="Quảng Trị">Quảng Trị</option>
                                    <option value="Sóc Trăng">Sóc Trăng</option>
                                    <option value="Sơn La">Sơn La</option>
                                    <option value="Tây Ninh">Tây Ninh</option>
                                    <option value="Thái Bình">Thái Bình</option>
                                    <option value="Thái Nguyên">Thái Nguyên</option>
                                    <option value="Thanh Hóa">Thanh Hóa</option>
                                    <option value="Thừa Thiên Huế">Thừa Thiên Huế</option>
                                    <option value="Tiền Giang">Tiền Giang</option>
                                    <option value="Trà Vinh">Trà Vinh</option>
                                    <option value="Tuyên Quang">Tuyên Quang</option>
                                    <option value="Vĩnh Long">Vĩnh Long</option>
                                    <option value="Vĩnh Phúc">Vĩnh Phúc</option>
                                    <option value="Yên Bái">Yên Bái</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-100">
                        <h2 className="text-xl font-bold mb-4">Phương thức thanh toán</h2>
                        <div className="space-y-3">
                            <label className="flex items-center p-4 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50">
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="CASH"
                                    checked={formData.paymentMethod === 'CASH'}
                                    onChange={handleChange}
                                    className="w-4 h-4 text-black focus:ring-black"
                                />
                                <span className="ml-3 font-medium">Thanh toán khi nhận hàng (COD)</span>
                            </label>
                            <label className="flex items-center p-4 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50">
                                <input
                                    type="radio"
                                    name="paymentMethod"
                                    value="BANK_TRANSFER"
                                    checked={formData.paymentMethod === 'BANK_TRANSFER'}
                                    onChange={handleChange}
                                    className="w-4 h-4 text-black focus:ring-black"
                                />
                                <span className="ml-3 font-medium">Chuyển khoản ngân hàng</span>
                            </label>
                        </div>
                    </div>
                </div>

                {/* Order Summary */}
                <div className="lg:col-span-5">
                    <div className="bg-gray-50 p-6 rounded-lg border border-gray-200 sticky top-4">
                        <h2 className="text-xl font-bold mb-4">Đơn hàng của bạn</h2>
                        
                        {/* Cart Items */}
                        <div className="space-y-4 mb-6">
                            {cartItems.map((item) => (
                                <div key={item.id} className="flex gap-4">
                                    <div className="w-16 h-16 bg-white rounded border border-gray-200 flex items-center justify-center overflow-hidden">
                                        <img src={item.image} alt={item.name} className="w-full h-full object-cover" />
                                    </div>
                                    <div className="flex-1">
                                        <h4 className="font-medium text-sm">{item.name}</h4>
                                        <p className="text-xs text-gray-500">SL: {item.quantity}</p>
                                    </div>
                                    <span className="font-medium text-sm">{(item.price * item.quantity).toLocaleString('vi-VN')} ₫</span>
                                </div>
                            ))}
                        </div>

                        <div className="border-t border-gray-200 pt-4 space-y-2 mb-6">
                            <div className="flex justify-between text-sm text-gray-600">
                                <span>Tạm tính</span>
                                <span>{subtotal.toLocaleString('vi-VN')} ₫</span>
                            </div>
                            <div className="flex justify-between text-sm text-gray-600">
                                <span>Phí vận chuyển</span>
                                <span>{shipping.toLocaleString('vi-VN')} ₫</span>
                            </div>
                            <div className="flex justify-between font-bold text-lg text-gray-800 pt-2 border-t border-gray-200">
                                <span>Tổng cộng</span>
                                <span>{total.toLocaleString('vi-VN')} ₫</span>
                            </div>
                        </div>

                        <button 
                            type="submit"
                            className="w-full bg-black text-white py-3 rounded-lg hover:bg-gray-800 transition-colors font-bold"
                        >
                            Đặt hàng
                        </button>
                        
                        <Link to="/cart" className="block text-center text-sm text-gray-500 mt-4 hover:underline">
                            Quay lại giỏ hàng
                        </Link>
                    </div>
                </div>
            </form>
        </div>
    );
};

export default CheckoutPage;
