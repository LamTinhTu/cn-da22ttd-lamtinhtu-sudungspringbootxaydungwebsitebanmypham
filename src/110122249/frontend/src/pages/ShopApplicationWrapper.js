import React from 'react'
import Navigation from '../components/Navigation/Navigation'
import { Outlet } from 'react-router-dom'
import PageTitleUpdater from '../components/common/PageTitleUpdater'


const ShopApplicationWrapper = () => {

  return (
    <div>
        <PageTitleUpdater />
        <Navigation />
        <div className="pt-[80px]">
            <Outlet />
        </div>
    </div>
  )
}

export default ShopApplicationWrapper