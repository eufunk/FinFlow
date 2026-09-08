export interface ComparisonPoint {
  year: number;
  value: number;
}

export interface ComparisonSeries {
  id: string;
  label: string;
  color: string;
  points: ComparisonPoint[];
}

interface ComparisonLineChartProps {
  series: ComparisonSeries[];
  height?: number;
}

/** Mehrfach-Serien-Variante von LineChart, für den Szenario-Vergleich (mehrere Linien + Legende). */
export function ComparisonLineChart({ series, height = 200 }: ComparisonLineChartProps) {
  const allPoints = series.flatMap((s) => s.points);
  if (allPoints.length === 0) {
    return null;
  }

  const width = 100;
  const values = allPoints.map((point) => point.value);
  const min = Math.min(...values, 0);
  const max = Math.max(...values, 1);
  const range = max - min || 1;
  const maxYear = Math.max(...allPoints.map((point) => point.year), 1);
  const stepX = width / maxYear;

  return (
    <div>
      <svg
        viewBox={`0 0 ${width} ${height}`}
        preserveAspectRatio="none"
        className="w-full"
        style={{ height }}
        role="img"
        aria-label="Vergleich der Vermögensentwicklung mehrerer Szenarien"
      >
        {series.map((s) => {
          const coordinates = [...s.points]
            .sort((a, b) => a.year - b.year)
            .map((point) => `${point.year * stepX},${height - ((point.value - min) / range) * height}`);
          return (
            <polyline
              key={s.id}
              fill="none"
              stroke={s.color}
              strokeWidth="1.5"
              vectorEffect="non-scaling-stroke"
              points={coordinates.join(" ")}
            />
          );
        })}
      </svg>
      <div className="mt-2 flex flex-wrap gap-3 text-xs">
        {series.map((s) => (
          <span key={s.id} className="flex items-center gap-1.5">
            <span className="h-2 w-2 rounded-full" style={{ backgroundColor: s.color }} />
            <span className="text-muted">{s.label}</span>
          </span>
        ))}
      </div>
    </div>
  );
}
