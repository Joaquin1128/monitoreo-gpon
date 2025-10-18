import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  CircularProgress,
  Alert,
  Button,
  LinearProgress,
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
  Search as SearchIcon,
  Schedule as ScheduleIcon
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { oltService, hubService, probeService } from '../services';
import { Olt, Hub, ProbeResult } from '../types';

const OltDashboard: React.FC = () => {
  const { hubId, oltId } = useParams<{ hubId?: string; oltId: string }>();
  const [olt, setOlt] = useState<Olt | null>(null);
  const [hub, setHub] = useState<Hub | null>(null);
  const [probeData, setProbeData] = useState<ProbeResult[]>([]);
  const [loading, setLoading] = useState(true);
  const [probeLoading, setProbeLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (oltId) {
      loadOltData();
      startProbing();
    }

    return () => {
      if (oltId) {
        probeService.stopProbing(Number(oltId));
      }
    };
  }, [oltId]);

  const loadOltData = async () => {
    try {
      setLoading(true);
      setError(null);

      if (!oltId || isNaN(Number(oltId))) {
        throw new Error('ID de OLT inválido');
      }

      const promises = [oltService.getById(Number(oltId))];
      
      if (hubId && !isNaN(Number(hubId))) {
        promises.push(hubService.getById(Number(hubId)));
      } else {
        promises.push(Promise.resolve(null));
      }

      const [oltData, hubData] = await Promise.all(promises);

      setOlt(oltData);
      setHub(hubData);
    } catch (err) {
      console.error('Error loading OLT data:', err);
      setError('Error al cargar los datos de la OLT');
    } finally {
      setLoading(false);
    }
  };

  const startProbing = () => {
    if (oltId && !isNaN(Number(oltId))) {
      probeService.startProbing(Number(oltId), 10000);
      
      const unsubscribe = probeService.subscribe(Number(oltId), (data) => {
        setProbeData(data);
        setProbeLoading(false);
      });

      setProbeLoading(true);
      oltService.probe(Number(oltId))
        .then((data) => {
          setProbeData(data);
          setProbeLoading(false);
        })
        .catch((err) => {
          console.error('Error in initial probe:', err);
          setProbeLoading(false);
        });

      return unsubscribe;
    }
  };

  const handleManualProbe = async () => {
    if (!oltId || isNaN(Number(oltId))) return;
    
    try {
      setProbeLoading(true);
      const data = await oltService.probe(Number(oltId));
      setProbeData(data);
    } catch (err) {
      console.error('Error in manual probe:', err);
    } finally {
      setProbeLoading(false);
    }
  };

  const getMetricStatus = (metricKey: string): 'success' | 'warning' | 'error' => {
    const metric = probeData.find(m => m.metricKey === metricKey);
    if (!metric) return 'warning';
    
    const value = metric.parsedValue || metric.raw;
    if (!value || value === 'N/A') return 'warning';
    
    if (metricKey.includes('power') && value) {
      const numValue = parseFloat(value);
      if (numValue < -30 || numValue > 0) return 'error';
      if (numValue < -25) return 'warning';
    }
    
    return 'success';
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
        <CircularProgress size={60} />
        <Typography variant="h6" sx={{ ml: 2 }}>
          Cargando datos de la OLT...
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
            <Button color="inherit" size="small" onClick={loadOltData}>
              Reintentar
            </Button>
          }
        >
          {error}
        </Alert>
      </Box>
    );
  }

  if (!olt) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="warning">
          OLT no encontrada
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ backgroundColor: '#f5f5f5', minHeight: '100vh' }}>
      {/* Header */}
      <Box sx={{ 
        backgroundColor: '#ffffff', 
        p: 3, 
        borderBottom: '1px solid #e0e0e0',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <IconButton 
            onClick={() => navigate('/devices')}
            sx={{ mr: 2, color: '#666' }}
          >
            <ArrowBackIcon />
          </IconButton>
          <Avatar sx={{ backgroundColor: '#ffb74d', mr: 2 }}>
            <RouterIcon />
          </Avatar>
          <Box>
            <Typography variant="h4" component="h1" sx={{ fontWeight: 'bold', color: '#1e1e1e' }}>
              {olt.name}
            </Typography>
            <Typography variant="body1" color="textSecondary">
              {hub && `Hub: ${hub.name}`} • IP: {olt.ipAddress || 'N/A'}
            </Typography>
          </Box>
        </Box>
        
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
          <Box sx={{ display: 'flex', gap: 1 }}>
            <IconButton size="small">
              <SearchIcon />
            </IconButton>
            <IconButton size="small">
              <ScheduleIcon />
            </IconButton>
            <Button
              variant="outlined"
              startIcon={<RefreshIcon />}
              onClick={handleManualProbe}
              disabled={probeLoading}
              size="small"
            >
              {probeLoading ? 'Probando...' : 'Actualizar'}
            </Button>
          </Box>
          <Chip
            label="30s"
            size="small"
            variant="outlined"
          />
        </Box>
      </Box>

      {/* Status Indicator */}
      {probeLoading && (
        <Box sx={{ p: 2, backgroundColor: '#fff3e0' }}>
          <LinearProgress />
          <Typography variant="body2" color="textSecondary" sx={{ mt: 1 }}>
            Ejecutando probe SNMP...
          </Typography>
        </Box>
      )}

      <Box sx={{ p: 3 }}>
        {/* OLT Overview Section */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, color: '#1e1e1e' }}>
            OLT Overview
          </Typography>
          
          <Box sx={{ 
            display: 'grid', 
            gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)', md: 'repeat(4, 1fr)' },
            gap: 3,
            mb: 4
          }}>
            {/* Uptime Card */}
            <Card sx={{ borderRadius: 3, height: '100%' }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                  1 día, 23 horas
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  Tiempo de actividad
                </Typography>
              </CardContent>
            </Card>

            {/* ONU Count Cards */}
            <Card sx={{ borderRadius: 3, height: '100%' }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#1976d2', mb: 1 }}>
                  6
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  Total ONUs
                </Typography>
              </CardContent>
            </Card>

            <Card sx={{ borderRadius: 3, height: '100%' }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#388e3c', mb: 1 }}>
                  5
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  ONU Activa
                </Typography>
              </CardContent>
            </Card>

            <Card sx={{ borderRadius: 3, height: '100%', backgroundColor: '#fff3e0' }}>
              <CardContent sx={{ textAlign: 'center' }}>
                <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#f57c00', mb: 1 }}>
                  1
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  ONU Inactiva
                </Typography>
              </CardContent>
            </Card>
          </Box>

          {/* Performance Gauges */}
          <Box sx={{ 
            display: 'grid', 
            gridTemplateColumns: { xs: '1fr', md: 'repeat(2, 1fr)' },
            gap: 3,
            mb: 4
          }}>
            <Card sx={{ borderRadius: 3 }}>
              <CardContent>
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                  CPU Load
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Box sx={{ position: 'relative', display: 'inline-flex' }}>
                    <CircularProgress
                      variant="determinate"
                      value={2}
                      size={120}
                      thickness={8}
                      sx={{
                        color: '#4caf50',
                        '& .MuiCircularProgress-circle': {
                          strokeLinecap: 'round',
                        }
                      }}
                    />
                    <Box
                      sx={{
                        top: 0,
                        left: 0,
                        bottom: 0,
                        right: 0,
                        position: 'absolute',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      <Typography variant="h4" component="div" sx={{ fontWeight: 'bold' }}>
                        2%
                      </Typography>
                    </Box>
                  </Box>
                </Box>
              </CardContent>
            </Card>

            <Card sx={{ borderRadius: 3 }}>
              <CardContent>
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                  Memory Usage
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Box sx={{ position: 'relative', display: 'inline-flex' }}>
                    <CircularProgress
                      variant="determinate"
                      value={65}
                      size={120}
                      thickness={8}
                      sx={{
                        color: '#ff9800',
                        '& .MuiCircularProgress-circle': {
                          strokeLinecap: 'round',
                        }
                      }}
                    />
                    <Box
                      sx={{
                        top: 0,
                        left: 0,
                        bottom: 0,
                        right: 0,
                        position: 'absolute',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      <Typography variant="h4" component="div" sx={{ fontWeight: 'bold' }}>
                        65%
                      </Typography>
                    </Box>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Box>

          {/* Sensor Card */}
          <Card sx={{ borderRadius: 3, mb: 4, backgroundColor: '#fff3e0' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <ThermostatIcon sx={{ fontSize: 40, color: '#f57c00', mr: 2 }} />
                <Box>
                  <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                    Sensor 4
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#f57c00' }}>
                    40 °C
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </motion.div>

        {/* Interface Overview */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
        >
          <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, color: '#1e1e1e' }}>
            GPON Interface Overview
          </Typography>
          
          <Box sx={{ 
            display: 'grid', 
            gridTemplateColumns: { xs: 'repeat(2, 1fr)', sm: 'repeat(4, 1fr)', md: 'repeat(8, 1fr)' },
            gap: 2,
            mb: 4
          }}>
            {Array.from({ length: 16 }, (_, i) => {
              const portNum = i + 1;
              const isUp = portNum === 1;
              const isDown = portNum >= 2 && portNum <= 4;
              
              return (
                <Card 
                  key={portNum}
                  sx={{ 
                    borderRadius: 2,
                    backgroundColor: isUp ? '#e8f5e8' : isDown ? '#ffebee' : '#f5f5f5',
                    border: isUp ? '2px solid #4caf50' : isDown ? '2px solid #f44336' : '1px solid #e0e0e0'
                  }}
                >
                  <CardContent sx={{ p: 2, textAlign: 'center' }}>
                    <Typography variant="body2" sx={{ fontWeight: 'bold', mb: 1 }}>
                      Gpon 1/{portNum}
                    </Typography>
                    <Chip
                      label={isUp ? 'UP' : isDown ? 'DOWN' : 'DISABLED'}
                      size="small"
                      color={isUp ? 'success' : isDown ? 'error' : 'default'}
                      sx={{ fontSize: '0.7rem' }}
                    />
                  </CardContent>
                </Card>
              );
            })}
          </Box>
        </motion.div>

        {/* SNMP Metrics */}
        {probeData.length > 0 && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.3 }}
          >
            <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, color: '#1e1e1e' }}>
              Métricas SNMP
            </Typography>
            
            <Card sx={{ borderRadius: 3 }}>
              <CardContent>
                <TableContainer>
                  <Table>
                    <TableHead>
                      <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
                        <TableCell sx={{ fontWeight: 'bold' }}>Métrica</TableCell>
                        <TableCell sx={{ fontWeight: 'bold' }}>Valor</TableCell>
                        <TableCell sx={{ fontWeight: 'bold' }}>Tipo</TableCell>
                        <TableCell sx={{ fontWeight: 'bold' }}>Estado</TableCell>
                        <TableCell sx={{ fontWeight: 'bold' }}>Fuente</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {probeData.map((metric, index) => (
                        <motion.tr
                          key={metric.metricKey}
                          initial={{ opacity: 0 }}
                          animate={{ opacity: 1 }}
                          transition={{ duration: 0.3, delay: index * 0.05 }}
                          style={{ display: 'table-row' }}
                        >
                          <TableCell>
                            <Typography variant="body2" sx={{ fontWeight: 'medium' }}>
                              {metric.metricKey}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2" sx={{ fontFamily: 'monospace' }}>
                              {metric.parsedValue || metric.raw || 'N/A'}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2">
                              {metric.valueType || 'N/A'}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Chip
                              label={getMetricStatus(metric.metricKey) === 'success' ? 'OK' : 
                                     getMetricStatus(metric.metricKey) === 'warning' ? 'Warning' : 'Error'}
                              color={getMetricStatus(metric.metricKey) as any}
                              size="small"
                              icon={getMetricStatus(metric.metricKey) === 'success' ? 
                                    <CheckCircleIcon /> : 
                                    getMetricStatus(metric.metricKey) === 'warning' ? 
                                    <WarningIcon /> : <ErrorIcon />}
                            />
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2">
                              {metric.source}
                            </Typography>
                          </TableCell>
                        </motion.tr>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </CardContent>
            </Card>
          </motion.div>
        )}
      </Box>
    </Box>
  );
};

export default OltDashboard;