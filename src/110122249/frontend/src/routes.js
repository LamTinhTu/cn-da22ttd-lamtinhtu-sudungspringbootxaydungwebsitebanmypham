import { createBrowserRouter } from "react-router-dom";
import Shop from "./Shop";
import ProductListPage from "./pages/ProductListPage/ProductListPage";
import ShopApplicationWrapper from "./pages/ShopApplicationWrapper";
import ProductDetails from "./pages/ProductDetailPage/ProductDetails";
import AdminLayout from "./pages/Admin/AdminLayout";
import DashboardHome from "./pages/Admin/DashboardHome";
import Manufacturers from "./pages/Admin/Manufacturers";
import Products from "./pages/Admin/Products";
import Orders from "./pages/Admin/Orders";
import Users from "./pages/Admin/Users";
import Reviews from "./pages/Admin/Reviews";
import EmployeeLayout from "./pages/Employee/EmployeeLayout";
import EmployeeDashboardHome from "./pages/Employee/DashboardHome";
import EmployeeProducts from "./pages/Employee/Products";
import EmployeeOrders from "./pages/Employee/Orders";
import CustomerLookup from "./pages/Employee/CustomerLookup";
import CartPage from "./pages/Cart/CartPage";
import CheckoutPage from "./pages/Checkout/CheckoutPage";
import PaymentTransferPage from "./pages/Checkout/PaymentTransferPage";
import MyOrders from "./pages/Orders/MyOrders";
import AboutUs from "./pages/AboutUs";

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
                path: "/about-us",
                element: <AboutUs />
            },
            {
                path: "/cart",
                element: <CartPage />
            },
            {
                path: "/checkout",
                element: <CheckoutPage />
            },
            {
                path: "/payment-transfer",
                element: <PaymentTransferPage />
            },
            {
                path: "/my-orders",
                element: <MyOrders />
            },
            {
                path: "/search",
                element: <ProductListPage />
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
                path: "/haircare",
                element: <ProductListPage categoryType={"HAIRCARE"} />
            },
            {
                path: "/product/:productId",
                element: <ProductDetails />
            }
        ]
    },
    {
        path: "/admin",
        element: <AdminLayout />,
        children: [
            {
                path: "",
                element: <DashboardHome />
            },
            {
                path: "manufacturers",
                element: <Manufacturers />
            },
            {
                path: "products",
                element: <Products />
            },
            {
                path: "orders",
                element: <Orders />
            },
            {
                path: "users",
                element: <Users />
            },
            {
                path: "reviews",
                element: <Reviews />
            },
            {
                path: "settings",
                element: <div>Cài đặt (Đang phát triển)</div>
            }
        ]
    },
    {
        path: "/employee",
        element: <EmployeeLayout />,
        children: [
            {
                path: "",
                element: <EmployeeDashboardHome />
            },
            {
                path: "orders",
                element: <EmployeeOrders />
            },
            {
                path: "products",
                element: <EmployeeProducts />
            },
            {
                path: "customer-lookup",
                element: <CustomerLookup />
            }
        ]
    }
]);