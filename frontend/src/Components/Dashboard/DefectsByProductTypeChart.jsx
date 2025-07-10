import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from "recharts";
import React, { useEffect, useState } from "react";
import api from "../services/axiosInstance";


export default function DefectsByProductTypeChart() {

  const [defectsByProductType, setDefectsByProductType] = useState([]);

  useEffect(() => {
    const fetchDefectsByProductType = async () => {
      try {
        const response = await api.get('/api/defect/report/by-product');
        console.log('Defects by product type data:', response.data);
        setDefectsByProductType(response.data);
      } catch (error) {
        console.error('Error fetching defects by product type:', error);
      }
    };
    fetchDefectsByProductType();
  }
  , []);

  return (
    <div className="bg-gray-800 p-6 rounded-xl shadow-md">
      <h2 className="text-lg text-white mb-4">Defects by Product Type</h2>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={defectsByProductType}>
          <XAxis dataKey="productType" stroke="#ccc" />
          <YAxis stroke="#ccc" />
          <Tooltip
      contentStyle={{ backgroundColor: "white", border: "none" }}
      cursor={{ fill: "none" }}
    />
          <Bar dataKey="defects_count" fill="#34d399" radius={[10, 10, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
