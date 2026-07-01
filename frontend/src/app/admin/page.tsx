import { ArchiveRestore, DatabaseBackup, HeartPulse, Shield, ShieldCheck } from "lucide-react";
import { Card, Stat } from "@/components/ui/card";

export default function AdminPage() {
  return (
    <div className="space-y-5">
      <section className="flex items-center justify-between"><div><p className="text-sm font-medium text-blue">Platform admin</p><h1 className="text-3xl font-semibold">Health, audit, backups</h1></div><Shield className="text-blue" /></section>
      <div className="grid gap-3 sm:grid-cols-3">
        <Stat label="API health" value="OK" detail="p95 latency 84ms" />
        <Stat label="Queue depth" value="42" detail="Email and export workers" />
        <Stat label="Audit events" value="18.4k" detail="30-day retained view" />
      </div>
      <section className="grid gap-4 lg:grid-cols-3">
        {[
          { title: "Security posture", text: "JWT, refresh tokens, rate limits, device tracking, audit trails, and tenant boundaries are enabled.", icon: ShieldCheck },
          { title: "Backup status", text: "Last scheduled database backup completed at 02:00 with restore validation pending.", icon: DatabaseBackup },
          { title: "Background jobs", text: "Email, certificate, export, webhook, and analytics workers are tracked from the operations dashboard.", icon: HeartPulse },
        ].map((item) => {
          const Icon = item.icon;
          return <Card key={item.title}><Icon className="mb-3 text-blue" size={22} /><h2 className="font-semibold">{item.title}</h2><p className="mt-2 text-sm leading-6 text-muted">{item.text}</p></Card>;
        })}
      </section>
      <Card>
        <div className="mb-4 flex items-center gap-2"><ArchiveRestore className="text-amber" size={20} /><h2 className="font-semibold">Recent audit events</h2></div>
        <div className="space-y-2 text-sm">
          {["Question SPR-0042 submitted for review", "Java Fresher Certification published", "126 candidate invitations queued", "Webhook delivery retried for LMS endpoint"].map((event) => (
            <div key={event} className="rounded-md border border-line px-3 py-2">{event}</div>
          ))}
        </div>
      </Card>
    </div>
  );
}
