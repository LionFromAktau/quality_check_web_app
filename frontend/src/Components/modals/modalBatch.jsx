import React, { useEffect, useState } from "react";
import api from "../services/axiosInstance";
import AnimatedModal from "../ui/AnimatedModal"; 


export default function ModalBatch({ id, onClose }) {
  const [batch, setBatch] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get(`/api/batches/${id}`)
      .then(res => {
        setBatch(res.data);
        setLoading(false);
      })
      .catch(err => {
        console.error("Failed to fetch batch details:", err);
        setLoading(false);
      });
  }, [id]);

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === "Escape") onClose();
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [onClose]);

  return (
    <AnimatedModal isOpen={true} onClose={onClose}>
      <button className="modal-close" onClick={onClose}>×</button>

      {loading ? (
        <p className="modal-loading">Loading...</p>
      ) : batch ? (
        <>
        <h2>Batch #{batch.batchId}</h2>
        <div className="modal-body">
          
          <div className="modal-row"><strong>Product ID:</strong> {batch.productId}</div>
          <div className="modal-row"><strong>Product Name:</strong> {batch.productName}</div>
          <div className="modal-row"><strong>Quantity:</strong> {batch.quantity}</div>
          <div className="modal-row"><strong>Status:</strong> {batch.status}</div>
          <div className="modal-row"><strong>Notes:</strong> {batch.notes || "—"}</div>
          <div className="modal-row"><strong>Created At:</strong> {new Date(batch.createdAt).toLocaleString('en-GB', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit'
})}</div>
          <div className="modal-row"><strong>Updated At:</strong> {new Date(batch.updatedAt).toLocaleString('en-GB', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit'
})}</div>
        </div>
        </>
      ) : (
        <p className="modal-error">Failed to load batch information.</p>
      )}
    </AnimatedModal>
  );
}
