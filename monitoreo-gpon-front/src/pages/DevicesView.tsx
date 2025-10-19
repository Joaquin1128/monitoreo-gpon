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
  Router as RouterIcon,
  Visibility as VisibilityIcon,
  Refresh as RefreshIcon,
  NetworkCheck as NetworkIcon,
  Add as AddIcon,
  FilterList as FilterIcon,
  MoreVert as MoreVertIcon
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { oltSnmpService } from '../services';
import { OltSummaryResponse } from '../types';

const DevicesView: React.FC = () => {
  const [oltsSummary, setOltsSummary] = useState<OltSummaryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    loadOltsSummary();
  }, []);

  const loadOltsSummary = async () => {
    try {
      setLoading(true);
      const summaryData = await oltSnmpService.getAllSummary();
      setOltsSummary(summaryData);
      setError(null);
    } catch (err) {
      console.error('Error loading OLTs summary:', err);
      setError('Error al cargar los dispositivos');
    } finally {
      setLoading(false);
    }
  };

  const handleViewOlt = (olt: OltSummaryResponse) => {
    // Verificar que olt.id sea válido
    if (!olt.id) {
      console.error('DevicesView - OLT ID inválido:', { 
        oltId: olt.id, 
        oltIdType: typeof olt.id
      });
      return;
    }
    
    // Navegar directamente al OLT
    navigate(`/devices/olt/${olt.id}`);
  };

  const getStatusColor = (olt: OltSummaryResponse) => {
    if (olt.snmpStatus === 'error') return 'error';
    if (olt.snmpStatus === 'timeout') return 'warning';
    return olt.overallStatus === 'online' ? 'success' : 
           olt.overallStatus === 'warning' ? 'warning' : 'error';
  };

  const getStatusText = (olt: OltSummaryResponse) => {
    if (olt.snmpStatus === 'error') return 'Error SNMP';
    if (olt.snmpStatus === 'timeout') return 'Timeout SNMP';
    return olt.overallStatus === 'online' ? 'Activo' : 
           olt.overallStatus === 'warning' ? 'Advertencia' : 'Inactivo';
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
            <Button color="inherit" size="small" onClick={loadOltsSummary}>
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
            onClick={loadOltsSummary}
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
              <Typography variant="h3" sx={{ fontWeight: 'bold', color: '#1976d2' }}>
                {oltsSummary.length}
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
          transition={{ duration: 0.5, delay: 0.1 }}
        >
          <Card sx={{ flex: 1, borderRadius: 3 }}>
            <CardContent sx={{ textAlign: 'center' }}>
              <Typography variant="h3" sx={{ fontWeight: 'bold', color: '#388e3c' }}>
                {oltsSummary.filter(olt => olt.overallStatus === 'online').length}
              </Typography>
              <Typography variant="body1" color="textSecondary">
                OLTs Activas
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
              <Typography variant="h3" sx={{ fontWeight: 'bold', color: '#f59e0b' }}>
                {oltsSummary.filter(olt => olt.snmpStatus === 'error').length}
              </Typography>
              <Typography variant="body1" color="textSecondary">
                Con Errores SNMP
              </Typography>
            </CardContent>
          </Card>
        </motion.div>
      </Box>

      {/* Devices Table */}
      {oltsSummary.length === 0 ? (
        <Paper sx={{ p: 6, textAlign: 'center', borderRadius: 3 }}>
          <RouterIcon sx={{ fontSize: 80, color: '#ccc', mb: 2 }} />
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
                Dispositivos ({oltsSummary.length})
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
                    <TableCell sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>NOMBRE</TableCell>
                    <TableCell sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>ESTADO</TableCell>
                    <TableCell sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>IP</TableCell>
                    <TableCell sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>MODELO</TableCell>
                    <TableCell sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>TIEMPO DE ACTIVIDAD</TableCell>
                    <TableCell sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>ACCIÓN</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {oltsSummary.map((olt, oltIndex) => (
                    <motion.tr
                      key={olt.id}
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      transition={{ duration: 0.3, delay: oltIndex * 0.05 }}
                      style={{ display: 'table-row' }}
                    >
                      <TableCell>
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                          <Avatar sx={{ 
                            backgroundColor: '#ffb74d', 
                            mr: 2,
                            width: 40,
                            height: 40
                          }}>
                            <RouterIcon />
                          </Avatar>
                          <Box>
                            <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
                              {olt.name}
                            </Typography>
                            <Typography variant="body2" color="textSecondary">
                              {olt.vendor} • {olt.model || 'N/A'}
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
                          {olt.uptime || 'N/A'}
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
                Mostrando desde 1 hasta {oltsSummary.length} de {oltsSummary.length} entradas
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