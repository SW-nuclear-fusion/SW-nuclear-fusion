import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSignup } from '../../context/SignupContext';
import AuthLayout from '../../components/auth/AuthLayout';
import SelectionButton from '../../components/auth/SelectionButton';
import { FaMars, FaVenus } from 'react-icons/fa';

export default function SignUpStep6_Gender() {
  const navigate = useNavigate();
  const { formData, setFormData } = useSignup();
  const [gender, setGender] = useState(formData.gender);

  const handleNext = () => {
    if (!gender) return alert('성별을 선택하세요.');

    setFormData((prev) => ({ ...prev, gender }));
    navigate('/welcome');
  };

  return (
    <AuthLayout title="사용자의 성별을\n선택해 주세요" onNext={handleNext}>
      <div className="grid grid-cols-2 gap-4">
        <SelectionButton
          text="남성"
          icon={<FaMars />}
          isSelected={gender === '남성'}
          onClick={() => setGender('남성')}
        />
        <SelectionButton
          text="여성"
          icon={<FaVenus />}
          isSelected={gender === '여성'}
          onClick={() => setGender('여성')}
        />
      </div>
    </AuthLayout>
  );
}