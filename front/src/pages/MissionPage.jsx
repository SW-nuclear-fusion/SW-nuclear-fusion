import React from 'react';
import { useNavigate } from 'react-router-dom';
import { FaBrain, FaMapMarkerAlt } from 'react-icons/fa';

export default function MissionPage() {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col h-screen">
      {/* 상단 헤더 */}
      <header className="bg-white shadow p-4 flex items-center justify-center sticky top-0 z-20">
         <h1 className="text-lg font-semibold mx-auto">미션</h1>
      </header>
      
      {/* 메인 콘텐츠 */}
      <main className="flex-grow overflow-y-auto p-4 space-y-4">
        {/* 퀴즈 미션 카드 */}
        <div
          onClick={() => navigate('/main/quiz')} // 퀴즈 페이지로 이동
          className="bg-purple-100 border border-purple-200 rounded-lg p-6 flex items-center gap-4 cursor-pointer hover:shadow-lg transition-shadow"
        >
          <FaBrain size={30} className="text-purple-600 flex-shrink-0" />
          <div>
            <h2 className="font-bold text-lg text-purple-800">일일 치매예방 퀴즈</h2>
            <p className="text-sm text-purple-700">하루 한 번 퀴즈를 풀고 뇌 건강을 챙기세요!</p>
          </div>
        </div>

        {/* 방문 미션 카드 */}
        <div
          onClick={() => navigate('/main/mission/locations')} // 방문 미션 목록으로 이동
          className="bg-green-100 border border-green-200 rounded-lg p-6 flex items-center gap-4 cursor-pointer hover:shadow-lg transition-shadow"
        >
          <FaMapMarkerAlt size={30} className="text-green-600 flex-shrink-0" />
          <div>
            <h2 className="font-bold text-lg text-green-800">방문 인증 미션</h2>
            <p className="text-sm text-green-700">지정된 장소를 방문하고 보상을 받으세요!</p>
          </div>
        </div>
      </main>
    </div>
  );
}