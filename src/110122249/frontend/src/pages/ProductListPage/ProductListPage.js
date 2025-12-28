import React, { useEffect, useMemo, useState } from "react";
import { useSearchParams, useNavigate } from 'react-router-dom';
import content from '../../data/content.json';
import ProductCard from './ProductCard';
import { getAllProducts } from "../../api/fetchProducts";
import { API_BASE_URL } from "../../api/constant";
import Footer from '../../components/Footer/Footer';

const categories = content?.categories;

const ProductListPage = ({categoryType}) => {
  const [products, setProducts] = useState([]);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const keyword = searchParams.get('q');

  const categoryContent = useMemo(()=>{
    return categories?.find((category)=> category.code === categoryType);
  },[categoryType]);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        // If we have a keyword, use it for backend search
        // Fetch more items to ensure we get enough results
        const params = keyword ? { keyword, size: 100 } : { size: 100 };
        
        // Add category filter if categoryType is specified
        if (categoryType) {
          params.category = categoryType;
        }
        
        const response = await getAllProducts(params);
        const allProducts = response?.data?.items || [];
        
        // Filter only SELLING products
        const sellingProducts = allProducts.filter(product => product.productStatus === 'SELLING');

        const mappedProducts = sellingProducts.map(product => {
           let imageUrl = product.images?.[0]?.imageURL;
           if (imageUrl && !imageUrl.startsWith('http')) {
               imageUrl = `${API_BASE_URL}${imageUrl}`;
           }
           
           return {
             id: product.productId,
             title: product.productName,
             description: product.productDescription,
             price: product.productPrice,
             thumbnail: imageUrl || require('../../assets/img/toners.jpg'), // Fallback
             category_id: categoryContent?.id // Mock category id for compatibility if needed
           };
        });

        setProducts(mappedProducts);
      } catch (error) {
        console.error("Error fetching products:", error);
      }
    };

    fetchProducts();
  }, [categoryType, categoryContent, keyword]);

    return (
        <>
            <div className="container mx-auto px-4 py-8">
                <div className="w-full">
                    {keyword && (
                    <p className='text-black text-2xl font-bold mb-6 text-center'>
                      Kết quả tìm kiếm cho: "{keyword}"
                    </p>
                    )}
                    {/* Products */}
                    <div className='grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6'>
                      {products?.map((item,index)=> (
                      <ProductCard key={item?.id || index} {...item}/>
                      ))}
                    </div>
                </div>
            </div>
            <Footer />
        </>
    )
}

export default ProductListPage;