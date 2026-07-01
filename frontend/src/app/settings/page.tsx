import { Bell, Braces, KeyRound, Mail, Palette, Settings, ShieldCheck } from "lucide-react";
import Link from "next/link";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

export default function SettingsPage() {
  return (
    <div className="space-y-5">
      <section className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div>
          <p className="text-sm font-medium text-blue">Enterprise settings</p>
          <h1 className="text-3xl font-semibold">Branding, email, security</h1>
          <p className="mt-2 max-w-2xl text-sm leading-6 text-muted">Configure organization identity, delivery templates, tokens, localization, and integrations.</p>
        </div>
        <Button asChild><Link href="/settings#saved"><Settings size={18} /> Save changes</Link></Button>
      </section>

      <section className="grid gap-4 lg:grid-cols-2">
        <Card>
          <div className="mb-4 flex items-center gap-2"><Palette className="text-blue" size={20} /><h2 className="font-semibold">Organization branding</h2></div>
          <div className="grid gap-3 sm:grid-cols-2">
            <label className="text-sm font-medium">Organization name<Input className="mt-2" defaultValue="Apex Learning Cloud" /></label>
            <label className="text-sm font-medium">Primary color<Input className="mt-2" defaultValue="#1D4ED8" /></label>
            <label className="text-sm font-medium">Certificate prefix<Input className="mt-2" defaultValue="APEX-CERT" /></label>
            <label className="text-sm font-medium">Locale<select className="mt-2 h-11 w-full rounded-md border border-line px-3 text-sm"><option>English</option><option>Hindi</option><option>Spanish</option></select></label>
          </div>
        </Card>
        <Card>
          <div className="mb-4 flex items-center gap-2"><Mail className="text-green" size={20} /><h2 className="font-semibold">SMTP and templates</h2></div>
          <div className="grid gap-3 sm:grid-cols-2">
            <label className="text-sm font-medium">SMTP host<Input className="mt-2" defaultValue="smtp.company.com" /></label>
            <label className="text-sm font-medium">Sender email<Input className="mt-2" defaultValue="assessments@company.com" /></label>
            <label className="text-sm font-medium">Reminder cadence<Input className="mt-2" defaultValue="24h, 2h before start" /></label>
            <label className="text-sm font-medium">Template<select className="mt-2 h-11 w-full rounded-md border border-line px-3 text-sm"><option>Invitation</option><option>Reminder</option><option>Result</option><option>Certificate</option></select></label>
          </div>
        </Card>
        <Card>
          <div className="mb-4 flex items-center gap-2"><ShieldCheck className="text-amber" size={20} /><h2 className="font-semibold">Security policy</h2></div>
          <div className="grid gap-3 sm:grid-cols-2">
            {["OTP verification", "Fullscreen required", "Copy paste detection", "Developer tools detection"].map((item) => (
              <label key={item} className="flex min-h-11 items-center gap-2 rounded-md border border-line px-3 text-sm"><input type="checkbox" defaultChecked /> {item}</label>
            ))}
          </div>
        </Card>
        <Card>
          <div className="mb-4 flex items-center gap-2"><Braces className="text-blue" size={20} /><h2 className="font-semibold">Integrations</h2></div>
          <div className="space-y-3">
            <label className="text-sm font-medium">Webhook URL<Input className="mt-2" defaultValue="https://lms.company.com/webhooks/skillforge" /></label>
            <div className="grid gap-3 sm:grid-cols-2">
              <Button asChild variant="outline"><Link href="/settings#api-key-rotated"><KeyRound size={17} /> Rotate API key</Link></Button>
              <Button asChild variant="outline"><Link href="/settings#test-event-sent"><Bell size={17} /> Send test event</Link></Button>
            </div>
          </div>
        </Card>
      </section>
    </div>
  );
}
