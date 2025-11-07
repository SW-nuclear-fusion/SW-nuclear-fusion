import React, { useState, useEffect, useMemo, useCallback } from 'react'; // useCallback 추가
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { FaPlus, FaCheck, FaTrashAlt, FaEdit } from "react-icons/fa"; // 아이콘 추가
import { IoIosCheckboxOutline } from "react-icons/io";

const formatTime = (timeStr) => { /* ... 이전 코드와 동일 ... */ };

export default function AlarmListPage() {
  const navigate = useNavigate();
  const [alarms, setAlarms] = useState([]);
  const [userInfo, setUserInfo] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedDate, setSelectedDate] = useState(() => {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return today;
  });
  // 날짜별 체크 상태 관리
  const [checkedAlarmsByDate, setCheckedAlarmsByDate] = useState({});
  const selectedDateString = selectedDate.toISOString().split('T')[0];

  // 데이터 로딩 함수
  const fetchData = useCallback(async () => {
    setIsLoading(true);
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) { navigate('/login'); return; }
      const config = { headers: { Authorization: `Bearer ${token}` } };
      const [alarmRes, userRes] = await Promise.all([
<<<<<<< HEAD
        axios.get('http://43.201.68.38:8080/api/user/alarms', config),
        axios.get('http://43.201.68.38:8080/api/user/me', config)
=======
        axios.get('http://localhost:8080/api/user/alarms', config),
        axios.get('http://localhost:8080/api/user/me', config)
>>>>>>> cad27c24f4c0e36f3b2cf20dbc2e1a6ebc5e11dd
      ]);
      setAlarms(alarmRes.data);
      setUserInfo(userRes.data);
      console.log("Fetched alarms:", alarmRes.data);
    } catch (error) {
      console.error('Failed to fetch data:', error);
      if (error.response?.status === 401 || error.response?.status === 403) navigate('/login');
    } finally {
      setIsLoading(false);
    }
  }, [navigate]);

  // 마운트 시 데이터 로딩
  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // 알람 체크오프 핸들러
  const handleCheckOff = async (alarmId) => { /* ... 이전 코드와 동일 (navigate('/reward', ...) 포함) ... */ };

  // 알람 토글 핸들러
  const handleToggle = async (alarmId, currentStatus) => {
    setAlarms(prev => prev.map(a => a.id === alarmId ? {...a, enabled: !currentStatus} : a));
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) throw new Error('토큰 없음');
      await axios.patch( // PATCH API 호출
<<<<<<< HEAD
        `http://43.201.68.38:8080/api/user/alarms/${alarmId}/toggle`, null,
=======
        `http://localhost:8080/api/user/alarms/${alarmId}/toggle`, null,
>>>>>>> cad27c24f4c0e36f3b2cf20dbc2e1a6ebc5e11dd
        { headers: { Authorization: `Bearer ${token}` } }
      );
      console.log(`Alarm ${alarmId} toggled successfully.`);
    } catch (error) {
      console.error('Failed to toggle alarm:', error);
      alert('알람 상태 변경 실패');
      setAlarms(prev => prev.map(a => a.id === alarmId ? {...a, enabled: currentStatus} : a));
    }
  };

  // 알람 삭제 핸들러
  const handleDelete = async (alarmId) => {
    if (!window.confirm("정말로 이 알람을 삭제하시겠습니까?")) return; // 사용자 확인

    // UI 즉시 업데이트
    const originalAlarms = [...alarms]; // 롤백용 원본 저장
    setAlarms(prev => prev.filter(a => a.id !== alarmId));
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) throw new Error('토큰 없음');
      await axios.delete( // DELETE API 호출
<<<<<<< HEAD
        `http://43.201.68.38:8080/api/user/alarms/${alarmId}`,
=======
        `http://localhost:8080/api/user/alarms/${alarmId}`,
>>>>>>> cad27c24f4c0e36f3b2cf20dbc2e1a6ebc5e11dd
        { headers: { Authorization: `Bearer ${token}` } }
      );
      console.log(`Alarm ${alarmId} deleted successfully.`);
      // 성공 시 별도 처리 없음
    } catch (error) {
      console.error('Failed to delete alarm:', error);
      alert('알람 삭제 실패');
      // 실패 시 UI 롤백
      setAlarms(originalAlarms);
    }
  };


  // '+' 버튼 클릭 핸들러 (fromList 상태 전달)
  const handleAddAlarm = () => {
    navigate('/alarm-setup', { state: { fromList: true } });
  };

  // 선택된 날짜에 표시할 알람 필터링
  const filteredAlarms = useMemo(() => { /* ... 이전 코드와 동일 ... */ }, [alarms, selectedDate]);
  const checkedTodaySet = checkedAlarmsByDate[selectedDateString] || new Set();

  return (
    <div className="flex flex-col h-screen bg-gray-50">
      {/* 상단 헤더 */}
      <header className="bg-white shadow p-4 flex items-center sticky top-0 z-20">
         <button onClick={() => navigate('/main')} className="text-xl p-2 -ml-2"> {/* 홈으로 가기 */}
             &larr;
         </button>
         <h1 className="text-lg font-semibold mx-auto">복약 알림</h1>
         <div className="w-8"></div>
      </header>

      {/* 달력 */}
      <div className="p-4 text-center text-lg font-semibold">
          {selectedDate.toLocaleDateString('ko-KR', { month: 'long', day: 'numeric', weekday: 'long' })}
      </div>


      {/* 알람 목록 */}
      <main className="flex-grow overflow-y-auto p-4 space-y-3">
        {/* 안내 메시지 */}
        <div className="bg-purple-100 border border-purple-200 text-purple-700 px-4 py-3 rounded relative" role="alert">
          <span className="block sm:inline"> 설정한 시간에 맞춰 복용을 체크하고 보상을 받으세요!</span>
        </div>

        {isLoading && <p className="text-center text-gray-500 py-10">로딩 중...</p>}

        {/* 알람 목록 렌더링 */}
        {!isLoading && filteredAlarms.length === 0 && ( <p className="text-center text-gray-500 py-10">알람 없음</p> )}
        {!isLoading && filteredAlarms.map((alarm) => {
          if (alarm.id === undefined || alarm.id === null) return null;
          const isChecked = checkedTodaySet.has(alarm.id);
          const isEnabled = alarm.enabled;

          return (
            <div key={alarm.id} className={`flex items-center p-4 rounded-lg shadow transition-opacity ${!isEnabled ? 'opacity-60 bg-gray-200' : isChecked ? 'bg-purple-100 border border-purple-300' : 'bg-white'}`}>
              {/* 시간 및 약 이름 */}
              <div className="flex-grow">
                <span className={`font-semibold ${isChecked ? 'text-purple-600' : 'text-gray-500'}`}>{formatTime(alarm.notificationTime)}</span>
                <p className={`font-bold text-lg ${isChecked ? 'text-purple-800' : 'text-gray-800'}`}>{alarm.medicationName}</p>
                 {/* 요일 */}
                <p className="text-xs text-gray-500 mt-1">{Array.from(alarm.notificationDays).join(', ')}</p>
              </div>
              {/* 버튼 그룹 */}
              <div className="flex items-center gap-2 flex-shrink-0 ml-4">
                  {/* 토글 버튼 */}
                  <label className="inline-flex items-center cursor-pointer" title={isEnabled ? "알람 끄기" : "알람 켜기"}>
                    <input type="checkbox" checked={isEnabled} onChange={() => handleToggle(alarm.id, isEnabled)} className="sr-only peer"/>
                    <div className="relative w-11 h-6 bg-gray-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-0.5 after:start-[2px] after:bg-white after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#A755F8]"></div>
                  </label>
                  {/* 체크 버튼 */}
                  {isEnabled && !isChecked && (
                    <button onClick={() => handleCheckOff(alarm.id)} className="text-green-500 hover:text-green-700 p-1" title="복용 완료 체크"><IoIosCheckboxOutline size={30} /></button>
                  )}
                  {isChecked && (<span className="text-green-500 p-1" title="복용 완료"><FaCheck size={24}/></span>)}
                   {!isEnabled && (<span className="text-gray-400 p-1"><IoIosCheckboxOutline size={30} /></span>)}
                   {/* 삭제 버튼 */}
                   <button onClick={() => handleDelete(alarm.id)} className="text-red-500 hover:text-red-700 p-1" title="알람 삭제"><FaTrashAlt size={18} /></button>
                   {/* 수정 버튼 (기능 미구현) */}
              </div>
            </div>
          );
        })}

        {/* 알람 추가 버튼 */}
        <button
            onClick={handleAddAlarm}
            className="mt-6 w-full bg-[#A755F8] hover:bg-purple-700 text-white font-semibold py-3 px-4 rounded-lg shadow flex items-center justify-center gap-2 transition-colors"
        >
          <FaPlus /> 알람 추가하기
        </button>

        {/* 보유 자원 */}
        {userInfo && ( <div className="text-center text-sm text-gray-600 pt-4"> <span>💧 {userInfo.userWater}</span> | <span>❤️ {userInfo.userAffection}</span> </div> )}

      </main>

      <BottomNav />
    </div>
  );
}