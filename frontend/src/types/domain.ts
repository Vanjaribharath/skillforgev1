export type TaskStatus = "TODO" | "IN_PROGRESS" | "DONE";
export type Priority = "LOW" | "MEDIUM" | "HIGH" | "URGENT";

export type Task = {
  id: string;
  title: string;
  description?: string;
  category?: string;
  priority: Priority;
  status: TaskStatus;
  startTime?: string;
  endTime?: string;
};

export type ScheduleBlock = {
  id: string;
  title: string;
  time: string;
  color: string;
  status: TaskStatus;
};
