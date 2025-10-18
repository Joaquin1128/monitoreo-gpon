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
  Divider,
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
        transition: 'width 0.3s ease',
        '& .MuiDrawer-paper': {
          width: currentWidth,
          boxSizing: 'border-box',
          backgroundColor: '#1a1a1a',
          color: '#ffffff',
          transition: 'width 0.3s ease',
          borderRight: '1px solid #333'
        },
      }}
    >
      {/* Header */}
      <Box sx={{ p: 2, borderBottom: '1px solid #333' }}>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
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
                      backgroundColor: '#d32f2f',
                      borderRadius: 1,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      mr: 2
                    }}
                  >
                    <HomeIcon sx={{ color: 'white', fontSize: 20 }} />
                  </Box>
                  <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#ffffff' }}>
                    MONITOREO GPON
                  </Typography>
                </Box>
              </motion.div>
            )}
          </AnimatePresence>
          
          <IconButton
            onClick={() => setCollapsed(!collapsed)}
            sx={{ color: '#ffffff' }}
            size="small"
          >
            {collapsed ? <ChevronRightIcon /> : <ChevronLeftIcon />}
          </IconButton>
        </Box>
      </Box>

      {/* Navigation */}
      <Box sx={{ flex: 1, overflow: 'auto' }}>
        <List sx={{ px: 1, py: 2 }}>
          {/* Dispositivos Menu */}
          <ListItem disablePadding>
            <ListItemButton
              onClick={() => navigate('/devices')}
              sx={{
                borderRadius: 1,
                mb: 1,
                backgroundColor: location.pathname === '/devices' ? '#d32f2f' : 'transparent',
                '&:hover': {
                  backgroundColor: '#d32f2f',
                },
              }}
            >
              <ListItemIcon>
                <DevicesIcon sx={{ color: '#ffffff' }} />
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
                      primary="Gestión de dispositivos" 
                      sx={{ 
                        '& .MuiListItemText-primary': { 
                          fontWeight: location.pathname === '/devices' ? 'bold' : 'normal',
                          fontSize: '0.9rem'
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
            <ListItem>
              <Box sx={{ display: 'flex', alignItems: 'center', width: '100%' }}>
                <CircularProgress size={20} sx={{ color: '#ffffff', mr: 2 }} />
                {!collapsed && (
                  <Typography variant="body2" sx={{ color: '#888' }}>
                    Cargando...
                  </Typography>
                )}
              </Box>
            </ListItem>
          ) : error ? (
            <ListItem>
              <Alert severity="error" sx={{ width: '100%', fontSize: '0.8rem' }}>
                {error}
              </Alert>
            </ListItem>
          ) : hubs.length === 0 ? (
            <ListItem>
              {!collapsed && (
                <Typography variant="body2" sx={{ color: '#888', fontSize: '0.8rem' }}>
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
                      borderRadius: 1,
                      mb: 0.5,
                      '&:hover': {
                        backgroundColor: '#333',
                      },
                    }}
                  >
                    <ListItemIcon>
                      <HubIcon sx={{ color: '#81c784' }} />
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
                              '& .MuiListItemText-primary': { fontSize: '0.85rem' },
                              '& .MuiListItemText-secondary': { fontSize: '0.75rem', color: '#888' }
                            }}
                          />
                        </motion.div>
                      )}
                    </AnimatePresence>
                    {!collapsed && (
                      expandedHubs.has(hub.id) ? (
                        <ExpandLess sx={{ color: '#888' }} />
                      ) : (
                        <ExpandMore sx={{ color: '#888' }} />
                      )
                    )}
                  </ListItemButton>
                </ListItem>

                <Collapse in={expandedHubs.has(hub.id) && !collapsed} timeout="auto" unmountOnExit>
                  <List component="div" disablePadding sx={{ pl: 2 }}>
                    {hub.olts?.map((olt) => (
                      <motion.div
                        key={olt.id}
                        initial={{ opacity: 0, height: 0 }}
                        animate={{ opacity: 1, height: 'auto' }}
                        transition={{ duration: 0.2 }}
                      >
                        <ListItem disablePadding>
                          <ListItemButton
                            onClick={() => handleOltClick(olt)}
                            sx={{
                              borderRadius: 1,
                              mb: 0.5,
                              backgroundColor: isOltSelected(olt) ? '#333' : 'transparent',
                              borderLeft: isOltSelected(olt) ? '3px solid #d32f2f' : '3px solid transparent',
                              '&:hover': {
                                backgroundColor: '#333',
                              },
                            }}
                          >
                            <ListItemIcon>
                              <RouterIcon sx={{ color: '#ffb74d', fontSize: '1.1rem' }} />
                            </ListItemIcon>
                            <ListItemText 
                              primary={olt.name}
                              secondary={olt.ipAddress}
                              sx={{ 
                                '& .MuiListItemText-primary': { 
                                  fontSize: '0.8rem',
                                  fontWeight: isOltSelected(olt) ? 'bold' : 'normal'
                                },
                                '& .MuiListItemText-secondary': { fontSize: '0.7rem', color: '#888' }
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
      <Box sx={{ p: 2, borderTop: '1px solid #333' }}>
        <Divider sx={{ borderColor: '#333', mb: 2 }} />
        
        <ListItemButton
          onClick={handleLogout}
          sx={{
            borderRadius: 1,
            backgroundColor: 'transparent',
            '&:hover': {
              backgroundColor: '#d32f2f',
            },
          }}
        >
          <ListItemIcon>
            <LogoutIcon sx={{ color: '#f44336' }} />
          </ListItemIcon>
          <AnimatePresence>
            {!collapsed && (
              <motion.div
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.3 }}
              >
                <ListItemText primary="Cerrar Sesión" />
              </motion.div>
            )}
          </AnimatePresence>
        </ListItemButton>

        {!collapsed && (
          <Box sx={{ mt: 2, textAlign: 'center' }}>
            <Typography variant="caption" sx={{ color: '#888', fontSize: '0.7rem' }}>
              GPON MONITOREO BY JOAQUIN
            </Typography>
          </Box>
        )}
      </Box>
    </Drawer>
  );
};

export default Sidebar;