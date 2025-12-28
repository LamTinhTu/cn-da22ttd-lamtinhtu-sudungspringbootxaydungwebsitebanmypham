import axios from "axios";
import { API_BASE_URL, getHeaders } from "./constant";

export const getAllUsers = async (params = {}) => {
    const url = `${API_BASE_URL}/api/v1/users`;
    try {
        const response = await axios.get(url, { 
            params,
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching users:", error);
        throw error;
    }
};

export const getUserById = async (userId) => {
    const url = `${API_BASE_URL}/api/v1/users/${userId}`;
    try {
        const response = await axios.get(url, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching user:", error);
        throw error;
    }
};

export const createUser = async (userData) => {
    const url = `${API_BASE_URL}/api/v1/users`;
    try {
        const response = await axios.post(url, userData, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error creating user:", error);
        throw error;
    }
};

export const updateUser = async (userId, userData) => {
    const url = `${API_BASE_URL}/api/v1/users/${userId}`;
    try {
        const response = await axios.put(url, userData, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error updating user:", error);
        throw error;
    }
};

export const deleteUser = async (userId) => {
    const url = `${API_BASE_URL}/api/v1/users/${userId}`;
    try {
        const response = await axios.delete(url, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error deleting user:", error);
        throw error;
    }
};
