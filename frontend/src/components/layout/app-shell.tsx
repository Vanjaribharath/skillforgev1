"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  BarChart3,
  BookOpen,
  CircleUserRound,
  ClipboardCheck,
  FileText,
  Home,
  Landmark,
  MonitorDot,
  PanelRight,
  Search,
  Settings,
  Shield,
  UserRoundCheck,
} from "lucide-react";
import { clsx } from "clsx";

const nav = [
  { href: "/", label: "Command", icon: Home },
  { href: "/question-bank", label: "Questions", icon: BookOpen },
  { href: "/assessments", label: "Assessments", icon: ClipboardCheck },
  { href: "/candidates", label: "Candidates", icon: UserRoundCheck },
  { href: "/live", label: "Live", icon: MonitorDot },
  { href: "/reports", label: "Reports", icon: BarChart3 },
  { href: "/candidate", label: "Test Player", icon: FileText },
  { href: "/search", label: "Search", icon: Search },
  { href: "/settings", label: "Settings", icon: Settings },
  { href: "/admin", label: "Admin", icon: Shield },
];

export function AppShell({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();

  return (
    <div className="min-h-screen bg-white text-ink">
      <aside className="fixed inset-y-0 left-0 z-20 hidden w-64 border-r border-line bg-white px-4 py-5 lg:block">
        <Link href="/" className="mb-8 flex items-center gap-3 rounded-lg px-2">
          <span className="grid h-9 w-9 place-items-center rounded-md bg-blue text-white"><Landmark size={20} /></span>
          <div>
            <div className="text-lg font-semibold tracking-normal">SkillForge</div>
            <div className="text-xs text-muted">Assess. Certify. Improve.</div>
          </div>
        </Link>
        <nav className="space-y-1">
          {nav.map((item) => (
            <NavItem key={item.href} item={item} active={pathname === item.href || pathname.startsWith(`${item.href}/`)} />
          ))}
        </nav>
      </aside>
      <header className="sticky top-0 z-10 border-b border-line bg-white/95 px-4 py-3 backdrop-blur lg:ml-64">
        <div className="mx-auto flex max-w-6xl items-center justify-between">
          <Link href="/" className="flex items-center gap-2 lg:hidden">
            <span className="grid h-8 w-8 place-items-center rounded-md bg-blue text-white"><Landmark size={18} /></span>
            <span className="font-semibold">SkillForge</span>
          </Link>
          <div className="hidden items-center gap-2 text-sm text-muted lg:flex">
            <PanelRight size={17} />
            Internal certification workspace
          </div>
          <button className="touch-target rounded-md border border-line p-2 text-muted" aria-label="Open profile">
            <CircleUserRound size={22} />
          </button>
        </div>
      </header>
      <main className="pb-24 lg:ml-64 lg:pb-10">
        <div className="mx-auto max-w-6xl px-4 py-5 sm:px-6 lg:px-8">{children}</div>
      </main>
      <nav className="fixed inset-x-0 bottom-0 z-30 grid grid-cols-5 border-t border-line bg-white px-2 py-2 lg:hidden">
        {nav.slice(0, 5).map((item) => (
          <MobileNavItem key={item.href} item={item} active={pathname === item.href || pathname.startsWith(`${item.href}/`)} />
        ))}
      </nav>
    </div>
  );
}

function NavItem({ item, active }: { item: (typeof nav)[number]; active: boolean }) {
  const Icon = item.icon;
  return (
    <Link
      href={item.href}
      className={clsx(
        "flex h-11 items-center gap-3 rounded-md px-3 text-sm font-medium text-muted transition",
        active && "bg-[#e8f0fe] text-blue",
        !active && "hover:bg-surface hover:text-ink",
      )}
    >
      <Icon size={19} />
      {item.label}
    </Link>
  );
}

function MobileNavItem({ item, active }: { item: (typeof nav)[number]; active: boolean }) {
  const Icon = item.icon;
  return (
    <Link href={item.href} className={clsx("flex flex-col items-center gap-1 rounded-md py-1 text-[11px] text-muted", active && "text-blue")}>
      <Icon size={20} />
      <span className="max-w-full truncate">{item.label}</span>
    </Link>
  );
}
