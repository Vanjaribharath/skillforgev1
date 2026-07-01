import { BarChart3 } from "lucide-react";
import { Card, Stat } from "@/components/ui/card";

export default function AnalyticsPage() {
  return (
    <div className="space-y-5">
      <section className="flex items-center justify-between"><div><p className="text-sm font-medium text-blue">Analytics</p><h1 className="text-3xl font-semibold">Execution trends</h1></div><BarChart3 className="text-blue" /></section>
      <div className="grid gap-3 sm:grid-cols-3">
        <Stat label="Weekly deep work" value="18h" detail="+12% from last week" />
        <Stat label="Completion rate" value="82%" detail="Tasks closed on schedule" />
        <Stat label="Context switches" value="14" detail="-5 from last week" />
      </div>
      <Card>
        <div className="flex h-52 items-end gap-3">
          {[55, 80, 64, 92, 38, 100, 72].map((height, index) => (
            <div key={index} className="w-full rounded-t-md bg-blue" style={{ height: `${height}%` }} />
          ))}
        </div>
      </Card>
    </div>
  );
}
