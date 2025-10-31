import React from "react";
import { MdHomeFilled } from "react-icons/md";
import { IoMdAlarm } from "react-icons/io";
import { SlPuzzle } from "react-icons/sl";
import { RiPokerHeartsLine } from "react-icons/ri";
import { HiOutlineUserCircle } from "react-icons/hi2";
import { useLocation, useNavigate } from "react-router-dom";

const BottomNav = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const current = location.pathname.split("/")[2] || "home"; // 기본은 home으로 설정

  const menuList = [
    {
      id: 1,
      name: "home",
      menu: "홈",
      icon: <MdHomeFilled size={24} />,
    },
    {
      id: 2,
      name: "alarm",
      menu: "알림",
      icon: <IoMdAlarm size={24} />,
    },
    {
      id: 3,
      name: "mission",
      menu: "게임",
      icon: <SlPuzzle size={24} />,
    },
    {
      id: 4,
      name: "healthy",
      menu: "건강",
      icon: <RiPokerHeartsLine size={24} />,
    },
    {
      id: 5,
      name: "my",
      menu: "내 정보",
      icon: <HiOutlineUserCircle size={24} />,
    },
  ];

  const handleNavigate = (name) => navigate(`/main/${name}`);

  return (
    <div className="h-[100px] pb-1 flex justify-around items-center">
      {menuList.map((menu) => (
        <div
          key={menu.id}
          onClick={() => handleNavigate(menu.name)}
          className="min-w-[50px] min-h-[50px] grid place-items-center"
        >
          <div
            className={current === menu.name ? "text-black" : "text-[#777777]"}
          >
            {menu.icon}
          </div>
          <div
            className={current === menu.name ? "text-black" : "text-[#777777]"}
          >
            {menu.menu}
          </div>
        </div>
      ))}
    </div>
  );
};

export default BottomNav;
