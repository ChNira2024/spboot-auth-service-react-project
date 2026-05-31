import useAuth from "@/auth/store";
import { refreshToken } from "@/service/AuthService";
import axios from "axios";

const apiClient = axios.create({
        // baseURL : import.meta.env.VITE_API_BASE_URL || "http://localhost:8083/api/v1",
        baseURL : import.meta.env.VITE_API_BASE_URL,
        headers:{
            'Content-Type':"application/json",
        },
        withCredentials:true,
        timeout:10000 //10 sec
});


//Before every request , interceptor will pass accessToken through header
apiClient.interceptors.request.use((config)=>{
    const accessToken = useAuth.getState().accessToken;
    if(accessToken){
        config.headers.Authorization = `Bearer ${accessToken}`
    }
    return config;
});

let isRefreshing = false;
let pending: any[] = [];

function queueRequest(cb: any) {
  pending.push(cb);
}

function resolveQueue(newToken: string) {
  pending.forEach((cb) => cb(newToken));
  pending = [];
}
//if error is coming from api then how to handle
apiClient.interceptors
.response.use((response) => response,
                                     async (error) => {
                                        console.log("error comes: ",error);
                                        const original = error.config;
                                        console.log("original: ",original)
                                        //const is401 = error.response.status === 401;
                                        const is401 = error?.response?.status === 401;
                                        console.log("original retry: ",original._retry);

                                        // ✅ 🚀 SKIP refresh for login/signup
                                        const isAuthApi =
                                        original?.url?.includes("/auth/login") ||
                                        original?.url?.includes("/auth/signup");

                                        if (isAuthApi) {
                                          return Promise.reject(error);   // 🔥 directly return
                                        }

                                      if(!is401 || original._retry){
                                        return Promise.reject(error);
                                      }

                                    // original._retry = true;
                                     //we will try to refresh the token:
                                    if (isRefreshing) {
                                    console.log("added to queue");
                                    return new Promise((resolve, reject) => {
                                        queueRequest((newToken: string) => {
                                        if (!newToken) return reject();
                                        original.headers.Authorization = `Bearer ${newToken}`;
                                        resolve(apiClient(original));
                                        });
                                    });
                                    }
                                    //start refresh
                                    isRefreshing = true;
                                    try {
                                    console.log("start refreshing...");
                                    const loginResponse = await refreshToken();
                                    const newToken = loginResponse.accessToken;
                                    if (!newToken) throw new Error("no access token received");
                                    useAuth
                                        .getState()
                                        .changeLocalLoginData(
                                        loginResponse.accessToken,
                                        loginResponse.user,
                                        true
                                        );
                                        resolveQueue(newToken);
                                        original.headers.Authorization = `Bearer ${newToken}`;
                                        return apiClient(original);
                                        } catch (error) {
                                        resolveQueue('null');
                                        //useAuth.getState().logout();
                                        return Promise.reject(error);
                                        } finally {
                                        isRefreshing = false;
                                        }
                                }
  
);

export default apiClient;

