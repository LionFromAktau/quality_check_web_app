import { useContext, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { UserContext } from '../../context/UserContext';
import ModalProfile from '../modals/modalProfile';
import './Sidebar.css';
import {
  FaBars, FaTimes,
  FaCheckSquare, FaExclamationTriangle, FaBoxOpen, FaClipboardList,
  FaChartBar, FaUserCircle
} from 'react-icons/fa';

const menuItems = [
  { icon: <FaClipboardList />, label: 'Inspections', path: '/', allowedRoles: ['role_admin', 'role_productionworker', 'role_qualitymanager', 'role_qualityinspector', 'role_productionmanager'] },
  { icon: <FaCheckSquare />, label: 'Checklists', path: '/checklists', allowedRoles: ['role_admin', 'role_productionworker', 'role_qualityinspector'] },
  { icon: <FaExclamationTriangle />, label: 'Defects', path: '/defects/report', allowedRoles: ['role_admin', 'role_qualitymanager', 'role_qualityinspector'] },
  { icon: <FaBoxOpen />, label: 'Products', path: '/products', allowedRoles: ['role_admin', 'role_qualitymanager', 'role_qualityinspector'] },
  { icon: <FaChartBar />, label: 'Reports', path: '/reports', allowedRoles: ['role_admin', 'role_qualitymanager', 'role_productionmanager'] },
];

export default function Sidebar({ isCollapsed, toggleSidebar }) {
  const location = useLocation();
  const [showProfileModal, setShowProfileModal] = useState(false);
  const { user, logout } = useContext(UserContext);

  return (
    <aside className={`sidebar ${isCollapsed ? 'collapsed' : ''}`}>
      <div className="sidebar-top">
        <div className="sidebar-header">
          <button className="sidebar-toggle-button" onClick={toggleSidebar}>
            {isCollapsed ? <FaBars /> : <FaTimes />}
          </button>
          {!isCollapsed && <h1 className="sidebar-logo">QCapp</h1>}
        </div>
        <ul className="sidebar-menu">
          {menuItems
            .filter(item => !item.allowedRoles || item.allowedRoles.includes(user?.role))
            .map((item, i) => (
              <Link to={item.path} className="sidebar-link" key={i}>
                <li className={`sidebar-item ${location.pathname === item.path ? 'active' : ''}`}>
                  <span className="icon">{item.icon}</span>
                  <span className="label">{item.label}</span>
                </li>
              </Link>
            ))}
        </ul>
      </div>
      <div className="sidebar-bottom">
        {user ? (
          <div className="sidebar-profile">
            <div
              className="profile-link"
              onClick={() => setShowProfileModal(true)}
              style={{ cursor: 'pointer' }}
            >
              <FaUserCircle className="profile-icon" />
              {!isCollapsed && <span className="profile-name">{user.username}</span>}
            </div>
            {!isCollapsed && (
              <button className="logout-button" onClick={logout}>Logout</button>
            )}
          </div>
        ) : (
          <div className="sidebar-profile not-logged-in">
            <FaUserCircle className="profile-icon" style={{ opacity: 0.5 }} />
            {!isCollapsed && <span className="profile-name">Not logged in</span>}
          </div>
        )}
      </div>
      {showProfileModal && <ModalProfile onClose={() => setShowProfileModal(false)} />}
    </aside>
  );
}