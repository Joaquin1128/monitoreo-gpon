import React, { useEffect, useState } from 'react';
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
  Collapse,
  IconButton,
  CircularProgress,
  Alert
} from '@mui/material';
import {
  Devices as DevicesIcon,
  ExpandLess,
  ExpandMore,
  Hub as HubIcon,
  Router as RouterIcon,
  Logout as LogoutIcon,
  ChevronLeft as ChevronLeftIcon,
  ChevronRight as ChevronRightIcon,
  Home as HomeIcon
} from '@mui/icons-material';
import { motion, AnimatePresence } from 'framer-motion';
import { hubService } from '../services/index';
import { Hub, Olt } from '../types';
import { tokenUtils } from '../utils/token';

const drawerWidth = 280;
const collapsedWidth = 64;

interface SidebarProps {
  selectedOltId?: number;
  onOltSelect?: (olt: Olt) => void;
}

const Sidebar: React.FC<SidebarProps> = ({ selectedOltId, onOltSelect }) => {
  const [hubs, setHubs] = useState<Hub[]>([]);
  const [expandedHubs, setExpandedHubs] = useState<Set<number>>(new Set());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    loadHubs();
  }, []);

  const loadHubs = async () => {
    try {
      setLoading(true);
      const hubsData = await hubService.getAll();
      setHubs(hubsData);
      setError(null);
    } catch (err) {
      console.error('Error loading hubs:', err);
      setError('Error al cargar los dispositivos');
    } finally {
      setLoading(false);
    }
  };

  const handleHubToggle = (hubId: number) => {
    const newExpanded = new Set(expandedHubs);
    if (newExpanded.has(hubId)) {
      newExpanded.delete(hubId);
    } else {
      newExpanded.add(hubId);
    }
    setExpandedHubs(newExpanded);
  };

  const handleOltClick = (olt: Olt) => {
    if (onOltSelect) {
      onOltSelect(olt);
    }
    
    // Debug: Log the complete OLT object to understand its structure
    console.log('OLT object:', olt);
    console.log('OLT ID:', olt.id, 'Type:', typeof olt.id);
    console.log('Hub object:', olt.hub);
    console.log('Hub ID:', olt.hub?.id, 'Type:', typeof olt.hub?.id);
    
    // Verificar que olt.id sea válido
    if (!olt.id) {
      console.error('OLT ID inválido:', { 
        oltId: olt.id, 
        oltIdType: typeof olt.id
      });
      return;
    }
    
    // Si no hay hub, navegar solo con oltId
    if (!olt.hub?.id) {
      console.warn('Hub ID no disponible, navegando solo con OLT ID');
      navigate(`/devices/olt/${olt.id}`);
      return;
    }
    
    // Navegación normal con hub y olt
    navigate(`/devices/hub/${olt.hub.id}/olt/${olt.id}`);
  };

  const handleLogout = () => {
    tokenUtils.clearToken();
    navigate('/login');
  };

  const isOltSelected = (olt: Olt) => {
    return selectedOltId === olt.id || location.pathname.includes(`/olt/${olt.id}`);
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

          {/* Hubs and OLTs */}
          {loading ? (
            <ListItem sx={{ px: collapsed ? 0.5 : 2 }}>
              <Box sx={{ 
                display: 'flex', 
                alignItems: 'center', 
                width: '100%',
                justifyContent: collapsed ? 'center' : 'flex-start'
              }}>
                <CircularProgress size={20} sx={{ color: '#9ca3af' }} />
                {!collapsed && (
                  <Typography variant="body2" sx={{ color: '#9ca3af', ml: 2 }}>
                    Cargando...
                  </Typography>
                )}
              </Box>
            </ListItem>
          ) : error ? (
            <ListItem sx={{ px: collapsed ? 0.5 : 2 }}>
              {!collapsed && (
                <Alert severity="error" sx={{ width: '100%', fontSize: '0.8rem' }}>
                  {error}
                </Alert>
              )}
            </ListItem>
          ) : hubs.length === 0 ? (
            <ListItem sx={{ px: collapsed ? 0.5 : 2 }}>
              {!collapsed && (
                <Typography variant="body2" sx={{ color: '#9ca3af', fontSize: '0.8rem' }}>
                  No hay dispositivos
                </Typography>
              )}
            </ListItem>
          ) : (
            hubs.map((hub) => (
              <motion.div
                key={hub.id}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.3 }}
              >
                <ListItem disablePadding>
                  <ListItemButton
                    onClick={() => handleHubToggle(hub.id)}
                    sx={{
                      borderRadius: 2,
                      mb: 0.5,
                      mx: collapsed ? 0.5 : 0,
                      backgroundColor: expandedHubs.has(hub.id) ? '#374151' : 'transparent',
                      color: '#9ca3af',
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
                      <HubIcon sx={{ 
                        fontSize: collapsed ? 24 : 20,
                        color: '#10b981'
                      }} />
                    </ListItemIcon>
                    <AnimatePresence>
                      {!collapsed && (
                        <motion.div
                          initial={{ opacity: 0, x: -20 }}
                          animate={{ opacity: 1, x: 0 }}
                          exit={{ opacity: 0, x: -20 }}
                          transition={{ duration: 0.3 }}
                          style={{ flex: 1 }}
                        >
                          <ListItemText 
                            primary={hub.name}
                            secondary={`${hub.olts?.length || 0} OLTs`}
                            sx={{ 
                              '& .MuiListItemText-primary': { 
                                fontSize: '0.875rem',
                                fontWeight: 500,
                                color: 'inherit'
                              },
                              '& .MuiListItemText-secondary': { 
                                fontSize: '0.75rem', 
                                color: '#6b7280' 
                              }
                            }}
                          />
                        </motion.div>
                      )}
                    </AnimatePresence>
                    {!collapsed && (
                      expandedHubs.has(hub.id) ? (
                        <ExpandLess sx={{ color: '#9ca3af' }} />
                      ) : (
                        <ExpandMore sx={{ color: '#9ca3af' }} />
                      )
                    )}
                  </ListItemButton>
                </ListItem>

                <Collapse in={expandedHubs.has(hub.id) && !collapsed} timeout="auto" unmountOnExit>
                  <List component="div" disablePadding sx={{ pl: 2 }}>
                    {hub.olts?.map((olt) => (
                      <motion.div
                        key={olt.id}
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ duration: 0.3 }}
                      >
                        <ListItem disablePadding>
                          <ListItemButton
                            onClick={() => handleOltClick(olt)}
                            sx={{
                              borderRadius: 2,
                              mb: 0.5,
                              backgroundColor: isOltSelected(olt) ? '#1e40af' : 'transparent',
                              color: isOltSelected(olt) ? '#f9fafb' : '#9ca3af',
                              '&:hover': {
                                backgroundColor: '#374151',
                                color: '#f9fafb',
                              },
                            }}
                          >
                            <ListItemIcon sx={{ minWidth: 32 }}>
                              <RouterIcon sx={{ 
                                fontSize: 18,
                                color: '#f59e0b'
                              }} />
                            </ListItemIcon>
                            <ListItemText 
                              primary={olt.name}
                              secondary={olt.ipAddress}
                              sx={{ 
                                '& .MuiListItemText-primary': { 
                                  fontSize: '0.8rem',
                                  fontWeight: isOltSelected(olt) ? 600 : 500,
                                  color: 'inherit'
                                },
                                '& .MuiListItemText-secondary': { 
                                  fontSize: '0.7rem', 
                                  color: '#6b7280' 
                                }
                              }}
                            />
                          </ListItemButton>
                        </ListItem>
                      </motion.div>
                    ))}
                  </List>
                </Collapse>
              </motion.div>
            ))
          )}
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
              GPON MONITOREO BY JOAQUIN
            </Typography>
          </Box>
        )}
      </Box>
    </Drawer>
  );
};

export default Sidebar;