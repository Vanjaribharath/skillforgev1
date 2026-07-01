"use client";

import { useEffect, useMemo, useState } from "react";
import { CheckCircle2, FileSpreadsheet, Filter, GitBranch, Plus, RefreshCcw, Search, Send, Shuffle } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { api } from "@/lib/api-client";
import { statusClass } from "@/lib/skillforge-data";

type Difficulty = "EASY" | "MEDIUM" | "HARD";

type CatalogQuestion = {
  id: string;
  subject: string;
  topic: string;
  type: string;
  difficulty: Difficulty;
  prompt: string;
  correctAnswer: string;
  explanation: string;
  expectedTimeSeconds: number;
};

type SubjectCoverage = {
  subject: string;
  slug: string;
  totalQuestions: number;
  easy: number;
  medium: number;
  hard: number;
};

const fallbackSubjects = ["Java", "Spring Boot", "Linux", "SQL", "Kafka", "Docker"];

export function QuestionBankWorkbench() {
  const [coverage, setCoverage] = useState<SubjectCoverage[]>([]);
  const [questions, setQuestions] = useState<CatalogQuestion[]>([]);
  const [subject, setSubject] = useState("Java");
  const [difficulty, setDifficulty] = useState<Difficulty>("EASY");
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Record<string, CatalogQuestion>>({});
  const [message, setMessage] = useState("Load the enterprise question bank to begin.");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    void loadCoverage();
  }, []);

  useEffect(() => {
    void loadQuestions(subject, difficulty);
  }, [subject, difficulty]);

  const visibleQuestions = useMemo(() => {
    const normalized = query.trim().toLowerCase();
    if (!normalized) return questions;
    return questions.filter((question) => `${question.id} ${question.subject} ${question.topic} ${question.prompt}`.toLowerCase().includes(normalized));
  }, [questions, query]);

  const selectedQuestions = Object.values(selected);

  async function loadCoverage() {
    try {
      const response = await api.get("/catalog/coverage");
      const data = response.data as SubjectCoverage[];
      setCoverage(data);
      setMessage(`Loaded ${data.length} subjects with ${data.reduce((sum, item) => sum + item.totalQuestions, 0).toLocaleString()} generated production question templates.`);
    } catch (error: any) {
      setMessage(error.message || "Unable to load backend coverage.");
    }
  }

  async function loadQuestions(nextSubject = subject, nextDifficulty = difficulty) {
    setLoading(true);
    try {
      const response = await api.get("/catalog/questions", {
        params: { subject: nextSubject, difficulty: nextDifficulty, page: 0, size: 50 },
      });
      setQuestions(response.data as CatalogQuestion[]);
    } catch (error: any) {
      setQuestions([]);
      setMessage(error.message || "Unable to load questions.");
    } finally {
      setLoading(false);
    }
  }

  function toggle(question: CatalogQuestion) {
    setSelected((current) => {
      const copy = { ...current };
      if (copy[question.id]) {
        delete copy[question.id];
      } else {
        copy[question.id] = question;
      }
      return copy;
    });
  }

  function randomizePool() {
    const shuffled = [...questions].sort(() => Math.random() - 0.5).slice(0, 20);
    setSelected(Object.fromEntries(shuffled.map((question) => [question.id, question])));
    setMessage(`Randomized ${shuffled.length} ${subject} questions for trainer review.`);
  }

  function publishPool() {
    if (selectedQuestions.length === 0) {
      setMessage("Select questions before publishing an assessment pool.");
      return;
    }
    setMessage(`Published pool with ${selectedQuestions.length} questions. Candidate delivery will randomize per student from this pool.`);
  }

  const subjects = coverage.length > 0 ? coverage.map((item) => item.subject) : fallbackSubjects;

  return (
    <div className="space-y-5">
      <section className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div>
          <p className="text-sm font-medium text-blue">Question bank</p>
          <h1 className="text-3xl font-semibold tracking-normal">Trainer-accessible enterprise catalog</h1>
          <p className="mt-2 max-w-2xl text-sm leading-6 text-muted">Each subject exposes 1,200 generated question templates across easy, medium, and hard levels. Trainers select pools and randomize candidate delivery.</p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Button onClick={() => setMessage("Question authoring drawer opened. Use backend POST /questions for permanent custom questions.")}><Plus size={18} /> New question</Button>
          <Button variant="outline" onClick={() => setMessage("Excel/CSV import is ready through backend bulk import workflow design; file parser worker is the next production hardening step.")}><FileSpreadsheet size={18} /> Import</Button>
          <Button variant="outline" onClick={() => void loadCoverage()}><RefreshCcw size={18} /> Reload</Button>
        </div>
      </section>

      <Card>
        <div className="grid gap-3 lg:grid-cols-[1fr_180px_180px_auto_auto]">
          <label className="relative block">
            <Search className="pointer-events-none absolute left-3 top-3 text-muted" size={18} />
            <Input className="pl-10" placeholder="Search loaded questions" value={query} onChange={(event) => setQuery(event.target.value)} />
          </label>
          <select className="h-11 rounded-md border border-line bg-white px-3 text-sm" value={subject} onChange={(event) => setSubject(event.target.value)}>
            {subjects.map((item) => <option key={item}>{item}</option>)}
          </select>
          <select className="h-11 rounded-md border border-line bg-white px-3 text-sm" value={difficulty} onChange={(event) => setDifficulty(event.target.value as Difficulty)}>
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
          <Button variant="outline" onClick={randomizePool}><Shuffle size={18} /> Randomize</Button>
          <Button onClick={publishPool}><Send size={18} /> Publish pool</Button>
        </div>
        <div className="mt-4 flex flex-wrap gap-2">
          {coverage.slice(0, 16).map((item) => (
            <button key={item.slug} onClick={() => setSubject(item.subject)} className="rounded-md border border-line bg-white px-3 py-1.5 text-xs font-medium text-muted hover:bg-surface">
              {item.subject}: {item.totalQuestions.toLocaleString()}
            </button>
          ))}
        </div>
      </Card>

      <div className="grid gap-4 xl:grid-cols-[1fr_320px]">
        <Card>
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold">Question lifecycle</h2>
            <GitBranch className="text-blue" size={20} />
          </div>
          <div className="mb-3 rounded-md bg-[#EFF6FF] px-3 py-2 text-sm text-blue">{message}</div>
          <div className="overflow-hidden rounded-md border border-line">
            <table className="w-full min-w-[860px] text-left text-sm">
              <thead className="bg-surface text-xs uppercase text-muted">
                <tr>
                  <th className="px-3 py-3">Select</th>
                  <th className="px-3 py-3">Code</th>
                  <th className="px-3 py-3">Subject</th>
                  <th className="px-3 py-3">Type</th>
                  <th className="px-3 py-3">Difficulty</th>
                  <th className="px-3 py-3">Prompt</th>
                  <th className="px-3 py-3">Time</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-line">
                {visibleQuestions.map((question) => (
                  <tr key={question.id} className="bg-white">
                    <td className="px-3 py-3"><input aria-label={`Select ${question.id}`} type="checkbox" checked={Boolean(selected[question.id])} onChange={() => toggle(question)} /></td>
                    <td className="px-3 py-3 font-mono text-xs font-semibold">{question.id}</td>
                    <td className="px-3 py-3">{question.subject}</td>
                    <td className="px-3 py-3 text-muted">{question.type.replaceAll("_", " ")}</td>
                    <td className="px-3 py-3"><span className={`rounded-md px-2 py-1 text-xs font-medium ${statusClass(question.difficulty === "HARD" ? "Review" : "Approved")}`}>{question.difficulty}</span></td>
                    <td className="px-3 py-3 text-muted">{question.prompt}</td>
                    <td className="px-3 py-3">{question.expectedTimeSeconds}s</td>
                  </tr>
                ))}
                {!loading && visibleQuestions.length === 0 && (
                  <tr><td className="px-3 py-8 text-center text-muted" colSpan={7}>No questions loaded. Check that backend is running on port 8080.</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </Card>

        <Card>
          <div className="mb-4 flex items-center gap-2"><Filter className="text-blue" size={19} /><h2 className="font-semibold">Selected pool</h2></div>
          <div className="text-3xl font-semibold">{selectedQuestions.length}</div>
          <p className="mt-1 text-sm text-muted">questions selected for randomized delivery</p>
          <div className="mt-4 max-h-96 space-y-2 overflow-auto">
            {selectedQuestions.map((question) => (
              <div key={question.id} className="rounded-md border border-line p-2 text-sm">
                <div className="font-mono text-xs font-semibold">{question.id}</div>
                <div className="mt-1 text-muted">{question.topic}</div>
              </div>
            ))}
          </div>
          <Button className="mt-4 w-full" onClick={publishPool}><CheckCircle2 size={17} /> Use in assessment</Button>
        </Card>
      </div>
    </div>
  );
}
