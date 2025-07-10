import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { useUser } from '../../context/UserContext';
import './DefectReportFormPage.css';
import api from '../services/axiosInstance';
import { motion, AnimatePresence } from "framer-motion";


export default function DefectReportFormPage({ checklistResult, onClose }) {
  const navigate = useNavigate();
  const { user } = useUser();

  const [result] = useState(checklistResult);
  const [answers, setAnswers] = useState([]);
  const [description, setDescription] = useState('');

  const [status, setStatus] = useState('');
  const [selectedImage, setSelectedImage] = useState(null);
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === "Escape") {
        setSelectedImage(null);
      }
    };

    if (selectedImage) {
      window.addEventListener("keydown", handleKeyDown);
    }

    return () => {
      window.removeEventListener("keydown", handleKeyDown);
    };
  }, [selectedImage]);

  useEffect(() => {
    if (!result) return;

    const fetchAnswers = async () => {
      try {
        const res = await api.get(`/api/checklist/results/${result.resultId}`);
        const rawAnswers = res.data;
        const enrichedAnswers = await Promise.all(
          rawAnswers.map(async (answer) => {
            if (answer.mediaUrl) {
              try {
                const imageRes = await api.get(answer.mediaUrl, {
                  responseType: 'blob',
                });
                const imageUrl = URL.createObjectURL(imageRes.data);
                return { ...answer, imageDataUrl: imageUrl };
              } catch (e) {
                console.error('Error loading image:', e);
                return { ...answer, imageDataUrl: null };
              }
            } else {
              return { ...answer, imageDataUrl: null };
            }
          })
        );

        setAnswers(enrichedAnswers);
        console.log('Loaded checklist answers:', enrichedAnswers);
      } catch (err) {
        console.error('Error loading checklist answers:', err);
      }
    };

    fetchAnswers();
  }, [result]);

  if (!result) return <main className="main-content">← Select a batch on the left</main>;

  const handleSubmit = async (e) => {
  e.preventDefault();

  if (!result || !user) {
    alert('Missing data');
    return;
  }

  if (!status || status === '') {
    alert('Select status please');
    return;
  }

  const payload = {
    userId: user.userId,
    checkResultId: result.resultId,
    description,
    status,
  };

  try {
    await api.post('/api/defect/report', payload);
    alert('Report submitted');
    onClose();
    navigate('/defects/report');
  } catch (err) {
    console.error('Error submitting report:', err);
    alert('Error: ' + (err.response?.data?.status || 'Unknown'));
  }
};


  return (
    <div className="defect-form-container">
      <button onClick={onClose} className="close-button">✖ Close</button>
      <h1>Report Defect for Batch #{result.batchId}</h1>
      <div className="checklist-preview">
        <h3>Checklist Summary:</h3>
        <ul>
          {answers.map((a) => (
            <li key={a.answerId} className={a.value ? 'passed' : 'failed'}>
              <span>{a.itemDescription}</span>
              
              {a.imageDataUrl && (
                <div className="image-preview">
                  <img
                    src={a.imageDataUrl}
                    alt={`Evidence for ${a.itemDescription}`}
                    style={{ maxWidth: '200px', marginTop: '8px', borderRadius: '6px', cursor: 'pointer' }}
                    onClick={() => setSelectedImage(a.imageDataUrl)}
                  />
                </div>
              )}
<p>Comment: {a.comment}</p>
            </li>
          ))}
        </ul>
      </div>
      <form onSubmit={handleSubmit} className="defect-form">
        <label>Status:</label>
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="">Select Status</option>
          <option value="Minor">Minor</option>
          <option value="Major">Major</option>
          <option value="Critical">Critical</option>
          <option value="Resolved">Resolved</option>
        </select>
        <label>Description:</label>
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Describe the issue..."
          rows={4}
        />
        <button type="submit">Submit Report</button>
      </form>
      <AnimatePresence>
        {selectedImage && (
          <motion.div
            className="image-overlay"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setSelectedImage(null)}
          >
            <motion.img
              src={selectedImage}
              alt="Enlarged"
              className="zoomed-image"
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.8, opacity: 0 }}
              transition={{ duration: 0.3 }}
              onClick={(e) => e.stopPropagation()}
            />
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
