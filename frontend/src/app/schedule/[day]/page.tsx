import { Card } from "@/components/ui/card";

export default async function DayDetailPage({ params }: { params: Promise<{ day: string }> }) {
  const { day } = await params;
  return (
    <div className="space-y-5">
      <h1 className="text-3xl font-semibold">Day plan</h1>
      <Card>
        <p className="text-sm text-muted">Focused schedule for {day}.</p>
      </Card>
    </div>
  );
}
