import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { GoArrowLeft } from "react-icons/go";
import waterIcon from '../assets/water.png';
import affectionIcon from '../assets/heart.png';

export default function RewardPage() {
  const navigate = useNavigate();
  const location = useLocation(); // 이전 페이지에서 전달된 state 데이터 받기
  
  // 이전 페이지(TodayAlarm)에서 전달한 보상 정보
  const rewardInfo = location.state?.reward;

  if (!rewardInfo) {
    React.useEffect(() => {
      console.warn("No reward info found, redirecting to alarm page.");
      navigate('/main/alarm');
    }, [navigate]);
    return null;
  }

  // 보상 타입에 따른 이미지와 텍스트 설정
  const isWaterReward = rewardInfo.type === 'water';
  const rewardImage = isWaterReward ? waterIcon : affectionIcon;
  const rewardText = isWaterReward ? '물' : '애정';

  // 닫기 버튼 핸들러
  const handleClose = () => {
    console.log(location.page);
    if (location.page === "quiz")
      navigate('/main/mission');
    if (location.page === "alarm")
      navigate('/main/alarm');
  };

  return (
    // 디자인에 맞는 배경색 적용
    <div className="bg-gradient-to-t from-[#F5E9FF] from-20% to-[#E1BEFF] h-full flex flex-col">
      {/* 상단바 (뒤로가기, 안내) */}
      <header className="h-[48px] flex justify-between items-center px-3 flex-shrink-0 text-gray-700">
        <GoArrowLeft size={24} onClick={handleClose} className="cursor-pointer"/>
      </header>

      {/* 메인 콘텐츠 */}
      <main className="flex-grow flex flex-col items-center justify-center text-center px-4">
        <h2 className="text-3xl font-semibold mb-10">
          복약 시간을 <br />잘 지켜주셨습니다!!!!!
        </h2>

        {/* 보상 표시 카드 */}
        <div className="bg-white w-[160px] h-[160px] rounded-3xl flex flex-col items-center justify-center shadow-lg mb-10">
          <img src={rewardImage} alt={rewardText} className="w-[80px] h-[80px] mb-2 object-contain" />
          <div className="text-2xl font-semibold">{rewardText} {rewardInfo.amount}개</div>
        </div>
      </main>

      {/* 하단 버튼 */}
      <footer className="flex gap-3 justify-center pb-16 px-4 flex-shrink-0">
        <button
          onClick={handleClose}
          className="w-[160px] bg-white py-3 rounded-xl shadow cursor-pointer text-gray-700 hover:bg-gray-50"
        >
          닫기sdafsdf
        </button>
      </footer>
    </div>
  );
}