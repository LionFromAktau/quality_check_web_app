import { useState, useEffect } from "react";
import { TrendingUp } from "lucide-react";
import "bootstrap/dist/css/bootstrap.min.css";
import "./KpiCards.css";
import api from "../services/axiosInstance";

export default function KPICards() {
  const [productsChecked, setProductsChecked] = useState(0);
  const [checksToday, setChecksToday] = useState(0);
  const [defectsDetected, setDefectsDetected] = useState(0);
  const [defectsToday, setDefectsToday] = useState(0);

  useEffect(() => {
  api.get("/api/kpi")
    .then(res => {
      const data = res.data;
      setProductsChecked(data.productsChecked);
      setChecksToday(data.checksToday);
      setDefectsDetected(data.defectsDetected);
      setDefectsToday(data.defectsToday);
    })
    .catch(err => {
      console.error("Failed to fetch KPI data:", err);
    });
}, []);


  const cards = [
    { label: "Products Checked", value: productsChecked, style: "kpi-card-gradient-blue" },
    { label: "Checks Today", value: checksToday, style: "kpi-card-gradient-gray" },
    { label: "Defects Detected", value: defectsDetected, style: "kpi-card-gradient-blue" },
    { label: "Defects Today", value: defectsToday, style: "kpi-card-gradient-gray" },
  ];

  return (
    <div className="row g-3">
      {cards.map((card, idx) => (
        <div key={idx} className="col-12 col-sm-6 col-xl-3">
          <div className={`kpi-card ${card.style}`}>
            <div className="kpi-label">{card.label}</div>
            <div className="kpi-value">{card.value.toLocaleString()}</div>
            <div className="kpi-icon">
              <TrendingUp size={16} className="text-white" />
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
