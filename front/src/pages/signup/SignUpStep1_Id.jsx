import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSignup } from '../../context/SignupContext';
import AuthLayout from '../../components/auth/AuthLayout';
import axios from 'axios';

export default function SignUpStep1_Id() {
  const navigate = useNavigate();
  const { formData, setFormData } = useSignup();

  // 이 페이지에서만 사용할 로컬 state
  const [localData, setLocalData] = useState({
    id: formData.id,
    password: '',
    passwordConfirm: '',
  });
  const [isIdChecked, setIsIdChecked] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setLocalData((prev) => ({ ...prev, [name]: value }));
    if (name === 'id') setIsIdChecked(false); // 아이디 변경 시 중복확인 리셋
  };

  const handleIdCheck = async () => {
    if (!localData.id) return alert('아이디를 입력하세요.');
    try {
      const response = await axios.get('http://43.201.68.38:8080/api/auth/check-id', {
        params: {
          id: localData.id
        }
      });

      if (response.data && response.data.isAvailable) {
        setIsIdChecked(true);
        alert('사용 가능한 아이디입니다.');
      } else {
        setIsIdChecked(false);
        alert('이미 사용 중인 아이디입니다.');
      }

    } catch (error) {
      console.error("ID check failed:", error);
      setIsIdChecked(false);

      if (error.response) {
        const message = error.response.data?.message || '이미 사용 중인 아이디입니다.';
        alert(message);
      } else if (error.request) {
        alert('서버 응답이 없습니다. 다시 시도해 주세요.'); // [수정] alert 사용
      } else {
        alert('오류가 발생했습니다. 다시 시도해 주세요.'); // [수정] alert 사용
      }
    }
  };

  const handleNext = () => {
    if (!isIdChecked) {
      // [수정] alert 사용
      alert('아이디 중복확인을 해주세요.');
      return;
    }

    if (localData.password !== localData.passwordConfirm) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }

    if (localData.password.length < 8) {
       alert('비밀번호는 8자 이상이어야 합니다.');
       return;
    }

    setFormData((prev) => ({
      ...prev,
      id: localData.id,
      password: localData.password,
    }));
    
    navigate('/signup/phone');
  };

  // 공통 input 스타일
  const inputStyles = 'w-full p-4 bg-white border border-gray-300 rounded-lg outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 shadow-sm';

  return (
    <AuthLayout title="아이디를 입력해 주세요" onNext={handleNext}>
      <div className="space-y-4">
        <div className="flex items-center gap-2">
          <input
            type="text"
            name="id"
            placeholder="아이디"
            value={localData.id}
            onChange={handleChange}
            className={inputStyles}
          />
          <button
            type="button"
            onClick={handleIdCheck}
            disabled={isIdChecked}
            className={`flex-shrink-0 px-4 py-3 text-sm font-medium rounded-lg ${
              isIdChecked ? 'bg-gray-300 text-gray-500' : 'bg-primary-light text-primary'
            }`}
          >
            {isIdChecked ? '확인완료' : '중복확인'}
          </button>
        </div>
        <input
          type="password"
          name="password"
          placeholder="비밀번호"
          value={localData.password}
          onChange={handleChange}
          className={inputStyles}
        />
        <input
          type="password"
          name="passwordConfirm"
          placeholder="비밀번호 확인"
          value={localData.passwordConfirm}
          onChange={handleChange}
          className={inputStyles}
        />
      </div>
    </AuthLayout>
  );
}