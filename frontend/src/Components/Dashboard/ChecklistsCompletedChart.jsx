import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import "./ChecklistsCompletedChart.css";
import { useEffect, useState } from "react";
import api from "../services/axiosInstance";
import { useUser }from "../../context/UserContext";



export default function ChecklistsCompletedChart() {

  const [checklistsCompleted, setChecklistsCompleted]=useState([])
  const user = useUser();
useEffect(()=>{
  if(!user) return;
  const fetchChecklists = async () => {
    try {
      const response = await api.get('/api/checklist/results/stats');
      console.log(response.data);
      setChecklistsCompleted(response.data);
    } catch (error) {
      console.error("Error fetching checklists:", error);
    }
  };

  fetchChecklists();
}, [user])

const flattenedData = checklistsCompleted.flatMap((entry) =>
  entry.months.map((m) => ({
    ...m,
    year: entry.year,
    label: `${m.month} ${entry.year}`,
  }))
);


  return (
  <div className="chart-container">
    <h2 className="chart-title">Checklists Completed</h2>
    <ResponsiveContainer width="100%" height={300}>
      <BarChart data={flattenedData} barCategoryGap={0} barGap={0}>
        <XAxis dataKey="label" stroke="#ccc" />
        <YAxis stroke="#ccc" />
        <Tooltip
          contentStyle={{ backgroundColor: "white", border: "none" }}
          cursor={{ fill: "none" }}
        />
        <Legend verticalAlign="top" align="right" iconType="circle" />
        <Bar
          dataKey="failed"
          fill="#555"
          stackId="a"
          name="Failed"
          barSize={4}
          radius={[4, 4, 0, 0]}
        />
        <Bar
          dataKey="succeed"
          fill="#f87171"
          stackId="a"
          name="Succeed"
          barSize={4}
          radius={[4, 4, 0, 0]}
          activeBar={{ fill: "#fff" }}
        />
      </BarChart>
    </ResponsiveContainer>
  </div>
);
}