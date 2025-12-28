import axios from "axios";
import { API_BASE_URL, getHeaders } from "./constant";

export const getAllProducts = async (params = {}) => {
    const url = `${API_BASE_URL}/api/v1/products`;
    try {
        const response = await axios.get(url, { 
            params,
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching products:", error);
        throw error;
    }
};

export const getProductById = async (productId) => {
    const url = `${API_BASE_URL}/api/v1/products/${productId}`;
    try {
        const response = await axios.get(url, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching product:", error);
        throw error;
    }
};

export const createProduct = async (productData) => {
    const url = `${API_BASE_URL}/api/v1/products`;
    try {
        const response = await axios.post(url, productData, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error creating product:", error);
        throw error;
    }
};

export const updateProduct = async (productId, productData) => {
    const url = `${API_BASE_URL}/api/v1/products/${productId}`;
    try {
        const response = await axios.put(url, productData, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error updating product:", error);
        throw error;
    }
};

export const deleteProduct = async (productId) => {
    const url = `${API_BASE_URL}/api/v1/products/${productId}`;
    try {
        const response = await axios.delete(url, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error deleting product:", error);
        throw error;
    }
};
