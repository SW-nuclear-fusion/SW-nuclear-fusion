import React, { createContext, useState, useContext } from 'react';

// 1. Context 생성
const SignupContext = createContext();

// 2. Context Provider 컴포넌트 생성
export function SignupProvider({ children }) {
  const [formData, setFormData] = useState({
    id: '',
    password: '',
    phone: '',
    fontSize: '보통',
    role: '',
    name: '',
    birthdate: '',
    gender: '',
    socialuserId: null,
  });

  return (
    <SignupContext.Provider value={{ formData, setFormData }}>
      {children}
    </SignupContext.Provider>
  );
}

// 3. Context를 쉽게 사용하기 위한 custom hook
export function useSignup() {
  const context = useContext(SignupContext);
  if (!context) {
    throw new Error('useSignup must be used within a SignupProvider');
  }
  return context;
}