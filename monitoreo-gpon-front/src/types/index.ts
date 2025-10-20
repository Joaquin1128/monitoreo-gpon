// Types based on backend entities
export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
}

export interface Vendor {
  id: number;
  name: string;
  description: string;
}

export interface DeviceType {
  id: number;
  name: string;
  description: string;
}

export interface HubRef {
  id: number;
  name: string;
}

export interface OltRef {
  id: number;
  name: string;
  ipAddress: string;
}

export interface Olt {
  id: number;
  name: string;
  ipAddress: string;
  model?: string;
  vendor?: Vendor;
  deviceType?: DeviceType;
  serialNumber?: string;
  cantPorts?: number;
  snmpVersion?: string;
  snmpCommunity?: string;
  snmpPort?: number;
  snmpTimeoutMs?: number;
  softVersion?: string;
  commandProtectionPassword?: string;
}

export interface Hub {
  id: number;
  name: string;
  latitude?: number;
  longitude?: number;
  createdAt?: string;
  updatedAt?: string;
  olts?: Olt[];
}

export interface OltSummaryResponse {
  id: number;
  name: string;
  ipAddress?: string;
  model?: string;
  vendor?: string;
  deviceType?: string;
  overallStatus: 'online' | 'offline' | 'warning';
  uptime?: string;
  lastUpdate: string;
  snmpStatus: 'success' | 'error' | 'timeout';
  errorMessage?: string;
}

export interface OltDetailedResponse {
  id: number;
  name: string;
  ipAddress?: string;
  model?: string;
  vendor?: string;
  deviceType?: string;
  overallStatus: 'online' | 'offline' | 'warning';
  uptime?: string;
  temperature?: string;
  cpuUsage?: string;
  memoryUsage?: string;
  interfaceStatus?: string;
  ports?: PortStatus[];
  lastUpdate: string;
  snmpStatus: 'success' | 'error' | 'timeout';
  errorMessage?: string;
}

export interface PortStatus {
  portNumber: number;
  status: string;
  speed?: string;
  description?: string;
  adminStatus?: string;
  operStatus?: string;
}

export interface Ont {
  id: number;
  olt?: OltRef;
  vendor?: Vendor;
  deviceType?: DeviceType;
  idCliente?: string;
  macAddr?: string;
  snOnt?: string;
  model?: string;
  tecnologia?: string;
  softVersion?: string;
  status?: string;
  boxName?: string;
  ipAddress?: string;
  codOlt?: string;
  fechaAct?: string;
}

export interface ProbeRequest {
  metricKeys?: string[];
  context?: Record<string, any>;
}

export interface ProbeResult {
  metricKey: string;
  raw?: string;
  parsedValue?: string;
  valueType?: string;
  source: string;
}

// UI Types
export interface MenuItem {
  id: string;
  label: string;
  icon: React.ReactNode;
  path?: string;
  children?: MenuItem[];
}

export interface DeviceStatus {
  status: 'active' | 'inactive' | 'warning' | 'error';
  uptime?: string;
  lastSeen?: string;
}

export interface MetricCard {
  title: string;
  value: string | number;
  unit?: string;
  status: 'good' | 'warning' | 'error';
  trend?: 'up' | 'down' | 'stable';
}
