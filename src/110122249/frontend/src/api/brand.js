import axios from "axios";
import { API_BASE_URL, getHeaders } from "./constant";

export const getAllBrands = async (params = {}) => {
    const url = `${API_BASE_URL}/api/v1/brands`;
    try {
        const response = await axios.get(url, { params });
        return response.data;
    } catch (error) {
        console.error("Error fetching brands:", error);
        throw error;
    }
};

export const createBrand = async (brandData) => {
    const url = `${API_BASE_URL}/api/v1/brands`;
    try {
        const response = await axios.post(url, brandData, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error creating brand:", error);
        throw error;
    }
};

export const updateBrand = async (brandId, brandData) => {
    const url = `${API_BASE_URL}/api/v1/brands/${brandId}`;
    try {
        const response = await axios.put(url, brandData, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error updating brand:", error);
        throw error;
    }
};

export const deleteBrand = async (brandId) => {
    const url = `${API_BASE_URL}/api/v1/brands/${brandId}`;
    try {
        const response = await axios.delete(url, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error deleting brand:", error);
        throw error;
    }
};
