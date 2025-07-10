import React, { useState, useMemo, useEffect } from "react";
import "./Reports.css";
import api from "../services/axiosInstance";
import ModalDefect from "../modals/modalDefect";
import ModalUser from "../modals/modalUser";
import Pagination from "../ui/Pagination";

export default function Reports() {
  const [statusFilter, setStatusFilter] = useState("");
  const [createdAfter, setCreatedAfter] = useState("");
  const [updatedAfter, setUpdatedAfter] = useState("");
  const [checklistResultId, setChecklistResultId] = useState(null);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [showUserModal, setShowUserModal] = useState(false);
  const [selectedDefectStatus, setSelectedDefectStatus] = useState(null);
  const [defectReportIdSearch, setDefectReportIdSearch] = useState("");
const [defectSearchError, setDefectSearchError] = useState("");



  const [defects, setDefects] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [sortBy, setSortBy] = useState("createdAt,desc");

  useEffect(() => {
  const fetchDefects = async () => {
    try {
      const response = await api.get("/api/defect/report", {
        params: {
          page,
          size: rowsPerPage,
          sort: sortBy,
          status: statusFilter || undefined,
        },
      });
      setDefects(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      console.error("Failed to fetch defect reports:", err);
    }
  };

  fetchDefects();
}, [page, rowsPerPage, sortBy, statusFilter]);



  const handleSort = (field) => {
    setSortBy((prevSortBy) => {
      const [prevField, prevDirection] = prevSortBy.split(',');
      if (prevField === field) {
        return `${field},${prevDirection === 'asc' ? 'desc' : 'asc'}`;
      } else {
        return `${field},asc`;
      }
    });
  };

  const handleSearchDefectById = () => {
  if (!defectReportIdSearch.trim()) return;

  api.get(`/api/defect/report/${defectReportIdSearch.trim()}`)
    .then(res => {
      const data = res.data;
      setChecklistResultId(data.checklistResultId);
      setSelectedUserId(data.userId);
      setSelectedDefectStatus(data.status);
      setDefectSearchError('');
    })
    .catch(err => {
      console.error("Defect not found:", err);
      setDefectSearchError("Defect report not found");
    });
};




 const filteredDefects = useMemo(() => {
  return defects.filter((defect) => {
    let matchesCreated = true;
    let matchesUpdated = true;

    if (createdAfter) {
      const selectedDate = new Date(createdAfter);
      const startOfDay = new Date(selectedDate.setHours(0, 0, 0, 0));
      const endOfDay = new Date(selectedDate.setHours(23, 59, 59, 999));
      const createdDate = new Date(defect.createdAt);
      matchesCreated = createdDate >= startOfDay && createdDate <= endOfDay;
    }

    if (updatedAfter) {
      const selectedUpdate = new Date(updatedAfter);
      const startOfUpdate = new Date(selectedUpdate.setHours(0, 0, 0, 0));
      const endOfUpdate = new Date(selectedUpdate.setHours(23, 59, 59, 999));
      const updatedDate = new Date(defect.updatedAt);
      matchesUpdated = updatedDate >= startOfUpdate && updatedDate <= endOfUpdate;
    }

    return matchesCreated && matchesUpdated;
  });
}, [defects, createdAfter, updatedAfter]);



  return (
    <div className="reports-container">
      <h2 className="reports-title">Defect Reports</h2>
      <div className="filters">
        <input
  type="text"
  placeholder="Search defect by ID"
  value={defectReportIdSearch}
  onChange={(e) => setDefectReportIdSearch(e.target.value)}
  onKeyDown={(e) => {
    if (e.key === 'Enter') handleSearchDefectById();
  }}
  className="filter-input"
/>
<button onClick={handleSearchDefectById} className="search-button">
  Find Defect by ID
</button>
{defectSearchError && (
  <p style={{ color: 'red', marginTop: '5px' }}>{defectSearchError}</p>
)}

        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="filter-select"
        >
          <option value="">All Statuses</option>
          <option value="CRITICAL">Critical</option>
          <option value="MAJOR">Major</option>
          <option value="MINOR">Minor</option>
          <option value="RESOLVED">Resolved</option>
        </select>
        <input
          type="date"
          value={createdAfter}
          onChange={(e) => setCreatedAfter(e.target.value)}
          className="filter-input"
          title="Created After"
        />
        <input
          type="date"
          value={updatedAfter}
          onChange={(e) => setUpdatedAfter(e.target.value)}
          className="filter-input"
          title="Updated After"
        />
      </div>

      <table className="data-table">
        <thead>
          <tr>
            <th onClick={() => handleSort("defectId")} style={{ cursor: "pointer" }}>
              Defect ID {sortBy.startsWith("defectId") && (sortBy.endsWith("asc") ? "↑" : "↓")}
            </th>
            <th>
              Checklist Result ID
            </th>
            <th>
              Description
            </th>
            <th>
              Status
            </th>
            <th onClick={() => handleSort("createdAt")} style={{ cursor: "pointer" }}>
              Created At {sortBy.startsWith("createdAt") && (sortBy.endsWith("asc") ? "↑" : "↓")}
            </th>
            <th onClick={() => handleSort("updatedAt")} style={{ cursor: "pointer" }}>
              Updated At {sortBy.startsWith("updatedAt") && (sortBy.endsWith("asc") ? "↑" : "↓")}
            </th>
          </tr>
        </thead>
        <tbody>
          {filteredDefects.map((defect) => (
            <tr key={defect.defectId}>
              <td>{defect.defectId}</td>
              <td>
                <span
                  className="clickable-link"
                  title="Checklist Review"
                  style={{ color: "#6366f1", cursor: "pointer", textDecoration: "underline" }}
                  onClick={() => {
                    setChecklistResultId(defect.checklistResultId)
                    setSelectedUserId(defect.userId);
                    setSelectedDefectStatus(defect.status);
                  }}
                >
                  {defect.checklistResultId}
                </span>
              </td>
              <td>{defect.description}</td>
              <td>
                <span
                  className={`status-badge ${defect.status === "CRITICAL"
                      ? "critical"
                      : defect.status === "MAJOR"
                        ? "major"
                        : defect.status === "RESOLVED"
                          ? "resolved"
                          : "minor"
                    }`}
                >
                  {defect.status}
                </span>
              </td>
              
              <td>{new Date(defect.createdAt).toLocaleString('en-GB', {
                day: '2-digit',
                month: 'short',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
              })}</td>
              <td>{new Date(defect.updatedAt).toLocaleString('en-GB', {
                day: '2-digit',
                month: 'short',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
              })}</td>
            </tr>
          ))}
        </tbody>
      </table>


  

        <label htmlFor="rowsPerPage">Rows per page:</label>
        <select
          id="rowsPerPage"
          value={rowsPerPage}
          onChange={(e) => {
            setRowsPerPage(Number(e.target.value));
            setPage(0); // сброс на первую страницу
          }}
        >
          <option value={10}>10</option>
          <option value={20}>20</option>
          <option value={30}>30</option>
          <option value={50}>50</option>
        </select>
        <Pagination
                currentPage={page}
                totalPages={totalPages}
                onPageChange={(newPage) => setPage(newPage)}
              />



      {checklistResultId && (
        <ModalDefect
  id={checklistResultId}
  userId={selectedUserId}
  status={selectedDefectStatus}
  onClose={() => {
    setChecklistResultId(null);
    setSelectedUserId(null);
    setSelectedDefectStatus(null);
  }}
/>

      )}


      {showUserModal && selectedUserId && (
        <ModalUser
          userId={selectedUserId}
          onClose={() => setShowUserModal(false)}
        />
      )}
    </div>
  );
}