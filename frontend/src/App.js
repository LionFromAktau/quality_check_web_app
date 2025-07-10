import './App.css';
import { useState } from "react";
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import { AnimatePresence } from 'framer-motion';

import Sidebar from './Components/Sidebar/Sidebar';
import Dashboard from './Components/pages/Dashboard';
import ChecklistAccessPage from './Components/pages/ChecklistAccessPage';
import ChecklistsPage from './Components/pages/ChecklistsPage';
import DefectsReportPage from './Components/pages/DefectsReportPage';
import DefectReportFormPage from './Components/pages/DefectReportFormPage';
import ProductsBatches from './Components/pages/ProductsBatches';
import Reports from './Components/pages/Reports';

import { UserProvider } from './context/UserContext';
import RoleProtectedRoute from './Components/services/RoleProtectedRoute';

import AnimatedPage from './Components/ui/AnimatedPage'; // 👈 Добавляем

function AppRoutes({ sidebarOpen, toggleSidebar }) {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        {/* Login Page (без сайдбара и анимации) */}
        {/* Защищённый блок */}
        <Route path="*" element={
          <RoleProtectedRoute>
            <div className={`app-container ${sidebarOpen ? 'sidebar-open' : 'sidebar-collapsed'}`}>
              <div className="sidebar-wrapper">
                <Sidebar isCollapsed={!sidebarOpen} toggleSidebar={toggleSidebar} />
              </div>

              <main className="main-content">
                <Routes location={location} key={location.pathname}>
                  <Route path="/" element={
                    // <RoleProtectedRoute allowedRoles={['role_admin', 'PRODUCTION_WORKER', 'QUALITY_MANAGER', 'QUALITY_INSPECTOR', 'PRODUCTION_MANAGER']}>
                      <AnimatedPage><Dashboard /></AnimatedPage>
                    // </RoleProtectedRoute>
                  } />
                  <Route path="/checklists" element={
                    // <RoleProtectedRoute allowedRoles={['role_admin', 'PRODUCTION_WORKER', 'QUALITY_INSPECTOR']}>
                      <AnimatedPage><ChecklistAccessPage /></AnimatedPage>
                    // </RoleProtectedRoute>
                  } />
                  <Route path="/checklists/:code" element={
                    // <RoleProtectedRoute allowedRoles={['role_admin', 'PRODUCTION_WORKER', 'QUALITY_INSPECTOR']}>
                      <AnimatedPage><ChecklistsPage /></AnimatedPage>
                    // </RoleProtectedRoute>
                  } />
                  <Route path="/defects/report" element={
                    // <RoleProtectedRoute allowedRoles={['role_admin', 'QUALITY_MANAGER', 'QUALITY_INSPECTOR']}>
                      <AnimatedPage><DefectsReportPage /></AnimatedPage>
                    // </RoleProtectedRoute>
                  } />
                  <Route path="/defects/report/batch/:code" element={
                    // <RoleProtectedRoute allowedRoles={['role_admin', 'QUALITY_MANAGER', 'QUALITY_INSPECTOR']}>
                      <AnimatedPage><DefectReportFormPage /></AnimatedPage>
                    // </RoleProtectedRoute>
                  } />
                  <Route path="/products" element={
                    // <RoleProtectedRoute allowedRoles={['role_admin', 'QUALITY_MANAGER', 'QUALITY_INSPECTOR']}>
                      <AnimatedPage><ProductsBatches /></AnimatedPage>
                    // </RoleProtectedRoute>
                  } />
                  <Route path="/reports" element={
                    // <RoleProtectedRoute allowedRoles={['role_admin', 'QUALITY_MANAGER', 'PRODUCTION_MANAGER']}>
                      <AnimatedPage><Reports /></AnimatedPage>
                    // </RoleProtectedRoute>
                  } />
                  
                </Routes>
              </main>
            </div>
          </RoleProtectedRoute>
        } />
      </Routes>
    </AnimatePresence>
  );
}

function App() {
  const [sidebarOpen, setSidebarOpen] = useState(true);

  return (
    <UserProvider>
      <Router>
        <AppRoutes sidebarOpen={sidebarOpen} toggleSidebar={() => setSidebarOpen(!sidebarOpen)} />
      </Router>
    </UserProvider>
  );
}

export default App;
