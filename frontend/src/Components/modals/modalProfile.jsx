import React, { useEffect, useState, useContext } from 'react';
import api from '../services/axiosInstance';
import AnimatedModal from '../ui/AnimatedModal';
import { UserContext } from '../../context/UserContext';
import './Modal.css';

export default function ModalProfile({ onClose }) {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const { user, logout } = useContext(UserContext);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await api.get(`/api/user/${user.userId}`);
        setProfile(res.data);
      } catch (err) {
        setError('error fetching profile data');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [user.userId]);

  // ⎋ Escape key closes modal
  useEffect(() => {
    const handleEscape = (event) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };

    window.addEventListener('keydown', handleEscape);
    return () => window.removeEventListener('keydown', handleEscape);
  }, [onClose]);

  const handleLogout = () => {
    logout();
    onClose();
  };

  return (
    <AnimatedModal isOpen={true} onClose={onClose}>
      <div className="modal-user-profile">
        <h2>User Profile</h2>

        {loading ? (
          <div className="modal-loading">Загрузка...</div>
        ) : error ? (
          <div className="modal-error">{error}</div>
        ) : (
          <div className="modal-summary">
            <p><strong>ID:</strong> {profile.userId}</p>
            <p><strong>Username:</strong> {profile.username}</p>
            <p><strong>Email:</strong> {profile.email}</p>
            <p>
              <strong>Role:</strong>{" "}
              <span className={`badge ${profile.role}`}>{profile.role}</span>
            </p>
            <p><strong>Checklists filled:</strong> {profile.checklistFilled}</p>
            <p><strong>Defect reports made:</strong> {profile.defectReportCreated}</p>

            <button className="modal-logout-button" onClick={handleLogout}>
              Logout
            </button>
          </div>
        )}
      </div>
    </AnimatedModal>
  );
}
