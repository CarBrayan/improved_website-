package com.regresion

object JavaScript {
    val script = """
    const $ = (sel) => document.querySelector(sel);

    const format = (num) => Number(num).toLocaleString(undefined, { maximumFractionDigits: 6 });

    function parseSeries(raw) {
      if (!raw) return [];
      return raw.split(",")
        .map(v => v.trim())
        .filter(v => v.length)
        .map(v => Number(v))
        .filter(v => !Number.isNaN(v));
    }

    async function calcular() {
      const btn = $("#btn");
      btn.disabled = true;
      $("#error").innerHTML = "";
      try {
        const xs = parseSeries($("#x").value);
        const ys = parseSeries($("#y").value);

        const resp = await fetch("/api/regression", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ x: xs, y: ys })
        });
        const data = await resp.json();
        if (!resp.ok) {
          throw new Error(data.message || "Error al calcular la regresión");
        }

        // KPIs
        $("#m").textContent = format(data.m);
        $("#b").textContent = format(data.b);
        $("#r").textContent = format(data.r);
        $("#r2").textContent = format(data.r2);
        $("#n").textContent = data.n;

        $("#equation").textContent = `y = ${format(data.m)}x + ${format(data.b)}`;

        // Chart
        renderChart(data.points, data.linePoints);
      } catch (e) {
        $("#error").innerHTML = `<div class='alert error'>${e.message}</div>`;
        console.error(e);
      } finally {
        btn.disabled = false;
      }
    }

    let chart;
    function renderChart(points, line) {
      const ctx = document.getElementById('chart').getContext('2d');
      if (chart) chart.destroy();
      chart = new Chart(ctx, {
        type: 'scatter',
        data: {
          datasets: [
            {
              label: 'Datos',
              data: points.map(p => ({x: p.x, y: p.y})),
              pointRadius: 4
            },
            {
              label: 'Línea de regresión',
              type: 'line',
              data: line.map(p => ({x: p.x, y: p.y})),
              fill: false
            }
          ]
        },
        options: {
          responsive: true,
          scales: {
            x: { type: 'linear', title: { display: true, text: 'X' } },
            y: { title: { display: true, text: 'Y' } }
          },
          plugins: {
            legend: { display: true },
            tooltip: { enabled: true }
          }
        }
      });
    }

    window.addEventListener("DOMContentLoaded", () => {
      $("#form").addEventListener("submit", (e) => {
        e.preventDefault();
        calcular();
      });
      ["#x", "#y"].forEach(sel => {
        $(sel).addEventListener("keydown", (ev) => {
          if (ev.key === "Enter") {
            ev.preventDefault();
            calcular();
          }
        });
      });
    });
    """.trimIndent()
}
