import { motion, useInView } from 'framer-motion';
import { useRef } from 'react';
import KPICards from '../Dashboard/KPICards';
import DefectsOverTimeChart from '../Dashboard/DefectsOverTimeChart';
import DefectsByProductTypeChart from '../Dashboard/DefectsByProductTypeChart';
import ChecklistsCompletedChart from '../Dashboard/ChecklistsCompletedChart';
import BatchesTable from '../Dashboard/BatchesTable';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../Dashboard/Dashboard.css';

const fadeSlide = {
  hidden: { opacity: 0, y: 60 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.5, ease: 'easeOut' } },
};

function AnimatedBlock({ children }) {
  const ref = useRef();
  const isInView = useInView(ref, { once: false, margin: '0px 0px -100px 0px' });

  return (
    <motion.div
      ref={ref}
      variants={fadeSlide}
      initial="hidden"
      animate={isInView ? 'visible' : 'hidden'}
    >
      {children}
    </motion.div>
  );
}

export default function Dashboard() {
  return (
    <div className="dashboard-page">
      <AnimatedBlock>
        <section className="mb-4">
          <div className="row g-3">
            <KPICards />
          </div>
        </section>
      </AnimatedBlock>

      <AnimatedBlock>
        <section className="section">
          <h2 className="section-title">Defects Over Time</h2>
          <DefectsOverTimeChart />
        </section>
      </AnimatedBlock>

      <AnimatedBlock>
        <section className="section">

            <DefectsByProductTypeChart />

        </section>
      </AnimatedBlock>

      <AnimatedBlock>
        <section className="section">
          <ChecklistsCompletedChart />
        </section>
      </AnimatedBlock>

      <AnimatedBlock>
        <section className="section">
          <BatchesTable />
        </section>
      </AnimatedBlock>
    </div>
  );
}
