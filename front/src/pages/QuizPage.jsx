import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import AuthLayout from '../components/auth/AuthLayout'; // 레이아웃 재사용 (선택 사항)

// API 호출을 위한 기본 axios 인스턴스 (헤더 자동 포함)
const api = axios.create({
<<<<<<< HEAD
<<<<<<< HEAD
    baseURL: 'http://43.201.68.38:8080',
=======
    baseURL: 'http://localhost:8080',
>>>>>>> cad27c24f4c0e36f3b2cf20dbc2e1a6ebc5e11dd
=======
    baseURL: 'http://localhost:8080',
>>>>>>> 1e287e9 (demo v1)
    headers: { 'Content-Type': 'application/json' }
});
api.interceptors.request.use(config => {
    const token = localStorage.getItem('accessToken');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});


export default function QuizPage() {
  const navigate = useNavigate();
  const [questions, setQuestions] = useState([]); // 퀴즈 문제 목록
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0); // 현재 문제 인덱스
  const [answers, setAnswers] = useState([]); // 사용자가 선택한 답안 목록
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null); // 에러 메시지 (예: "오늘 퀴즈 이미 완료")


  // 퀴즈 문제 불러오기
  useEffect(() => {
    const fetchQuiz = async () => {
      setIsLoading(true);
      setError(null);
      try {
        const response = await api.get('/api/missions/quiz');
        setQuestions(response.data); // DTO 목록
      } catch (err) {
        console.error("Failed to fetch quiz:", err);
        setError(err.response?.data?.message || '퀴즈를 불러오는 데 실패했습니다.');
      } finally {
        setIsLoading(false);
      }
    };
    fetchQuiz();
  }, []); // 마운트 시 1회 실행

  // 답안 선택 핸들러
  const handleAnswerSelect = (questionId, selectedOption) => {
    const newAnswers = [...answers, { questionId, submittedAnswer: selectedOption }];
    setAnswers(newAnswers);

    // 다음 문제로 이동 또는 제출
    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
    } else {
      // 마지막 문제 -> 제출
      submitAnswers(newAnswers);
    }
  };

  // 퀴즈 제출 핸들러
  const submitAnswers = async (finalAnswers) => {
    setIsLoading(true);
    setError(null);
    try {
      // POST /api/missions/quiz/submit
      const response = await api.post('/api/missions/quiz/submit', finalAnswers);
      const result = response.data; // QuizResultDto

      console.log("Quiz submitted:", result);
      
      // 보상 페이지로 이동 (RewardDto 전달)
      if (result.reward) {
        navigate('/reward', { 
            state: { 
                reward: result.reward, 
                from: "quiz" }});
      } else {
        alert(`퀴즈 완료! ${result.totalCount}개 중 ${result.correctCount}개 정답. (보상 없음)`);
        navigate('/main/mission'); // 미션 페이지로 복귀
      }

    } catch (err) {
      console.error("Failed to submit quiz:", err);
      setError(err.response?.data?.message || '퀴즈 제출 중 오류 발생');
      // 에러 발생 시 미션 페이지로 복귀
      setTimeout(() => navigate('/main/mission'), 2000);
    }
  };

  // 렌더링
  if (isLoading) {
    return <AuthLayout title="퀴즈 로딩 중..." showBackButton={false}><p className="text-center">...</p></AuthLayout>;
  }
  if (error) { // 예: "오늘 퀴즈 이미 완료"
    return <AuthLayout title="알림" showBackButton={true}><p className="text-center">{error}</p></AuthLayout>;
  }
  if (questions.length === 0 || currentQuestionIndex >= questions.length) {
    return <AuthLayout title="퀴즈" showBackButton={true}><p className="text-center">표시할 퀴즈가 없습니다.</p></AuthLayout>;
  }

  const currentQuestion = questions[currentQuestionIndex];

  return (
    // AuthLayout 재사용 (뒤로가기 버튼은 미션 페이지로)
    <AuthLayout
      title={`퀴즈 ${currentQuestionIndex + 1} / ${questions.length}`}
      showBackButton={true}
      // 하단 버튼 숨김 (AuthLayout 수정 필요 시) 또는 nextButtonText=""
      nextButtonText="" 
    >
      <div className="text-center">
        {/* 질문 */}
        <h2 className="text-xl font-semibold mb-8">{currentQuestion.questionText}</h2>
        {/* 보기 버튼 (이미지 퀴즈 디자인 참고) */}
        <div className="grid grid-cols-2 gap-4">
          {currentQuestion.options.map((option, index) => (
            <button
              key={index}
              onClick={() => handleAnswerSelect(currentQuestion.id, option)}
              className="bg-white border-2 border-purple-200 rounded-lg p-6 text-lg font-medium hover:bg-purple-50 transition-colors"
            >
              {option}
            </button>
          ))}
        </div>
      </div>
    </AuthLayout>
  );
}