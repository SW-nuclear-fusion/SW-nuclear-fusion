import { createBrowserRouter } from "react-router-dom";
import auth from "./auth";
import home from "./home";
import MainLayout from "./MainLayout";
import etc from "./etc";

const router = createBrowserRouter([
  {
    path: "/",
    children: [...auth, ...etc],
  },
  {
    path: "/main",
    element: <MainLayout />,
    children: [...home],
  },
]);

export default router;
