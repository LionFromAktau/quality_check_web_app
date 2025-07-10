import "./BatchesTable.css";
import React, { useEffect, useState } from "react";
import api from "../services/axiosInstance";

export default function BatchesTable() {
const [batches, setBatches] = useState([]);

  useEffect(() => {
    const fetchBatches = async () => {
      try {
        const response = await api.get('/api/batches');
        console.log('Batches data:', response.data.content);
        setBatches(response.data.content);
      } catch (error) {
        console.error('Error fetching batches:', error);
      }
    };
    fetchBatches();
  }
  , []);


  return (
    <div className="projects-table">
      <h2>Batches</h2>
      <div className="overflow-x-auto">
        <table>
          <thead>
            <tr>
              <th>Batch</th>
              <th>Product Name</th>
              <th>Start Date</th>
              <th>Total Units</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {batches.map((batches, idx) => (
              <tr key={idx}>
                <td>{batches.batchId}</td>
                <td>{batches.productName}</td>
                <td>{new Date(batches.createdAt).toLocaleString('en-GB', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit'
})}</td>

                <td>{batches.quantity}</td>
                <td>
                  <span className={`status-badge ${
                    batches.status === "CHECKING" ? "status-pending" :
                    batches.status === "CHECKED" ? "status-complete" :
                    "status-rejected"
                  }`}>
                    {batches.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
