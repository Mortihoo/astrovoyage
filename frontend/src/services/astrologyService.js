import axios from 'axios';
import authService from './authService';
import config from '../config/config';

export const axiosInstance = axios.create({
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
      console.log('Adding token to request:', token);
    } else {
      console.warn('No token found for request');
    }
    return config;
  },
  error => {
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// Add response interceptor to handle 401 errors
axiosInstance.interceptors.response.use(
  response => response,
  error => {
    console.error('Response error:', error.response?.data || error.message);
    if (error.response?.status === 401) {
      authService.logout();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

/**
 * Generate an astrological chart based on birth data
 * @param {Object} birthData - The birth data
 * @param {Date} birthData.birthDate - The birth date
 * @param {Date} birthData.birthTime - The birth time
 * @param {string} birthData.latitude - The birth latitude
 * @param {string} birthData.longitude - The birth longitude
 * @param {string} birthData.name - The person's name
 * @param {string} birthData.location - The birth location
 * @returns {Promise<Object>} - The chart data
 */
export const generateChart = async (chartData) => {
  try {
    console.log('Generating chart with data:', chartData);
    const response = await axiosInstance.post('/birth-charts', {
      name: chartData.name,
      birthDateTime: chartData.birthDateTime,  // Already in ISO format
      location: chartData.location,  // Already formatted as "city, country"
      timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
      gender: chartData.gender  // Add gender field
    });
    console.log('Chart generation response:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error generating chart:', error.response?.data || error.message);
    throw error;
  }
};

/**
 * Get sample chart data for testing
 * @returns {Object} - Sample chart data
 */
export const getSampleChartData = () => {
  return {
    planets: [
      { name: "Sun", symbol: "☉", position: 30, degree: 24, minute: 52 },     // In Aries
      { name: "Moon", symbol: "☽", position: 120, degree: 1, minute: 35 },    // In Leo
      { name: "Mercury", symbol: "☿", position: 20, degree: 26, minute: 35 }, // In Aries
      { name: "Venus", symbol: "♀", position: 20, degree: 2, minute: 0 },     // In Aries
      { name: "Mars", symbol: "♂", position: 90, degree: 25, minute: 51 },    // In Cancer
      { name: "Jupiter", symbol: "♃", position: 90, degree: 29, minute: 0 },  // In Cancer
      { name: "Saturn", symbol: "♄", position: 270, degree: 22, minute: 24 }, // In Capricorn 
      { name: "Uranus", symbol: "♅", position: 330, degree: 26, minute: 0 },  // In Pisces
      { name: "Neptune", symbol: "♆", position: 150, degree: 25, minute: 0 }, // In Leo
      { name: "Pluto", symbol: "♇", position: 210, degree: 17, minute: 0 },   // In Libra
      { name: "North Node", symbol: "☊", position: 180, degree: 26, minute: 53 }, // In Virgo
      { name: "South Node", symbol: "☋", position: 0, degree: 26, minute: 53 }    // In Aries
    ],
    aspects: [
      { start: "Sun", end: "Moon", type: "square" },
      { start: "Sun", end: "Venus", type: "conjunction" },
      { start: "Moon", end: "Mars", type: "trine" },
      { start: "Jupiter", end: "Saturn", type: "opposition" },
      { start: "Mercury", end: "Pluto", type: "square" },
      { start: "Venus", end: "Neptune", type: "trine" },
      { start: "Mars", end: "Uranus", type: "square" }
    ],
    houses: [
      { number: 1, start: 0 },
      { number: 2, start: 30 },
      { number: 3, start: 60 },
      { number: 4, start: 90 },
      { number: 5, start: 120 },
      { number: 6, start: 150 },
      { number: 7, start: 180 },
      { number: 8, start: 210 },
      { number: 9, start: 240 },
      { number: 10, start: 270 },
      { number: 11, start: 300 },
      { number: 12, start: 330 }
    ],
    points: {
      asc: 0,    // Ascendant at Aries 0°
      mc: 270,   // Midheaven at Capricorn 0°
      dsc: 180,  // Descendant at Libra 0°
      ic: 90     // Imum Coeli at Cancer 0°
    }
  };
};

/**
 * Get a random generated chart for demo purposes
 * @returns {Object} - Randomly generated chart data
 */
export const getRandomChartData = () => {
  const sampleData = getSampleChartData();
  
  return {
    ...sampleData,
    planets: sampleData.planets.map(planet => ({
      ...planet,
      position: Math.floor(Math.random() * 360),
      degree: Math.floor(Math.random() * 30),
      minute: Math.floor(Math.random() * 60)
    }))
  };
}; 