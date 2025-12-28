import React from 'react';

export const CustomLeftArrow = ({ onClick, ...rest }) => {
  return (
    <button
      onClick={() => onClick()}
      className="absolute left-0 z-10 p-3 bg-white rounded-full shadow-lg hover:bg-pink-50 transition-all border border-pink-100 group"
      style={{ left: "20px" }}
      aria-label="Previous slide"
    >
      <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#ec4899" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="group-hover:scale-110 transition-transform">
        <path d="M15 18l-6-6 6-6" />
      </svg>
    </button>
  );
};

export const CustomRightArrow = ({ onClick, ...rest }) => {
  return (
    <button
      onClick={() => onClick()}
      className="absolute right-0 z-10 p-3 bg-white rounded-full shadow-lg hover:bg-pink-50 transition-all border border-pink-100 group"
      style={{ right: "20px" }}
      aria-label="Next slide"
    >
      <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#ec4899" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className="group-hover:scale-110 transition-transform">
        <path d="M9 18l6-6-6-6" />
      </svg>
    </button>
  );
};
