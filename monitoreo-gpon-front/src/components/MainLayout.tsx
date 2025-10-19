import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Box } from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';
import Sidebar from '../components/Sidebar';
import DevicesView from '../pages/DevicesView';
import OltDashboard from '../pages/OltDashboard';

const MainLayout: React.FC = () => {
  return (
    <Box sx={{ display: 'flex', height: '100vh', backgroundColor: '#f9fafb' }}>
      {/* Main Sidebar */}
      <Sidebar />
      
      {/* Main Content */}
      <Box sx={{ flex: 1, overflow: 'auto' }}>
        <AnimatePresence mode="wait">
          <Routes>
            <Route path="/" element={<Navigate to="/devices" replace />} />
            <Route path="/devices" element={<DevicesView />} />
            <Route 
              path="/devices/hub/:hubId/olt/:oltId" 
              element={
                <motion.div
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: -20 }}
                  transition={{ duration: 0.3 }}
                >
                  <OltDashboard />
                </motion.div>
              } 
            />
            <Route 
              path="/devices/olt/:oltId" 
              element={
                <motion.div
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: -20 }}
                  transition={{ duration: 0.3 }}
                >
                  <OltDashboard />
                </motion.div>
              } 
            />
          </Routes>
        </AnimatePresence>
      </Box>
    </Box>
  );
};

export default MainLayout;