import { getToken } from "../utils/jwt-helper";
export const API_URLS = {
    GET_PRODUCTS:'/api/v1/products',
    GET_PRODUCT: (id) => `/api/v1/products/${id}`,
    GET_CATEGORIES:'/api/v1/brands',
    GET_CATEGORY: (id) => `/api/v1/brands/${id}`,
    LOGIN: '/api/v1/auth/login',
    REGISTER: '/api/v1/auth/register',
    FORGOT_PASSWORD: '/api/v1/auth/forgot-password',
    RESET_PASSWORD: '/api/v1/auth/reset-password',
    CHECK_PHONE: '/api/v1/auth/check-phone',
}

export const API_BASE_URL = 'http://localhost:5000';


export const getHeaders = ()=>{
    return {
        'Authorization':`Bearer ${getToken()}`
    }
}