import { create } from "zustand";

type User = {
  id: string;
  email: string;
  name: string;
  role: "USER" | "ADMIN";
};

type AuthState = {
  user: User | null;
  accessToken: string | null;
  setSession: (user: User, accessToken: string) => void;
  logout: () => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  accessToken: null,
  setSession: (user, accessToken) => {
    if (typeof window !== "undefined") {
      window.localStorage.setItem("executionos.accessToken", accessToken);
    }
    set({ user, accessToken });
  },
  logout: () => {
    if (typeof window !== "undefined") {
      window.localStorage.removeItem("executionos.accessToken");
    }
    set({ user: null, accessToken: null });
  },
}));
