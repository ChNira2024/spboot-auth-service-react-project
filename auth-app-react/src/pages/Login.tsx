import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { motion } from "framer-motion";
import { Mail, Lock, CheckCircle2Icon } from "lucide-react";
import { Alert, AlertTitle } from "@/components/ui/alert";

import OAuth2Buttons from "@/components/OAuth2Buttons";
import type LoginData from "@/models/LoginData";
import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router";
import toast from "react-hot-toast";
// import { loginUser } from "@/service/AuthService";
import { Spinner } from "@/components/ui/spinner";
import useAuth from "@/auth/store";

function Login() {
  const [loginData, setLoginData] = useState<LoginData>({
    email: "",
    password: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const navigate = useNavigate();
 const login = useAuth((state)=>state.login);

  // Clear error when user types
  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setError(null);
    setLoginData({
      ...loginData, //previoud data
      [event.target.name]: event.target.value, //new data
    });
  };

  const handleFormSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);

    // Validation
    if (!loginData.email.trim()) {
      setError("Email is required");
      return;
    }

    if (!/\S+@\S+\.\S+/.test(loginData.email)) {
      setError("Invalid email format");
      return;
    }

    if (!loginData.password.trim()) {
      setError("Password is required");
      return;
    }

    try {
        setLoading(true);
        //const userInfo = await loginUser(loginData);
      //   if (userInfo?.accessToken) {
      //   navigate("/dashboard");
      // }

    const logInApiResponse =   await login(loginData)
    console.log("logInApiResponse: ",logInApiResponse)
      toast.success("Login successful");
       if (logInApiResponse?.accessToken) {
         navigate("/dashboard");
       }  

    } catch (err: any) {
      console.error("Login error:", err);
      const message =
        err?.response?.data?.message ||
        err?.message ||
        "Login failed. Please try again.";
      setError(message);
       setLoginData({
    email: "",
    password: "",
  });
    } finally {
      setLoading(false);
    }
    /*
  catch (err: any) {
  console.error("Login error:", err);
  const status = err?.response?.status;
  const serverMessage = err?.response?.data?.message;
  let message = "Login failed. Please try again.";
  // ✅ Handle common cases
  if (status === 401) {
    message = "Invalid email or password";
  } 
  else if (status === 403) {
    message = "Your account is disabled. Contact support.";
  } 
  else if (serverMessage) {
    message = serverMessage;
  }
  setError(message);
}
    */

  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background text-foreground px-4 py-10">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
        className="w-full max-w-md"
      >
        <Card className="bg-card/70 backdrop-blur-xl border-border shadow-2xl rounded-2xl p-6">
          <CardContent>
            {/* Heading */}
            <h1 className="text-4xl font-bold text-center">Welcome Back</h1>

            <p className="text-center text-muted-foreground mt-2">
              Login to access your authentication app
            </p>

            {/*ERROR ALERT */}
            {error && (
              <div className="mt-6">
                <Alert variant="destructive">
                  <CheckCircle2Icon />
                  <AlertTitle>{error}</AlertTitle>
                </Alert>
              </div>
            )}

            {/* FORM */}
            <form onSubmit={handleFormSubmit} className="mt-8 space-y-6">
              
              {/* Email */}
              <div className="space-y-2">
                <Label>Email</Label>
                <div className="relative">
                  <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
                  <Input
                    type="email"
                    placeholder="you@example.com"
                    className="pl-10"
                    name="email"
                    value={loginData.email}
                    onChange={handleInputChange}
                  />
                </div>
              </div>

              {/* Password */}
              <div className="space-y-2">
                <Label>Password</Label>
                <div className="relative">
                  <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
                  <Input
                    type="password"
                    placeholder="••••••••"
                    className="pl-10"
                    name="password"
                    value={loginData.password}
                    onChange={handleInputChange}
                  />
                </div>
              </div>

              {/* Button Diable: Prevents multiple clicks by disabling button while API is running(user hit one time,loading is true so disable button) */}
              <Button
                type="submit"
                disabled={loading}
                className="w-full flex items-center justify-center gap-2 rounded-2xl text-lg"
              >
                {loading ? (
                  <>
                    <Spinner />
                    Please wait...
                  </>
                ) : (
                  "Login"
                )}
                {/* {loading ? "Logging in..." : "Login"} */}
              </Button>

              {/* Divider */}
              <div className="flex items-center gap-4 my-4">
                <div className="flex-1 h-[1px] bg-border"></div>
                <span className="text-muted-foreground text-sm">OR</span>
                <div className="flex-1 h-[1px] bg-border"></div>
              </div>

              {/* OAuth */}
              <OAuth2Buttons />
            </form>
          </CardContent>
        </Card>
      </motion.div>
    </div>
  );
}

export default Login;