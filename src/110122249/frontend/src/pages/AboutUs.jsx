import React from 'react';
import { API_BASE_URL } from '../api/constant';

const AboutUs = () => {
    return (
        <div className="bg-gray-50 min-h-screen py-16" style={{
            backgroundImage: `url(${API_BASE_URL}/uploads/7.png)`,
            backgroundSize: 'cover',
            backgroundPosition: 'center',
            backgroundAttachment: 'fixed'
        }}>
            <div className="container mx-auto px-4 max-w-5xl">
                {/* Header Section */}
                <div className="text-center mb-16">
                    <h1 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4 tracking-wide">
                        Về Chúng Tôi
                    </h1>
                    <div className="w-24 h-1 bg-pink-500 mx-auto rounded-full"></div>
                    <p className="mt-6 text-lg text-black max-w-2xl mx-auto font-light">
                        Khám phá vẻ đẹp tinh tế từ thiên nhiên
                    </p>
                </div>

                {/* Main Content Card */}
                <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
                    <div className="md:flex">
                        {/* Left Side - Decorative */}
                        <div className="md:w-1/2 bg-gradient-to-br from-pink-100 to-blue-100 p-12 flex items-center justify-center relative overflow-hidden">
                             {/* Decorative circles */}
                            <div className="absolute top-0 left-0 w-64 h-64 bg-white opacity-20 rounded-full -translate-x-1/2 -translate-y-1/2"></div>
                            <div className="absolute bottom-0 right-0 w-48 h-48 bg-pink-300 opacity-20 rounded-full translate-x-1/3 translate-y-1/3"></div>
                            
                            <div className="relative z-10 text-center">
                                <h2 className="text-4xl font-bold text-gray-800 mb-4">Ocean & Butterfly</h2>
                                <div className="w-16 h-1 bg-gray-800 mx-auto mb-4"></div>
                                <p className="text-gray-600 italic text-lg">"Đẹp từ sự tự nhiên"</p>
                            </div>
                        </div>

                        {/* Right Side - Content */}
                        <div className="md:w-1/2 p-10 md:p-14 flex flex-col justify-center">
                            <div className="space-y-6 text-gray-700 leading-relaxed">
                                <p className="text-lg">
                                    <span className="font-bold text-pink-600 text-xl">Ocean & Butterfly</span> là nơi hội tụ của vẻ đẹp tinh tế, dịu dàng và cảm hứng từ thiên nhiên. 
                                    Lấy hình ảnh đại dương sâu thẳm và cánh bướm mong manh làm biểu tượng, chúng tôi mang đến những sản phẩm không chỉ đẹp về hình thức mà còn truyền tải cảm giác tự do, tươi mới và đầy sức sống.
                                </p>

                                <p className="text-base text-gray-600">
                                    Chúng tôi luôn chú trọng lựa chọn những sản phẩm chất lượng, an toàn và phù hợp với xu hướng hiện đại, giúp khách hàng tự tin thể hiện phong cách và cá tính riêng. 
                                    Mỗi sản phẩm đều được chọn lọc kỹ lưỡng, với mong muốn mang lại trải nghiệm mua sắm thoải mái, thân thiện và đáng tin cậy.
                                </p>
                            </div>
                        </div>
                    </div>
                    
                    {/* Bottom Quote Section */}
                    <div className="bg-gray-900 text-white p-12 text-center">
                        <svg className="w-10 h-10 mx-auto mb-6 text-pink-400 opacity-50" fill="currentColor" viewBox="0 0 24 24">
                            <path d="M14.017 21L14.017 18C14.017 16.0547 15.3738 14.5559 17.2141 14.5559C18.8525 14.5559 20.0585 15.2045 20.0585 16.9159L20.0585 21L22 21L22 15.8025C22 13.1699 19.3882 11.0745 16.234 11.0745C13.2142 11.0745 10.8926 12.6528 10.8926 15.4496L10.8926 21L14.017 21ZM5 21L5 18C5 16.0547 6.35683 14.5559 8.19722 14.5559C9.83564 14.5559 11.0417 15.2045 11.0417 16.9159L11.0417 21L12.9833 21L12.9833 15.8025C12.9833 13.1699 10.3714 11.0745 7.21726 11.0745C4.19749 11.0745 1.87583 12.6528 1.87583 15.4496L1.87583 21L5 21Z" />
                        </svg>
                        <p className="text-2xl md:text-3xl italic font-light max-w-3xl mx-auto leading-normal">
                            "Ocean & Butterfly không ngừng hoàn thiện để trở thành người bạn đồng hành cùng bạn trên hành trình chăm sóc bản thân và tận hưởng cuộc sống mỗi ngày."
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AboutUs;
