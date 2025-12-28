import React from 'react';

const SectionHeading = ({title}) => {
  return (
    <div className='flex justify-center my-8'>
        <h2 className='text-3xl font-bold text-gray-800 uppercase tracking-wide relative pb-2 border-b-4 border-pink-500'>
            {title}
        </h2>
    </div>
  )
}

export default SectionHeading;