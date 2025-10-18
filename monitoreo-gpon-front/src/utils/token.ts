// Token management utilities
const TOKEN_KEY = 'mgpon_auth_token';

export const tokenUtils = {
  setToken: (token: string): void => {
    try {
      localStorage.setItem(TOKEN_KEY, token);
    } catch (error) {
      console.error('Error saving token:', error);
    }
  },

  getToken: (): string | null => {
    try {
      return localStorage.getItem(TOKEN_KEY);
    } catch (error) {
      console.error('Error getting token:', error);
      return null;
    }
  },

  clearToken: (): void => {
    try {
      localStorage.removeItem(TOKEN_KEY);
    } catch (error) {
      console.error('Error clearing token:', error);
    }
  },

  isAuthenticated: (): boolean => {
    return !!tokenUtils.getToken();
  }
};
