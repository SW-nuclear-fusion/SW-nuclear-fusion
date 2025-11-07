import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSignup } from '../../context/SignupContext';
import AuthLayout from '../../components/auth/AuthLayout';
import axios from 'axios';

export default function SignUpStep2_Phone() {
  const navigate = useNavigate();
  const { formData, setFormData } = useSignup();
  const [phone, setPhone] = useState(formData.phone);
  const [isPhoneChecked, setIsPhoneChecked] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    // '-' 제거 및 숫자만 입력받도록 (선택적)
    const formattedPhone = e.target.value.replace(/[^0-9]/g, '');
    setPhone(formattedPhone);
    setIsPhoneChecked(false);
  };

  const handlePhoneCheck = async () => {
    if (!phone) {
      alert('휴대폰 번호를 입력하세요.');
      return;
    }
    // (선택 사항) 간단한 휴대폰 번호 형식 검증
    if (phone.length < 10 || phone.length > 11) {
      alert('올바른 휴대폰 번호 형식이 아닙니다. (10-11자리)');
      return;
    }

    setIsLoading(true);
    try {
      const response = await axios.get('http://43.201.68.38:8080/api/auth/check-phone', {
        params: {
          phone: phone
        }
      });

      if (response.data && response.data.isAvailable) {
        setIsPhoneChecked(true);
        alert('사용 가능한 휴대폰 번호입니다.');
      } else {
        setIsPhoneChecked(false);
        alert('이미 사용 중인 휴대폰 번호입니다.');
      }

    } catch (error) {
      console.error("Phone check failed:", error);
      setIsPhoneChecked(false);
      if (error.response) {
        const message = error.response.data?.message || '이미 사용 중인 휴대폰 번호입니다.';
        alert(message);
      } else if (error.request) {
        alert('서버 응답이 없습니다. 다시 시도해 주세요.');
      } else {
        alert('오류가 발생했습니다. 다시 시도해 주세요.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleNext = () => {
    if (isLoading) return;

    if (!isPhoneChecked) {
      alert('휴대폰 번호 중복확인을 해주세요.');
      return;
    }
    if (!phone) {
      alert('휴대폰 번호를 입력하세요.');
      return;
    }
    setFormData((prev) => ({ ...prev, phone }));
    navigate('/signup/fontsize'); // 다음 단계로
  };

  const inputStyles = 'w-full p-4 bg-white border border-gray-300 rounded-lg outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 shadow-sm';

  return (
    <AuthLayout title="휴대폰 번호를 입력해 주세요" onNext={handleNext}>
      <div className="flex items-center gap-2">
        <input
          type="tel"
          placeholder="휴대폰 번호 ('-' 제외)"
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
          className={inputStyles}
        />
        <button
              type="button"
              onClick={handlePhoneCheck}
              disabled={isPhoneChecked}
              className={`flex-shrink-0 px-4 py-3 text-sm font-medium rounded-lg ${
                isPhoneChecked ? 'bg-gray-300 text-gray-500' : 'bg-primary-light text-primary'
              }`}
            >
              {isPhoneChecked ? '확인완료' : '중복확인'}
            </button>
      </div>
    </AuthLayout>
  );
}