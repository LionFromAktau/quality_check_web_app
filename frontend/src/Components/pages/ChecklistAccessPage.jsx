import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/axiosInstance';
import './ChecklistAccessPage.css';

export default function ChecklistAccessPage() {
  const [code, setCode] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const { data: batch } = await api.get(`/api/batches/${code.trim()}`);

      if (batch.status === 'CHECKED') {
        setError('Checklist already completed for this batch.');
        return;
      }

      setError('');
      navigate(`/checklists/${batch.batchId}`);
      console.log('Batch data:', batch);
    } catch (err) {
      console.error('Error during request:', err);
      setError('Batch with this ID not found.');
    }
  };

  return (
    <div className="worker-access-container">
      <h2>Enter Batch ID</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          maxLength={4}
          value={code}
          onChange={(e) => setCode(e.target.value)}
          placeholder="e.g. 1"
        />
        <button className="report-btn" type="submit">Go to Checklists</button>
        {error && <p className="error-message">{error}</p>}
      </form>
    </div>
  );
}
