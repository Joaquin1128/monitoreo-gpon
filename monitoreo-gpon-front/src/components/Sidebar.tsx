import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  Box,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Typography,
  IconButton
} from '@mui/material';
import {
  Devices as DevicesIcon,
  Logout as LogoutIcon,
  ChevronLeft as ChevronLeftIcon,
  ChevronRight as ChevronRightIcon,
  Home as HomeIcon
} from '@mui/icons-material';
import { motion, AnimatePresence } from 'framer-motion';
import { tokenUtils } from '../utils/token';

const drawerWidth = 280;
const collapsedWidth = 64;

interface SidebarProps {
  // Props simplificadas - ya no necesitamos manejar OLTs seleccionados
}

const Sidebar: React.FC<SidebarProps> = () => {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    tokenUtils.clearToken();
    navigate('/login');
  };

  const currentWidth = collapsed ? collapsedWidth : drawerWidth;

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: currentWidth,
        flexShrink: 0,
        transition: 'width 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        '& .MuiDrawer-paper': {
          width: currentWidth,
          boxSizing: 'border-box',
          backgroundColor: '#1f2937',
          color: '#f9fafb',
          transition: 'width 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          borderRight: '1px solid #374151',
          boxShadow: '0 0 0 1px rgba(0, 0, 0, 0.05), 0 4px 6px -1px rgba(0, 0, 0, 0.1)',
          overflow: 'hidden',
        },
      }}
    >
      {/* Header */}
      <Box sx={{ 
        p: collapsed ? 1.5 : 2, 
        borderBottom: '1px solid #374151',
        minHeight: collapsed ? 64 : 'auto'
      }}>
        <Box sx={{ 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: collapsed ? 'center' : 'space-between',
          height: collapsed ? 32 : 'auto'
        }}>
          <AnimatePresence>
            {!collapsed && (
              <motion.div
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.3 }}
              >
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Box
                    sx={{
                      width: 32,
                      height: 32,
                      backgroundColor: '#3b82f6',
                      borderRadius: 1.5,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      mr: 2,
                      boxShadow: '0 2px 4px rgba(59, 130, 246, 0.3)'
                    }}
                  >
                    <HomeIcon sx={{ color: 'white', fontSize: 20 }} />
                  </Box>
                  <Typography variant="h6" sx={{ 
                    fontWeight: 600, 
                    color: '#f9fafb',
                    fontSize: '1.1rem'
                  }}>
                    MonitoreoGpon
                  </Typography>
                </Box>
              </motion.div>
            )}
          </AnimatePresence>
          
          <IconButton
            onClick={() => setCollapsed(!collapsed)}
            sx={{ 
              color: '#9ca3af',
              '&:hover': {
                backgroundColor: '#374151',
                color: '#f9fafb'
              }
            }}
            size="small"
          >
            {collapsed ? <ChevronRightIcon /> : <ChevronLeftIcon />}
          </IconButton>
        </Box>
      </Box>

      {/* Navigation */}
      <Box sx={{ 
        flex: 1, 
        overflow: collapsed ? 'visible' : 'auto',
        display: 'flex',
        flexDirection: 'column'
      }}>
        <List sx={{ 
          px: collapsed ? 0.5 : 1, 
          py: collapsed ? 1 : 2,
          flex: 1
        }}>
          {/* Dispositivos Menu */}
          <ListItem disablePadding>
            <ListItemButton
              onClick={() => navigate('/devices')}
              sx={{
                borderRadius: 2,
                mb: 1,
                mx: collapsed ? 0.5 : 0,
                backgroundColor: location.pathname === '/devices' ? '#1e40af' : 'transparent',
                color: location.pathname === '/devices' ? '#f9fafb' : '#9ca3af',
                '&:hover': {
                  backgroundColor: '#374151',
                  color: '#f9fafb',
                },
                minHeight: collapsed ? 48 : 'auto',
                justifyContent: collapsed ? 'center' : 'flex-start',
                px: collapsed ? 1 : 2,
              }}
            >
              <ListItemIcon sx={{ 
                minWidth: collapsed ? 'auto' : 40,
                justifyContent: 'center',
                color: 'inherit'
              }}>
                <DevicesIcon sx={{ 
                  fontSize: collapsed ? 24 : 20,
                  color: 'inherit'
                }} />
              </ListItemIcon>
              <AnimatePresence>
                {!collapsed && (
                  <motion.div
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, x: -20 }}
                    transition={{ duration: 0.3 }}
                  >
                    <ListItemText 
                      primary="Dispositivos" 
                      sx={{ 
                        '& .MuiListItemText-primary': { 
                          fontWeight: location.pathname === '/devices' ? 600 : 500,
                          fontSize: '0.875rem',
                          color: 'inherit'
                        } 
                      }} 
                    />
                  </motion.div>
                )}
              </AnimatePresence>
            </ListItemButton>
          </ListItem>

        </List>
      </Box>

      {/* Footer */}
      <Box sx={{ 
        p: collapsed ? 1.5 : 2, 
        borderTop: '1px solid #374151',
        minHeight: collapsed ? 64 : 'auto'
      }}>
        <ListItemButton
          onClick={handleLogout}
          sx={{
            borderRadius: 2,
            backgroundColor: 'transparent',
            color: '#ef4444',
            justifyContent: collapsed ? 'center' : 'flex-start',
            px: collapsed ? 1 : 2,
            minHeight: collapsed ? 48 : 'auto',
            border: 'none',
            '&:hover': {
              backgroundColor: '#374151',
              color: '#fca5a5',
            },
          }}
        >
          <ListItemIcon sx={{ 
            minWidth: collapsed ? 'auto' : 40,
            justifyContent: 'center',
            color: 'inherit'
          }}>
            <LogoutIcon sx={{ 
              fontSize: collapsed ? 24 : 20,
              color: 'inherit'
            }} />
          </ListItemIcon>
          <AnimatePresence>
            {!collapsed && (
              <motion.div
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.3 }}
              >
                <ListItemText 
                  primary="Cerrar Sesión" 
                  sx={{ 
                    '& .MuiListItemText-primary': { 
                      fontWeight: 500,
                      fontSize: '0.875rem',
                      color: 'inherit'
                    } 
                  }} 
                />
              </motion.div>
            )}
          </AnimatePresence>
        </ListItemButton>

        {!collapsed && (
          <Box sx={{ mt: 2, textAlign: 'center' }}>
            <Typography variant="caption" sx={{ color: '#6b7280', fontSize: '0.7rem' }}>
              MonitoreoGpon By Joaquin
            </Typography>
          </Box>
        )}
      </Box>
    </Drawer>
  );
};

export default Sidebar;