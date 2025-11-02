import React from "react";
import root from "./router/root";
import { RouterProvider } from "react-router-dom";

function App() {
  return (
    <div className="flex justify-center min-h-screen">
      <div className="w-[375px] h-[812px] border border-black bg-white">
        <RouterProvider router={root} />
      </div>
    </div>
  );
}

export default App;
