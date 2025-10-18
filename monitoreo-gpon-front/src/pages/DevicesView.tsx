import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Chip,
  Button,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Tooltip,
  CircularProgress,
  Alert,
  Avatar
} from '@mui/material';
import {
  Hub as HubIcon,
  Router as RouterIcon,
  Visibility as VisibilityIcon,
  Refresh as RefreshIcon,
  NetworkCheck as NetworkIcon,
  Add as AddIcon,
  FilterList as FilterIcon,
  MoreVert as MoreVertIcon
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { hubService } from '../services';
import { Hub, Olt } from '../types';

const DevicesView: React.FC = () => {
  const [hubs, setHubs] = useState<Hub[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

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

  const handleViewOlt = (olt: Olt) => {
    // Debug: Log the complete OLT object to understand its structure
    console.log('DevicesView - OLT object:', olt);
    console.log('DevicesView - OLT ID:', olt.id, 'Type:', typeof olt.id);
    console.log('DevicesView - Hub object:', olt.hub);
    console.log('DevicesView - Hub ID:', olt.hub?.id, 'Type:', typeof olt.hub?.id);
    
    // Verificar que olt.id sea válido
    if (!olt.id) {
      console.error('DevicesView - OLT ID inválido:', { 
        oltId: olt.id, 
        oltIdType: typeof olt.id
      });
      return;
    }
    
    // Si no hay hub, navegar solo con oltId
    if (!olt.hub?.id) {
      console.warn('DevicesView - Hub ID no disponible, navegando solo con OLT ID');
      navigate(`/devices/olt/${olt.id}`);
      return;
    }
    
    // Navegación normal con hub y olt
    navigate(`/devices/hub/${olt.hub.id}/olt/${olt.id}`);
  };

  const getStatusColor = (olt: Olt) => {
    return olt.ipAddress ? 'success' : 'warning';
  };

  const getStatusText = (olt: Olt) => {
    return olt.ipAddress ? 'Activo' : 'Sin IP';
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
        <CircularProgress size={60} />
        <Typography variant="h6" sx={{ ml: 2 }}>
          Cargando dispositivos...
        </Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert 
          severity="error" 
          action={
            <Button color="inherit" size="small" onClick={loadHubs}>
              Reintentar
            </Button>
          }
        >
          {error}
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3, backgroundColor: '#f9fafb', minHeight: '100vh' }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" component="h1" sx={{ fontWeight: 'bold', color: '#111827', mb: 1 }}>
            Gestión de dispositivos
          </Typography>
          <Typography variant="body1" color="textSecondary">
            Administra y monitorea tus dispositivos GPON
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="outlined"
            startIcon={<FilterIcon />}
            sx={{ borderRadius: 2 }}
          >
            Filtros
          </Button>
          <Button
            variant="outlined"
            startIcon={<RefreshIcon />}
            onClick={loadHubs}
            disabled={loading}
            sx={{ borderRadius: 2 }}
          >
            Actualizar
          </Button>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            sx={{ 
              borderRadius: 2,
              backgroundColor: '#3b82f6',
              '&:hover': {
                backgroundColor: '#1e40af'
              }
            }}
          >
            Registrar dispositivos
          </Button>
        </Box>
      </Box>

      {/* Stats Cards */}
      <Box sx={{ display: 'flex', gap: 3, mb: 4 }}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <Card sx={{ flex: 1, borderRadius: 3 }}>
            <CardContent sx={{ textAlign: 'center' }}>
              <Typography variant="h3" sx={{ fontWeight: 'bold', color: '#d32f2f' }}>
                {hubs.length}
              </Typography>
              <Typography variant="body1" color="textSecondary">
                Hubs Totales
              </Typography>
            </CardContent>
          </Card>
        </motion.div>
        
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
        >
          <Card sx={{ flex: 1, borderRadius: 3 }}>
            <CardContent sx={{ textAlign: 'center' }}>
              <Typography variant="h3" sx={{ fontWeight: 'bold', color: '#1976d2' }}>
                {hubs.reduce((total, hub) => total + (hub.olts?.length || 0), 0)}
              </Typography>
              <Typography variant="body1" color="textSecondary">
                OLTs Totales
              </Typography>
            </CardContent>
          </Card>
        </motion.div>
        
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
        >
          <Card sx={{ flex: 1, borderRadius: 3 }}>
            <CardContent sx={{ textAlign: 'center' }}>
              <Typography variant="h3" sx={{ fontWeight: 'bold', color: '#388e3c' }}>
                {hubs.reduce((total, hub) => 
                  total + (hub.olts?.filter(olt => olt.ipAddress).length || 0), 0
                )}
              </Typography>
              <Typography variant="body1" color="textSecondary">
                OLTs Activas
              </Typography>
            </CardContent>
          </Card>
        </motion.div>
      </Box>

      {/* Devices Table */}
      {hubs.length === 0 ? (
        <Paper sx={{ p: 6, textAlign: 'center', borderRadius: 3 }}>
          <HubIcon sx={{ fontSize: 80, color: '#ccc', mb: 2 }} />
          <Typography variant="h5" color="textSecondary" sx={{ mb: 1 }}>
            No hay dispositivos disponibles
          </Typography>
          <Typography variant="body1" color="textSecondary">
            Los dispositivos aparecerán aquí una vez que estén configurados en el sistema.
          </Typography>
        </Paper>
      ) : (
        <Card sx={{ borderRadius: 3, overflow: 'hidden' }}>
          <CardContent sx={{ p: 0 }}>
            {/* Table Header */}
            <Box sx={{ 
              backgroundColor: '#f8f9fa', 
              p: 2, 
              borderBottom: '1px solid #e0e0e0',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                Dispositivos ({hubs.reduce((total, hub) => total + (hub.olts?.length || 0), 0)})
              </Typography>
              <Box sx={{ display: 'flex', gap: 1 }}>
                <Typography variant="body2" color="textSecondary">
                  Mostrar 10 entradas
                </Typography>
                <IconButton size="small">
                  <MoreVertIcon />
                </IconButton>
              </Box>
            </Box>

            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
                    <TableCell sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>NOMBRE PARA MOSTRAR</TableCell>
                    <TableCell sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>STATUS</TableCell>
                    <TableCell sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>IP</TableCell>
                    <TableCell sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>MODELO</TableCell>
                    <TableCell sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>TIEMPO DE ACTIVIDAD</TableCell>
                    <TableCell sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>ACCIÓN</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {hubs.map((hub, hubIndex) => (
                    <React.Fragment key={hub.id}>
                      {/* Hub Row */}
                      <motion.tr
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        transition={{ duration: 0.3, delay: hubIndex * 0.05 }}
                        style={{ display: 'table-row' }}
                      >
                        <TableCell>
                          <Box sx={{ display: 'flex', alignItems: 'center' }}>
                            <Avatar sx={{ 
                              backgroundColor: '#81c784', 
                              mr: 2,
                              width: 40,
                              height: 40
                            }}>
                              <HubIcon />
                            </Avatar>
                            <Box>
                              <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
                                {hub.name}
                              </Typography>
                              <Typography variant="body2" color="textSecondary">
                                Hub • {hub.olts?.length || 0} OLTs
                              </Typography>
                            </Box>
                          </Box>
                        </TableCell>
                        <TableCell>
                          <Chip
                            label="Activo"
                            color="success"
                            size="small"
                            icon={<NetworkIcon />}
                          />
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2" sx={{ fontFamily: 'monospace' }}>
                            N/A
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2">
                            Hub
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2">
                            Siempre activo
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Box sx={{ display: 'flex', gap: 1 }}>
                            <Tooltip title="Ver detalles">
                              <IconButton size="small" sx={{ color: '#1976d2' }}>
                                <VisibilityIcon />
                              </IconButton>
                            </Tooltip>
                            <Tooltip title="Actualizar">
                              <IconButton size="small" sx={{ color: '#388e3c' }}>
                                <RefreshIcon />
                              </IconButton>
                            </Tooltip>
                          </Box>
                        </TableCell>
                      </motion.tr>

                      {/* OLT Rows */}
                      {hub.olts?.map((olt, oltIndex) => (
                        <motion.tr
                          key={olt.id}
                          initial={{ opacity: 0 }}
                          animate={{ opacity: 1 }}
                          transition={{ duration: 0.3, delay: (hubIndex * 0.05) + (oltIndex * 0.02) }}
                          style={{ display: 'table-row' }}
                        >
                          <TableCell>
                            <Box sx={{ display: 'flex', alignItems: 'center', pl: 4 }}>
                              <Avatar sx={{ 
                                backgroundColor: '#ffb74d', 
                                mr: 2,
                                width: 32,
                                height: 32
                              }}>
                                <RouterIcon sx={{ fontSize: 18 }} />
                              </Avatar>
                              <Box>
                                <Typography variant="body2" sx={{ fontWeight: 'medium' }}>
                                  {olt.name}
                                </Typography>
                                <Typography variant="caption" color="textSecondary">
                                  OLT • {olt.cantPorts || 0} puertos
                                </Typography>
                              </Box>
                            </Box>
                          </TableCell>
                          <TableCell>
                            <Chip
                              label={getStatusText(olt)}
                              color={getStatusColor(olt) as any}
                              size="small"
                              icon={<NetworkIcon />}
                            />
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2" sx={{ fontFamily: 'monospace' }}>
                              {olt.ipAddress || 'N/A'}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2">
                              {olt.model || 'N/A'}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2">
                              {olt.ipAddress ? '1 día, 23 horas' : 'N/A'}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Box sx={{ display: 'flex', gap: 1 }}>
                              <Tooltip title="Ver detalles">
                                <IconButton 
                                  size="small" 
                                  sx={{ color: '#1976d2' }}
                                  onClick={() => handleViewOlt(olt)}
                                >
                                  <VisibilityIcon />
                                </IconButton>
                              </Tooltip>
                              <Tooltip title="Actualizar">
                                <IconButton size="small" sx={{ color: '#388e3c' }}>
                                  <RefreshIcon />
                                </IconButton>
                              </Tooltip>
                            </Box>
                          </TableCell>
                        </motion.tr>
                      ))}
                    </React.Fragment>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>

            {/* Pagination */}
            <Box sx={{ 
              backgroundColor: '#f8f9fa', 
              p: 2, 
              borderTop: '1px solid #e0e0e0',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <Typography variant="body2" color="textSecondary">
                Mostrando desde 1 hasta {hubs.reduce((total, hub) => total + (hub.olts?.length || 0), 0)} de {hubs.reduce((total, hub) => total + (hub.olts?.length || 0), 0)} entradas
              </Typography>
              <Box sx={{ display: 'flex', gap: 1 }}>
                <Button size="small" disabled>Anterior</Button>
                <Button size="small" variant="contained" sx={{ backgroundColor: '#3b82f6' }}>1</Button>
                <Button size="small" disabled>Siguiente</Button>
              </Box>
            </Box>
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default DevicesView;