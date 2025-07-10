import React, { useEffect, useState } from "react";
import api from "../services/axiosInstance";
import "./Modal.css";
import AnimatedModal from "../ui/AnimatedModal";
import ModalUser from "./modalUser";
import { motion, AnimatePresence } from "framer-motion";

export default function ModalDefect({ id, onClose, status, userId }) {
  const [answers, setAnswers] = useState(null);
  const [showUserModal, setShowUserModal] = useState(false);
  const [selectedImage, setSelectedImage] = useState(null);

useEffect(() => {
  const handleKeyDown = (e) => {
    if (e.key === "Escape") {
      if (selectedImage) {
        setSelectedImage(null); 
      } else if (showUserModal) {
        setShowUserModal(false); 
      } else {
        onClose(); 
      }
    }
  };

  window.addEventListener("keydown", handleKeyDown);
  return () => {
    window.removeEventListener("keydown", handleKeyDown);
  };
}, [selectedImage, showUserModal, onClose]);



  useEffect(() => {
    const fetchChecklistResults = async () => {
      try {
        const res = await api.get(`/api/checklist/results/${id}`);
        const rawAnswers = res.data;

        const enrichedAnswers = await Promise.all(
          rawAnswers.map(async (item) => {
            if (item.mediaUrl) {
              try {
                const imageRes = await api.get(item.mediaUrl, { responseType: 'blob' });
                const blobUrl = URL.createObjectURL(imageRes.data);
                return { ...item, imageDataUrl: blobUrl };
              } catch (e) {
                console.error("Failed to load image:", e);
                return { ...item, imageDataUrl: null };
              }
            } else {
              return { ...item, imageDataUrl: null };
            }
          })
        );

        setAnswers(enrichedAnswers);
        console.log("Checklist results:", enrichedAnswers);
      } catch (err) {
        console.error("Error loading checklist results:", err);
      }
    };

    fetchChecklistResults();
  }, [id]);

  const first = answers?.[0];

  return (
    <>
      <AnimatedModal isOpen={true} onClose={onClose} className={showUserModal ? "shift-left" : ""}>
        {!answers ? (
          <div className="modal-loading">Loading...</div>
        ) : (
          <>
            <h2>Checklist Result #{id}</h2>

            <div className="modal-summary">
              <p><strong>Product:</strong> {first.productName}</p>
              <p><strong>Batch ID:</strong> {first.batchId}</p>
              <p><strong>Result ID:</strong> {first.resultId}</p>
              <p>
                <strong>User ID:</strong>{" "}
                <span
                  className="clickable-link"
                  onClick={() => setShowUserModal(true)}
                  style={{ color: "#3b82f6", cursor: "pointer", textDecoration: "underline" }}
                >
                  {userId}
                </span>
              </p>
              <p><strong>Status: </strong><span
  className={`status-badge ${
    status === "CRITICAL"
      ? "critical"
      : status === "MAJOR"
      ? "major"
      : status === "RESOLVED"
      ? "resolved"
      : "minor"
  }`}
>
  {status}
</span>
</p>

            </div>

            <div className="modal-body scrollable">
              {answers.map((item) => (
                <div key={item.answerId} className="modal-row">
                  <div><strong>{item.itemDescription}</strong></div>
                  <div>Comment: {item.comment || "—"}</div>
                  <div>{item.value ? "✅ Pass" : "❌ Fail"}</div>
                  <div>
                    {item.imageDataUrl ? (
                      <img
                        src={item.imageDataUrl}
                        alt="preview"
                        className="thumbnail"
                        onClick={() => setSelectedImage(item.imageDataUrl)}
                        style={{ maxWidth: "120px", cursor: "pointer", borderRadius: "6px", marginTop: "6px" }}
                      />
                    ) : (
                      "No media"
                    )}
                  </div>
                </div>
              ))}
            </div>
          </>
        )}
      </AnimatedModal>

      {showUserModal && (
        <ModalUser userId={userId} onClose={() => setShowUserModal(false)} />
      )}
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


    </>
  );
}
