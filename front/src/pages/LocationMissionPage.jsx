import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import AuthLayout from '../components/auth/AuthLayout';
import { FaMapMarkerAlt, FaSpinner } from 'react-icons/fa';

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
}, error => Promise.reject(error));


export default function LocationMissionPage() {
  const navigate = useNavigate();
  const [missions, setMissions] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isCheckingGps, setIsCheckingGps] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMissions = async () => {
      setIsLoading(true);
      setError(null);
      try {
        const response = await api.get('/api/missions/locations');

        if (Array.isArray(response.data)) {
            setMissions(response.data);
        } else {
            console.error("Failed to fetch missions: Expected array but got something else.", response.data);
            setError('미션 목록을 불러오지 못했습니다. (인증 실패 가능성)');
            setMissions([]);
        }

      } catch (err) {
        console.error("Failed to fetch missions:", err);
        const message = err.response?.data?.message || err.message || '미션 목록 로딩 실패';
        setError(message);
        setMissions([]);
      } finally {
        setIsLoading(false);
      }
    };
    fetchMissions();
  }, []);

  const handleVisitAttempt = (missionId) => {
    if (isCheckingGps) return;
    setIsCheckingGps(missionId);
    setError(null);

    if (!navigator.geolocation) {
        alert("GPS를 사용할 수 없습니다.");
        setIsCheckingGps(null);
        return;
    }

    navigator.geolocation.getCurrentPosition(
      async (position) => {
        const { latitude, longitude } = position.coords;
        console.log("위도 :" + latitude);
        console.log("경도 : " + longitude);
        try {
          const response = await api.post(
            `/api/missions/visit/${missionId}`,
            { latitude, longitude }
          );
          navigate('/reward', { state: { reward: response.data, from: "quiz" } }); // 보상 페이지로
        } catch (err) {
          alert(err.response?.data?.message || '방문 인증 실패');
        } finally {
          setIsCheckingGps(null);
        }
      },
      (geoError) => {
        alert(`GPS 위치 확인 실패: ${geoError.message}`);
        setIsCheckingGps(null);
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
    );
  };

  return (
    <AuthLayout title="방문 인증 미션" nextButtonText="" showBackButton={true}>
      <div className="space-y-4">
        {isLoading && <p className="text-center">미션 목록 로딩 중...</p>}
        {error && <p className="text-center text-red-500">{error}</p>}
        {!isLoading && Array.isArray(missions) && missions.length > 0 && missions.map((mission) => (
          <div key={mission.id} className="bg-white rounded-lg p-4 shadow flex justify-between items-center">
            <div className="flex items-center gap-3">
              <FaMapMarkerAlt className="text-green-500" />
              <span className="font-semibold">{mission.name}</span>
            </div>
            <button
              onClick={() => handleVisitAttempt(mission.id)}
              disabled={isCheckingGps !== null}
              className="bg-[#A755F8] text-white px-4 py-2 rounded-lg text-sm font-semibold disabled:bg-gray-400 w-28"
            >
              {isCheckingGps === mission.id ? (
                <FaSpinner className="animate-spin mx-auto" />
              ) : ( "방문 인증" )}
            </button>
          </div>
        ))}
        {!isLoading && Array.isArray(missions) && missions.length === 0 && !error && (
            <p className="text-center text-gray-500">진행 가능한 방문 미션이 없습니다.</p>
        )}
      </div>
    </AuthLayout>
  );
}