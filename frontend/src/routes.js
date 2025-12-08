import { createBrowserRouter } from "react-router-dom";
import Shop from "./Shop";
import ProductListPage from "./pages/ProductListPage/ProductListPage";
import ShopApplicationWrapper from "./pages/ShopApplicationWrapper";
import ProductDetails from "./pages/ProductDetailPage/ProductDetails";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <ShopApplicationWrapper />,
        children: [
            {
                path: "/",
                element: <Shop />
            },
            {
                path: "/makeup",
                element: <ProductListPage categoryType={"MAKEUP"} />
            },
            {
                path: "/skincare",
                element: <ProductListPage categoryType={"SKINCARE"} />
            },
            {
                path: "/product/:productId",
                element: <ProductDetails />
            }
        ]
    },
]);