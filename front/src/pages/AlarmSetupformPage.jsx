import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import AuthLayout from '../components/auth/AuthLayout';
import axios from 'axios';

// 요일 선택 버튼 컴포넌트
const DayButton = ({ day, selected, onClick }) => (
  <button
    type="button"
    onClick={() => onClick(day)}
    className={`w-10 h-10 rounded-full border text-sm font-medium transition-colors ${
      selected ? 'bg-[#A755F8] text-white border-[#A755F8]' : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
    }`}
  >
    {day}
  </button>
);

export default function AlarmSetupFormPage() {
  const navigate = useNavigate();
  const location = useLocation(); // 이전 페이지 정보 확인용
  const cameFromListPage = location.state?.fromList === true;
  const [medicationName, setMedicationName] = useState('');
  const [timesPerDay, setTimesPerDay] = useState(1);
  const [alarms, setAlarms] = useState([
    { time: '07:00', isEveryday: true, selectedDays: new Set(['매일']) }
  ]);

  useEffect(() => {
    setAlarms(prevAlarms => {
      const currentLength = prevAlarms.length;
      const newLength = timesPerDay;
      if (newLength > currentLength) {
        const newItems = Array(newLength - currentLength).fill(null).map(() => ({
          time: '09:00', isEveryday: true, selectedDays: new Set(['매일'])
        }));
        return [...prevAlarms, ...newItems];
      } else if (newLength < currentLength) {
        return prevAlarms.slice(0, newLength);
      }
      return prevAlarms;
    });
  }, [timesPerDay]);

  const daysOfWeek = ['월', '화', '수', '목', '금', '토', '일'];

  const handleTimeChange = (index, newTime) => {
    setAlarms(prev => prev.map((alarm, i) => i === index ? { ...alarm, time: newTime } : alarm));
  };
  const handleEverydayChange = (index, checked) => {
    setAlarms(prev => prev.map((alarm, i) => i === index ? {
      ...alarm,
      isEveryday: checked,
      selectedDays: checked ? new Set(['매일']) : new Set(['월','화','수','목','금','토','일'])
    } : alarm));
  };
  const handleDayClick = (index, day) => {
    setAlarms(prev => prev.map((alarm, i) => {
      if (i === index) {
        const newDays = new Set(alarm.selectedDays);
        if (newDays.has('매일')) newDays.delete('매일');
        if (newDays.has(day)) newDays.delete(day); else newDays.add(day);
        const isNowEveryday = newDays.size === 0 || newDays.size === 7;
        if (isNowEveryday) { newDays.clear(); newDays.add('매일'); }
        return { ...alarm, selectedDays: newDays, isEveryday: isNowEveryday };
      }
      return alarm;
    }));
  };

  const handleComplete = async () => {
    if (!medicationName.trim()) return alert('복약 종류를 입력하세요.');
    const isValid = alarms.every(alarm => alarm.selectedDays.size > 0);
    if (!isValid) return alert('모든 알람의 복용 요일을 선택하세요.');

    const alarmsToSave = alarms.map(a => ({
        medicationName: medicationName.trim(),
        notificationTime: a.time,
        notificationDays: Array.from(a.selectedDays),
        enabled: true,
    }));

    try {
      const token = localStorage.getItem('accessToken');
      if (!token) throw new Error('로그인 토큰이 없습니다.');

      console.log("Saving new alarms:", alarmsToSave);
      const response = await axios.post(
<<<<<<< HEAD
<<<<<<< HEAD
        'http://43.201.68.38:8080/api/user/alarms',
=======
        'http://localhost:8080/api/user/alarms',
>>>>>>> cad27c24f4c0e36f3b2cf20dbc2e1a6ebc5e11dd
=======
        'http://localhost:8080/api/user/alarms',
>>>>>>> 1e287e9 (demo v1)
        alarmsToSave, // DTO 리스트 전송
        { headers: { Authorization: `Bearer ${token}` } }
      );
      console.log('Alarm(s) added successfully:', response.data);

      if (location.state == "alarmpage") {
        navigate('/main/alarm');
      } else {
        navigate('/select-plant');
      }
    } catch (error) {
      console.error('Failed to add alarm:', error);
      alert(error.response?.data?.message || error.message || '알람 저장에 실패했습니다.');
    }
  };

  return (
    <AuthLayout title="복약 알림 설정" onNext={handleComplete} nextButtonText="완료">
      <div className="space-y-6">
        {/* 복약 종류 */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">복약 종류</label>
          <input type="text" value={medicationName} onChange={e => setMedicationName(e.target.value)} placeholder="ex) 비타민" className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:border-[#A755F8] focus:ring-1 focus:ring-[#A755F8]" />
        </div>
        {/* 일일 복용 횟수 */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">일일 복용 횟수</label>
          <div className="flex items-center gap-2">
            <input type="number" min="1" max="10" value={timesPerDay} onChange={e => setTimesPerDay(Math.max(1, parseInt(e.target.value) || 1))} className="w-16 p-3 text-center border border-gray-300 rounded-lg focus:outline-none focus:border-[#A755F8]" />
            <span>회</span>
          </div>
        </div>
        {/* 동적으로 생성되는 알람 설정 UI */}
        {alarms.map((alarm, index) => (
          <div key={index} className={`border-gray-200 ${index > 0 ? 'border-t pt-4 mt-4' : ''}`}>
            <label className="block text-sm font-medium text-gray-700 mb-2">복용 시간 {index + 1}</label>
            {/* 매일 체크박스 */}
            <div className="flex items-center mb-3">
              <label className="flex items-center space-x-2 cursor-pointer">
                <input type="checkbox" checked={alarm.isEveryday} onChange={(e) => handleEverydayChange(index, e.target.checked)} className="form-checkbox h-4 w-4 text-[#A755F8] border-gray-300 rounded focus:ring-[#A755F8]" />
                <span className="text-sm">매일</span>
              </label>
            </div>
            {/* 요일 버튼 */}
            <div className="flex justify-between mb-4">
              {daysOfWeek.map(day => ( <DayButton key={day} day={day} selected={alarm.selectedDays.has(day)} onClick={() => handleDayClick(index, day)} /> ))}
            </div>
            {/* 시간 선택 */}
            <input type="time" value={alarm.time} onChange={(e) => handleTimeChange(index, e.target.value)} className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:border-[#A755F8]" />
          </div>
        ))}
      </div>
    </AuthLayout>
  );
}