import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useSignup } from '../../context/SignupContext';
import AuthLayout from '../../components/auth/AuthLayout';
import SelectionButton from '../../components/auth/SelectionButton';

export default function SignUpStep4_Role() {
  const navigate = useNavigate();
  const { formData, setFormData } = useSignup();
  const [role, setRole] = useState(formData.role);
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const socialUserId = searchParams.get('userId');
    if (socialUserId) {
      // 소셜 로그인
      console.log('Social User ID:', socialUserId);
      setFormData((prev) => ({ 
        ...prev, 
        socialUserId: socialUserId,
        id: socialUserId,
      }));
    }
  }, [searchParams, setFormData]);

  const handleNext = () => {
    if (!role) {
      return alert('역할을 선택해주세요.');
    }
    setFormData((prev) => ({ ...prev, role }));
    navigate('/signup/name'); 
  };

  const showBackButton = !formData.socialUserId;

  return (
    <AuthLayout
      title="가입 유형을
선택해주세요"
      onNext={handleNext}
      nextButtonText="다음"
      showBackButton={showBackButton}
    >
      <div className="grid grid-cols-2 gap-4">
        <SelectionButton
          text="시니어"
          isSelected={role === 'SENIOR'}
          onClick={() => setRole('SENIOR')}
        />
        <SelectionButton
          text="보호자"
          isSelected={role === 'GUARDIAN'}
          onClick={() => setRole('GUARDIAN')}
        />
      </div>
    </AuthLayout>
  );
}