import apiClient from './api';
import { Hub, Olt, Ont, ProbeRequest, ProbeResult, OltSummaryResponse, OltDetailedResponse } from '../types';

// Hub Service
export const hubService = {
  async getAll(): Promise<Hub[]> {
    return apiClient.getHubs();
  },

  async getById(id: number): Promise<Hub> {
    return apiClient.getHub(id);
  },

  async getOlts(hubId: number): Promise<Olt[]> {
    return apiClient.getHubOlts(hubId);
  }
};

// OLT Service
export const oltService = {
  async getAll(): Promise<Olt[]> {
    return apiClient.getOlts();
  },

  async getById(id: number): Promise<Olt> {
    return apiClient.getOlt(id);
  },

  async getByHub(hubId: number): Promise<Olt[]> {
    return apiClient.getOltsByHub(hubId);
  },

  async probe(id: number, request?: ProbeRequest): Promise<ProbeResult[]> {
    return apiClient.probeOlt(id, request);
  }
};

// OLT SNMP Service
export const oltSnmpService = {
  async getAllSummary(): Promise<OltSummaryResponse[]> {
    return apiClient.getOltsSummary();
  },

  async getDetailed(id: number): Promise<OltDetailedResponse> {
    return apiClient.getOltDetailed(id);
  }
};

// ONT Service
export const ontService = {
  async getAll(): Promise<Ont[]> {
    return apiClient.getOnts();
  },

  async getById(id: number): Promise<Ont> {
    return apiClient.getOnt(id);
  },

  async getByOlt(oltId: number): Promise<Ont[]> {
    return apiClient.getOntsByOlt(oltId);
  },

  async probe(id: number, request?: ProbeRequest): Promise<ProbeResult[]> {
    return apiClient.probeOnt(id, request);
  }
};

// Auth Service
export const authService = {
  async login(username: string, password: string) {
    return apiClient.login({ username, password });
  }
};

// Probe Service for real-time monitoring
export class ProbeService {
  private intervals: Map<number, NodeJS.Timeout> = new Map();
  private listeners: Map<number, Set<(data: ProbeResult[]) => void>> = new Map();

  startProbing(oltId: number, intervalMs: number = 10000): void {
    if (this.intervals.has(oltId)) {
      this.stopProbing(oltId);
    }

    const interval = setInterval(async () => {
      try {
        const data = await oltService.probe(oltId);
        const listeners = this.listeners.get(oltId);
        if (listeners) {
          listeners.forEach(listener => listener(data));
        }
      } catch (error) {
        console.error(`Error probing OLT ${oltId}:`, error);
      }
    }, intervalMs);

    this.intervals.set(oltId, interval);
  }

  stopProbing(oltId: number): void {
    const interval = this.intervals.get(oltId);
    if (interval) {
      clearInterval(interval);
      this.intervals.delete(oltId);
    }
  }

  subscribe(oltId: number, listener: (data: ProbeResult[]) => void): () => void {
    if (!this.listeners.has(oltId)) {
      this.listeners.set(oltId, new Set());
    }
    
    const listeners = this.listeners.get(oltId)!;
    listeners.add(listener);

    return () => {
      listeners.delete(listener);
      if (listeners.size === 0) {
        this.listeners.delete(oltId);
        this.stopProbing(oltId);
      }
    };
  }

  stopAll(): void {
    this.intervals.forEach((interval) => clearInterval(interval));
    this.intervals.clear();
    this.listeners.clear();
  }
}

export const probeService = new ProbeService();
