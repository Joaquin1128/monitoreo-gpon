import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Alert,
  Button,
  Avatar,
  IconButton
} from '@mui/material';
import {
  Router as RouterIcon,
  Refresh as RefreshIcon,
  ArrowBack as ArrowBackIcon,
  Thermostat as ThermostatIcon,
  CheckCircle as CheckCircleIcon,
  Warning as WarningIcon,
  Error as ErrorIcon,
  Memory as MemoryIcon,
  Speed as SpeedIcon,
  Schedule as ScheduleIcon
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { oltSnmpService } from '../services';
import { OltDetailedResponse } from '../types';

const OltDashboard: React.FC = () => {
  const { oltId } = useParams<{ oltId: string }>();
  const [oltDetailed, setOltDetailed] = useState<OltDetailedResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (oltId) {
      loadOltDetailed();
    }
  }, [oltId]);

  // Load detailed data for the specific OLT (calls /api/snmp/olts/{id}/detailed)
  // This endpoint returns comprehensive information: temperature, CPU, memory, 
  // interface status, and other detailed metrics
  const loadOltDetailed = async () => {
    if (!oltId) {
      setError('ID de dispositivo no válido');
      setLoading(false);
      return;
    }
    
    try {
      setLoading(true);
      setError(null);
      const detailedData = await oltSnmpService.getDetailed(parseInt(oltId));
      setOltDetailed(detailedData);
    } catch (err: any) {
      console.error('Error loading OLT detailed data:', err);
      const errorMessage = err?.response?.data?.message || err?.message || 'Error al cargar los datos del dispositivo';
      setError(errorMessage);
      setOltDetailed(null);
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'online':
        return <CheckCircleIcon sx={{ color: '#4caf50' }} />;
      case 'warning':
        return <WarningIcon sx={{ color: '#ff9800' }} />;
      case 'offline':
        return <ErrorIcon sx={{ color: '#f44336' }} />;
      default:
        return <WarningIcon sx={{ color: '#ff9800' }} />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'online':
        return 'success';
      case 'warning':
        return 'warning';
      case 'offline':
        return 'error';
      default:
        return 'warning';
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
        <CircularProgress size={60} />
        <Typography variant="h6" sx={{ ml: 2 }}>
          Cargando datos del dispositivo...
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
            <Button color="inherit" size="small" onClick={loadOltDetailed}>
              Reintentar
            </Button>
          }
        >
          {error}
        </Alert>
      </Box>
    );
  }

  if (!oltDetailed) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="warning">
          No se encontraron datos del dispositivo
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3, backgroundColor: '#f9fafb', minHeight: '100vh' }}>
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <IconButton 
          onClick={() => navigate('/devices')}
          sx={{ mr: 2 }}
        >
          <ArrowBackIcon />
        </IconButton>
        
        <Avatar sx={{ 
          backgroundColor: '#ffb74d', 
          mr: 2,
          width: 48,
          height: 48
        }}>
          <RouterIcon />
        </Avatar>
        
        <Box sx={{ flex: 1 }}>
          <Typography variant="h4" component="h1" sx={{ fontWeight: 'bold', color: '#111827', mb: 1 }}>
            {oltDetailed.name}
          </Typography>
          <Typography variant="body1" color="textSecondary">
            {oltDetailed.vendor} • {oltDetailed.model} • {oltDetailed.ipAddress}
          </Typography>
        </Box>
        
        <Button
          variant="outlined"
          startIcon={<RefreshIcon />}
          onClick={loadOltDetailed}
          disabled={loading}
          sx={{ borderRadius: 2 }}
        >
          Actualizar
        </Button>
      </Box>

      {/* Status Overview */}
      <Box sx={{ 
        display: 'flex', 
        flexWrap: 'wrap', 
        gap: 3, 
        mb: 4 
      }}>
        <Box sx={{ 
          flex: { xs: '1 1 100%', md: '1 1 calc(25% - 18px)' },
          minWidth: { xs: '100%', md: '200px' }
        }}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
          >
            <Card sx={{ borderRadius: 3 }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
                  {getStatusIcon(oltDetailed.overallStatus)}
                </Box>
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                  Estado General
                </Typography>
                <Chip
                  label={oltDetailed.overallStatus === 'online' ? 'Activo' : 
                         oltDetailed.overallStatus === 'warning' ? 'Advertencia' : 'Inactivo'}
                  color={getStatusColor(oltDetailed.overallStatus) as any}
                  size="small"
                />
              </CardContent>
            </Card>
          </motion.div>
        </Box>

        <Box sx={{ 
          flex: { xs: '1 1 100%', md: '1 1 calc(25% - 18px)' },
          minWidth: { xs: '100%', md: '200px' }
        }}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.1 }}
          >
            <Card sx={{ borderRadius: 3 }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <ScheduleIcon sx={{ fontSize: 40, color: '#1976d2', mb: 2 }} />
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                  Tiempo de Actividad
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  {oltDetailed.uptime || 'N/A'}
                </Typography>
              </CardContent>
            </Card>
          </motion.div>
        </Box>

        <Box sx={{ 
          flex: { xs: '1 1 100%', md: '1 1 calc(25% - 18px)' },
          minWidth: { xs: '100%', md: '200px' }
        }}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.2 }}
          >
            <Card sx={{ borderRadius: 3 }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <ThermostatIcon sx={{ fontSize: 40, color: '#f59e0b', mb: 2 }} />
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                  Temperatura
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  {oltDetailed.temperature || 'N/A'}
                </Typography>
              </CardContent>
            </Card>
          </motion.div>
        </Box>

        <Box sx={{ 
          flex: { xs: '1 1 100%', md: '1 1 calc(25% - 18px)' },
          minWidth: { xs: '100%', md: '200px' }
        }}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.3 }}
          >
            <Card sx={{ borderRadius: 3 }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <SpeedIcon sx={{ fontSize: 40, color: '#10b981', mb: 2 }} />
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                  CPU Usage
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  {oltDetailed.cpuUsage || 'N/A'}
                </Typography>
              </CardContent>
            </Card>
          </motion.div>
        </Box>
      </Box>

      {/* Detailed Metrics */}
      <Box sx={{ 
        display: 'flex', 
        flexWrap: 'wrap', 
        gap: 3 
      }}>
        <Box sx={{ 
          flex: { xs: '1 1 100%', md: '1 1 calc(50% - 12px)' },
          minWidth: { xs: '100%', md: '300px' }
        }}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.4 }}
          >
            <Card sx={{ borderRadius: 3 }}>
              <CardContent>
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 3 }}>
                  Métricas del Sistema
                </Typography>
                
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <MemoryIcon sx={{ mr: 2, color: '#6b7280' }} />
                  <Box sx={{ flex: 1 }}>
                    <Typography variant="body2" color="textSecondary">
                      Uso de Memoria
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
                      {oltDetailed.memoryUsage || 'N/A'}
                    </Typography>
                  </Box>
                </Box>

                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <RouterIcon sx={{ mr: 2, color: '#6b7280' }} />
                  <Box sx={{ flex: 1 }}>
                    <Typography variant="body2" color="textSecondary">
                      Estado de Interfaces
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
                      {oltDetailed.interfaceStatus || 'N/A'}
                    </Typography>
                  </Box>
                </Box>

                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <ScheduleIcon sx={{ mr: 2, color: '#6b7280' }} />
                  <Box sx={{ flex: 1 }}>
                    <Typography variant="body2" color="textSecondary">
                      Última Actualización
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
                      {new Date(oltDetailed.lastUpdate).toLocaleString()}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </motion.div>
        </Box>

        <Box sx={{ 
          flex: { xs: '1 1 100%', md: '1 1 calc(50% - 12px)' },
          minWidth: { xs: '100%', md: '300px' }
        }}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.5 }}
          >
            <Card sx={{ borderRadius: 3 }}>
              <CardContent>
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 3 }}>
                  Estado SNMP
                </Typography>
                
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Box sx={{ flex: 1 }}>
                    <Typography variant="body2" color="textSecondary">
                      Estado de Conexión SNMP
                    </Typography>
                    <Chip
                      label={oltDetailed.snmpStatus === 'success' ? 'Conectado' : 
                             oltDetailed.snmpStatus === 'error' ? 'Error' : 'Timeout'}
                      color={oltDetailed.snmpStatus === 'success' ? 'success' : 
                             oltDetailed.snmpStatus === 'error' ? 'error' : 'warning'}
                      size="small"
                      sx={{ mt: 1 }}
                    />
                  </Box>
                </Box>

                {oltDetailed.errorMessage && (
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <ErrorIcon sx={{ mr: 2, color: '#f44336' }} />
                    <Box sx={{ flex: 1 }}>
                      <Typography variant="body2" color="textSecondary">
                        Mensaje de Error
                      </Typography>
                      <Typography variant="body2" sx={{ color: '#f44336' }}>
                        {oltDetailed.errorMessage}
                      </Typography>
                    </Box>
                  </Box>
                )}

              </CardContent>
            </Card>
          </motion.div>
        </Box>
      </Box>
    </Box>
  );
};

export default OltDashboard;