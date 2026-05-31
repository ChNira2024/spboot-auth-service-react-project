import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { motion } from "framer-motion";
import useAuth from "@/auth/store";
import { useEffect, useState } from "react";
import toast from "react-hot-toast";

function Userprofile() {
  const user = useAuth((state) => state.user);
  useAuth((state) => state.setUser); // IMPORTANT (add in store)

  const [isEditing, setIsEditing] = useState(false);

  const [form, setForm] = useState({
    name: "",
    email: "",
  });

  useEffect(() => {
    if (user) {
      setForm({
        name: user.name || "",
        email: user.email || "",
      });
    }
  }, [user]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSave = async () => {
    try {
      // 👉 call API here later
      // await updateProfile(form)

    //   setUser({
    //     ...user,
    //     name: form.name,
    //     email: form.email,
    //   });

      toast.success("Profile updated successfully");
      setIsEditing(false);
    } catch (err) {
      toast.error("Failed to update profile");
    }
  };

  return (
    <div className="min-h-screen bg-muted/30 p-6 flex justify-center">
      <div className="w-full max-w-4xl space-y-6">

        {/* HEADER */}
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <h1 className="text-3xl font-bold">My Profile</h1>
          <p className="text-muted-foreground">
            Manage your account information
          </p>
        </motion.div>

        {/* MAIN CARD */}
        <Card className="rounded-2xl shadow-xl">
          <CardHeader>
            <CardTitle>Personal Information</CardTitle>
          </CardHeader>

          <CardContent className="space-y-6">

            {/* AVATAR SECTION */}
            <div className="flex items-center gap-6">
              <Avatar className="w-20 h-20">
                <AvatarImage src={user?.image} />
                <AvatarFallback>
                  {user?.name?.charAt(0)}
                </AvatarFallback>
              </Avatar>

              <div>
                <h2 className="font-semibold text-lg">{user?.name}</h2>
                <p className="text-sm text-muted-foreground">
                  {user?.email}
                </p>
              </div>

              <div className="ml-auto">
                {!isEditing ? (
                  <Button onClick={() => setIsEditing(true)}>
                    Edit Profile
                  </Button>
                ) : (
                  <div className="flex gap-2">
                    <Button variant="outline" onClick={() => setIsEditing(false)}>
                      Cancel
                    </Button>
                    <Button onClick={handleSave}>
                      Save
                    </Button>
                  </div>
                )}
              </div>
            </div>

            {/* FORM */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">

              <div className="space-y-2">
                <Label>Name</Label>
                <Input
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  disabled={!isEditing}
                />
              </div>

              <div className="space-y-2">
                <Label>Email</Label>
                <Input
                  name="email"
                  value={form.email}
                  disabled
                />
              </div>

              <div className="space-y-2">
                <Label>Provider</Label>
                <Input value={user?.provider} disabled />
              </div>

              <div className="space-y-2">
                <Label>Status</Label>
                <Input value={user?.enabled ? "Active" : "Disabled"} disabled />
              </div>

            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

export default Userprofile;