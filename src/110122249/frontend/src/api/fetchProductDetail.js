import axios from "axios";
import { API_BASE_URL } from "./constant";

/**
 * Lấy chi tiết sản phẩm theo ID từ backend
 * @param {number} productId - ID của sản phẩm
 * @returns {Promise} - Promise chứa thông tin chi tiết sản phẩm
 */
export const getProductById = async (productId) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/api/v1/products/${productId}`);
        return response.data;
    } catch (error) {
        console.error('Error fetching product detail:', error);
        throw error;
    }
};
