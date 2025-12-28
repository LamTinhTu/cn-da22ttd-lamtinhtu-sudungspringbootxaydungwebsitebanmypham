import React from 'react'
import { API_BASE_URL } from '../../api/constant';

const HeroSection = () => {
  return (
    <div className='relative flex items-center bg-cover flext-start bg-center text-left h-svh w-full' style={{backgroundImage
    : `url(${API_BASE_URL}/uploads/6.png)`}}>
      <div className='absolute top-0 left-0 right-0'>
        <main className='px-10 lg:px-24 z-10'>
            </main>
      </div>
    </div>
  )
}

export default HeroSection