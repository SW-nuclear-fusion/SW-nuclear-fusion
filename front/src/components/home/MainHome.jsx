import React from "react";
import Purplelv1 from "../../assets/level1purple.png";
import Bluelv1 from "../../assets/level1blue.png";
import Yellowlv1 from "../../assets/level1purple.png";
import Pinklv1 from "../../assets/level1pink.png";
import Purplelv2 from "../../assets/level2purple.png";
import Bluelv2 from "../../assets/level2blue.png";
import Yellowlv2 from "../../assets/level2purple.png";
import Pinklv2 from "../../assets/level2pink.png";
import Purplelv3 from "../../assets/level3purple.png";
import Bluelv3 from "../../assets/level3blue.png";
import Yellowlv3 from "../../assets/level3purple.png";
import Pinklv3 from "../../assets/level3pink.png";
import defaultPlant from "../../assets/level1purple.png";
import PlantStatusCard from "./PlantStatusCard";
import { IoIosArrowForward } from "react-icons/io";

// 식물 색상-이미지 매핑
const plantImageMap = {
  purple: { 1: Purplelv1, 2: Purplelv2, 3: Purplelv3 },
  blue: { 1: Bluelv1, 2: Bluelv2, 3: Bluelv3 },
  yellow: { 1: Yellowlv1, 2: Yellowlv2, 3: Yellowlv3 },
  pink: { 1: Pinklv1, 2: Pinklv2, 3: Pinklv3 },
};

const MainHome = ({ userInfo, setUserInfo }) => {

  const calculateLevelInfo = (exp) => {
    let currentLevel = 1;
    let requiredExpForNext = 100;
    let accumulatedExpForPrevLevels = 0;

    while (exp >= accumulatedExpForPrevLevels + requiredExpForNext) {
      accumulatedExpForPrevLevels += requiredExpForNext;
      currentLevel++;
      requiredExpForNext = currentLevel * 100;
    }

    return currentLevel;
  };

  const lev = calculateLevelInfo(userInfo.plantExp || 0);

  const plantColor = userInfo.plantColor || 'purple';
  const plantImage = plantImageMap[plantColor][lev];
  const plantName = userInfo.plantName || "내 식물";
  const userName = userInfo.name || "사용자";

  return (
    <div className="mx-4 mt-2 relative">
      {/* DTO의 name 필드 사용 */}
      <div className="text-2xl">
        <span className="font-bold">{userName}님,</span>
        반가워요!
        <br />
        오늘 기분은 어떠신가요?
      </div>

      {/* 치매 검사 버튼 */}
      <div className="bg-white w-auto inline-block px-4 h-10 mt-2 rounded-xl flex items-center justify-center cursor-pointer shadow">
        <div className="flex text-md items-center gap-1">
          <div>치매 검사 확인하기</div>
          <IoIosArrowForward />
        </div>
      </div>

      {/* 식물 이미지 표시 */}
      <div className="flex justify-center mt-8">
        <img
          src={plantImage}
          alt={plantName}
          className="h-64 z-0"
        />
      </div>

      <div className="mt-4 relative z-10">
        <PlantStatusCard userInfo={userInfo} setUserInfo={setUserInfo} />
      </div>
    </div>
  );
};

export default MainHome;