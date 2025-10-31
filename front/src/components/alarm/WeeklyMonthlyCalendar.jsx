import React, { useMemo, useState, useEffect, useCallback } from "react";
import { FaRegCalendar } from "react-icons/fa6";

/* --- 날짜 계산 헬퍼 함수들 --- */
const getStartOfWeekMonday = (date = new Date()) => {
  if (!(date instanceof Date) || isNaN(date)) date = new Date(); // 유효하지 않으면 오늘 날짜
  const d = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  const day = d.getDay();
  const daysSinceMonday = (day + 6) % 7;
  return new Date(d.getFullYear(), d.getMonth(), d.getDate() - daysSinceMonday);
};
const buildWeekDatesFromMonday = (startOfWeekMonday) => {
  if (!(startOfWeekMonday instanceof Date) || isNaN(startOfWeekMonday)) return []; // 유효하지 않으면 빈 배열
  const arr = [];
  for (let i = 0; i < 7; i++) { arr.push( new Date( startOfWeekMonday.getFullYear(), startOfWeekMonday.getMonth(), startOfWeekMonday.getDate() + i ) ); }
  return arr;
};
const buildMonthGrid = (year, month) => {
  if (isNaN(year) || isNaN(month)) return []; // 유효하지 않으면 빈 배열
  const firstOfMonth = new Date(year, month, 1);
  const firstDayWeekday = firstOfMonth.getDay();
  const leading = (firstDayWeekday + 6) % 7;
  const totalCells = 42;
  const grid = [];
  for (let i = -leading + 1; i <= totalCells - leading; i++) { grid.push(new Date(year, month, i)); }
  return grid;
};
const korWeekNames = ["월", "화", "수", "목", "금", "토", "일"];

const WeeklyMonthlyCalendar = ({ selectedDate, onDateChange }) => {
  const today = useMemo(() => { const d = new Date(); d.setHours(0, 0, 0, 0); return d; }, []);

  // selectedDate 유효성 검사 추가 (렌더링 오류 방지)
  const validSelectedDate = selectedDate instanceof Date && !isNaN(selectedDate) ? selectedDate : today;

  const startOfWeek = useMemo(() => getStartOfWeekMonday(validSelectedDate), [validSelectedDate]);
  const weekDates = useMemo(() => buildWeekDatesFromMonday(startOfWeek), [startOfWeek]);
  const [isMonthModalOpen, setIsMonthModalOpen] = useState(false);
  const [modalYearMonth, setModalYearMonth] = useState({ year: validSelectedDate.getFullYear(), month: validSelectedDate.getMonth() });

  const openMonthModal = () => { setModalYearMonth({ year: validSelectedDate.getFullYear(), month: validSelectedDate.getMonth() }); setIsMonthModalOpen(true); };
  const closeMonthModal = () => { setIsMonthModalOpen(false); };
  const prevMonth = () => { setModalYearMonth(s => { const m = s.month - 1; return m < 0 ? { year: s.year - 1, month: 11 } : { year: s.year, month: m }; }); };
  const nextMonth = () => { setModalYearMonth(s => { const m = s.month + 1; return m > 11 ? { year: s.year + 1, month: 0 } : { year: s.year, month: m }; }); };
  const handleKeyDown = useCallback( (e) => { if (e.key === "Escape" && isMonthModalOpen) closeMonthModal(); }, [isMonthModalOpen]);

  useEffect(() => {
    if (isMonthModalOpen) { window.addEventListener("keydown", handleKeyDown); document.body.style.overflow = "hidden"; }
    else { window.removeEventListener("keydown", handleKeyDown); document.body.style.overflow = ""; }
    return () => { window.removeEventListener("keydown", handleKeyDown); document.body.style.overflow = ""; };
  }, [isMonthModalOpen, handleKeyDown]);

  const monthGrid = useMemo(() => buildMonthGrid(modalYearMonth.year, modalYearMonth.month), [modalYearMonth]);
  const onClickWeekDay = (date) => { onDateChange(new Date(date.getFullYear(), date.getMonth(), date.getDate())); };
  const onClickMonthDay = (date) => { onDateChange(new Date(date.getFullYear(), date.getMonth(), date.getDate())); closeMonthModal(); };

  return (
    <>
      {/* 주간 캘린더 UI */}
      <div className="h-[145px] max-w-xl mx-auto p-4">
        <div className="flex items-center justify-between">
            <div onClick={() => onDateChange(today)} className="cursor-pointer" title="오늘 날짜로 이동">
                <div className="text-2xl text-white font-medium">
                {validSelectedDate.toLocaleString("ko-KR", { month: "long" })}{" "}
                {validSelectedDate.toLocaleString("ko-KR", { day: "numeric" })}
                </div>
            </div>
            <button onClick={openMonthModal} aria-label="월간 달력 보기" className="p-2 rounded-full hover:bg-white/10 active:scale-95 transition-colors" title="전체 월 보기">
                <FaRegCalendar className="w-5 h-5 text-white" />
            </button>
        </div>
        <div className="text-white rounded-xl pt-2">
            <div className="flex justify-between items-center">
                {weekDates.map((d, idx) => {
                    const isToday = d.getTime() === today.getTime();
                    const isSelected = d.getTime() === validSelectedDate.getTime(); // validSelectedDate 사용
                    return (
                        <div key={idx} onClick={() => onClickWeekDay(d)} className={ `flex-1 cursor-pointer rounded-2xl select-none transition-all py-1 ${isSelected ? "bg-white text-black font-semibold" : isToday ? "bg-white/40 text-white" : "bg-transparent text-white hover:bg-white/10"}` }>
                            <div className="flex flex-col items-center">
                                <div className="text-sm pt-1 opacity-90">{korWeekNames[idx]}</div>
                                <div className="w-10 h-10 flex items-center justify-center text-lg">{d.getDate()}</div>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
      </div>

      {/* 월간 달력 모달 UI */}
      {isMonthModalOpen && (
        <>
          <div className="fixed inset-0 bg-black/40 z-40" onClick={closeMonthModal} aria-hidden="true" />
          <div className="fixed left-1/2 transform -translate-x-1/2 bottom-4 z-50 w-[92%] max-w-md bg-white rounded-2xl shadow-xl overflow-hidden animate-slideup" role="dialog" aria-modal="true">
            <div className="flex items-center justify-between px-4 py-3 border-b">
                <button onClick={prevMonth} className="p-2 rounded-md hover:bg-gray-100" aria-label="이전 달">◀</button>
                <div className="text-center font-semibold text-lg">{modalYearMonth.year}년 {new Date(modalYearMonth.year, modalYearMonth.month).toLocaleString("ko-KR", { month: "long" })}</div>
                <div className="flex items-center gap-2">
                    <button onClick={nextMonth} className="p-2 rounded-md hover:bg-gray-100" aria-label="다음 달">▶</button>
                    <button onClick={closeMonthModal} aria-label="닫기" className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center hover:bg-gray-200">✕</button>
                </div>
            </div>
            <div className="p-4">
              <div className="grid grid-cols-7 gap-1 text-xs text-center text-gray-500 mb-2"> {korWeekNames.map(k => <div key={k} className="py-1">{k}</div>)} </div>
              <div className="grid grid-cols-7 gap-1">
                {monthGrid.map((d, i) => {
                  const inCurrentMonth = d.getMonth() === modalYearMonth.month;
                  const isToday = d.getTime() === today.getTime();
                  const isSelected = d.getTime() === validSelectedDate.getTime(); // validSelectedDate 사용
                  return ( <button key={i} onClick={() => onClickMonthDay(d)} className={ `py-3 rounded-lg w-full h-12 flex items-center justify-center transition-colors text-sm ${inCurrentMonth ? "text-gray-800 hover:bg-gray-100" : "text-gray-300"} ${isSelected ? " bg-purple-100 ring-1 ring-purple-200 text-purple-700 font-semibold" : ""} ${isToday ? " font-bold underline" : ""}` } disabled={!inCurrentMonth} > <span>{d.getDate()}</span> </button> );
                })}
              </div>
            </div>
          </div>
          <style>{` @keyframes slideup { from { transform: translate(-50%, 100%); opacity: 0; } to { transform: translate(-50%, 0); opacity: 1; } } .animate-slideup { animation: slideup 250ms ease-out forwards; } `}</style>
        </>
      )}
    </>
  );
};

export default WeeklyMonthlyCalendar;