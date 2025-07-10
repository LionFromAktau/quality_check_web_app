import { motion, AnimatePresence } from "framer-motion";
import "../modals/Modal.css";

export default function AnimatedModal({
  isOpen,
  onClose,
  children,
  className = "",
  transparentBackground = false,
}) {
  return (
    <AnimatePresence>
      {isOpen && (
        <div
          className={`modal-overlay ${transparentBackground ? "no-blur" : ""}`}
          onClick={(e) => {
            if (!transparentBackground && e.target === e.currentTarget) {
              onClose();
            }
          }}
          style={transparentBackground ? { pointerEvents: "none" } : {}}
        >
          <motion.div
            className={`modal-box ${className}`}
            initial={{ opacity: 0, scale: 0.9, x: className.includes("shift-left") ? -60 : 0 }}
            animate={{ opacity: 1, scale: 1, x: className.includes("shift-left") ? -200 : 0 }}
            exit={{ opacity: 0, scale: 0.9 }}
            transition={{ duration: 0.3 }}
            style={{ pointerEvents: "auto" }}
            onClick={(e) => e.stopPropagation()}
          >

            <button className="modal-close" onClick={onClose}>×</button>
            {children}
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
}