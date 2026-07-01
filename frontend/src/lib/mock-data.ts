import type { ScheduleBlock, Task } from "@/types/domain";

export const tasks: Task[] = [
  { id: "1", title: "Ship dashboard read model", category: "Code", priority: "HIGH", status: "IN_PROGRESS", startTime: "09:00" },
  { id: "2", title: "Review Redis rate limit config", category: "Ops", priority: "MEDIUM", status: "TODO", startTime: "11:00" },
  { id: "3", title: "Write journal summary", category: "Learning", priority: "LOW", status: "DONE", startTime: "17:00" },
];

export const schedule: ScheduleBlock[] = [
  { id: "a", title: "Deep work: API contracts", time: "09:00", color: "#1a73e8", status: "IN_PROGRESS" },
  { id: "b", title: "Learning block", time: "13:00", color: "#188038", status: "TODO" },
  { id: "c", title: "Ship notes", time: "16:00", color: "#f9ab00", status: "TODO" },
];
