import React from "react";
import axios from 'axios';
import waterIcon from '../../assets/water.png';
import affectionIcon from '../../assets/heart.png';

// 레벨 및 필요 경험치 계산 함수
const calculateLevelInfo = (exp) => {
  let currentLevel = 1;
  let requiredExpForNext = 100;
  let accumulatedExpForPrevLevels = 0;

  while (exp >= accumulatedExpForPrevLevels + requiredExpForNext) {
    accumulatedExpForPrevLevels += requiredExpForNext;
    currentLevel++;
    requiredExpForNext = currentLevel * 100;
  }

  const expWithinCurrentLevel = exp - accumulatedExpForPrevLevels;

  return {
    level: currentLevel,                 
    expInLevel: expWithinCurrentLevel,
    requiredExpNextLevel: requiredExpForNext
  };
};

const PlantStatusCard = ({ userInfo, setUserInfo, className }) => {
  if (!userInfo) {
    return <div className={`bg-white p-4 rounded-xl shadow-lg ${className}`}>Loading plant status...</div>;
  }

  const { plantName, plantExp, userWater, userAffection } = userInfo;
  const { level, expInLevel, requiredExpNextLevel } = calculateLevelInfo(plantExp);

  // 물 주기 핸들러
  const handleWaterPlant = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) throw new Error('로그인 토큰을 찾을 수 없습니다.');

      const response = await axios.post(
<<<<<<< HEAD
<<<<<<< HEAD
        'http://43.201.68.38:8080/api/user/plant/water',
=======
        'http://localhost:8080/api/user/plant/water',
>>>>>>> cad27c24f4c0e36f3b2cf20dbc2e1a6ebc5e11dd
=======
        'http://localhost:8080/api/user/plant/water',
>>>>>>> 1e287e9 (demo v1)
        null,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      setUserInfo(response.data);
      console.log('Watered plant! Updated Info:', response.data);
    } catch (error) {
      console.error('Failed to water plant:', error);
      alert(error.response?.data?.message || error.message || '물 주기에 실패했습니다.');
    }
  };

  // 애정 주기 핸들러
  const handleGiveAffection = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) throw new Error('로그인 토큰을 찾을 수 없습니다.');

      const response = await axios.post(
<<<<<<< HEAD
<<<<<<< HEAD
        'http://43.201.68.38:8080/api/user/plant/affection',
=======
        'http://localhost:8080/api/user/plant/affection',
>>>>>>> cad27c24f4c0e36f3b2cf20dbc2e1a6ebc5e11dd
=======
        'http://localhost:8080/api/user/plant/affection',
>>>>>>> 1e287e9 (demo v1)
        null,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      setUserInfo(response.data);
      console.log('Gave affection! Updated Info:', response.data);
    } catch (error) {
      console.error('Failed to give affection:', error);
      alert(error.response?.data?.message || error.message || '애정 주기에 실패했습니다.');
    }
  };

  return (
    <div className={`bg-white p-4 rounded-xl shadow-lg ${className}`}>
      {/* 식물 이름 및 레벨 */}
      <h3 className="font-bold text-lg mb-2">
        {plantName || '내 식물'} <span className="text-sm font-normal text-gray-500">Level {level}</span>
      </h3>

      {/* 경험치 바 */}
      <div className="mb-2">
        <div className="flex justify-between text-sm font-medium text-gray-700 mb-1">
          <span>경험치</span>
          <span>{expInLevel} / {requiredExpNextLevel}</span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2.5">
          <div
            className="bg-[#A755F8] h-2.5 rounded-full"
            style={{ width: `${(expInLevel / requiredExpNextLevel) * 100}%` }}
          ></div>
        </div>
      </div>

      {/* 보유 자원 */}
      <div className="flex justify-between text-sm text-gray-600 mb-4">
        <span><img src={waterIcon} alt="물 아이콘" className="inline h-4 w-4 mr-1"/> 물: {userWater}</span>
        <span><img src={affectionIcon} alt="애정 아이콘" className="inline h-4 w-4 mr-1"/> 애정: {userAffection}</span>
      </div>

      {/* 상호작용 버튼 */}
      <div className="flex gap-2">
        <button
          onClick={handleWaterPlant}
          className="flex-1 bg-blue-100 text-blue-700 font-semibold py-2 px-4 rounded-lg text-sm hover:bg-blue-200 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-1"
          disabled={userWater <= 0}
        >
          {/* 물 주기 아이콘 */}
          <img src={waterIcon} alt="" className="h-4 w-4"/> 물 주기
        </button>
        <button
          onClick={handleGiveAffection}
          className="flex-1 bg-pink-100 text-pink-700 font-semibold py-2 px-4 rounded-lg text-sm hover:bg-pink-200 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-1"
          disabled={userAffection <= 0}
        >
          {/* 애정 주기 아이콘 */}
          <img src={affectionIcon} alt="" className="h-4 w-4"/> 애정 주기
        </button>
      </div>
    </div>
  );
};

export default PlantStatusCard;