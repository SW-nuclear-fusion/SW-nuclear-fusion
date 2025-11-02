import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthLayout from '../components/auth/AuthLayout';
import axios from 'axios'; // [!] axios import

export default function LoginPage() {
  const navigate = useNavigate();
  const [userId, setUserId] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleLogin = async () => {
    setError('');
    const loginData = { userId, password };

    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', loginData);
      console.log(response);
      const token = response.data.accessToken;
      localStorage.setItem('accessToken', token);
      navigate('/main/home');
    } catch (err) {
      console.error('Login error:', err);
      const message = err.response?.data?.message || err.message || '로그인 중 오류 발생';
      setError(message);
    }
  };

  const inputStyles =
    'w-full p-4 text-base bg-white border border-gray-300 rounded-lg outline-none focus:border-[#A755F8] focus:ring-2 focus:ring-[#A755F8]/20 shadow-sm';

  return (
    <AuthLayout title="로그인" onNext={handleLogin} nextButtonText="로그인">
      <div className="space-y-4">
        <input
          type="text"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
          className={inputStyles}
          placeholder="아이디"
          autoFocus
        />
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className={inputStyles}
          placeholder="비밀번호"
        />
        {error && <p className="text-red-500 text-sm text-center">{error}</p>}
      </div>
    </AuthLayout>
  );
}