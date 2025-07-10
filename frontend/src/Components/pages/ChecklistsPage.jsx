import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import api from '../services/axiosInstance';
import './ChecklistsPage.css';
import { useUser } from '../../context/UserContext';
import imageCompression from 'browser-image-compression';
import { useNavigate } from 'react-router-dom';



export default function ChecklistsPage() {
  const { code } = useParams();
  const [batch, setBatch] = useState(null);
  const [error, setError] = useState('');
  const [checkedItems, setCheckedItems] = useState({});
  const [comments, setComments] = useState({});
  const [mediaFiles, setMediaFiles] = useState({});
  const [submitStatus, setSubmitStatus] = useState('');
  const { user, isUserReady } = useUser();
  const [checklistItems, setChecklistItems] = useState([]);
  const [previewImages, setPreviewImages] = useState({});
const navigate = useNavigate();

  useEffect(() => {
    const fetchChecklistItems = async () => {
      try {
        const res = await api.get(`/api/products/${batch.productId}`);
        setChecklistItems(res.data.checklistItems);
        console.log("Fetched checklist items:", res.data.checklistItems);
      } catch (err) {
        console.error('Error fetching checklists', err);
      }
    };

    if (batch?.productId) {
      fetchChecklistItems();
    }
  }, [batch]);

  useEffect(() => {
    const fetchBatch = async () => {
      try {
        const response = await api.get(`/api/batches/${code}`);
        setBatch(response.data);
      } catch (err) {
        setError(`Batch with code ${code} not found`);
      }
    };

    if (code) {
      fetchBatch();
    }
  }, [code]);

  const handleCheckboxChange = (id) => {
    setCheckedItems((prev) => ({
      ...prev,
      [id]: !prev[id],
    }));
  };

  const handleCommentChange = (id, text) => {
    setComments((prev) => ({
      ...prev,
      [id]: text,
    }));
  };

const handleFileChange = async (id, file) => {
  if (!user?.userId) {
    setSubmitStatus("User not loaded");
    return;
  }

  if (file) {
    try {
      const options = {
        maxSizeMB: 1,
        maxWidthOrHeight: 1024,
        useWebWorker: true,
      };
      const compressedFile = await imageCompression(file, options);

      if (previewImages[id]) {
        URL.revokeObjectURL(previewImages[id]);
      }

      const previewUrl = URL.createObjectURL(compressedFile);

      setMediaFiles((prev) => ({
        ...prev,
        [id]: compressedFile,
      }));

      setPreviewImages((prev) => ({
        ...prev,
        [id]: previewUrl,
      }));

    } catch (error) {
      setSubmitStatus("Error compressing image");
        }
  }
};



useEffect(() => {
  return () => {
    Object.values(previewImages).forEach((url) => {
      if (url) URL.revokeObjectURL(url);
    });
  };
}, [previewImages]);


 const handleSubmitChecklist = async () => {
  if (!user?.userId) {
    setSubmitStatus("User not loaded");
    return;
  }

  const formData = new FormData();
  formData.append("batchId", batch.batchId);
  formData.append("userId", user.userId);


  checklistItems.forEach((item, index) => {
    console.log("item.itemId:", item);
    formData.append(`checklistAnswers[${index}].checklistItemId`, item.id);
    formData.append(`checklistAnswers[${index}].value`, !!checkedItems[item.id]);
    formData.append(`checklistAnswers[${index}].comment`, comments[item.id] || "");
    if (mediaFiles[item.id]) {
      formData.append(`checklistAnswers[${index}].media`, mediaFiles[item.id]);
    }
     console.log("Response from checklist submission:", item);
  });
  try {
    const response = await api.post('/api/checklist/results', formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    
    setSubmitStatus(response.data.status || "Successfully submitted");

    setTimeout(() => {
      navigate("/checklists");
    }, 1000);

  } catch (err) {
    setSubmitStatus("Error submitting checklist");
  }
};



  if (!isUserReady) return <p>Loading...</p>;
  if (error) return <div className="checklists-container">{error}</div>;
  if (!batch) return <div className="checklists-container">Loading...</div>;

  return (
    <div className="checklists-container">
      <h1 className="checklists-title">Checklist</h1>
      <div className="checklists-details">
        Batch <strong>#{batch.batchId}</strong><br />
        Product: <strong>{batch.productName}</strong>
      </div>

      <div className="checklists-grid">
        {checklistItems.map((item) => (
          <div key={item.id} className="checklist-item">
            <label>
              <input
                type="checkbox"
                checked={!!checkedItems[item.id]}
                onChange={() => {handleCheckboxChange(item.id);}}
              />{" "}
              {item.description}
            </label>

            <input
              type="text"
              placeholder="Comment (optional)"
              value={comments[item.id] || ""}
              onChange={(e) => handleCommentChange(item.id, e.target.value)}
              className="comment-input"
            />

            <input
  type="file"
  accept="image/*"
  onChange={(e) => handleFileChange(item.id, e.target.files[0])}
  className="file-input"
/>

{previewImages[item.id] && (
  <img
    src={previewImages[item.id]}
    alt="Preview"
    className="preview-image"
    style={{
      marginTop: '10px',
      maxWidth: '200px',
      maxHeight: '150px',
      borderRadius: '8px',
      objectFit: 'cover',
      border: '1px solid #ccc',
    }}
  />
)}
          </div>
        ))}
      </div>

      <div className="checklists-actions">
        <button className="report-btn" onClick={handleSubmitChecklist}>Submit Checklist</button>
      </div>

      {submitStatus && <p className="submit-status">{submitStatus}</p>}
    </div>
  );
}
