import React from "react"
import HeroSection from "./components/HeroSection/HeroSection"
import NewArrivals from "./components/Sections/NewArrivals"
import BestSellers from "./components/Sections/BestSellers"
import 'react-multi-carousel/lib/styles.css';
import Footer from "./components/Footer/Footer";
import FallingPetals from "./components/common/FallingPetals";

const Shop = () => {
    return (
       <>
       <FallingPetals />
       <HeroSection />
       <NewArrivals />
       <BestSellers />
       <Footer />
       </>
    )
}

export default Shop
