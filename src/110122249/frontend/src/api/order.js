import axios from "axios";
import { API_BASE_URL, getHeaders } from "./constant";

export const getAllOrders = async (params = {}) => {
    const url = `${API_BASE_URL}/api/v1/orders`;
    try {
        const response = await axios.get(url, { 
            params,
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching orders:", error);
        throw error;
    }
};

export const getOrderById = async (orderId) => {
    const url = `${API_BASE_URL}/api/v1/orders/${orderId}`;
    try {
        const response = await axios.get(url, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching order:", error);
        throw error;
    }
};

export const updateOrderStatus = async (orderId, status) => {
    const url = `${API_BASE_URL}/api/v1/orders/${orderId}/status`;
    try {
        const response = await axios.put(url, null, {
            params: { status },
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error updating order status:", error);
        throw error;
    }
};

export const updatePaymentStatus = async (orderId, isPaid) => {
    const url = `${API_BASE_URL}/api/v1/orders/${orderId}/payment-status`;
    try {
        const response = await axios.put(url, null, {
            params: { isPaid },
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error updating payment status:", error);
        throw error;
    }
};

export const cancelOrder = async (orderId) => {
    const url = `${API_BASE_URL}/api/v1/orders/${orderId}/cancel`;
    try {
        const response = await axios.put(url, null, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error cancelling order:", error);
        throw error;
    }
};

export const deleteOrder = async (orderId) => {
    const url = `${API_BASE_URL}/api/v1/orders/${orderId}`;
    try {
        const response = await axios.delete(url, {
            headers: getHeaders()
        });
        return response.data;
    } catch (error) {
        console.error("Error deleting order:", error);
        throw error;
    }
};

export const placeOrderAPI = async (data)=>{
    const url = API_BASE_URL + '/api/v1/orders';
    try{
        const response = await axios(url,{
            method:"POST",
            data:data,
            headers:getHeaders()
        });
        return response?.data;
    }
    catch(err){
        console.error("PlaceOrder API error:", err.response || err);
        throw err;
    }
}

export const confirmPaymentAPI = async (data)=>{
    // data should contain orderId and paymentMethod
    const url = API_BASE_URL + `/api/v1/orders/${data?.orderId}/payment?paymentMethod=${data?.paymentMethod}`;
    try{
        const response = await axios(url,{
            method:"PUT",
            data:{}, // PUT request usually expects body, but this endpoint uses params
            headers:getHeaders()
        });
        return response?.data;
    }
    catch(err){
        throw new Error(err);
    }
}