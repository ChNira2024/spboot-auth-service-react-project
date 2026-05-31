import type LoginData from "@/models/LoginData";
import type LoginResponseData from "@/models/LoginResponseData";
import type User from "@/models/User";
import { loginUser, logoutUser } from "@/service/AuthService";
import { create } from "zustand";
import { persist } from "zustand/middleware";

const LOCAL_KEY = "app_state";

// global auth state
type AuthState = {
  accessToken: string | null;
  user: User | null;
  authStatus: boolean;
  authLoading: boolean;

  login: (loginData: LoginData) => Promise<LoginResponseData>;
  logout: (silent?: boolean) => void;
  checkLogin: () => boolean;

  changeLocalLoginData: (
    accessToken: string,
    user: User,
    authStatus: boolean
  ) => void;

  // ✅ ADD THIS
  setUser: (user: User) => void;
};

// main logic for global state
const useAuth = create<AuthState>()(
  persist(
    (set, get) => ({
      accessToken: null,
      user: null,
      authStatus: false,
      authLoading: false,

      changeLocalLoginData: (accessToken, user, authStatus) => {
        set({
          accessToken,
          user,
          authStatus,
        });
      },

      login: async (loginData) => {
        console.log("started login...");
        set({ authLoading: true });

        try {
          const loginResponseData = await loginUser(loginData);

          console.log("loginResponseData :", loginResponseData);

          set({
            accessToken: loginResponseData.accessToken,
            user: loginResponseData.user,
            authStatus: true,
          });

          return loginResponseData;
        } catch (error) {
          console.log(error);
          throw error;
        } finally {
          set({
            authLoading: false,
          });
        }
      },

      logout: async (silent = false) => {
        try {
          set({ authLoading: true });

          if (!silent) {
            await logoutUser();
          }
        } catch (error) {
          console.log("logout error:", error);
        } finally {
          set({
            accessToken: null,
            user: null,
            authStatus: false,
            authLoading: false,
          });
        }
      },

      checkLogin: () => {
        const { accessToken, authStatus } = get();
        return !!(accessToken && authStatus);
      },

      // ✅ ADD THIS FUNCTION (IMPORTANT)
      setUser: (user: User) => {
        set({ user });
      },
    }),
    {
      name: LOCAL_KEY,
    }
  )
);

export default useAuth;