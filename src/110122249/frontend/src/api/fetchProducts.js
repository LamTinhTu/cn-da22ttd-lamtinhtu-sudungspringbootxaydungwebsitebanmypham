import axios from "axios";
import { API_BASE_URL, API_URLS } from "./constant"


export const getAllProducts = async (params = {})=>{
    let url = API_BASE_URL + API_URLS.GET_PRODUCTS;
    const queryParams = [];
    
    // Support legacy arguments (id, typeId) if params is not an object
    if (typeof params !== 'object') {
        // This handles the case where it might be called as getAllProducts(id, typeId)
        // But based on usage in ProductListPage.js: getAllProducts()
        // And usage in other files?
    } else {
        if (params.keyword) queryParams.push(`keyword=${encodeURIComponent(params.keyword)}`);
        if (params.brandId) queryParams.push(`brandId=${params.brandId}`);
        if (params.minPrice) queryParams.push(`minPrice=${params.minPrice}`);
        if (params.maxPrice) queryParams.push(`maxPrice=${params.maxPrice}`);
        if (params.page) queryParams.push(`page=${params.page}`);
        if (params.size) queryParams.push(`size=${params.size}`);
        if (params.sort) queryParams.push(`sort=${params.sort}`);
        if (params.category) queryParams.push(`category=${params.category}`);
    }

    if(queryParams.length > 0){
        url += '?' + queryParams.join('&');
    }

    try{
        const result = await axios(url,{
            method:"GET"
        });
        return result?.data;
    }
    catch(err){
        console.error(err);
    }
}

export const getProductBySlug = async (slug)=>{
    const url = API_BASE_URL + API_URLS.GET_PRODUCTS + `?slug=${slug}`;
    try{
        const result = await axios(url,{
            method:"GET",
        });
        return result?.data?.[0];
    }
    catch(err){
        console.error(err);
    }
}