import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

export default function SchoolHome() {
  return (
    <div className="bg-background text-foreground">

      {/* HERO SECTION */}
      <section className="relative h-[80vh] flex items-center justify-center text-center px-6 bg-gradient-to-r from-blue-900 to-indigo-800 text-white">
        <div>
          <h1 className="text-5xl md:text-6xl font-bold">
            Welcome to ABC Public School
          </h1>
          <p className="mt-4 text-lg md:text-xl max-w-2xl mx-auto">
            Empowering students with knowledge, discipline, and excellence since 1995.
          </p>

          <div className="mt-6 flex justify-center gap-4">
            <Button className="bg-white text-black hover:bg-gray-200">
              Admission Open
            </Button>
            <Button variant="outline" className="border-white text-white">
              Explore Campus
            </Button>
          </div>
        </div>
      </section>

      {/* ANNOUNCEMENTS */}
      <section className="py-16 px-6 max-w-6xl mx-auto">
        <h2 className="text-3xl font-bold mb-8 text-center">
          Latest Announcements
        </h2>

        <div className="grid md:grid-cols-3 gap-6">
          {[
            "Admissions open for 2026-27",
            "Annual Sports Day on July 15",
            "Mid-term exams schedule released",
          ].map((item, i) => (
            <Card key={i}>
              <CardContent className="p-6">
                <p>{item}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </section>

      {/* ABOUT SCHOOL */}
      <section className="py-16 px-6 bg-muted">
        <div className="max-w-5xl mx-auto text-center">
          <h2 className="text-3xl font-bold mb-6">About Our School</h2>
          <p className="text-muted-foreground">
            ABC Public School is committed to academic excellence and holistic
            development. We provide modern education combined with strong moral values.
          </p>
        </div>
      </section>

      {/* COURSES / CLASSES */}
      <section className="py-16 px-6 max-w-6xl mx-auto">
        <h2 className="text-3xl font-bold text-center mb-10">
          Our Classes
        </h2>

        <div className="grid md:grid-cols-3 gap-6">
          {[
            { title: "Primary School", desc: "Classes 1 to 5" },
            { title: "Middle School", desc: "Classes 6 to 8" },
            { title: "High School", desc: "Classes 9 to 12" },
          ].map((item, i) => (
            <Card key={i}>
              <CardContent className="p-6 text-center">
                <h3 className="text-xl font-semibold">{item.title}</h3>
                <p className="text-muted-foreground mt-2">{item.desc}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </section>

      {/* STATS */}
      <section className="py-16 bg-blue-900 text-white text-center">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6 max-w-5xl mx-auto">
          {[
            { value: "1500+", label: "Students" },
            { value: "75+", label: "Teachers" },
            { value: "30+", label: "Years Experience" },
            { value: "100%", label: "Results" },
          ].map((stat, i) => (
            <div key={i}>
              <h3 className="text-3xl font-bold">{stat.value}</h3>
              <p>{stat.label}</p>
            </div>
          ))}
        </div>
      </section>

      {/* GALLERY */}
      <section className="py-16 px-6 max-w-6xl mx-auto">
        <h2 className="text-3xl font-bold text-center mb-10">
          School Gallery
        </h2>

        <div className="grid md:grid-cols-3 gap-4">
          {[1, 2, 3, 4, 5, 6].map((img) => (
            <div
              key={img}
              className="h-40 bg-gray-300 rounded-lg flex items-center justify-center"
            >
              Image {img}
            </div>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section className="py-20 text-center bg-muted">
        <h2 className="text-3xl font-bold">
          Ready to Join Our School?
        </h2>
        <p className="mt-3 text-muted-foreground">
          Apply now and give your child a bright future.
        </p>

        <Button className="mt-6">Apply Now</Button>
      </section>

      {/* FOOTER */}
      <footer className="py-8 text-center border-t text-muted-foreground">
        © {new Date().getFullYear()} ABC Public School
      </footer>
    </div>
  );
}