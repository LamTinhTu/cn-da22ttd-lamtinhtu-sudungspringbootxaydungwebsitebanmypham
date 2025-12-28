import { configureStore } from '@reduxjs/toolkit';
import cartReducer from './features/cartSlice';
import commonReducer from './features/common';

export const store = configureStore({
  reducer: {
    cart: cartReducer,
    common: commonReducer
  },
});
