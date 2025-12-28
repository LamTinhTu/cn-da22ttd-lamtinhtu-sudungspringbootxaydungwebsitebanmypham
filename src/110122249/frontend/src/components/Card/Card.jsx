import React from 'react'
import { Link } from 'react-router-dom'
import ArrowIcon from '../common/ArrowIcon'


const Card = ({imagePath, title, description, actionArrow, height, width, productId, objectFit}) => {
  const cardContent = (
    <div className='flex flex-col p-4 h-full'>
        <div className={`h-[${height? height:'240px'}] w-full overflow-hidden rounded-lg border mb-3`}>
            <img 
                className={`w-full h-full ${objectFit || 'object-cover'} hover:scale-105 transition-transform duration-300 cursor-pointer`} 
                src={imagePath} 
                alt={title || 'Product'}
            />
        </div>
         <div className='flex flex-col items-center text-center flex-grow'>
            <p className='text-[16px] font-medium text-gray-800 line-clamp-2'>{title}</p>
            {description && <p className='text-[14px] text-gray-600 mt-1 font-semibold'>{description}</p>}
            {actionArrow && <span className='cursor-pointer mt-2'><ArrowIcon /></span>}
        </div>
    </div>
  );

  // Nếu có productId thì wrap trong Link, không thì hiển thị bình thường
  return productId ? (
    <Link to={`/product/${productId}`} className="block hover:shadow-lg transition-shadow rounded-lg">
      {cardContent}
    </Link>
  ) : (
    cardContent
  );
}

export default Card