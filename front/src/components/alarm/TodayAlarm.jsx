import React, { useState, useEffect, useMemo } from "react";
import { PiWarningFill } from "react-icons/pi";
import { GoChevronRight, GoX, GoCheck } from "react-icons/go";
import { IoIosAdd, IoIosCheckboxOutline } from "react-icons/io";
import { useNavigate } from "react-router-dom";
import axios from 'axios';

// 시간 포맷 유틸리티 함수
const formatTime = (timeStr) => {
    if (!timeStr) return '';
    try {
        const [hour, minute] = timeStr.split(':');
        const hourNum = parseInt(hour, 10);
        const period = hourNum < 12 ? '오전' : '오후';
        const displayHour = hourNum === 0 ? 12 : hourNum > 12 ? hourNum - 12 : hourNum;
        return `${period} ${displayHour}:${minute}`;
    } catch (e) { console.error("Error formatting time:", timeStr, e); return timeStr; }
};

// Date 객체에서 요일 문자열 반환
const getDayStringFromDate = (date) => {
    if (!(date instanceof Date) || isNaN(date)) { console.error("Invalid date:", date); return ''; }
    const dayIndex = date.getDay();
    const days = ['일', '월', '화', '수', '목', '금', '토'];
    return days[dayIndex];
};

// props: alarms, selectedDate, userInfo, setUserInfo, checkedAlarmsByDate, onCheckSuccess
const TodayAlarm = ({ alarms = [], selectedDate, userInfo, setUserInfo, checkedAlarmsByDate, onCheckSuccess }) => {
  const navigate = useNavigate();
  const [isWarning, setIsWarning] = useState(true); // 알림 설정 경고

  const yy = selectedDate.getFullYear();
  const mm = (selectedDate.getMonth() + 1).toString().padStart(2, '0');
  const dd = selectedDate.getDate().toString().padStart(2, '0');
  // 선택된 날짜 문자열 (YYYY-MM-DD)
  const selectedDateString = `${yy}-${mm}-${dd}`;

  console.log(selectedDateString);
  // 선택된 날짜에 표시할 알람 목록 필터링
  const filteredAlarms = useMemo(() => {
      if (!selectedDateString) return [];
      const selectedDayString = getDayStringFromDate(selectedDate);
      if (!selectedDayString) return [];
      const selectedDateStart = new Date(selectedDate); // 시간 00:00

      return alarms.filter(alarm => {
            if (!Array.isArray(alarm.notificationDays)) return false;
            let isAfterCreation = true;
            if (alarm.createdAt) { // createdAt 필드가 있다고 가정
                try {
                    const creationDate = new Date(alarm.createdAt.split('T')[0]);
                    creationDate.setHours(0, 0, 0, 0);
                    isAfterCreation = selectedDateStart.getTime() >= creationDate.getTime();
                } catch (e) { console.error("Error parsing createdAt:", alarm.createdAt, e); }
            }
            const isDayMatch = alarm.notificationDays.includes("매일") || alarm.notificationDays.includes(selectedDayString);
            return isDayMatch && isAfterCreation;
       });
  }, [alarms, selectedDate, selectedDateString]);

  // 알람 체크오프 핸들러
  const handleCheckOff = async (alarmId) => {
    const checkedSetForThisDate = checkedAlarmsByDate[selectedDateString] || new Set();
    if (checkedSetForThisDate.has(alarmId)) { alert("이미 체크 완료된 알람입니다."); return; }
    console.log(`Checking off alarm: ${alarmId} for date: ${selectedDateString}`);
    const previousUserInfo = userInfo ? { ...userInfo } : null;

    try {
      const token = localStorage.getItem('accessToken');
      if (!token) throw new Error('토큰 없음');
      const response = await axios.post(
        `http://localhost:8080/api/user/alarms/${alarmId}/check`, null,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      const updatedUserInfo = response.data;
      setUserInfo(updatedUserInfo); // 부모 userInfo 상태 업데이트
      console.log('Alarm checked off! Updated user info:', updatedUserInfo);
      if (onCheckSuccess) onCheckSuccess(selectedDate); // 부모에게 성공 알림

      // 보상 계산 및 페이지 이동
      if (previousUserInfo) {
          let rewardType = null, rewardAmount = 0;
          if (updatedUserInfo.userWater > previousUserInfo.userWater) {
              rewardType = 'water'; rewardAmount = updatedUserInfo.userWater - previousUserInfo.userWater;
          } else if (updatedUserInfo.userAffection > previousUserInfo.userAffection) {
              rewardType = 'affection'; rewardAmount = updatedUserInfo.userAffection - previousUserInfo.userAffection;
          }
          if (rewardType && rewardAmount > 0) {
              console.log(`Reward calculated: ${rewardType} x ${rewardAmount}`);
              navigate('/reward', { 
                state: { reward: { type: rewardType, amount: rewardAmount }, from: "alarm" } });
              return;
          } else { console.log("No reward given."); alert('복용 체크 완료!'); }
      } else { alert('복용 체크 완료!'); }

    } catch (error) {
      console.error('Failed to check off alarm:', error);
      const message = error.response?.data?.message || error.message || '알람 체크 실패';
      alert(message);
    }
  };

  // '추가하기' 버튼 핸들러
  const handleAddAlarm = () => { navigate('/alarm-setup', { state: "alarmpage" }); };

  // 현재 날짜에 체크된 알람 ID Set (부모 상태 사용)
  const checkedTodaySet = checkedAlarmsByDate[selectedDateString] || new Set();

  return (
    <div className="relative p-3 pt-5 flex flex-col flex-grow">
      {/* 알림 설정 경고 */}
      {isWarning && ( <div className="mb-5 p-3 h-auto bg-purple-50 rounded-2xl text-xs flex justify-between items-start"> <PiWarningFill size={30} color="#B259FF" className="flex-shrink-0 mt-1 mr-2"/> <div className="min-h-10 flex-col content-center flex-grow mr-2"> <div className="w-full mb-1">설정에서 알림을 허용해 주셔야 복용 알림이 도착할 수 있습니다.</div> <div className="flex items-center text-[#B259FF] font-semibold cursor-pointer">설정 바로가기 <GoChevronRight /></div> </div> <GoX size={20} color="#777777" className="cursor-pointer flex-shrink-0" onClick={() => setIsWarning(false)} /> </div> )}
      {/* 알람 목록 */}
      <div className="space-y-3 flex-grow">
        {filteredAlarms.length === 0 ? ( <p className="text-center text-gray-500 py-10">선택된 날짜에 복약 일정이 없습니다.</p> ) : (
          filteredAlarms.map((alarm) => {
            if (alarm.id === undefined || alarm.id === null) return null;
            const isChecked = checkedTodaySet.has(alarm.id); // 체크 여부
            const isEnabled = alarm.enabled; // 활성화 여부
            return (
              <div key={alarm.id} className={`flex items-center transition-opacity ${!isEnabled ? 'opacity-50' : ''}`}>
                <div className={`w-[60px] text-sm text-center font-semibold ${isChecked ? 'text-[#B259FF]' : 'text-gray-500'}`}> {formatTime(alarm.notificationTime)} </div>
                <div className={`self-stretch border-l-2 px-3 ${isChecked ? 'border-[#B259FF]' : 'border-gray-300'}`} />
                <div className={`flex-grow rounded-2xl min-h-[70px] flex justify-between px-4 py-2 items-center transition-colors ${isChecked ? 'bg-[#B259FF] text-white' : 'bg-gray-100 text-black'}`}>
                  <div><div className="font-bold">{alarm.medicationName}</div></div>
                  {isEnabled && !isChecked && ( <button onClick={() => handleCheckOff(alarm.id)} className="cursor-pointer text-[#B259FF] hover:text-purple-700 p-2" title="복용 완료 체크"><IoIosCheckboxOutline size={32} /></button> )}
                  {isChecked && (<span className="text-white p-2" title="복용 완료"><GoCheck size={28}/></span>)}
                  {!isEnabled && (<span className="text-gray-400 p-2" title="알람 비활성화됨"><IoIosCheckboxOutline size={32} /></span>)}
                </div>
              </div>
            );
          })
        )}
      </div>
      {/* 추가하기 버튼 */}
      <div className="mt-auto pt-4 pb-2 w-full flex justify-center">
        <button onClick={handleAddAlarm} className="w-32 h-10 bg-[#B259FF] text-white flex justify-center items-center rounded-md shadow hover:bg-purple-700 transition-colors">
          <div className="text-sm font-semibold mr-1">추가하기</div> <IoIosAdd size={24} />
        </button>
      </div>
    </div>
  );
};

export default TodayAlarm;