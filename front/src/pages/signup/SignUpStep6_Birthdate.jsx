import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSignup } from '../../context/SignupContext';
import AuthLayout from '../../components/auth/AuthLayout';

export default function SignUpStep6_Birthdate() {
  const navigate = useNavigate();
  const { formData, setFormData } = useSignup();

  // 생년월일을 YYYY, MM, DD로 분리
  const [date, setDate] = useState({
    year: formData.birthdate.split('-')[0] || '',
    month: formData.birthdate.split('-')[1] || '',
    day: formData.birthdate.split('-')[2] || '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setDate((prev) => ({ ...prev, [name]: value.replace(/[^0-9]/g, '') })); // 숫자만 입력
  };

  const handleNext = () => {
    const { year, month, day } = date;
    if (!year || !month || !day) return alert('생년월일을 모두 입력하세요.');
    const formattedMonth = month.padStart(2, '0');
    const formattedDay = day.padStart(2, '0');
    const birthdate = `${year}-${formattedMonth}-${formattedDay}`;
    
    // // 유효한 날짜인지 추가 검증 (선택 사항)
    // try {
    //     LocalDate.parse(birthdate); // Java 백엔드와 동일한 방식으로 파싱 시도
    // } catch (e) {
    //     return alert('유효하지 않은 날짜입니다.');
    // }

    setFormData((prev) => ({ ...prev, birthdate }));
    navigate('/signup/gender');
  };

  return (
    <AuthLayout title="사용자의 생년월일을
입력해 주세요" onNext={handleNext}>
      <div className="flex items-center gap-3">
        <input
          type="tel" name="year" value={date.year} onChange={handleChange}
          placeholder="0000" maxLength={4}
          className="w-1/3 p-4 text-center bg-white border border-gray-300 rounded-lg outline-none focus:border-primary"
        />
        <span className="text-lg">년</span>
        <input
          type="tel" name="month" value={date.month} onChange={handleChange}
          placeholder="00" maxLength={2}
          className="w-1/4 p-4 text-center bg-white border border-gray-300 rounded-lg outline-none focus:border-primary"
        />
        <span className="text-lg">월</span>
        <input
          type="tel" name="day" value={date.day} onChange={handleChange}
          placeholder="00" maxLength={2}
          className="w-1/4 p-4 text-center bg-white border border-gray-300 rounded-lg outline-none focus:border-primary"
        />
        <span className="text-lg">일</span>
      </div>
    </AuthLayout>
  );
}