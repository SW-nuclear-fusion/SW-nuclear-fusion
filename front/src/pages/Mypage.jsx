import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import MyPageCalendar from '../components/mypage/MyPageCalendar'; // 방금 만든 달력

// API 호출용 (토큰 자동 포함)
const api = axios.create({
    baseURL: 'http://43.201.68.38:8080',
    headers: { 'Content-Type': 'application/json' }
});
api.interceptors.request.use(config => {
    const token = localStorage.getItem('accessToken');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
}, error => Promise.reject(error));

// 이모지 선택 버튼
const MoodButton = ({ emoji, selected, onClick }) => (
    <button
        onClick={() => onClick(emoji)}
        className={`text-3xl p-3 rounded-full transition-transform transform hover:scale-110 ${selected ? 'bg-purple-200 ring-2 ring-purple-400' : 'bg-gray-100'}`}
    >
        {emoji}
    </button>
);

export default function MyPage() {
  const navigate = useNavigate();
  const [selectedDate, setSelectedDate] = useState(() => { // 오늘 날짜 (시간 제거)
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return today;
  });
  
  // 현재 달력에 표시할 연/월 (API 호출용)
  const [currentViewMonth, setCurrentViewMonth] = useState({
      year: selectedDate.getFullYear(),
      month: selectedDate.getMonth() + 1 // 1월 = 1
  });
  
  // 월간 데이터 (날짜(YYYY-MM-DD)를 키로 갖는 Map)
  const [monthlyDataMap, setMonthlyDataMap] = useState(new Map());
  
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // 월간 데이터 로딩 함수
  const fetchMonthlyData = useCallback(async (year, month) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await api.get(`http://43.201.68.38:8080/api/mypage/monthly-data?year=${year}&month=${month}`);
      console.log(response.data);
      if (Array.isArray(response.data)) {
        // 1. 백엔드가 배열(List)을 반환한 경우 (기존 코드)
        const dataMap = new Map(response.data.map(item => [item.date, item]));
        setMonthlyDataMap(dataMap);
      } else if (typeof response.data === 'object' && response.data !== null) {
        // 2. 백엔드가 객체(Map)를 반환한 경우 (수정된 코드)
        const dataMap = new Map(Object.entries(response.data));
        setMonthlyDataMap(dataMap);
      } else {
        // 3. 예외 처리
        throw new Error("Invalid data format from server");
      }
      
      console.log(`Fetched monthly data for ${year}-${month}:`, monthlyDataMap);

    } catch (err) {
      console.error("Failed to fetch monthly data:", err);
      setError("데이터 로딩 실패");
      if (err.response?.status === 401) navigate('/login');
    } finally {
      setIsLoading(false);
    }
  }, [navigate]);

  useEffect(() => {
    fetchMonthlyData(currentViewMonth.year, currentViewMonth.month);
  }, [fetchMonthlyData, currentViewMonth]); 

  // 달력에서 월 변경 시 호출될 콜백
  const handleMonthChange = (year, month) => {
    setCurrentViewMonth({ year, month }); // API 호출 트리거
  };
  
  // 달력에서 날짜 선택 시
  const handleDateChange = (date) => {
    setSelectedDate(new Date(date.getFullYear(), date.getMonth(), date.getDate()));
  };

  // 선택된 날짜의 데이터
  const yy = selectedDate.getFullYear();
  const mm = (selectedDate.getMonth() + 1).toString().padStart(2, '0');
  const dd = selectedDate.getDate().toString().padStart(2, '0');
  const selectedDateString = `${yy}-${mm}-${dd}`;
  const dataForSelectedDate = monthlyDataMap.get(selectedDateString) || {};

  // 오늘 날짜인지 확인
  const isTodaySelected = selectedDateString === new Date().toISOString().split('T')[0];
  
  // 감정 기록 핸들러
  const handleMoodSelect = async (moodIcon_temp) => {
      if (!isTodaySelected) {
          alert("감정 기록은 오늘 날짜만 가능합니다.");
          return;
      }
      console.log("Saving mood:", moodIcon_temp);
      try {
        const MOOD_MAP = {
          '😊': 'HAPPY',
          '😐': 'NEUTRAL',
          '😢': 'SAD',
          '😡': 'ANGRY',
          '😴': 'TIRED' // 예시
        };
        const moodIcon = MOOD_MAP[moodIcon_temp];
        if (!moodIcon) {
            alert("유효하지 않은 감정입니다.");
            return;
        }
          const response = await api.post('/api/mypage/mood', { moodIcon });
          const updatedDailyData = response.data; // DailyDataDto 반환

          // 성공 시, 월간 데이터 맵(Map)을 업데이트하여 UI 즉시 반영
          setMonthlyDataMap(prevMap => {
              const newMap = new Map(prevMap);
              const todayString = updatedDailyData.date;
              const existingData = prevMap.get(todayString) || {};
              const REVERSE_MOOD_MAP = {
                'HAPPY': '😊',
                'NEUTRAL': '😐',
                'SAD': '😢',
                'ANGRY': '😡',
                'TIRED': '😴'
              };
              const icon = REVERSE_MOOD_MAP[updatedDailyData.moodIcon];
              newMap.set(todayString, { 
                ...existingData, 
                ...updatedDailyData,
                moodIcon_temp: icon
              });
              return newMap;
          });
          alert("감정이 기록되었습니다!");

      } catch (err) {
          console.error("Failed to save mood:", err);
          alert(err.response?.data?.message || "감정 기록 실패");
      }
  };


  return (
    <div className="flex flex-col h-screen">
      {/* 상단 헤더 */}
      <header className="bg-white shadow p-4 flex items-center justify-center sticky top-0 z-20">
         <h1 className="text-lg font-semibold mx-auto">마이 페이지</h1>
      </header>

      {/* 메인 콘텐츠 (스크롤) */}
      <main className="flex-grow overflow-y-auto p-4 space-y-6 bg-gray-50">
        
        {/* 1. 월간 달력 위젯 */}
        <MyPageCalendar
          selectedDate={selectedDate}
          onDateChange={handleDateChange}
          onMonthChange={handleMonthChange}
          monthlyDataMap={monthlyDataMap}
        />

        {/* 2. 오늘 감정 기록 */}
        {isTodaySelected && (
            <div className="bg-white p-4 rounded-lg shadow-md">
                <h2 className="font-semibold text-lg mb-3 text-center">오늘 하루는 어땠나요?</h2>
                <div className="flex justify-around items-center">
                    <MoodButton emoji="😊" selected={dataForSelectedDate.moodIcon === 'HAPPY'} onClick={handleMoodSelect} />
                    <MoodButton emoji="😐" selected={dataForSelectedDate.moodIcon === 'NEUTRAL'} onClick={handleMoodSelect} />
                    <MoodButton emoji="😢" selected={dataForSelectedDate.moodIcon === 'SAD'} onClick={handleMoodSelect} />
                    <MoodButton emoji="😡" selected={dataForSelectedDate.moodIcon === 'ANGRY'} onClick={handleMoodSelect} />
                    <MoodButton emoji="😴" selected={dataForSelectedDate.moodIcon === 'TIRED'} onClick={handleMoodSelect} />
                </div>
            </div>
       )}

        {/* 3. 선택된 날짜의 상세 기록 */}
        <div className="bg-white p-4 rounded-lg shadow-md">
            <h2 className="font-semibold text-lg mb-3">
                {selectedDate.toLocaleString("ko-KR", { month: "long", day: "numeric" })} 기록
            </h2>
            {/* 로딩 중 또는 데이터 없음 표시 */}
            {isLoading && <p className="text-gray-500">기록 로딩 중...</p>}
            {!isLoading && !dataForSelectedDate.medicationsTaken && !dataForSelectedDate.quizCorrectCount && !dataForSelectedDate.moodIcon && (
                <p className="text-gray-500">이 날짜에는 기록이 없습니다.</p>
            )}
            
            {/* 복약 기록 */}
            {dataForSelectedDate.medicationsTaken && dataForSelectedDate.medicationsTaken.length > 0 && (
                <div className="mb-2">
                    <h3 className="font-medium text-purple-700">복약 완료</h3>
                    <ul className="list-disc list-inside text-sm text-gray-600">
                        {dataForSelectedDate.medicationsTaken.map((med, i) => (
                            <li key={i}>{med}</li>
                        ))}
                    </ul>
                </div>
            )}
            {/* 퀴즈 기록 */}
            {dataForSelectedDate.quizCorrectCount != null && ( // 0점일 수도 있으므로 null/undefined 체크
                <div className="mb-2">
                    <h3 className="font-medium text-blue-700">퀴즈</h3>
                    <p className="text-sm text-gray-600">총 {dataForSelectedDate.quizCorrectCount}개 정답</p>
                </div>
            )}
            {dataForSelectedDate.moodIcon && !isTodaySelected && (
                 <div className="mb-2">
                    <h3 className="font-medium text-yellow-700">오늘의 감정</h3>
                    <p className="text-3xl">{dataForSelectedDate.moodIcon_temp}</p>
                </div>
            )}
        </div>
      </main>
    </div>
  );
}

