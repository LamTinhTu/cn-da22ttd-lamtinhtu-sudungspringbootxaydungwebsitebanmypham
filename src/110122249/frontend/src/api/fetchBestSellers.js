import axios from 'axios';
import { API_BASE_URL } from './constant';

/**
 * Get best selling products
 * @param {number} limit - Number of products to return (default: 10)
 * @returns {Promise} Response from API
 */
export const getBestSellers = async (limit = 10) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/api/v1/products/best-sellers`, {
            params: { limit }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching best sellers:', error);
        throw error;
    }
};
