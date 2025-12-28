import React from 'react';
import { Link } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { selectCartItems, selectCartTotal, removeFromCart, updateQuantity } from '../../store/features/cartSlice';

const CartPage = () => {
    const cartItems = useSelector(selectCartItems);
    const subtotal = useSelector(selectCartTotal);
    const dispatch = useDispatch();

    const handleUpdateQuantity = (id, change) => {
        const item = cartItems.find(i => i.id === id);
        if (item) {
            dispatch(updateQuantity({ id, quantity: item.quantity + change }));
        }
    };

    const handleRemoveItem = (id) => {
        dispatch(removeFromCart(id));
    };

    const shipping = 30000;
    const total = subtotal + shipping;

    if (cartItems.length === 0) {
        return (
            <div className="container mx-auto px-4 py-16 text-center">
                <h2 className="text-2xl font-bold mb-4">Giỏ hàng của bạn đang trống</h2>
                <p className="text-gray-600 mb-8">Hãy thêm sản phẩm vào giỏ hàng để tiếp tục mua sắm.</p>
                <Link to="/" className="inline-block bg-black text-white px-8 py-3 rounded-full hover:bg-gray-800 transition-colors">
                    Tiếp tục mua sắm
                </Link>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-8 font-sans">
            <h1 className="text-3xl font-bold mb-8">Giỏ hàng</h1>
            
            <div className="flex flex-col lg:flex-row gap-8">
                {/* Cart Items List */}
                <div className="flex-1">
                    <div className="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
                        <div className="hidden md:grid grid-cols-12 gap-4 p-4 bg-gray-50 border-b border-gray-100 text-sm font-medium text-gray-500">
                            <div className="col-span-6">Sản phẩm</div>
                            <div className="col-span-2 text-center">Đơn giá</div>
                            <div className="col-span-2 text-center">Số lượng</div>
                            <div className="col-span-2 text-right">Thành tiền</div>
                        </div>

                        <div className="divide-y divide-gray-100">
                            {cartItems.map((item) => (
                                <div key={item.id} className="p-4 grid grid-cols-1 md:grid-cols-12 gap-4 items-center">
                                    <div className="col-span-1 md:col-span-6 flex gap-4">
                                        <img src={item.image} alt={item.name} className="w-20 h-20 object-cover rounded-md bg-gray-100" />
                                        <div>
                                            <h3 className="font-medium text-gray-800">{item.name}</h3>
                                            <p className="text-sm text-gray-500 mt-1">
                                                {item.color && `Màu: ${item.color}`}
                                                {item.size && `Size: ${item.size}`}
                                            </p>
                                            <button 
                                                onClick={() => handleRemoveItem(item.id)}
                                                className="text-red-500 text-sm mt-2 hover:underline"
                                            >
                                                Xóa
                                            </button>
                                        </div>
                                    </div>
                                    
                                    <div className="col-span-1 md:col-span-2 text-center md:text-center flex justify-between md:block">
                                        <span className="md:hidden text-gray-500">Đơn giá:</span>
                                        <span>{item.price?.toLocaleString('vi-VN')} ₫</span>
                                    </div>

                                    <div className="col-span-1 md:col-span-2 flex justify-center items-center gap-2">
                                        <button 
                                            onClick={() => handleUpdateQuantity(item.id, -1)}
                                            className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center hover:bg-gray-100"
                                        >
                                            -
                                        </button>
                                        <span className="w-8 text-center">{item.quantity}</span>
                                        <button 
                                            onClick={() => handleUpdateQuantity(item.id, 1)}
                                            className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center hover:bg-gray-100"
                                        >
                                            +
                                        </button>
                                    </div>

                                    <div className="col-span-1 md:col-span-2 text-right md:text-right flex justify-between md:block font-medium">
                                        <span className="md:hidden text-gray-500">Thành tiền:</span>
                                        <span>{(item.price * item.quantity).toLocaleString('vi-VN')} ₫</span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Order Summary */}
                <div className="w-full lg:w-96">
                    <div className="bg-white rounded-lg shadow-sm border border-gray-100 p-6 sticky top-4">
                        <h2 className="text-lg font-bold mb-4">Tổng đơn hàng</h2>
                        
                        <div className="space-y-3 mb-6">
                            <div className="flex justify-between text-gray-600">
                                <span>Tạm tính</span>
                                <span>{subtotal.toLocaleString('vi-VN')} ₫</span>
                            </div>
                            <div className="flex justify-between text-gray-600">
                                <span>Phí vận chuyển</span>
                                <span>{shipping.toLocaleString('vi-VN')} ₫</span>
                            </div>
                            <div className="border-t border-gray-100 pt-3 flex justify-between font-bold text-lg text-gray-800">
                                <span>Tổng cộng</span>
                                <span>{total.toLocaleString('vi-VN')} ₫</span>
                            </div>
                        </div>

                        <Link 
                            to="/checkout"
                            className="block w-full bg-black text-white text-center py-3 rounded-lg hover:bg-gray-800 transition-colors font-medium"
                        >
                            Tiến hành thanh toán
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CartPage;
