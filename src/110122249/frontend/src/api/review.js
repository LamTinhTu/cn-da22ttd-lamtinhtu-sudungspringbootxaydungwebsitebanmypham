import axios from "axios";
import { API_BASE_URL, getHeaders } from "./constant";

export const getAllReviews = async (params = {}) => {
    const url = `${API_BASE_URL}/api/v1/reviews`;
    try {
        const response = await axios.get(url, { 
            params,
            headers: getHeaders() 
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching all reviews:", error);
        throw error;
    }
};

export const getReviewsByProduct = async (productId) => {
    const url = `${API_BASE_URL}/api/v1/reviews/product/${productId}`;
    try {
        const response = await axios.get(url);
        return response.data;
    } catch (error) {
        console.error("Error fetching reviews:", error);
        throw error;
    }
};

export const getProductRating = async (productId) => {
    const url = `${API_BASE_URL}/api/v1/reviews/product/${productId}/rating`;
    try {
        const response = await axios.get(url);
        return response.data;
    } catch (error) {
        console.error("Error fetching product rating:", error);
        throw error;
    }
};

export const getMyReviews = async () => {
    const url = `${API_BASE_URL}/api/v1/reviews/my-reviews`;
    try {
        const response = await axios.get(url, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching user reviews:", error);
        throw error;
    }
};

export const createReview = async (reviewData) => {
    const url = `${API_BASE_URL}/api/v1/reviews`;
    try {
        const response = await axios.post(url, reviewData, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error creating review:", error);
        throw error;
    }
};

export const updateReview = async (reviewId, reviewData) => {
    const url = `${API_BASE_URL}/api/v1/reviews/${reviewId}`;
    try {
        const response = await axios.put(url, reviewData, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error updating review:", error);
        throw error;
    }
};

export const deleteReview = async (reviewId) => {
    const url = `${API_BASE_URL}/api/v1/reviews/${reviewId}`;
    try {
        const response = await axios.delete(url, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error deleting review:", error);
        throw error;
    }
};
