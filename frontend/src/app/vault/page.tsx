import { BookOpen, Folder } from "lucide-react";
import { Card } from "@/components/ui/card";

const categories = ["System design", "AI tools", "Frontend patterns", "Deployment notes"];

export default function VaultPage() {
  return (
    <div className="space-y-5">
      <section className="flex items-center justify-between">
        <div><p className="text-sm font-medium text-blue">Vault</p><h1 className="text-3xl font-semibold">Knowledge library</h1></div>
        <BookOpen className="text-blue" />
      </section>
      <div className="grid gap-3 sm:grid-cols-2">
        {categories.map((category) => (
          <Card key={category}>
            <Folder className="mb-3 text-amber" />
            <h2 className="font-semibold">{category}</h2>
            <p className="mt-1 text-sm text-muted">Pinned notes, links, snippets, and decisions.</p>
          </Card>
        ))}
      </div>
    </div>
  );
}
