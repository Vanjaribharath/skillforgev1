import { BookOpen, ClipboardCheck, Search, UserRoundCheck } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";

export default function SearchPage() {
  return (
    <div className="space-y-5">
      <section className="flex items-center justify-between"><div><p className="text-sm font-medium text-blue">Global search</p><h1 className="text-3xl font-semibold">Find assessment assets</h1></div><Search className="text-blue" /></section>
      <Card>
        <Input placeholder="Search questions, candidates, assessments, reports, departments, batches" />
      </Card>
      <section className="grid gap-3 md:grid-cols-3">
        {[
          { title: "Questions", count: "3,840", icon: BookOpen },
          { title: "Assessments", count: "142", icon: ClipboardCheck },
          { title: "Candidates", count: "1,284", icon: UserRoundCheck },
        ].map((item) => {
          const Icon = item.icon;
          return <Card key={item.title}><Icon className="mb-3 text-blue" size={22} /><div className="text-sm text-muted">{item.title}</div><div className="mt-1 text-2xl font-semibold">{item.count}</div></Card>;
        })}
      </section>
    </div>
  );
}
