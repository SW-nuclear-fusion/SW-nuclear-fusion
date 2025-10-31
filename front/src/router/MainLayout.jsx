import React from 'react'
import { Outlet } from 'react-router-dom'
import BottomNav from '../components/BottomNav'

const MainLayout = () => {
  return (
    <div className="flex flex-col h-full">
      {/* 페이지 내용 */}
      <div className="flex-1 overflow-auto">
        <Outlet />
      </div>

      {/* 하단 네비게이션 */}
      <BottomNav />
    </div>
  )
}

export default MainLayout