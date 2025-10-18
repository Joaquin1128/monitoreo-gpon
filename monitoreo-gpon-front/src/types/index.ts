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

export interface Hub {
  id: number;
  name: string;
  latitude?: number;
  longitude?: number;
  createdAt?: string;
  updatedAt?: string;
  olts?: Olt[];
}

export interface Olt {
  id: number;
  hub?: HubRef;
  vendor?: Vendor;
  deviceType?: DeviceType;
  name: string;
  ipAddress?: string;
  serialNumber?: string;
  model?: string;
  cantPorts?: number;
  snmpVersion?: string;
  snmpCommunity?: string;
  snmpPort?: number;
  snmpTimeoutMs?: number;
  softVersion?: string;
  commandProtectionPassword?: string;
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
