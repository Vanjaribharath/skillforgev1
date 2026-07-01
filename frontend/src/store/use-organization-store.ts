import { create } from "zustand";

type OrganizationState = {
  organizationId: string;
  setOrganizationId: (id: string) => void;
};

// We use a hardcoded specific UUID as per instructions
export const useOrganizationStore = create<OrganizationState>((set) => ({
  organizationId: "123e4567-e89b-12d3-a456-426614174000",
  setOrganizationId: (id) => set({ organizationId: id }),
}));
