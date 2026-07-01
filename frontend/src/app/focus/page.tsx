"use client";

import { Pause, Play, RotateCcw, TimerReset } from "lucide-react";
import { Card } from "@/components/ui/card";
import { useFocusStore } from "@/store/use-focus-store";

export default function FocusPage() {
  const { secondsRemaining, running, start, pause, reset } = useFocusStore();
  const minutes = Math.floor(secondsRemaining / 60).toString().padStart(2, "0");
  const seconds = (secondsRemaining % 60).toString().padStart(2, "0");

  return (
    <div className="space-y-5">
      <section>
        <p className="text-sm font-medium text-blue">Deep work</p>
        <h1 className="text-3xl font-semibold">Protect the next block</h1>
      </section>
      <Card className="text-center">
        <TimerReset className="mx-auto text-blue" size={32} />
        <div className="mt-5 text-7xl font-semibold tracking-normal">{minutes}:{seconds}</div>
        <div className="mt-6 flex justify-center gap-3">
          <button className="touch-target rounded-full bg-blue px-5 text-sm font-semibold text-white" onClick={() => start(secondsRemaining)}>
            <Play size={18} className="inline" /> {running ? "Running" : "Start"}
          </button>
          <button className="touch-target rounded-full border border-line px-4 text-sm" onClick={pause}><Pause size={18} className="inline" /> Pause</button>
          <button className="touch-target rounded-full border border-line px-4 text-sm" onClick={reset}><RotateCcw size={18} className="inline" /> Reset</button>
        </div>
      </Card>
    </div>
  );
}
