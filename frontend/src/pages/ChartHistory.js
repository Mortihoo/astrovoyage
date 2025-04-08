import React, { useState, useEffect } from 'react';
import { 
  Container, 
  Box, 
  Typography, 
  List, 
  ListItem, 
  ListItemText, 
  ListItemSecondaryAction,
  IconButton,
  Paper,
  Divider,
  CircularProgress,
  Alert
} from '@mui/material';
import { Delete as DeleteIcon, Visibility as VisibilityIcon } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { axiosInstance } from '../services/astrologyService';

const ChartHistory = () => {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchChartRecords();
  }, []);

  const fetchChartRecords = async () => {
    try {
      setLoading(true);
      const response = await axiosInstance.get('/api/chart-records');
      setRecords(response.data);
    } catch (err) {
      setError('Failed to fetch chart records');
      console.error('Error fetching chart records:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    try {
      await axiosInstance.delete(`/api/chart-records/${id}`);
      setRecords(records.filter(record => record.id !== id));
    } catch (err) {
      setError('Failed to delete chart record');
      console.error('Error deleting chart record:', err);
    }
  };

  const handleView = (record) => {
    navigate('/astrology', { state: { chartData: JSON.parse(record.chartData) } });
  };

  if (loading) {
    return (
      <Container>
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="md">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Chart History
        </Typography>
        
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {records.length === 0 ? (
          <Typography variant="body1" color="text.secondary">
            No chart records found. Generate a new chart to see it here.
          </Typography>
        ) : (
          <Paper elevation={3}>
            <List>
              {records.map((record, index) => (
                <React.Fragment key={record.id}>
                  <ListItem>
                    <ListItemText
                      primary={record.name}
                      secondary={
                        <>
                          <Typography component="span" variant="body2" color="text.primary">
                            {new Date(record.birthDateTime).toLocaleDateString()} at {record.location}
                          </Typography>
                          <br />
                          <Typography component="span" variant="body2" color="text.secondary">
                            Created: {new Date(record.createdAt).toLocaleString()}
                          </Typography>
                        </>
                      }
                    />
                    <ListItemSecondaryAction>
                      <IconButton
                        edge="end"
                        aria-label="view"
                        onClick={() => handleView(record)}
                        sx={{ mr: 1 }}
                      >
                        <VisibilityIcon />
                      </IconButton>
                      <IconButton
                        edge="end"
                        aria-label="delete"
                        onClick={() => handleDelete(record.id)}
                      >
                        <DeleteIcon />
                      </IconButton>
                    </ListItemSecondaryAction>
                  </ListItem>
                  {index < records.length - 1 && <Divider />}
                </React.Fragment>
              ))}
            </List>
          </Paper>
        )}
      </Box>
    </Container>
  );
};

export default ChartHistory; 