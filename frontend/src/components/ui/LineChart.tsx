export interface LineChartPoint {
  label: string;
  value: number;
}

interface LineChartProps {
  points: LineChartPoint[];
  height?: number;
}

/**
 * Bewusst eine kleine handgeschriebene SVG-Chart-Komponente statt einer Chart-Bibliothek -
 * für eine einzelne Linie ist eine zusätzliche Abhängigkeit nicht gerechtfertigt.
 */
export function LineChart({ points, height = 160 }: LineChartProps) {
  if (points.length === 0) {
    return null;
  }

  const width = 100;
  const values = points.map((point) => point.value);
  const min = Math.min(...values, 0);
  const max = Math.max(...values, 1);
  const range = max - min || 1;
  const stepX = points.length > 1 ? width / (points.length - 1) : 0;

  const coordinates = points.map((point, index) => {
    const x = index * stepX;
    const y = height - ((point.value - min) / range) * height;
    return `${x},${y}`;
  });

  const first = points[0];
  const last = points[points.length - 1];

  return (
    <div>
      <svg
        viewBox={`0 0 ${width} ${height}`}
        preserveAspectRatio="none"
        className="h-40 w-full"
        role="img"
        aria-label="Vermögensentwicklung über die Laufzeit"
      >
        <polyline
          fill="none"
          stroke="var(--color-accent)"
          strokeWidth="1.5"
          vectorEffect="non-scaling-stroke"
          points={coordinates.join(" ")}
        />
      </svg>
      <div className="mt-1 flex justify-between text-xs text-muted">
        <span>{first.label}</span>
        <span>{last.label}</span>
      </div>
    </div>
  );
}
