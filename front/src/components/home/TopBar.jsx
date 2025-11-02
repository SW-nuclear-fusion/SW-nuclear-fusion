import React from "react";
import logo from "../../assets/logo.png";
import { BsBellFill } from "react-icons/bs";

const TopBar = () => {
  return (
    <div className="flex justify-between p-4">
      <img src={logo} className="w-[65px]" />

      <div className="flex">
        {/* 리워드 구매 페이지 */}
        <BsBellFill size={24} className="mx-2" />
        <BsBellFill size={24} className="mx-2" />
      </div>
    </div>
  );
};

export default TopBar;
