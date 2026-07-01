import { NotebookPen } from "lucide-react";
import { Card } from "@/components/ui/card";

export default function JournalPage() {
  return (
    <div className="space-y-5">
      <section className="flex items-center justify-between"><div><p className="text-sm font-medium text-blue">Journal</p><h1 className="text-3xl font-semibold">Daily review</h1></div><NotebookPen className="text-blue" /></section>
      <Card>
        <label className="text-sm font-medium" htmlFor="learnings">Learnings</label>
        <textarea id="learnings" className="mt-2 min-h-36 w-full rounded-md border border-line p-3 outline-none focus:border-blue" placeholder="What did you learn today?" />
      </Card>
    </div>
  );
}
