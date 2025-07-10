import React, { useEffect, useState, useMemo } from "react";
import './ProductsBatches.css';
import api from "../services/axiosInstance";
import ModalBatch from "../modals/modalBatch";
import ModalProduct from "../modals/modalProduct";
import Pagination from "../ui/Pagination";

export default function ProductsBatches() {
  const [showProducts, setShowProducts] = useState(true);
  const [products, setProducts] = useState([]);
  const [batches, setBatches] = useState([]);
  const [selectedBatchId, setSelectedBatchId] = useState(null);

  const [productsPage, setProductsPage] = useState(0);
  const [batchesPage, setBatchesPage] = useState(0);
  const [productsTotalPages, setProductsTotalPages] = useState(0);
  const [selectedProductId, setSelectedProductId] = useState(null);

  const [batchSearchError, setBatchSearchError] = useState('');
  const [productSearchError, setProductSearchError] = useState('');

  const [batchIdSearch, setBatchIdSearch] = useState("");

  const [productIdSearch, setProductIdSearch] = useState("");

  const [batchesTotalPages, setBatchesTotalPages] = useState(0);
  const [showBatchesRows, setShowBatchesRows] = useState(10);
  const [showProductsRows, setShowProductsRows] = useState(10);

  const [createdAfter, setCreatedAfter] = useState("");
  const [updatedAfter, setUpdatedAfter] = useState("");
  const [batchesStatusFilter, setBatchesStatusFilter] = useState("");


  const [batchCreatedAfter, setBatchCreatedAfter] = useState("");
  const [batchUpdatedAfter, setBatchUpdatedAfter] = useState("");
  const [sortBatchesBy, setSortBatchesBy] = useState("createdAt,desc");
  const [sortProductsBy, setsortProductsBy] = useState("createdAt,desc");

  useEffect(() => {
    api.get(`/api/products?page=${productsPage}&size=${showProductsRows}&sort=${sortProductsBy}`)
      .then(response => {
        setProducts(response.data.content);
        setProductsTotalPages(response.data.totalPages);
      })
      .catch(error => console.error("Error fetching products:", error));
  }, [productsPage, showProductsRows, sortProductsBy]);

  const handleSortProducts = (field) => {
    const [currentField, currentOrder] = sortProductsBy.split(',');
    const newOrder = currentField === field && currentOrder === 'asc' ? 'desc' : 'asc';
    setsortProductsBy(`${field},${newOrder}`);
  };


  useEffect(() => {
  if (!showProducts) {
    api.get("/api/batches", {
      params: {
        page: batchesPage,
        size: showBatchesRows,
        sort: sortBatchesBy,
        status: batchesStatusFilter || undefined, // не отправлять, если пусто
      },
    })
      .then(response => {
        setBatches(response.data.content);
        setBatchesTotalPages(response.data.totalPages);
      })
      .catch(error => console.error("Error fetching batches:", error));
  }
}, [showProducts, batchesPage, showBatchesRows, sortBatchesBy, batchesStatusFilter]);


  const handleSortBatches = (column) => {
    setSortBatchesBy(prev => {
      const [field, direction] = prev.split(",");
      if (field === column) {
        return `${column},${direction === "asc" ? "desc" : "asc"}`;
      } else {
        return `${column},asc`;
      }
    });
  };


  const filteredProducts = useMemo(() => {
    return products.filter(product => {


      const matchesCreated = createdAfter
        ? new Date(product.createdAt).toISOString().split('T')[0] === createdAfter
        : true;

      const matchesUpdated = updatedAfter
        ? new Date(product.updatedAt).toISOString().split('T')[0] === updatedAfter
        : true;


      return matchesCreated && matchesUpdated;
    });
  }, [products, createdAfter, updatedAfter]);

  const filteredBatches = useMemo(() => {
    return batches.filter(batch => {

      const matchesCreated = batchCreatedAfter
        ? new Date(batch.createdAt).toISOString().split('T')[0] === batchCreatedAfter
        : true;

      const matchesUpdated = batchUpdatedAfter
        ? new Date(batch.updatedAt).toISOString().split('T')[0] === batchUpdatedAfter
        : true;

      return  matchesCreated && matchesUpdated;
    });
  }, [batches, batchCreatedAfter, batchUpdatedAfter]);

  const handleSearchBatchById = () => {
    if (!batchIdSearch.trim()) return;
    api.get(`/api/batches/${batchIdSearch.trim()}`)
      .then(res => {
        console.log("Batch found:", res.data);
        setSelectedBatchId(res.data.batchId);
        setBatchSearchError('');
      })
      .catch(err => {
        console.error("Batch not found:", err);
        setBatchSearchError('Batch not found');
      });
  };

  const handleSearchProductById = () => {
    if (!productIdSearch.trim()) return;
    api.get(`/api/products/${productIdSearch.trim()}`)
      .then(res => {
        setSelectedProductId(res.data.id);
        setProductSearchError('');
      })
      .catch(err => {
        console.error("Product not found:", err);
        setProductSearchError('Product not found');

      });
  };

  return (
    <div>
      <div className="buttons-toggle">
        <button
          onClick={() => setShowProducts(true)}
          className={showProducts ? 'active-toggle' : ''}
        >
          Show Products
        </button>
        <button
          onClick={() => setShowProducts(false)}
          className={!showProducts ? 'active-toggle' : ''}
        >
          Show Batches
        </button>
      </div>
      <h1>{showProducts ? "Products List" : "Batches List"}</h1>
      {showProducts ? (
        <div>
          <div className="filters">
            <input
              type="text"
              placeholder="Search product by ID"
              value={productIdSearch}
              onChange={(e) => setProductIdSearch(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter') handleSearchProductById();
              }}
              className="filter-input"
            />
            <button onClick={handleSearchProductById} className="search-button">
              Find Product by ID
            </button>
            {productSearchError && (
              <p style={{ color: 'red', marginTop: '5px' }}>{productSearchError}</p>
            )}
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
          {filteredProducts.length > 0 ? (
            <div>
              <div className="rows-selector">
                <label htmlFor="productRows">Rows per page: </label>
                <select
                  id="productRows"
                  value={showProductsRows}
                  onChange={(e) => {
                    setShowProductsRows(Number(e.target.value));
                    setProductsPage(0);
                  }}
                >
                  <option value={10}>10</option>
                  <option value={20}>20</option>
                  <option value={30}>30</option>
                  <option value={50}>50</option>
                </select>
              </div>

              <table className="data-table">
                <thead>
                  <tr>
                    <th onClick={() => handleSortProducts("id")} style={{ cursor: "pointer" }}>
                      Product ID {sortProductsBy.startsWith("id") && (sortProductsBy.endsWith("asc") ? "↑" : "↓")}
                    </th>
                    <th>
                      Product Name
                    </th>
                    <th>
                      Type
                    </th>
                    <th onClick={() => handleSortProducts("createdAt")} style={{ cursor: "pointer" }}>
                      Created {sortProductsBy.startsWith("createdAt") && (sortProductsBy.endsWith("asc") ? "↑" : "↓")}
                    </th>
                    <th onClick={() => handleSortProducts("updatedAt")} style={{ cursor: "pointer" }}>
                      Updated {sortProductsBy.startsWith("updatedAt") && (sortProductsBy.endsWith("asc") ? "↑" : "↓")}
                    </th>
                  </tr>
                </thead>

                <tbody>
                  {filteredProducts.map(product => (
                    <tr
                      key={product.id}
                      onClick={() => setSelectedProductId(product.id)}
                      style={{ cursor: "pointer" }}
                    >
                      <td>{product.id}</td>
                      <td>{product.name}</td>
                      <td>{product.productType}</td>
                      <td>{new Date(product.createdAt).toLocaleString('en-GB', {
                        day: '2-digit',
                        month: 'short',
                        year: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                      })}</td>
                      <td>{new Date(product.updatedAt).toLocaleString('en-GB', {
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
              
              <div className="rows-selector">
                <label htmlFor="productRows">Rows per page: </label>
                <select
                  id="productRows"
                  value={showProductsRows}
                  onChange={(e) => {
                    setShowProductsRows(Number(e.target.value));
                    setProductsPage(0);
                  }}
                >
                  <option value={10}>10</option>
                  <option value={20}>20</option>
                  <option value={30}>30</option>
                  <option value={50}>50</option>
                </select>
                <Pagination
                currentPage={productsPage}
                totalPages={productsTotalPages}
                onPageChange={(page) => setBatchesPage(page)}
              />
              </div>

            </div>

          ) : (
            <p className="no-data">No products found with current filters.</p>
          )}
        </div>
      ) : (
        <div>
          <div className="filters">
            <input
              type="text"
              placeholder="Search batch by ID"
              value={batchIdSearch}
              onChange={(e) => setBatchIdSearch(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter') handleSearchBatchById();
              }}
              className="filter-input"
            />
            <button onClick={handleSearchBatchById} className="search-button">
              Find Batch by ID
            </button>
            {batchSearchError && (
              <p style={{ color: 'red', marginTop: '5px' }}>{batchSearchError}</p>
            )}
            <select
  value={batchesStatusFilter}
  onChange={(e) => setBatchesStatusFilter(e.target.value)}
  className="filter-select"
>
  <option value="">All statuses</option>
  <option value="CHECKING">Checking</option>
  <option value="CHECKED">Checked</option>
</select>

            <input
              type="date"
              value={batchCreatedAfter}
              onChange={(e) => setBatchCreatedAfter(e.target.value)}
              className="filter-input"
              title="Created After"
            />
            <input
              type="date"
              value={batchUpdatedAfter}
              onChange={(e) => setBatchUpdatedAfter(e.target.value)}
              className="filter-input"
              title="Updated After"
            />
          </div>

          {filteredBatches.length > 0 ? (
            <div>
              <div className="rows-selector">
                <label htmlFor="rows">Rows per page: </label>
                <select
                  id="rows"
                  value={showBatchesRows}
                  onChange={(e) => {
                    setShowBatchesRows(Number(e.target.value));
                    setBatchesPage(0);
                  }}
                >
                  <option value={10}>10</option>
                  <option value={20}>20</option>
                  <option value={30}>30</option>
                  <option value={50}>50</option>
                </select>

              </div>
              <table className="data-table">
                <thead>
                  <tr>
                    <th onClick={() => handleSortBatches("batchId")} style={{ cursor: "pointer" }}>
                      Batch ID {sortBatchesBy.startsWith("batchId") && (sortBatchesBy.endsWith("asc") ? "↑" : "↓")}
                    </th>
                    <th >
                      Product Name
                    </th>
                    <th onClick={() => handleSortBatches("quantity")} style={{ cursor: "pointer" }}>
                      Quantity {sortBatchesBy.startsWith("quantity") && (sortBatchesBy.endsWith("asc") ? "↑" : "↓")}
                    </th>
                    <th>
                      Status
                    </th>
                    <th>Notes</th>
                    <th onClick={() => handleSortBatches("createdAt")} style={{ cursor: "pointer" }}>
                      Created {sortBatchesBy.startsWith("createdAt") && (sortBatchesBy.endsWith("asc") ? "↑" : "↓")}
                    </th>
                    <th onClick={() => handleSortBatches("updatedAt")} style={{ cursor: "pointer" }}>
                      Updated {sortBatchesBy.startsWith("updatedAt") && (sortBatchesBy.endsWith("asc") ? "↑" : "↓")}
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {filteredBatches.map(batch => (
                    <tr key={batch.batchId} onClick={() => setSelectedBatchId(batch.batchId)} style={{ cursor: "pointer" }}>
                      <td>{batch.batchId}</td>
                      <td>{batch.productName}</td>
                      <td>{batch.quantity}</td>
                      <td>{batch.status}</td>
                      <td>{batch.notes || '-'}</td>
                      <td>{new Date(batch.createdAt).toLocaleString('en-GB', {
                        day: '2-digit',
                        month: 'short',
                        year: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                      })
                      }</td>
                      <td>{new Date(batch.updatedAt).toLocaleString('en-GB', {
                        day: '2-digit',
                        month: 'short',
                        year: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                      })
                      }</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <div className="rows-selector">
                <label htmlFor="rows">Rows per page: </label>
                <select
                  id="rows"
                  value={showBatchesRows}
                  onChange={(e) => {
                    setShowBatchesRows(Number(e.target.value));
                    setBatchesPage(0);
                  }}
                >
                  <option value={10}>10</option>
                  <option value={20}>20</option>
                  <option value={30}>30</option>
                  <option value={50}>50</option>
                </select>
              </div>
              <Pagination
                currentPage={batchesPage}
                totalPages={batchesTotalPages}
                onPageChange={(page) => setBatchesPage(page)}
              />
            </div>
          ) : (
            <p className="no-data">No batches found with current filters.</p>
          )}
        </div>
      )}
      {selectedBatchId && (
        <ModalBatch id={selectedBatchId} onClose={() => setSelectedBatchId(null)} />
      )}
      {selectedProductId && (
        <ModalProduct id={selectedProductId} onClose={() => setSelectedProductId(null)} />
      )}

    </div>
  );
}