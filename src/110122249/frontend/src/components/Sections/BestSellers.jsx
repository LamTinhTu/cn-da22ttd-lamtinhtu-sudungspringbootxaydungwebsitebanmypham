import React, { useEffect, useState } from "react";
import SectionHeading from "./SectionHeading/SectionHeading";
import Card from "../Card/Card";
import Carousel from "react-multi-carousel";
import { responsive } from "../../utils/Section.constants.js";
import { getBestSellers } from "../../api/fetchBestSellers";
import { API_BASE_URL } from "../../api/constant";
import { CustomLeftArrow, CustomRightArrow } from "../common/CarouselArrows";
import "./NewArrivals.css";

const BestSellers = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const response = await getBestSellers(10);
        const productList = response?.data || [];
        
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
        console.error("Failed to fetch best sellers:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <>
        <SectionHeading title={"Sản Phẩm Bán Chạy"} />
        <div className="px-8 py-10 text-center text-gray-500">
          Đang tải sản phẩm bán chạy...
        </div>
      </>
    );
  }

  if (items.length === 0) {
    return (
      <>
        <SectionHeading title={"Sản Phẩm Bán Chạy"} />
        <div className="px-8 py-10 text-center text-gray-500">
          Không có sản phẩm bán chạy
        </div>
      </>
    );
  }

  return (
    <>
      <SectionHeading title={"Sản Phẩm Bán Chạy"} />
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
         {items.map((item, index) => (
           <Card 
             key={item?.id || index} 
             productId={item.id} 
             title={item.title} 
             description={item.description} 
             imagePath={item.imagePath}
             objectFit="object-contain"
           />
         ))}
      </Carousel>
    </>
  );
};

export default BestSellers;
