import React, { useState, useEffect } from "react";
import MainHome from "../components/home/MainHome";
import TopBar from "../components/home/TopBar";
import axios from 'axios';

// 식물 색상별 배경색 Tailwind 클래스 매핑 (기존과 동일)
const backgroundColors = {
  purple: 'from-[#edd9ff] to-[#faf4ff]',
  blue:   'from-blue-100 to-blue-50',
  yellow: 'from-yellow-100 to-yellow-50',
  pink:   'from-pink-100 to-pink-50',
  default: 'from-gray-100 to-gray-50'
};

const HomePage = () => {
  // userInfo 상태를 null로 초기화 (로딩 상태 구분)
  const [userInfo, setUserInfo] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [bgColor, setBgColor] = useState(backgroundColors.default);

  useEffect(() => {
    const fetchUserInfo = async () => {
      setIsLoading(true);
      try {
        const token = localStorage.getItem('accessToken');
        if (!token) {
          console.error('로그인 토큰 없음');
          return;
        }

<<<<<<< HEAD
        const response = await axios.get('http://43.201.68.38:8080/api/user/me', {
=======
        const response = await axios.get('http://localhost:8080/api/user/me', {
>>>>>>> cad27c24f4c0e36f3b2cf20dbc2e1a6ebc5e11dd
          headers: { Authorization: `Bearer ${token}` }
        });

        setUserInfo(response.data);
        setBgColor(backgroundColors[response.data.plantColor] || backgroundColors.default);
      } catch (error) {
        console.error('사용자 정보 로딩 실패:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchUserInfo();
  }, []);

  return (
    <div className={`bg-gradient-to-t ${bgColor} h-full relative`}>
      <TopBar />
      {isLoading && <div className="p-4 text-center">사용자 정보를 불러오는 중...</div>}
      {!isLoading && userInfo && (
        <MainHome userInfo={userInfo} setUserInfo={setUserInfo} />
      )}
    </div>
  );
};
export default HomePage;