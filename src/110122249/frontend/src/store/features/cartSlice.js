import { createSlice } from "@reduxjs/toolkit";

const loadCartFromStorage = () => {
    try {
        const serializedState = localStorage.getItem('cart');
        if (serializedState === null) {
            return [];
        }
        return JSON.parse(serializedState);
    } catch (err) {
        return [];
    }
};

const saveCartToStorage = (cart) => {
    try {
        const serializedState = JSON.stringify(cart);
        localStorage.setItem('cart', serializedState);
    } catch {
        // ignore write errors
    }
};

const initialState = {
    cartItems: loadCartFromStorage(),
};

export const cartSlice = createSlice({
    name: 'cart',
    initialState,
    reducers: {
        addToCart: (state, action) => {
            const item = action.payload;
            const existingItem = state.cartItems.find(i => i.id === item.id);
            if (existingItem) {
                existingItem.quantity += item.quantity;
            } else {
                state.cartItems.push(item);
            }
            saveCartToStorage(state.cartItems);
        },
        removeFromCart: (state, action) => {
            const id = action.payload;
            state.cartItems = state.cartItems.filter(item => item.id !== id);
            saveCartToStorage(state.cartItems);
        },
        updateQuantity: (state, action) => {
            const { id, quantity } = action.payload;
            const item = state.cartItems.find(i => i.id === id);
            if (item) {
                item.quantity = Math.max(1, quantity);
            }
            saveCartToStorage(state.cartItems);
        },
        clearCart: (state) => {
            state.cartItems = [];
            saveCartToStorage(state.cartItems);
        }
    }
});

export const { addToCart, removeFromCart, updateQuantity, clearCart } = cartSlice.actions;

export const selectCartItems = (state) => state.cart.cartItems;
export const selectCartTotal = (state) => state.cart.cartItems.reduce((total, item) => total + item.price * item.quantity, 0);
export const selectCartCount = (state) => state.cart.cartItems.reduce((count, item) => count + item.quantity, 0);

export default cartSlice.reducer;
