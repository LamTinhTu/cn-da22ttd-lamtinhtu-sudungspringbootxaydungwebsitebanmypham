import axios from 'axios';
import { API_BASE_URL } from './constant';

/**
 * Get dashboard statistics
 * @returns {Promise} Response from API
 */
export const getDashboardStats = async () => {
    try {
        const token = localStorage.getItem('authToken');
        const response = await axios.get(`${API_BASE_URL}/api/v1/dashboard/stats`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching dashboard stats:', error);
        throw error;
    }
};

/**
 * Get recent orders
 * @param {number} limit - Number of orders to return (default: 5)
 * @returns {Promise} Response from API
 */
export const getRecentOrders = async (limit = 5) => {
    try {
        const token = localStorage.getItem('authToken');
        const response = await axios.get(`${API_BASE_URL}/api/v1/dashboard/recent-orders`, {
            params: { limit },
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching recent orders:', error);
        throw error;
    }
};

/**
 * Get top selling products
 * @param {number} limit - Number of products to return (default: 5)
 * @returns {Promise} Response from API
 */
export const getTopProducts = async (limit = 5) => {
    try {
        const token = localStorage.getItem('authToken');
        const response = await axios.get(`${API_BASE_URL}/api/v1/dashboard/top-products`, {
            params: { limit },
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching top products:', error);
        throw error;
    }
};
