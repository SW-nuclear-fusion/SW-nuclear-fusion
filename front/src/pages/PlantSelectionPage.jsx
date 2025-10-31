import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthLayout from '../components/auth/AuthLayout';
import plantPurple from '../assets/purple.png';
import plantBlue from '../assets/blue.png';
import plantYellow from '../assets/blue.png';
import plantPink from '../assets/pink.png';

// 식물 데이터 (color와 image 매핑)
const plants = [
  { color: 'purple', image: plantPurple }, // import된 변수 사용
  { color: 'blue', image: plantBlue },
  { color: 'yellow', image: plantYellow },
  { color: 'pink', image: plantPink },
];

export default function PlantSelectionPage() {
  const navigate = useNavigate();
  const [currentIndex, setCurrentIndex] = useState(0); // 현재 보여줄 식물의 인덱스

  // 다음 식물 보기 (배열 끝이면 처음으로)
  const handleNextPlant = () => {
    setCurrentIndex((prevIndex) => (prevIndex + 1) % plants.length);
  };

  // 이전 식물 보기 (배열 처음이면 끝으로)
  const handlePrevPlant = () => {
    setCurrentIndex((prevIndex) => (prevIndex - 1 + plants.length) % plants.length);
  };

  // "선택" 버튼 클릭 시
  const handleSelect = () => {
    const selectedPlant = plants[currentIndex]; // 현재 선택된 식물 정보
    // 이름 짓는 페이지로 이동하면서 URL 파라미터로 색상 전달
    navigate(`/name-plant/${selectedPlant.color}`);
  };

  return (
    <AuthLayout
      title="사용자의 식물을 선택해 주세요."
      onNext={handleSelect} // 하단 버튼 클릭 시 handleSelect 실행
      nextButtonText="선택"
      showBackButton={false} // 뒤로가기 버튼 없음
    >
      <div className="text-center">
        <p className="text-gray-600 mb-8">
          앱에서 퀴즈를 풀 때마다 식물 성장에 필요한 물과 사랑을 얻을 수 있어요.
        </p>
        {/* 식물 캐러셀 */}
        <div className="relative flex items-center justify-center h-64"> {/* 높이 고정 */}
          {/* 이전 버튼 (<) */}
          <button
            type="button"
            onClick={handlePrevPlant}
            className="absolute left-0 text-3xl text-gray-400 p-4 z-10 hover:text-gray-600">
            &lt;
          </button>

          {/* 현재 식물 이미지 표시 영역 */}
          {/* [!] 2. div 대신 img 태그 사용 */}
          <div className="w-48 h-48 flex justify-center items-center">
             <img
                src={plants[currentIndex].image} // src 속성에 이미지 변수 할당
                alt={`식물 ${plants[currentIndex].color}`} // alt 텍스트
                className="max-w-full max-h-full object-contain" // 이미지가 영역 넘치지 않도록
             />
          </div>

          {/* 다음 버튼 (>) */}
          <button
            type="button"
            onClick={handleNextPlant}
            className="absolute right-0 text-3xl text-gray-400 p-4 z-10 hover:text-gray-600">
            &gt;
          </button>
        </div>
        {/* 페이지네이션 (점 표시) */}
        <div className="flex justify-center mt-4 space-x-2">
          {plants.map((_, index) => (
            <span
              key={index}
              className={`block w-2 h-2 rounded-full transition-colors ${
                index === currentIndex ? 'bg-[#A755F8]' : 'bg-gray-300' // 현재 식물 점 색상 강조
              }`}
            ></span>
          ))}
        </div>
      </div>
    </AuthLayout>
  );
}