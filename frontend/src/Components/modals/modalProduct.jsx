import React, { useEffect, useState } from "react";
import api from "../services/axiosInstance";
import AnimatedModal from "../ui/AnimatedModal"; // путь укажи свой
import "./Modal.css";

export default function ModalProduct({ id, onClose }) {
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);

 useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === "Escape") onClose();
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [onClose]);

  useEffect(() => {
    api.get(`/api/products/${id}`)
      .then(res => {
        setProduct(res.data);
        setLoading(false);
      })
      .catch(err => {
        console.error("Failed to fetch product details:", err);
        setLoading(false);
      });
  }, [id]);

  return (
    <AnimatedModal isOpen={true} onClose={onClose}>
      {loading ? (
        <div className="modal-loading">Loading...</div>
      ) : !product ? (
        <div className="modal-error">Failed to load product information.</div>
      ) : (
        <>
          <h2>Product #{product.id}</h2>
          <div className="modal-body">
            <div className="modal-row">
              <span><strong>Name:</strong></span>
              <span>{product.name}</span>
            </div>
            <div className="modal-row">
              <span><strong>Type:</strong></span>
              <span>{product.productType}</span>
            </div>
            <div className="modal-row">
              <span><strong>Created At:</strong></span>
              <span>{new Date(product.createdAt).toLocaleString('en-GB', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit'
})}</span>
            </div>
            <div className="modal-row">
              <span><strong>Updated At:</strong></span>
              <span>{new Date(product.updatedAt).toLocaleString('en-GB', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit'
})}</span>
            </div>
          </div>
        </>
      )}
    </AnimatedModal>
  );
}
