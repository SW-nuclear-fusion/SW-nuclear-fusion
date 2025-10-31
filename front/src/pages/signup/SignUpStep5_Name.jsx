import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSignup } from '../../context/SignupContext';
import AuthLayout from '../../components/auth/AuthLayout';

export default function SignUpStep5_Name() {
  const navigate = useNavigate();
  const { formData, setFormData } = useSignup();
  const [name, setName] = useState(formData.name);

  const handleNext = () => {
    if (!name) return alert('이름 또는 별명을 입력하세요.');
    setFormData((prev) => ({ ...prev, name }));
    navigate('/signup/birthdate');
  };

  return (
    <AuthLayout title="만나서 반가워요!
성함을 알려주세요" onNext={handleNext}>
      <input
        type="text"
        placeholder="이름 또는 별명"
        value={name}
        onChange={(e) => setName(e.target.value)}
        className="w-full p-4 bg-white border border-gray-300 rounded-lg outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 shadow-sm"
      />
    </AuthLayout>
  );
}