import React from "react";
import Reward from "../components/alarm/Reward";
import DementiaTest from "../components/home/DementiaTest";

const etc = [
  {
    path: "reward",
    element: <Reward />,
  },
  {
    path: "todayquiz",
    element: <DementiaTest />,
  },
];

export default etc;
