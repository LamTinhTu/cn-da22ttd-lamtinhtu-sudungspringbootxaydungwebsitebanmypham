import React from 'react'
import SvgFavourite from '../../components/common/SvgFavourite'
import { Link } from 'react-router-dom'


const ProductCard = ({id, title,description,price,brand,thumbnail}) => {
  return (
    <div className='flex flex-col hover:shadow-lg transition-shadow rounded-lg p-3 border border-transparent hover:border-gray-100'>
        <Link to={`/product/${id}`} className="block overflow-hidden rounded-lg mb-3 aspect-[3/4]">
            <img 
                className="w-full h-full object-cover hover:scale-105 transition-transform duration-300 cursor-pointer" 
                src={thumbnail} 
                alt={title}
            />
        </Link>
         <div className='flex flex-col items-center text-center'>
            <p className='text-[16px] font-medium text-gray-800 line-clamp-2 mb-1'>{title}</p>
            {brand && <p className='text-[12px] text-gray-500'>{brand}</p>}
            <p className='font-bold text-black mt-1'>{price?.toLocaleString('vi-VN')} VND</p>
        </div>
    </div>
  )
}

export default ProductCard