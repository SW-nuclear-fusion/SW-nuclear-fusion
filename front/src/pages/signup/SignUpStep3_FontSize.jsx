import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSignup } from '../../context/SignupContext';
import AuthLayout from '../../components/auth/AuthLayout';
import SelectionButton from '../../components/auth/SelectionButton';

export default function SignUpStep3_FontSize() {
  const navigate = useNavigate();
  const { formData, setFormData } = useSignup();
  const [fontSize, setFontSize] = useState(formData.fontSize);
  
  const options = ['크게', '기본']; // 옵션 통일

  const handleNext = () => {
    setFormData((prev) => ({ ...prev, fontSize }));
    navigate('/signup/role');
  };

  return (
    <AuthLayout
      title="읽기 편한 글자 크기를
선택해주세요"
      onNext={handleNext}
      nextButtonText="확인"
    >
      <div className="grid grid-cols-2 gap-4">
        {options.map((option) => (
          <SelectionButton
            key={option}
            text={option}
            isSelected={fontSize === option}
            onClick={() => setFontSize(option)}
          />
        ))}
      </div>
    </AuthLayout>
  );
}