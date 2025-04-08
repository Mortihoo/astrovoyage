import React, { useState, useEffect } from 'react';
import { 
  Box, 
  TextField, 
  Button, 
  Paper, 
  Typography, 
  List, 
  ListItem, 
  ListItemText,
  CircularProgress,
  Divider
} from '@mui/material';
import { Send as SendIcon } from '@mui/icons-material';
import { axiosInstance } from '../services/astrologyService';

const ChartInterpretation = ({ chartData }) => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    console.log('ChartInterpretation mounted with chartData:', chartData);
    // Reset messages when chartData changes
    setMessages([]);
  }, [chartData]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!input.trim()) return;

    // Add user message
    const userMessage = { role: 'user', content: input };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setLoading(true);

    try {
      console.log('Sending request to backend with chartData:', chartData);
      // Call backend API for interpretation using axiosInstance
      const response = await axiosInstance.post('/api/interpret', {
        question: input,
        chartData: {
          ...chartData,
          planetPositions: chartData.planetPositions,
          planetSpeeds: chartData.planetSpeeds,
          houses: chartData.houses,
          aspects: chartData.aspects,
          aspectsDetails: chartData.aspectsDetails,
          birthDateTime: chartData.birthDateTime,
          location: chartData.location,
          gender: chartData.gender
        }
      });

      console.log('Received response from backend:', response.data);
      
      // Add AI response
      setMessages(prev => [...prev, { role: 'assistant', content: response.data.interpretation }]);
    } catch (error) {
      console.error('Error getting interpretation:', error);
      setMessages(prev => [...prev, { 
        role: 'assistant', 
        content: 'Sorry, there was an error processing your request. Please try again.' 
      }]);
    } finally {
      setLoading(false);
    }
  };

  if (!chartData) {
    console.log('No chartData available, not rendering ChartInterpretation');
    return null;
  }

  return (
    <Paper 
      elevation={3} 
      sx={{ 
        p: 3, 
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        bgcolor: 'white',
        minHeight: '800px', // Set minimum height
        maxHeight: '800px'  // Set maximum height
      }}
    >
      <Typography variant="h6" gutterBottom>
        Ask about your chart
      </Typography>
      
      <Box sx={{ 
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        mb: 2,
        border: '1px solid #e0e0e0',
        borderRadius: 1,
        bgcolor: '#fafafa',
        overflow: 'hidden' // Prevent content overflow
      }}>
        <List sx={{
          flex: 1,
          overflowY: 'auto',
          p: 2,
          '&::-webkit-scrollbar': {
            width: '8px',
          },
          '&::-webkit-scrollbar-track': {
            backgroundColor: '#f5f5f5',
            borderRadius: '4px',
          },
          '&::-webkit-scrollbar-thumb': {
            backgroundColor: '#bdbdbd',
            borderRadius: '4px',
            '&:hover': {
              backgroundColor: '#9e9e9e',
            },
          },
        }}>
          {messages.map((message, index) => (
            <React.Fragment key={index}>
              <ListItem sx={{ 
                display: 'flex', 
                flexDirection: 'column',
                alignItems: message.role === 'user' ? 'flex-end' : 'flex-start',
                mb: 1,
                px: 0
              }}>
                <Paper 
                  elevation={1} 
                  sx={{ 
                    p: 2, 
                    maxWidth: '80%',
                    bgcolor: message.role === 'user' ? '#e3f2fd' : '#f5f5f5'
                  }}
                >
                  <ListItemText 
                    primary={message.content}
                    sx={{ 
                      wordBreak: 'break-word',
                      whiteSpace: 'pre-wrap'
                    }}
                  />
                </Paper>
              </ListItem>
              {index < messages.length - 1 && <Divider sx={{ my: 1 }} />}
            </React.Fragment>
          ))}
          {loading && (
            <ListItem sx={{ justifyContent: 'center' }}>
              <CircularProgress size={24} />
            </ListItem>
          )}
        </List>
      </Box>

      <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', gap: 1 }}>
        <TextField
          fullWidth
          variant="outlined"
          placeholder="Ask about your chart..."
          value={input}
          onChange={(e) => setInput(e.target.value)}
          disabled={loading}
          sx={{ flexGrow: 1 }}
        />
        <Button 
          type="submit" 
          variant="contained" 
          endIcon={<SendIcon />}
          disabled={loading}
          sx={{ minWidth: '100px' }}
        >
          Send
        </Button>
      </Box>
    </Paper>
  );
};

export default ChartInterpretation; 