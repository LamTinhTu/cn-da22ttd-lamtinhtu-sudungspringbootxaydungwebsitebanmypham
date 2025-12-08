import React, { useState} from 'react'
import RangeSlider from 'react-range-slider-input';
import 'react-range-slider-input/dist/style.css';
import './PriceFilter.css';

const PriceFilter = () => {
    const [range,setRange] = useState({
        min:10,
        max:250
    })
    return (
        <div>
            <p className="text-[16px] text-black mt-5">Khoảng Giá</p>
            <RangeSlider className={'custom-range-slider'} min={0} max={5000} defaultValue={[range.min,range.max]} onInput = {(values)=> setRange({
            min:values[0],
            max:values[1]
        })}/>

        <div className='flex justify-between'>
            <div className='border rounded-lg h-8 mt-4 max-w-[50%] w-[40%] flex items-center'><input type='number' value={range?.min} className='outline-none px-4 text-gray-600' min={0} max="499" disabled placeholder='Thấp'/><p className='pl-2 text-gray-600'>VND</p> </div>
            <div className='border rounded-lg h-8 mt-4 max-w-[50%] w-[40%] flex items-center'><input type='number' value={range?.max} className='outline-none px-4 text-gray-600' min={0} max="500" disabled placeholder='Cao'/><p className='pl-2 text-gray-600'>VND</p> </div>
        </div>
        </div>
    )
}

export default PriceFilter;