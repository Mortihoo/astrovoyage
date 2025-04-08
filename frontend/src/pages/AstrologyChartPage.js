import React, { useState, useEffect } from 'react';
import { Container, Typography, Paper, Box, Alert } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import AstrologyChart from '../components/AstrologyChart';
import ChartForm from '../components/ChartForm';
import ChartInterpretation from '../components/ChartInterpretation';
import { getSampleChartData, generateChart, getRandomChartData } from '../services/astrologyService';

const AstrologyChartPage = () => {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [chartData, setChartData] = useState(null);
  const [showInterpretation, setShowInterpretation] = useState(false);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    
    console.log('AstrologyChartPage mounted');
    // Load sample chart data on component mount
    const sampleData = getSampleChartData();
    console.log('Loaded sample chart data:', sampleData);
    setChartData(sampleData);
  }, [isAuthenticated, navigate]);

  const handleFormSubmit = async (formData) => {
    console.log('Form submitted with data:', formData);
    setLoading(true);
    setError('');
    setShowInterpretation(false);
    
    try {
      // In a production app, we would call the actual API
      // const data = await generateChart(formData);
      
      // For demo purposes, simulate API delay and use random data
      await new Promise(resolve => setTimeout(resolve, 1500));
      const data = {
        ...getRandomChartData(),
        gender: formData.gender // Include gender in chart data
      };
      console.log('Generated chart data:', data);
      
      setChartData(data);
      setShowInterpretation(true);
    } catch (err) {
      console.error('Error generating chart:', err);
      setError('Failed to generate chart. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  console.log('Current chartData state:', chartData);
  console.log('Show interpretation:', showInterpretation);

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom align="center">
          Astrological Chart
        </Typography>
        
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
        
        <ChartForm onSubmit={handleFormSubmit} loading={loading} />
        
        {chartData && (
          <Box sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ p: 2, display: 'flex', justifyContent: 'center', mb: 4 }}>
              <AstrologyChart chartData={chartData} />
            </Paper>
            {showInterpretation && <ChartInterpretation chartData={chartData} />}
          </Box>
        )}
      </Box>
    </Container>
  );
};

export default AstrologyChartPage; 