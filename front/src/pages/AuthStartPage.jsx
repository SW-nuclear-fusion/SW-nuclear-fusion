import React from 'react';
import { useNavigate } from 'react-router-dom';
import { RiKakaoTalkFill } from 'react-icons/ri';
import { FcGoogle } from 'react-icons/fc';
import logo from '../assets/logo.png';
import characterIcon from '../assets/level1purple.png';

function AuthStartPage() {
  const navigate = useNavigate();
  const mainButtonStyles =
    'w-full p-4 text-base font-semibold rounded-xl bg-[#A755F8] text-white shadow-md';
  const socialButtonStyles =
    'w-full py-3 px-4 flex items-center justify-center gap-3 rounded-lg shadow-md bg-[#A755F8] text-white font-medium';

  return (
    <div className="bg-white h-full flex flex-col p-6">
      <header className="h-[60px] flex items-center">
        <img src={logo} alt="로고" className="h-8" />
      </header>

      <main className="flex-grow flex flex-col justify-center items-center text-center">
        <div className="w-40 h-40 bg-white rounded-3xl shadow-lg flex justify-center items-center mb-10">
          <img src={characterIcon} alt="캐릭터" />
        </div>
      </main>

      <footer className="flex-shrink-0 pb-4 space-y-3">
        {/* 1. 로그인 버튼 */}
        <button
          className={mainButtonStyles}
          onClick={() => navigate('/login')}
        >
          로그인
        </button>
        
        {/* 2. 회원가입 버튼 */}
        <button
          className={mainButtonStyles}
          onClick={() => navigate('/signup/id')}
        >
          회원가입
        </button>

        {/* 3. 소셜 로그인 버튼들 */}
        <div className="flex justify-center gap-4 pt-4">
          <button
            className={socialButtonStyles}
            onClick={() => console.log('구글 로그인')}
          >
            <FcGoogle size={24} />
            <span>Google로 시작</span>
          </button>
          <button
            className={socialButtonStyles}
            onClick={() => console.log('카카오 로그인')}
          >
            <RiKakaoTalkFill size={24} />
            <span>Kakao로 시작</span>
          </button>
        </div>
      </footer>
    </div>
  );
}

export default AuthStartPage;