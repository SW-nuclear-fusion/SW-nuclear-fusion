import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import TodayAlarm from "../components/alarm/TodayAlarm";
import WeeklyMonthlyCalendar from "../components/alarm/WeeklyMonthlyCalendar";

const AlarmPage = () => {
  const navigate = useNavigate();
  const [alarms, setAlarms] = useState([]);
  const [userInfo, setUserInfo] = useState(null);
  const [selectedDate, setSelectedDate] = useState(() => {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return today;
  });
  const [isLoading, setIsLoading] = useState(true);
  const [checkedAlarmsByDate, setCheckedAlarmsByDate] = useState({});
  const fetchData = useCallback(async () => {
    setIsLoading(true);
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        console.error("No token found, redirecting to login.");
        navigate('/login');
        return;
      }
      const config = { headers: { Authorization: `Bearer ${token}` } };
      const [alarmRes, userRes] = await Promise.all([
        axios.get('http://localhost:8080/api/user/alarms', config),
        axios.get('http://localhost:8080/api/user/me', config)
      ]);

      setAlarms(alarmRes.data);
      setUserInfo(userRes.data);
      console.log("Fetched alarms:", alarmRes.data);
      console.log("Fetched user info:", userRes.data);
    } catch (error) {
      console.error('Failed to fetch data:', error);
      if (error.response?.status === 401 || error.response?.status === 403) {
          navigate('/');
      }
    } finally {
      setIsLoading(false);
    }
  }, [navigate]);

  const fetchCheckedAlarms = useCallback(async (date) => {
      if (!(date instanceof Date) || isNaN(date)) {
          console.error("fetchCheckedAlarms: Invalid date provided", date);
          return;
      }
      const yy = date.getFullYear();
      const mm = (date.getMonth() + 1).toString().padStart(2, '0');
      const dd = date.getDate().toString().padStart(2, '0');
      const dateString = `${yy}-${mm}-${dd}`;
      console.log(`Fetching checked alarms for date: ${dateString}`);
      try {
          const token = localStorage.getItem('accessToken');
          if (!token) return;
          const response = await axios.get(
              `http://localhost:8080/api/user/alarms/checked?date=${dateString}`,
              { headers: { Authorization: `Bearer ${token}` } }
          );

          const checkedIdsSet = new Set(response.data);
          setCheckedAlarmsByDate(prev => ({
              ...prev,
              [dateString]: checkedIdsSet
          }));
      } catch (error) {
          console.error(`Failed to fetch checked alarms for ${dateString}:`, error);
      }
  }, []);

  useEffect(() => {
    fetchData().then(() => {
        fetchCheckedAlarms(selectedDate);
    });
  }, [fetchData]);

  useEffect(() => {
      if (!(selectedDate instanceof Date) || isNaN(selectedDate)) return;

      const yy = selectedDate.getFullYear();
      const mm = (selectedDate.getMonth() + 1).toString().padStart(2, '0');
      const dd = selectedDate.getDate().toString().padStart(2, '0');
      const dateString = `${yy}-${mm}-${dd}`;
      if (checkedAlarmsByDate[dateString] === undefined) {
           fetchCheckedAlarms(selectedDate);
      }
  }, [selectedDate, fetchCheckedAlarms, checkedAlarmsByDate]);

  const handleDateChange = (newDate) => {
      if (!(newDate instanceof Date) || isNaN(newDate)) return;
      const dateOnly = new Date(newDate.getFullYear(), newDate.getMonth(), newDate.getDate());
      setSelectedDate(dateOnly);
  };

  const handleAlarmCheckSuccess = useCallback((dateChecked, checkedAlarmId) => {
      if (!(dateChecked instanceof Date) || isNaN(dateChecked) || checkedAlarmId === undefined) return;
      const yy = date.getFullYear();
      const mm = (date.getMonth() + 1).toString().padStart(2, '0');
      const dd = date.getDate().toString().padStart(2, '0');
      const dateString = `${yy}-${mm}-${dd}`;
      
      setCheckedAlarmsByDate(prev => {
          const currentCheckedSet = prev[dateString] ? new Set(prev[dateString]) : new Set();
          currentCheckedSet.add(checkedAlarmId);
          return {
            ...prev,
            [dataString]: currentCheckedSet
          }});
  }, []);

  return (
    <div className="flex flex-col h-screen">
      <div className="bg-gradient-to-t from-[#B259FF] from-55% to-[#D3A7FA] flex-shrink-0">
        {/* 페이지 헤더 */}
        <header className="h-[48px] flex items-center justify-center text-white font-semibold">복약 알림</header>
        {/* 주간/월간 달력 컴포넌트 렌더링 및 props 전달 */}
        <WeeklyMonthlyCalendar
            selectedDate={selectedDate}
            onDateChange={handleDateChange}
        />
      </div>

      <div className="flex-grow overflow-y-auto bg-white rounded-t-3xl relative -mt-3 z-10">
        {isLoading ? (
          <p className="text-center p-10 text-gray-500">알람 정보를 불러오는 중...</p>
        ) : (
          <TodayAlarm
            alarms={alarms}
            selectedDate={selectedDate}
            userInfo={userInfo}
            setUserInfo={setUserInfo}
            checkedAlarmsByDate={checkedAlarmsByDate}
            onCheckSuccess={handleAlarmCheckSuccess}
          />
        )}
      </div>
    </div>
  );
};

export default AlarmPage;