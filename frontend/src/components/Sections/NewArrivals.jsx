import React from "react";
import SectionHeading from "./SectionHeading/SectionHeading";
import Card from "../Card/Card";
import Carousel from "react-multi-carousel";
import { responsive } from "../../utils/Section.constants.js";
import "./NewArrivals.css";

const items = [
  { title: "Toners", imagePath:require('../../assets/img/toners.jpg')},
  { title: "Kem Chống Nắng", imagePath:require('../../assets/img/kcn.jpg')},
  { title: "Son", imagePath:require('../../assets/img/son.jpg')},
  { title: "Body Lotion", imagePath:require('../../assets/img/bodylotion.jpg')},
  { title: "Nước Hoa", imagePath:require('../../assets/img/nuochoa.jpg')},
];

const NewArrivals = () => {
  return (
    <>
      <SectionHeading title={"Sản Phẩm Mới"} />
      <Carousel
        responsive={responsive}
        autoPlay={false}
        swipeable={true}
        draggable={false}
        showDots={false}
        infinite={false}
        partialVisible={false}
        itemClass={'react-slider-custom-item'}
        className='px-8'
      >
         {items && items?.map((item,index)=> <Card key={item?.title +index} title={item.title} imagePath={item.imagePath}/>)}
      </Carousel>
    </>
  );
};

export default NewArrivals;
