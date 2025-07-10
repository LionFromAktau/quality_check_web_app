import React from "react";
import "./Pagination.css";


const Pagination = ({ currentPage, totalPages, onPageChange }) => {
  const getPages = () => {
    const pages = [];

    if (totalPages <= 7) {
      for (let i = 0; i < totalPages; i++) pages.push(i);
    } else {
      if (currentPage <= 3) {
        pages.push(0, 1, 2, 3, "...", totalPages - 1);
      } else if (currentPage >= totalPages - 4) {
        pages.push(0, "...", totalPages - 4, totalPages - 3, totalPages - 2, totalPages - 1);
      } else {
        pages.push(0, "...", currentPage - 1, currentPage, currentPage + 1, "...", totalPages - 1);
      }
    }

    return pages;
  };

  return (
    <div className="pagination">
      <button disabled={currentPage === 0} onClick={() => onPageChange(currentPage - 1)}>
        &laquo;
      </button>

      {getPages().map((p, idx) =>
        p === "..." ? (
          <span key={idx} className="ellipsis">...</span>
        ) : (
          <button
            key={idx}
            className={p === currentPage ? "active" : ""}
            onClick={() => onPageChange(p)}
          >
            {p + 1}
          </button>
        )
      )}

      <button
        disabled={currentPage === totalPages - 1}
        onClick={() => onPageChange(currentPage + 1)}
      >
        &raquo;
      </button>
    </div>
  );
};

export default Pagination;
