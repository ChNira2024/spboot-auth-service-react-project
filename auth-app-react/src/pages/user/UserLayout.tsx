import useAuth from "@/auth/store";
import { Navigate, Outlet } from "react-router";

function UserLayout() {
  const checkLogin = useAuth((state:any) => state.checkLogin);

  if (checkLogin())
    return (
      <div>
        <Outlet />
      </div>
    );
  else return <Navigate to={"/login"} />;
}

export default UserLayout;