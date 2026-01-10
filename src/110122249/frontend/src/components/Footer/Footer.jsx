import React from 'react'

const Footer = () => {
  return (
    <div className='bg-black text-white py-6 mt-12'>
        <div className='container mx-auto px-4'>
            <div className='flex justify-between items-start mb-4 px-4 md:px-32'>
                <div className='text-left'>
                    <p className='text-[16px] font-semibold mb-3'>Mọi thắc mắc liên hệ:</p>
                    <p className='text-[14px] mb-1'>Số điện thoại: 0339626863</p>
                    <p className='text-[14px] mb-1'>Email: ocean&butterfly@gmail.com</p>
                </div>
                <div className='text-left'>
                    <p className='text-[16px] font-semibold mb-3'>Vị trí:</p>
                    <p className='text-[14px]'>
                        <a 
                            href="https://www.google.com/maps?q=Ấp+Xoài+Lơ,+Xã+Lưu+Nghiệp+Anh,+tỉnh+Vĩnh+Long" 
                            target="_blank" 
                            rel="noopener noreferrer"
                            className="hover:text-gray-300 transition-colors duration-200"
                        >
                            Ấp Xoài Lơ, Xã Lưu Nghiệp Anh, tỉnh Vĩnh Long
                        </a>
                    </p>
                </div>
            </div>
            <p className='text-sm text-white text-center pt-4 border-t border-gray-700'>Copyright © 2025 Ocean and Butterfly</p>
        </div>
    </div>
  )
}

export default Footer