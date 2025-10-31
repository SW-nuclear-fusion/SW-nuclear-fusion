import React, { useState, useMemo } from 'react';

// 날짜 계산 헬퍼 (기존과 유사)
const buildMonthGrid = (year, month) => {
  const firstOfMonth = new Date(year, month, 1);
  // [수정] 일요일(0) 시작 기준 캘린더 (기존 코드가 월(1) 시작처럼 계산되어 수정)
  const firstDayWeekday = firstOfMonth.getDay(); // 0: 일요일, 1: 월요일 ...
  const leading = firstDayWeekday; // 일요일이 0이므로, 0부터 시작
  const totalCells = 42;
  const grid = [];
  // [수정] i = -leading 로 시작 (0 - 0 = 0 -> 1일)
  for (let i = -leading; i < totalCells - leading; i++) {
    grid.push(new Date(year, month, i + 1)); // 1일부터 시작하도록 i + 1
  }
  return grid;
};
// [수정] 일요일 시작 기준
const korWeekNames = ["일", "월", "화", "수", "목", "금", "토"];

/**
* 마이페이지 전용 월간 달력 컴포넌트
* @param {Date} selectedDate - 현재 선택된 날짜 (Date 객체)
* @param {function} onDateChange - 날짜 클릭 시 호출될 콜백 함수
* @param {function} onMonthChange - 달 변경 시 호출될 콜백 함수 (year, month 전달)
* @param {Map<string, object>} monthlyDataMap - 날짜(YYYY-MM-DD)를 키로 갖는 DailyDataDto 맵
*/
export default function MyPageCalendar({ selectedDate, onDateChange, onMonthChange, monthlyDataMap }) {
  const today = useMemo(() => {
    const d = new Date(); d.setHours(0, 0, 0, 0); return d;
  }, []);

  // 현재 달력에 표시할 연/월 상태
  const [viewYearMonth, setViewYearMonth] = useState({
    year: selectedDate.getFullYear(),
    month: selectedDate.getMonth(),
  });

  // 이전 달
  const prevMonth = () => {
    setViewYearMonth((s) => {
      const m = s.month - 1;
      const newY = m < 0 ? s.year - 1 : s.year;
      const newM = m < 0 ? 11 : m;
      onMonthChange(newY, newM + 1); // 부모에게 알림 (1월=1)
      return { year: newY, month: newM };
    });
  };

  // 다음 달
  const nextMonth = () => {
    setViewYearMonth((s) => {
      const m = s.month + 1;
      const newY = m > 11 ? s.year + 1 : s.year;
      const newM = m > 11 ? 0 : m;
      onMonthChange(newY, newM + 1); // 부모에게 알림 (1월=1)
      return { year: newY, month: newM };
    });
  };

  // 날짜 클릭
  const handleDateClick = (date) => {
    onDateChange(date); // 부모에게 선택된 날짜 알림
  };

  // 현재 연/월 기준 달력 그리드 생성
  const monthGrid = useMemo(
    () => buildMonthGrid(viewYearMonth.year, viewYearMonth.month),
    [viewYearMonth]
  );

  // 날짜를 YYYY-MM-DD 형식으로 변환하는 헬퍼
  const toDateString = (date) => {
    const y = date.getFullYear();
    const m = (date.getMonth() + 1).toString().padStart(2, '0');
    const d = date.getDate().toString().padStart(2, '0');
    return `${y}-${m}-${d}`;
  };

  return (
    <div className="bg-white p-4 rounded-lg shadow-md">
      {/* 헤더: < 2025년 10월 > */}
      <div className="flex items-center justify-between px-2 py-2">
        <button onClick={prevMonth} className="p-2 rounded-md hover:bg-gray-100" aria-label="이전 달">◀</button>
        <div className="text-center font-semibold text-lg">
          {viewYearMonth.year}년{" "}
          {new Date(viewYearMonth.year, viewYearMonth.month).toLocaleString("ko-KR", { month: "long" })}
        </div>
        <button onClick={nextMonth} className="p-2 rounded-md hover:bg-gray-100" aria-label="다음 달">▶</button>
      </div>

      {/* 요일 헤더 (일~토) */}
      <div className="grid grid-cols-7 gap-1 text-xs text-center text-gray-500 mb-2">
        {korWeekNames.map((k) => <div key={k} className="py-1">{k}</div>)}
      </div>

      {/* 날짜 그리드 */}
      <div className="grid grid-cols-7 gap-1">
        {monthGrid.map((date, i) => {
          const dateString = toDateString(date);
          const dataForDay = monthlyDataMap.get(dateString); // 해당 날짜의 데이터

          const inCurrentMonth = date.getMonth() === viewYearMonth.month;
          const isToday = toDateString(date) === toDateString(today);
          const isSelected = toDateString(date) === toDateString(selectedDate);

          return (
            <button
              key={i}
              onClick={() => handleDateClick(date)}
              disabled={!inCurrentMonth} // 현재 월 날짜만 클릭 가능
              className={`
                h-16 flex flex-col items-center justify-start p-1 rounded-lg transition-colors relative
                ${!inCurrentMonth ? "text-gray-300" : "text-gray-800 hover:bg-gray-100"}
                ${isSelected ? " bg-purple-100 ring-2 ring-purple-300" : ""}
                ${isToday ? " font-bold" : ""}
              `}
            >
              {/* 날짜 */}
              <span className={isSelected ? "text-purple-700" : isToday ? "text-blue-600" : ""}>
                {date.getDate()}
              </span>
              
                {/* [!!!] UI 추천: 도트(Dot) 시스템 [!!!] */}
                {/* 아이콘 대신 작은 점으로 공간을 절약합니다. */}
                {dataForDay && inCurrentMonth && (
                <div className="flex justify-center items-center space-x-1 mt-1 absolute bottom-2 left-0 right-0">
                  {/* 1. 복약 기록 (초록색 점) */}
                  {dataForDay.medicationsTaken && dataForDay.medicationsTaken.length > 0 && (
                    <div className="w-1.5 h-1.5 rounded-full bg-green-500" title={dataForDay.medicationsTaken.join(', ')}></div>
                  )}
                  {/* 2. 퀴즈 기록 (파란색 점) */}
                  {dataForDay.quizCorrectCount != null && (
                    <div className="w-1.5 h-1.5 rounded-full bg-blue-500" title={`퀴즈: ${dataForDay.quizCorrectCount}개`}></div>
                 )}
                  {/* 3. 감정 기록 (노란색 점) */}
                  {/* [수정] DTO에 moodIcon가 반환될 것이므로, status로 체크 */}
                  {dataForDay.moodIcon && (
                     <div className="w-1.5 h-1.5 rounded-full bg-yellow-400" title={`감정: ${dataForDay.moodIcon}`}></div>
                  )}
                </div>
              )}
            </button>
          );
        })}
      </div>
    </div>
  );
}

