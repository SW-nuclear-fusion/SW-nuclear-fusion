import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import AuthLayout from '../components/auth/AuthLayout';
import axios from 'axios';
import pottedPlantPurple from '../assets/level3purple.png';
import pottedPlantBlue from '../assets/level3blue.png';
import pottedPlantYellow from '../assets/level3blue.png';
import pottedPlantPink from '../assets/level3pink.png';

// 식물 색상과 import된 화분 이미지 변수 매핑
const pottedPlantImages = {
  purple: pottedPlantPurple,
  blue: pottedPlantBlue,
  yellow: pottedPlantYellow,
  pink: pottedPlantPink,
};

// 기본 이미지 (색상 매핑 실패 시)
const defaultPottedPlantImage = pottedPlantPurple; // 예시로 보라색 사용

export default function PlantConfirmationPage() {
  const navigate = useNavigate();
  const { color, name } = useParams(); // URL에서 색상과 이름 가져오기

  const handleConfirm = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        throw new Error('로그인 토큰이 없습니다. 다시 로그인해주세요.');
      }

      // 백엔드 API 호출 (식물 정보 저장)
      const response = await axios.post(
<<<<<<< HEAD
<<<<<<< HEAD
        'http://43.201.68.38:8080/api/user/plant', // 백엔드 주소 확인!
=======
        'http://localhost:8080/api/user/plant', // 백엔드 주소 확인!
>>>>>>> cad27c24f4c0e36f3b2cf20dbc2e1a6ebc5e11dd
=======
        'http://localhost:8080/api/user/plant', // 백엔드 주소 확인!
>>>>>>> 1e287e9 (demo v1)
        { plantColor: color, plantName: name },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      console.log('Plant info saved:', response.data.message);
      navigate('/main/home'); // 메인 홈 화면으로 이동

    } catch (error) {
      console.error('Failed to save plant info:', error);
      const message = error.response?.data?.message || error.message || '식물 정보 저장 중 오류 발생';
      alert(`오류: ${message}`);
    }
  };

  // 표시할 화분 이미지 결정
  const currentPottedPlantImage = pottedPlantImages[color] || defaultPottedPlantImage;

  return (
    <AuthLayout
      title="" // 제목 없음
      onNext={handleConfirm}
      nextButtonText="확인"
      showBackButton={false} // 뒤로가기 없음
    >
      <div className="text-center">
        {/* [!] 화분에 심은 식물 이미지를 img 태그로 표시 */}
        <div className="w-56 h-56 mx-auto mb-8 flex justify-center items-center"> {/* 이미지 컨테이너 크기 */}
           <img
              src={currentPottedPlantImage} // src에 이미지 변수 할당
              alt={`화분에 심은 ${name || color} 식물`} // alt 텍스트 (식물 이름 포함)
              className="max-w-full max-h-full object-contain" // 이미지가 영역 넘치지 않도록
           />
        </div>
        <p className="text-xl font-semibold text-gray-800">안녕하세요!</p>
        <p className="text-lg text-gray-600 mt-1">저를 선택해 주셔서 감사합니다!</p>
      </div>
    </AuthLayout>
  );
}