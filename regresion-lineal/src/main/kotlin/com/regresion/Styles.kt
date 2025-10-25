package com.regresion

object Styles {
    // CSS para inyectar en la página
    val css = """
    :root {
      --bg: #0f172a;
      --card: #111827;
      --text: #e5e7eb;
      --accent: #60a5fa;
      --accent-2: #34d399;
      --warn: #f59e0b;
      --error: #f87171;
    }

    * { box-sizing: border-box; }

    body {
      margin: 0;
      padding: 0;
      font-family: ui-sans-serif, system-ui, -apple-system, "Segoe UI", Roboto, Arial, "Noto Sans", "Apple Color Emoji", "Segoe UI Emoji";
      color: var(--text);
      background: linear-gradient(135deg,#0f172a 0%, #1f2937 100%);
      min-height: 100vh;
      display: grid;
      place-items: start center;
    }

    .container {
      width: min(1100px, 95%);
      margin: 3rem auto;
    }

    .title {
      font-size: clamp(1.6rem, 2.5vw + 1rem, 2.6rem);
      font-weight: 800;
      letter-spacing: -0.02em;
      margin-bottom: 1rem;
      display:flex; align-items:center; gap:.6rem;
    }

    .card {
      background: linear-gradient(180deg, rgba(255,255,255,.04), rgba(255,255,255,.02));
      border: 1px solid rgba(255,255,255,.08);
      border-radius: 18px;
      padding: 1.25rem;
      box-shadow: 0 20px 60px rgba(0,0,0,.35);
    }

    .grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1rem;
    }
    @media (max-width: 900px) { .grid { grid-template-columns: 1fr; } }

    label { font-weight: 600; font-size:.95rem; }
    textarea {
      width: 100%;
      border-radius: 14px;
      padding: .85rem 1rem;
      border: 1px solid rgba(255,255,255,.1);
      background: rgba(17,24,39,.65);
      color: var(--text);
      resize: vertical;
      min-height: 90px;
      outline: none;
    }
    textarea:focus { border-color: var(--accent); box-shadow: 0 0 0 3px rgba(96,165,250,.25); }

    .btn {
      margin-top: .5rem;
      background: linear-gradient(90deg, var(--accent), #818cf8);
      color: #0b1220;
      border: none;
      border-radius: 12px;
      padding: .8rem 1.2rem;
      font-weight: 800;
      cursor: pointer;
      box-shadow: 0 12px 30px rgba(99,102,241,.3);
    }
    .btn:disabled { filter: grayscale(.6); opacity:.7; cursor:not-allowed; }

    .kpi {
      display: grid;
      grid-template-columns: repeat(5, minmax(0,1fr));
      gap: .75rem;
      margin-top: 1rem;
    }
    @media (max-width: 900px) { .kpi { grid-template-columns: repeat(2,1fr); } }

    .kpi .item {
      background: rgba(255,255,255,.04);
      border: 1px solid rgba(255,255,255,.08);
      border-radius: 14px; padding: .9rem;
    }
    .item .label { font-size:.8rem; opacity:.8 }
    .item .value { font-weight: 800; font-size: 1.15rem; }

    .alert { padding:.8rem 1rem; border-radius:12px; }
    .alert.warn { background: rgba(245,158,11,.15); border:1px solid rgba(245,158,11,.35); }
    .alert.error { background: rgba(248,113,113,.15); border:1px solid rgba(248,113,113,.35); }

    canvas { background: transparent; border-radius: 14px; }
    .footer { opacity:.75; font-size:.9rem; margin-top: .75rem }
    """.trimIndent()
}
