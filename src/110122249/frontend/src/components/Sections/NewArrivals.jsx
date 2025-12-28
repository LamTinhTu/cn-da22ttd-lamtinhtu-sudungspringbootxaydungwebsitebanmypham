import React, { useEffect, useState } from "react";
import SectionHeading from "./SectionHeading/SectionHeading";
import Card from "../Card/Card";
import Carousel from "react-multi-carousel";
import { responsive } from "../../utils/Section.constants.js";
import { getAllProducts } from "../../api/fetchProducts";
import { API_BASE_URL } from "../../api/constant";
import { CustomLeftArrow, CustomRightArrow } from "../common/CarouselArrows";
import "./NewArrivals.css";

const NewArrivals = () => {
  const [items, setItems] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await getAllProducts();
        const productList = response?.data?.items || [];
        
        // Filter only SELLING products
        const sellingProducts = productList.filter(product => product.productStatus === 'SELLING');
        
        const mappedItems = sellingProducts.map(product => {
          let imageUrl = product.images?.[0]?.imageURL;
          if (imageUrl && !imageUrl.startsWith('http')) {
              imageUrl = `${API_BASE_URL}${imageUrl}`;
          }

          return {
            id: product.productId,
            title: product.productName,
            description: `${product.productPrice?.toLocaleString('vi-VN')} VND`,
            imagePath: imageUrl || require('../../assets/img/toners.jpg'), // Fallback image
          };
        });
        setItems(mappedItems);
      } catch (error) {
        console.error("Failed to fetch new arrivals:", error);
      }
    };

    fetchData();
  }, []);

  return (
    <>
      <SectionHeading title={"Sản Phẩm Mới"} />
      <Carousel
        responsive={responsive}
        autoPlay={false}
        swipeable={true}
        draggable={false}
        showDots={false}
        infinite={true}
        partialVisible={false}
        itemClass={'react-slider-custom-item'}
        className='px-8'
        customLeftArrow={<CustomLeftArrow />}
        customRightArrow={<CustomRightArrow />}
      >
         {items && items?.map((item,index)=> <Card key={item?.id || index} productId={item.id} title={item.title} description={item.description} imagePath={item.imagePath} objectFit="object-contain"/>)}
      </Carousel>
    </>
  );
};

export default NewArrivals;
