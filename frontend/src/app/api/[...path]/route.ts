import { NextRequest, NextResponse } from "next/server";

const API_URL = process.env.BACKEND_API_URL ?? "http://localhost:8080/api/v1";

async function proxy(request: NextRequest, context: { params: Promise<{ path: string[] }> }) {
  const params = await context.params;
  const url = `${API_URL}/${params.path.join("/")}${request.nextUrl.search}`;
  const response = await fetch(url, {
    method: request.method,
    headers: request.headers,
    body: request.method === "GET" || request.method === "HEAD" ? undefined : await request.text(),
  });
  return new NextResponse(response.body, { status: response.status, headers: response.headers });
}

export { proxy as GET, proxy as POST, proxy as PUT, proxy as PATCH, proxy as DELETE };
