import { useEffect, useState } from "react";
import api from "../services/axiosInstance";
import AnimatedModal from "../ui/AnimatedModal";

export default function ModalUser({ userId, onClose }) {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const fetchChecklistResults = async () => {
      try {
        const res = await api.get(`/api/user/${userId}`);
        setUser(res.data || null);
      } catch (error) {
        console.error("Error fetching checklist results for user:", error);
      }
    };

    if (userId) fetchChecklistResults();
  }, [userId]);
  

  return (
    <AnimatedModal isOpen={true} onClose={onClose} className="side" transparentBackground={true}>
      {!user ? (
        <div className="modal-loading">Loading user...</div>
      ) : (
        <div className="modal-user-profile">
          <h2>User Profile</h2>
          <div className="modal-summary">
            <p><strong>User ID:</strong> {user.userId}</p>
            <p><strong>Username:</strong> {user.username}</p>
            <p><strong>Email:</strong> {user.email}</p>
            <p><strong>Role:</strong> {user.role}</p>
            <p><strong>Checklists Filled:</strong> {user.checklistFilled}</p>
            <p><strong>Defect Reports Created:</strong> {user.defectReportCreated}</p>
          </div>
        </div>
      )}
    </AnimatedModal>
  );
}
