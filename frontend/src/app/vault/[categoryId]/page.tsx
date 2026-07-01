import { Card } from "@/components/ui/card";

export default async function VaultCategoryPage({ params }: { params: Promise<{ categoryId: string }> }) {
  const { categoryId } = await params;
  return <Card><h1 className="text-2xl font-semibold">Category {categoryId}</h1><p className="mt-2 text-sm text-muted">Knowledge items load here.</p></Card>;
}
