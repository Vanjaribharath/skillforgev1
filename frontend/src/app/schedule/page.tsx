import { CalendarDays, GripVertical } from "lucide-react";
import { Card } from "@/components/ui/card";
import { schedule } from "@/lib/mock-data";

export default function SchedulePage() {
  return (
    <div className="space-y-5">
      <Header title="Schedule" subtitle="Calendar-style planning with sortable execution blocks." icon={<CalendarDays />} />
      <Card>
        <div className="grid gap-3">
          {schedule.map((block) => (
            <div key={block.id} className="grid grid-cols-[auto_64px_1fr] items-center gap-3 rounded-md border border-line p-3">
              <GripVertical size={18} className="text-muted" />
              <span className="text-sm font-medium text-muted">{block.time}</span>
              <div>
                <div className="font-medium">{block.title}</div>
                <div className="mt-1 text-sm text-muted">{block.status.replace("_", " ")}</div>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}

function Header({ title, subtitle, icon }: { title: string; subtitle: string; icon: React.ReactNode }) {
  return <section className="flex items-center justify-between"><div><h1 className="text-3xl font-semibold">{title}</h1><p className="mt-1 text-sm text-muted">{subtitle}</p></div><div className="text-blue">{icon}</div></section>;
}
