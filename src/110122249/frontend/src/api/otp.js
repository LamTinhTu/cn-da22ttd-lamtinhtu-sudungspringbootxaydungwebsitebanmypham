import axios from 'axios';
import { API_BASE_URL } from './constant';

export const sendOTP = async (phoneNumber) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/api/v1/auth/send-otp`, {
            phoneNumber
        });
        return response;
    } catch (error) {
        throw error;
    }
};

export const verifyOTP = async (phoneNumber, otp) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/api/v1/auth/verify-otp`, {
            phoneNumber,
            otp
        });
        return response;
    } catch (error) {
        throw error;
    }
};
