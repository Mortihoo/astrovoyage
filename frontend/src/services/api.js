import axios from 'axios';
import authService from './authService';
import config from '../config/config';

const axiosInstance = axios.create({
  baseURL: config.API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Add request interceptor to include auth token
axiosInstance.interceptors.request.use(
  config => {
    const token = authService.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

const api = {
    generateBirthChart: async (birthData) => {
        try {
            const response = await axiosInstance.post('/api/birth-chart', {
                name: birthData.name,
                birthDateTime: birthData.birthDateTime,
                location: birthData.location,
                timezone: birthData.timezone
            });
            return response.data;
        } catch (error) {
            throw error.response?.data || 'An error occurred while generating the birth chart';
        }
    }
};

export default api; 