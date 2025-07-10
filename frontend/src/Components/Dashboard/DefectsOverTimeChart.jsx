import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import React, { useEffect, useState } from 'react';
import api from '../services/axiosInstance'; 

export default function DefectsOverTimeChart() {
const [defectsOverTime, setDefectsOverTime] = useState([]);

useEffect(() => {
  const fetchDefectsOverTime = async () => {
    try {
      const response = await api.get('/api/defect/report/over-time');
      console.log('Defects over time data:', response.data);
      setDefectsOverTime(response.data);
    } catch (error) {
      console.error('Error fetching defects over time:', error);
    }
  };
  fetchDefectsOverTime();
}, []);



  return (
    <ResponsiveContainer width="100%" height={300}>
      <LineChart data={defectsOverTime}>
        <XAxis dataKey="month" />
        <YAxis />
        <Tooltip
      contentStyle={{ backgroundColor: "white", border: "none" }}
      cursor={{ fill: "none" }}
    />
        <Line type="monotone" dataKey="defects" stroke="#60a5fa" strokeWidth={3} dot />
      </LineChart>
    </ResponsiveContainer>
  );
}
