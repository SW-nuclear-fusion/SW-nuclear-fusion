import React from 'react';
import AuthStartPage from '../pages/AuthStartPage';
import LoginPage from '../pages/LoginPage';
import SignUpStep1_Id from '../pages/signup/SignUpStep1_Id';
import SignUpStep2_Phone from '../pages/signup/SignUpStep2_Phone';
import SignUpStep3_FontSize from '../pages/signup/SignUpStep3_FontSize';
import SignUpStep4_Role from '../pages/signup/SignUpStep4_Role';
import SignUpStep5_Name from '../pages/signup/SignUpStep5_Name';
import SignUpStep6_Birthdate from '../pages/signup/SignUpStep6_Birthdate';
import SignUpStep7_Gender from '../pages/signup/SignUpStep7_Gender';
import WelcomePage from '../pages/WelcomePage';
import PlantSelectionPage from '../pages/PlantSelectionPage';
import PlantNamingPage from '../pages/PlantNamingPage';
import PlantConfirmationPage from '../pages/PlantConfirmationPage';
import AlarmSetupFormPage from '../pages/AlarmSetupformPage';
import AlarmListPage from '../pages/AlarmListPage';

const authRoutes = [
  {
    path: '/',
    element: <AuthStartPage />,
  },
  {
    path: 'login',
    element: <LoginPage />,
  },
  {
    path: 'signup/id',
    element: <SignUpStep1_Id />,
  },
  {
    path: 'signup/phone',
    element: <SignUpStep2_Phone />,
  },
  {
    path: 'signup/fontsize',
    element: <SignUpStep3_FontSize />,
  },
  {
    path: 'signup/role',
    element: <SignUpStep4_Role />,
  },
  {
    path: 'signup/name',
    element: <SignUpStep5_Name />,
  },
  {
    path: 'signup/birthdate',
    element: <SignUpStep6_Birthdate />,
  },
  {
    path: 'signup/gender',
    element: <SignUpStep7_Gender />,
  },
  {
    path: 'welcome',
    element: <WelcomePage />,
  },
  {
    path: 'select-plant',
    element: <PlantSelectionPage />,
  },
  {
    path: 'name-plant/:color',
    element: <PlantNamingPage />,
  },
  {
    path: 'confirm-plant/:color/:name',
    element: <PlantConfirmationPage />
  },
  {
    path: 'alarm-setup',
    element: <AlarmSetupFormPage />
  },
  {
    path: 'alarm-list',
    element: <AlarmListPage />
  },
];

export default authRoutes;