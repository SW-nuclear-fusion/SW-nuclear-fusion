import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useSignup } from '../context/SignupContext';
import axios from 'axios';
import logo from '../assets/logo.png';

export default function WelcomePage() {
  const navigate = useNavigate();
  const { formData } = useSignup();
<<<<<<< HEAD
  const API_BASE_URL = 'http://43.201.68.38:8080';
=======
  const API_BASE_URL = 'http://localhost:8080';
>>>>>>> cad27c24f4c0e36f3b2cf20dbc2e1a6ebc5e11dd

  // [!] 회원가입 API 호출 및 후속 처리 함수
  const handleSignupAndProceed = async (nextPath) => {
    let apiEndpoint = '';
    let requestPayload = {};

    try {
      // formData에서 소셜/로컬 구분하여 API 엔드포인트 및 데이터 설정
      if (formData.socialUserId) {
        apiEndpoint = `${API_BASE_URL}/api/auth/signup-social`;
        requestPayload = {
          userId: formData.socialUserId,
          phone: formData.phone,
          role: formData.role,
          fontSize: formData.fontSize,
          birthdate: formData.birthdate,
          gender: formData.gender,
        };
      } else {
        apiEndpoint = `${API_BASE_URL}/api/auth/signup`;
        requestPayload = {
          id: formData.id,
          password: formData.password,
          phone: formData.phone,
          role: formData.role,
          fontSize: formData.fontSize,
          name: formData.name,
          birthdate: formData.birthdate,
          gender: formData.gender,
        };
      }

      // axios.post 호출하여 회원가입/정보등록 실행
      const response = await axios.post(apiEndpoint, requestPayload);
      const token = response.data.accessToken; // 토큰 받기

      console.log("Signup Success from WelcomePage! Received Token:", token);
      console.log("Type of Token:", typeof token);

      // 토큰 저장
      if (token && typeof token === 'string') {
        localStorage.setItem('accessToken', token);
        console.log("Token saved successfully to localStorage.");
        // 인자로 받은 다음 경로로 이동
        navigate(nextPath);
      } else {
        console.error('Invalid token received after signup:', token);
        throw new Error('회원가입 후 토큰 처리 중 오류가 발생했습니다.');
      }

    } catch (error) {
      console.error('Signup failed from WelcomePage:', error);
      const message = error.response?.data?.message || error.message || '회원가입 처리 중 오류 발생';
      alert(`회원가입 실패: ${message}`);
    }
  };

  // '정보 입력하기' 버튼 클릭 시
  const handleEnterInfo = () => {
    // 회원가입 API 호출 후 /alarm-setup 으로 이동
    handleSignupAndProceed('/alarm-setup');
  };

  // '건너뛰기' 버튼 클릭 시
  const handleSkip = () => {
    // 회원가입 API 호출 후 /select-plant 로 이동
    handleSignupAndProceed('/select-plant');
  };

  return (
    <div className="bg-white h-full flex flex-col p-6">
      <header className="flex h-[50px] items-center">
        {/* 뒤로가기 버튼 제거 또는 비활성화 */}
      </header>

      <main className="flex-grow flex flex-col justify-center items-center text-center px-4">
        {/* 로고, 환영 메시지 등 */}
        <div className="flex items-center justify-center mb-6">
          <img src={logo} alt="시루 로고" className="h-10" />
          <div className="w-12 h-12 bg-[#A755F8] rounded-full flex justify-center items-center text-2xl text-white ml-2 shadow-sm">
            <span>💜</span>
          </div>
        </div>
        <h2 className="text-2xl font-semibold text-gray-800">
          시니링에 오신 것을 환영합니다
        </h2>
        <p className="text-lg text-gray-600 mt-2">
          복용 정보를 입력해 볼까요?
        </p>
      </main>

      <footer className="flex-shrink-0 pb-4 space-y-3">
        {/* 핸들러 연결 */}
        <button
          className="w-full p-4 text-base font-semibold text-white rounded-xl bg-[#A755F8] shadow-md"
          onClick={handleEnterInfo}
        >
          정보 입력하기
        </button>
        {/* 핸들러 연결 */}
        <button
          className="w-full p-4 text-base font-semibold rounded-xl bg-white text-[#A755F8] border border-[#A755F8] shadow-sm"
          onClick={handleSkip}
        >
          건너뛰기
        </button>
      </footer>
    </div>
  );
}