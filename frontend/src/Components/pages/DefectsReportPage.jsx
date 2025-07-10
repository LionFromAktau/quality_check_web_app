import { useEffect, useRef, useState } from 'react';
import { useUser } from '../../context/UserContext';
import api from '../services/axiosInstance';
import './DefectsReportPage.css';
import DefectReportFormPage from './DefectReportFormPage';

export default function DefectsReportPage() {
  const [pendingReports, setPendingReports] = useState([]);
  const [selectedReport, setSelectedReport] = useState(null);
  const [leftWidth, setLeftWidth] = useState(600);
  const isResizing = useRef(false);
  const { user, isUserReady } = useUser();
  const containerRef = useRef(null);

  useEffect(() => {
    if (!isUserReady || !user?.userId) return;

    const fetchChecklistResults = async () => {
      try {
        const response = await api.get('api/checklist/results/failed');
        const results = response.data.map((r) => ({ ...r, submitted: false }));
        setPendingReports(results);
        console.log('Fetched checklist results:', response.data);
      } catch (err) {
        console.error('Error fetching checklist results:', err);
      }
    };

    fetchChecklistResults();
  }, [isUserReady, user?.userId]);

  useEffect(() => {
    const handleMouseMove = (e) => {
  if (!isResizing.current) return;
  const container = containerRef.current;
  const containerLeft = container.getBoundingClientRect().left;
  const newWidth = e.clientX - containerLeft;

  // allow folding all the way to 0
  if (newWidth < 0) {
    setLeftWidth(0);
  } else if (newWidth > window.innerWidth - 200) {
    setLeftWidth(window.innerWidth - 200);
  } else {
    setLeftWidth(newWidth);
  }
};

    const handleMouseUp = () => {
      isResizing.current = false;
      document.body.style.userSelect = '';
    };

    window.addEventListener('mousemove', handleMouseMove);
    window.addEventListener('mouseup', handleMouseUp);
    return () => {
      window.removeEventListener('mousemove', handleMouseMove);
      window.removeEventListener('mouseup', handleMouseUp);
    };
  }, []);

  if (!isUserReady) return <p>Loading...</p>;
  if (!user) return <p>Please log in</p>;

  const filteredReports = pendingReports.filter((r) => !r.submitted);

  return (
    <div className="defects-resizable-layout" ref={containerRef}>
      <div className="left-panel" style={{ width: leftWidth }}>
        <h1 className="defects-report-title">Defect Reports</h1>

        <div className="tab-buttons">
          <h1>
            To Be Filled
          </h1>
        </div>

        <div className="defects-grid">
          {filteredReports.length === 0 && (
            <p>No batches requiring a report</p>
          )}
          {filteredReports.map((res) => (
            <div
              key={res.resultId}
              className={`defect-card ${res.submitted ? 'submitted' : ''} ${selectedReport?.resultId === res.resultId ? 'active' : ''
                }`}
              onClick={() => {
                if (!res.submitted) {
                  setSelectedReport(null);
                  setTimeout(() => setSelectedReport(res), 0);
                  console.log('Selected report:', res);
                }
              }}
            >
              <h3>{res.productName}</h3>
              <p>
                <strong>Batch ID:</strong> {res.batchId}
              </p>
              {res.submitted ? (
                <p className="status-text">Report submitted</p>
              ) : (
                <button className="report-btn">Fill Report</button>
              )}
            </div>
          ))}
        </div>
      </div>
      <div
        className="resizer"
        onMouseDown={() => {
          isResizing.current = true;
          document.body.style.userSelect = 'none';
        }}
      />
      <div className="right-panel">
        {selectedReport ? (
          <DefectReportFormPage
            checklistResult={selectedReport}
            onClose={() => {
              setPendingReports(prev =>
                prev.map(r =>
                  r.resultId === selectedReport.resultId ? { ...r, submitted: true } : r
                )
              );
              setSelectedReport(null);
            }}
          />
        ) : (
          <p className="placeholder-text">← Select a report to fill</p>
        )}
      </div>
    </div>
  );
}
