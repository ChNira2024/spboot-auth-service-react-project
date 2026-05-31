import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { motion } from "framer-motion";
import { Mail, Lock, User, CheckCircle2Icon } from "lucide-react";
import { Alert, AlertTitle } from "@/components/ui/alert";

import { useState } from "react";
import { useNavigate } from "react-router";
import OAuth2Buttons from "@/components/OAuth2Buttons";
import type RegisterData from "@/models/RegisterData";
import toast from "react-hot-toast";
import { registerUser } from "@/service/AuthService";

function Signup() {
  const [data, setData] = useState<RegisterData>({
    name: "",
    email: "",
    password: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const navigate = useNavigate();

  // ✅ Clear error on typing
  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setError(null);

    setData((prev) => ({
      ...prev,
      [event.target.name]: event.target.value,
    }));
  };

  //// handling form change(text input, email, password, number , textarea)
  const handleFormSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setError(null);

    // Validation
    if (!data.name.trim()) {
      setError("Name is required");
      return;
    }

    if (!data.email.trim()) {
      setError("Email is required");
      return;
    }

    if (!/\S+@\S+\.\S+/.test(data.email)) {
      setError("Invalid email format");
      return;
    }

    if (!data.password.trim()) {
      setError("Password is required");
      return;
    }

    if (data.password.length < 6) {
      setError("Password must be at least 6 characters");
      return;
    }

    //form submit for registrations
    try {
      setLoading(true);
      await registerUser(data);

      // SUCCESS → only toast
      toast.success("User registered successfully");
      setData({
        name: "",
        email: "",
        password: "",
      });

      navigate("/login");

    } catch (err: any) {
      console.error(err);

      const serverMessage = err?.response?.data?.message;
      const serverStatus = err?.response?.status;

      // ERROR → only Alert
      if (serverStatus === 409 ||(serverMessage && serverMessage.toLowerCase().includes("exists"))) {
        setError("An account with this email already exists");
      } else if (serverMessage) {
        setError(serverMessage);
      } else {
        setError("Something went wrong. Please try again later.");
      }
    } finally {
      setLoading(false);
    }
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
            <h1 className="text-4xl font-bold text-center">
              Create Your Account
            </h1>

            <p className="text-center text-muted-foreground mt-2">
              Join the next-generation authentication platform
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

            {/* Form */}
            <form onSubmit={handleFormSubmit} className="mt-8 space-y-6">
              
              {/* Name */}
              <div className="space-y-2">
                <Label>Name</Label>
                <div className="relative">
                  <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
                  <Input
                    type="text"
                    placeholder="John Doe"
                    className="pl-10"
                    name="name"
                    value={data.name}
                    onChange={handleInputChange}
                  />
                </div>
              </div>

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
                    value={data.email}
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
                    placeholder="••••••"
                    className="pl-10"
                    name="password"
                    value={data.password}
                    onChange={handleInputChange}
                  />
                </div>
              </div>

              {/* Button */}
              <Button
                type="submit"
                disabled={loading}
                className="w-full rounded-2xl text-lg"
              >
                {loading ? "Signing up..." : "Sign Up"}
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

export default Signup;