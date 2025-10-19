import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { tokenUtils } from '../utils/token';
import {
  AuthRequest,
  AuthResponse,
  Hub,
  Olt,
  Ont,
  ProbeRequest,
  ProbeResult,
  OltSummaryResponse,
  OltDetailedResponse
} from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
      timeout: 30000,
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      (config) => {
        const token = tokenUtils.getToken();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor to handle auth errors
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          tokenUtils.clearToken();
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // Auth endpoints
  async login(credentials: AuthRequest): Promise<AuthResponse> {
    const response: AxiosResponse<AuthResponse> = await this.client.post('/api/auth/login', credentials);
    return response.data;
  }

  // Hub endpoints
  async getHubs(): Promise<Hub[]> {
    const response: AxiosResponse<Hub[]> = await this.client.get('/api/hubs');
    return response.data;
  }

  async getHub(hubId: number): Promise<Hub> {
    const response: AxiosResponse<Hub> = await this.client.get(`/api/hubs/${hubId}`);
    return response.data;
  }

  async getHubOlts(hubId: number): Promise<Olt[]> {
    const response: AxiosResponse<Olt[]> = await this.client.get(`/api/hubs/${hubId}/olts`);
    return response.data;
  }

  // OLT endpoints
  async getOlts(): Promise<Olt[]> {
    const response: AxiosResponse<Olt[]> = await this.client.get('/api/olts');
    return response.data;
  }

  async getOlt(oltId: number): Promise<Olt> {
    const response: AxiosResponse<Olt> = await this.client.get(`/api/olts/${oltId}`);
    return response.data;
  }

  async getOltsByHub(hubId: number): Promise<Olt[]> {
    const response: AxiosResponse<Olt[]> = await this.client.get(`/api/olts/hub/${hubId}`);
    return response.data;
  }

  // ONT endpoints
  async getOnts(): Promise<Ont[]> {
    const response: AxiosResponse<Ont[]> = await this.client.get('/api/onts');
    return response.data;
  }

  async getOnt(ontId: number): Promise<Ont> {
    const response: AxiosResponse<Ont> = await this.client.get(`/api/onts/${ontId}`);
    return response.data;
  }

  async getOntsByOlt(oltId: number): Promise<Ont[]> {
    const response: AxiosResponse<Ont[]> = await this.client.get(`/api/onts/olt/${oltId}`);
    return response.data;
  }

  // SNMP Status endpoints
  async getOltsSummary(): Promise<OltSummaryResponse[]> {
    const response: AxiosResponse<OltSummaryResponse[]> = await this.client.get('/api/snmp/olts/summary');
    return response.data;
  }

  async getOltDetailed(oltId: number): Promise<OltDetailedResponse> {
    const response: AxiosResponse<OltDetailedResponse> = await this.client.get(`/api/snmp/olts/${oltId}/detailed`);
    return response.data;
  }

  // SNMP Probe endpoints
  async probeOlt(oltId: number, request?: ProbeRequest): Promise<ProbeResult[]> {
    const response: AxiosResponse<ProbeResult[]> = await this.client.post(
      `/api/snmp/olts/${oltId}/probe`,
      request || {}
    );
    return response.data;
  }

  async probeOnt(ontId: number, request?: ProbeRequest): Promise<ProbeResult[]> {
    const response: AxiosResponse<ProbeResult[]> = await this.client.post(
      `/api/snmp/onts/${ontId}/probe`,
      request || {}
    );
    return response.data;
  }
}

// Export singleton instance
export const apiClient = new ApiClient();
export default apiClient;