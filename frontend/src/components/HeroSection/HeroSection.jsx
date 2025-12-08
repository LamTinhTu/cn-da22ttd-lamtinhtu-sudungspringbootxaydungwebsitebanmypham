import React from 'react'
import HeroImg from '../../assets/img/hero-img.png'

const HeroSection = () => {
  return (
    <div className='relative flex items-center bg-cover flext-start bg-center text-left h-svh w-full' style={{backgroundImage
    : `url(${HeroImg})`}}>
     {/*  <div className='absolte top-0 left-0 right-0'>
        <main className='px-10 lg:px-24 z-10'>
                <div className='text-left'>
                    <h2 className='text-2xl text-white'>T-shirt / Tops</h2>
                </div>
                <p className='mt-3 text-white sm:mt-5 sm:max-w-xl text-6xl'>
                Summer 
                Value Pack
                </p>
                <p className='mt-3 text-white sm:mt-5 sm:max-w-xl text-2xl'>
                cool / colorful / comfy
                </p>
                <button className='border rounded mt-6 border-black hover:bg-white hover:text-black hover:border-black text-white bg-black w-44 h-12'>
                    Shop Now
                </button>
            </main>
      </div> */}
    </div>
  )
}

export default HeroSection