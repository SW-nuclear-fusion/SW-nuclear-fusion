import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import AuthLayout from '../components/auth/AuthLayout';
import plantPurple from '../assets/purple.png';
import plantBlue from '../assets/blue.png';
import plantYellow from '../assets/blue.png';
import plantPink from '../assets/pink.png';

// 식물 색상과 import된 이미지 변수 매핑
const plantImages = {
  purple: plantPurple,
  blue: plantBlue,
  yellow: plantYellow, // 실제 파일 이름 확인 필요
  pink: plantPink,
};

// 기본 이미지 (색상 매핑 실패 시 보여줄 이미지)
const defaultPlantImage = plantPurple; // 예시로 보라색 사용

export default function PlantNamingPage() {
  const navigate = useNavigate();
  const { color } = useParams(); // URL에서 식물 색상 가져오기
  const [plantName, setPlantName] = useState('');

  const handleConfirm = () => {
    if (!plantName.trim()) {
      return alert('식물 이름을 입력해주세요.');
    }
    navigate(`/confirm-plant/${color}/${plantName}`); // 다음 페이지로 색상과 이름 전달
  };

  // 표시할 이미지 결정 (해당 색상 이미지가 없으면 기본 이미지)
  const currentPlantImage = plantImages[color] || defaultPlantImage;

  return (
    <AuthLayout
      title="식물 이름을 입력해주세요"
      onNext={handleConfirm}
      nextButtonText="확인"
      // 뒤로가기 버튼은 자동으로 이전 페이지(식물 선택)로 이동
    >
      <div className="text-center">
        <p className="text-gray-600 mb-8">
          친구가 될 식물의 이름을 지어주세요!
        </p>
        {/* [!] 선택된 식물 이미지를 img 태그로 표시 */}
        <div className="w-48 h-48 mx-auto mb-8 flex justify-center items-center">
          <img
            src={currentPlantImage} // src 속성에 이미지 변수 할당
            alt={`식물 ${color}`} // alt 텍스트
            className="max-w-full max-h-full object-contain" // 이미지가 영역 넘치지 않도록
          />
        </div>
        {/* 이름 입력 */}
        <input
          type="text"
          value={plantName}
          onChange={(e) => setPlantName(e.target.value)}
          placeholder="이름 또는 별명"
          maxLength={15}
          className="w-full p-4 text-center bg-white border border-gray-300 rounded-lg outline-none focus:border-[#A755F8] focus:ring-2 focus:ring-[#A755F8]/20 shadow-sm"
          autoFocus
        />
      </div>
    </AuthLayout>
  );
}