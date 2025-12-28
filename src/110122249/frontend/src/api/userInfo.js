import axios from "axios";
import { API_BASE_URL, getHeaders } from "./constant";

export const fetchUserDetails = async (userId)=>{
    const url = API_BASE_URL + `/api/v1/users/${userId}`;
    try{
        const response = await axios(url,{
            method:"GET",
            headers:getHeaders()
        });
        return response?.data;
    }
    catch(err){
        throw new Error(err);
    }
}

// Address APIs are not supported in current backend version
export const addAddressAPI = async (data)=>{
    // const url = API_BASE_URL + '/api/address';
    console.warn("addAddressAPI not implemented in backend");
    return null;
}

export const deleteAddressAPI = async (id)=>{
    // const url = API_BASE_URL + `/api/address/${id}`;
    console.warn("deleteAddressAPI not implemented in backend");
    return null;
}

export const fetchOrderAPI = async (userId)=>{
    const url = API_BASE_URL + `/api/v1/orders/user/${userId}`;
    try{
        const response = await axios(url,{
            method:"GET",
            headers:getHeaders()
        });
        return response?.data;
    }
    catch(err){
        throw new Error(err);
    }
}

export const cancelOrderAPI = async (id)=>{
    const url = API_BASE_URL + `/api/v1/orders/${id}/cancel`;
    try{
        const response = await axios(url,{
            method:"PUT",
            headers:getHeaders()
        });
        return response?.data;
    }
    catch(err){
        throw new Error(err);
    }
}