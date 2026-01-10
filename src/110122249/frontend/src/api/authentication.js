import axios from "axios";
import { API_BASE_URL, API_URLS } from "./constant"


export const loginAPI = async (body)=>{
    const url = API_BASE_URL + API_URLS.LOGIN;
    console.log('Login API - URL:', url);
    console.log('Login API - Request body:', body);
    try{
        const response = await axios(url,{
            method:"POST",
            data:body,
            headers: {
                'Content-Type': 'application/json'
            }
        });
        console.log('Login API - Response:', response);
        return response?.data;
    }
    catch(err){
        console.error('Login API - Error details:', {
            message: err.message,
            response: err.response?.data,
            status: err.response?.status,
            statusText: err.response?.statusText
        });
        throw err;
    }
}

export const registerAPI = async (body)=>{
    const url = API_BASE_URL + API_URLS.REGISTER;
    try{
        const response = await axios(url,{
            method:"POST",
            data:body
        });
        return response;
    }
    catch(err){
        throw err;
    }
}

export const resetPasswordAPI = async (body) => {
    const url = API_BASE_URL + API_URLS.RESET_PASSWORD;
    try {
        const response = await axios(url, {
            method: "POST",
            data: body,
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return response;
    } catch (err) {
        throw err;
    }
}

export const checkPhoneExistsAPI = async (body) => {
    const url = API_BASE_URL + API_URLS.CHECK_PHONE;
    try {
        const response = await axios(url, {
            method: "POST",
            data: body,
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return response;
    } catch (err) {
        throw err;
    }
}

export const forgotPasswordAPI = async (body) => {
    const url = API_BASE_URL + API_URLS.FORGOT_PASSWORD;
    try {
        const response = await axios(url, {
            method: "POST",
            data: body
        });
        return response;
    } catch (err) {
        throw err;
    }
}

export const verifyAPI = async (body)=>{
    const url = API_BASE_URL + '/api/auth/verify';
    try{
        const response = await axios(url,{
            method:"POST",
            data:body
        });
        return response?.data;
    }
    catch(err){
        throw new Error(err);
    }
}