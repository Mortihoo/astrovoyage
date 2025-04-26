import React, { useState, useEffect } from 'react';
import { Container, Box, Alert, Grid, Paper, CircularProgress, Fab, Tooltip } from '@mui/material';
import ChartForm from '../components/ChartForm';
import AstrologyChart from '../components/AstrologyChart';
import ChartInterpretation from '../components/ChartInterpretation';
import PlanetPositions from '../components/PlanetPositions';
import { generateChart } from '../services/astrologyService';
import { axiosInstance } from '../services/astrologyService';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import RefreshIcon from '@mui/icons-material/Refresh';

const AstrologyPage = () => {
  const [chartData, setChartData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showInterpretation, setShowInterpretation] = useState(false);
  const [showForm, setShowForm] = useState(true);
  const location = useLocation();
  const navigate = useNavigate();
  const { isAuthenticated, isLoading } = useAuth();

  useEffect(() => {
    if (!isLoading) {
      if (!isAuthenticated) {
        navigate('/login', { replace: true });
        return;
      }

      if (location.state?.chartData) {
        setChartData(location.state.chartData);
        setShowInterpretation(true);
        setShowForm(false);
      }
    }
  }, [location.state, isAuthenticated, navigate, isLoading]);

  if (isLoading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <CircularProgress />
      </Box>
    );
  }

  const handleSubmit = async (formData) => {
    try {
      setLoading(true);
      setError(null);
      setShowInterpretation(false);
      const data = await generateChart(formData);
      setChartData(data);
      setShowInterpretation(true);
      setShowForm(false);

      // Save chart record
      try {
        await axiosInstance.post('/chart-records', {
          name: formData.name,
          birthDateTime: formData.birthDateTime,
          location: formData.location,
          timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
          gender: formData.gender,
          chartData: JSON.stringify(data)
        });
      } catch (err) {
        console.error('Error saving chart record:', err);
        setError('Failed to save chart record');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to generate chart');
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = () => {
    setShowForm(true);
    setShowInterpretation(false);
    setChartData(null);
  };

  return (
    <Container maxWidth="lg">
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      
      {showForm && (
        <ChartForm onSubmit={handleSubmit} loading={loading} />
      )}

      {chartData && (
        <>
          <Grid container spacing={3}>
            <Grid item xs={12} md={8}>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                <AstrologyChart chartData={chartData} />
                {showInterpretation && (
                  <ChartInterpretation chartData={chartData} />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} md={4}>
              <Box sx={{ position: 'sticky', top: 20 }}>
                <PlanetPositions chartData={chartData} />
              </Box>
            </Grid>
          </Grid>
          <Tooltip title="Recalculate" placement="left">
            <Fab
              color="primary"
              aria-label="refresh"
              onClick={handleRefresh}
              sx={{
                position: 'fixed',
                right: 24,
                bottom: 24,
                zIndex: 1000,
              }}
            >
              <RefreshIcon />
            </Fab>
          </Tooltip>
        </>
      )}
    </Container>
  );
};

export default AstrologyPage; 