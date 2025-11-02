import React from "react";
import HomePage from "../pages/HomePage";
import AlarmPage from "../pages/AlarmPage";
import MissionPage from '../pages/MissionPage';
import QuizPage from '../pages/QuizPage';
import LocationMissionPage from '../pages/LocationMissionPage';
import Mypage from "../pages/Mypage";
import HealthyPage from "../pages/HealthyPage";

const home = [
  {
      path: "home",
      element: <HomePage/>
  },
  {
    path: "alarm",
    element: <AlarmPage/>
  },
  {
    path: "mission",
    element: <MissionPage/>
  },
  {
    path: "healthy",
    element: <HealthyPage />
  },
  {
    path: "my",
    element: <Mypage/>
  },
  {
    path: "quiz",
    element: <QuizPage />
  },
  {
    path: "mission/locations",
    element: <LocationMissionPage />
  },
];

export default home;
