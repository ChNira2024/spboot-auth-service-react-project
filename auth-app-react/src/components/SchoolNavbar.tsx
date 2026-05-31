import { useState } from "react";
import { NavLink } from "react-router";
import { Button } from "@/components/ui/button";
import { Menu, X } from "lucide-react";

function SchoolNavbar() {
  const [open, setOpen] = useState(false);

  return (
    // <nav className="sticky top-0 z-50 bg-white border-b shadow-sm">
    <nav className="sticky top-0 z-50 bg-white dark:bg-gray-900 border-b shadow-sm">
      <div className="max-w-7xl mx-auto px-6 flex justify-between items-center h-16">
        
        {/* LOGO / BRAND */}
        <div className="flex items-center gap-3">
          <div className="h-10 w-10 bg-blue-600 text-white flex items-center justify-center rounded-full font-bold">
            ABC
          </div>
          <div>
            <h1 className="text-lg font-semibold">ABC Public School</h1>
            <p className="text-xs text-gray-500">Excellence in Education</p>
          </div>
        </div>

        {/* DESKTOP MENU */}
        <div className="hidden md:flex items-center gap-6 font-medium">
          <NavLink to="/" className="hover:text-blue-600">
            Home
          </NavLink>
          <NavLink to="/about" className="hover:text-blue-600">
            About
          </NavLink>
          <NavLink to="/academics" className="hover:text-blue-600">
            Academics
          </NavLink>
          <NavLink to="/admissions" className="hover:text-blue-600">
            Admissions
          </NavLink>
          <NavLink to="/contact" className="hover:text-blue-600">
            Contact
          </NavLink>
        </div>

        {/* ACTION BUTTONS */}
        <div className="hidden md:flex gap-3">
          <NavLink to="/login">
            <Button size="sm" variant="outline">
              Login
            </Button>
          </NavLink>
          <NavLink to="/signup">
            <Button size="sm">Apply Now</Button>
          </NavLink>
        </div>

        {/* MOBILE MENU ICON */}
        <div className="md:hidden">
          <button onClick={() => setOpen(!open)}>
            {open ? <X /> : <Menu />}
          </button>
        </div>
      </div>

      {/* MOBILE MENU */}
      {open && (
        <div className="md:hidden px-6 pb-4 space-y-3 bg-white border-t">
          <NavLink to="/" onClick={() => setOpen(false)} className="block">
            Home
          </NavLink>
          <NavLink to="/about" onClick={() => setOpen(false)} className="block">
            About
          </NavLink>
          <NavLink to="/academics" onClick={() => setOpen(false)} className="block">
            Academics
          </NavLink>
          <NavLink to="/admissions" onClick={() => setOpen(false)} className="block">
            Admissions
          </NavLink>
          <NavLink to="/contact" onClick={() => setOpen(false)} className="block">
            Contact
          </NavLink>

          <div className="pt-3 flex gap-3">
            <NavLink to="/login" className="w-full">
              <Button variant="outline" className="w-full">
                Login
              </Button>
            </NavLink>
            <NavLink to="/signup" className="w-full">
              <Button className="w-full">Apply</Button>
            </NavLink>
          </div>
        </div>
      )}
    </nav>
  );
}

export default SchoolNavbar;