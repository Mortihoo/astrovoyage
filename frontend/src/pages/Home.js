import React from 'react';
import { Typography, Button, Box, Paper, Grid } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import StarIcon from '@mui/icons-material/Star';
import PsychologyIcon from '@mui/icons-material/Psychology';
import AutoGraphIcon from '@mui/icons-material/AutoGraph';
import RocketLaunchIcon from '@mui/icons-material/RocketLaunch';
import { useAuth } from '../context/AuthContext';

function Home() {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const handleGenerateChart = () => {
    if (!isAuthenticated) {
      navigate('/login');
    } else {
      navigate('/astrology');
    }
  };

  return (
    <Box
      sx={{
        height: 'calc(100vh - 64px)', // subtract navbar height
        width: '100vw',
        overflow: 'hidden',
        backgroundImage: 'url("https://images.unsplash.com/photo-1534447677768-be436bb09401?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1974&q=80")',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
        position: 'relative',
        '&::before': {
          content: '""',
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'linear-gradient(135deg, rgba(108, 99, 255, 0.8) 0%, rgba(255, 101, 132, 0.8) 100%)',
          zIndex: 1,
        },
      }}
    >
      <Box
        sx={{
          position: 'relative',
          zIndex: 2,
          textAlign: 'center',
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          px: 2,
          color: 'white',
        }}
      >
        <Box sx={{ mb: { xs: 1, md: 2 } }}>
          <StarIcon sx={{ fontSize: { xs: 50, md: 70 }, color: 'white', filter: 'drop-shadow(0 0 10px rgba(255, 255, 255, 0.5))' }} />
        </Box>
        <Typography 
          variant="h1" 
          component="h1" 
          gutterBottom
          sx={{
            textShadow: '0 2px 4px rgba(0, 0, 0, 0.2)',
            mb: 1,
            fontSize: { xs: '2rem', md: '3rem' },
          }}
        >
          Unlock Your Cosmic Destiny
        </Typography>
        <Typography 
          variant="h4" 
          component="h2" 
          gutterBottom 
          sx={{ 
            mb: { xs: 3, md: 4 },
            fontWeight: 400,
            maxWidth: '800px',
            mx: 'auto',
            textShadow: '0 1px 2px rgba(0, 0, 0, 0.2)',
            fontSize: { xs: '1.1rem', md: '1.5rem' },
          }}
        >
          Discover the secrets of your birth chart and embark on a journey of self-discovery
        </Typography>
        <Button
          variant="contained"
          size="large"
          onClick={handleGenerateChart}
          sx={{ 
            fontSize: '1.1rem',
            py: 1.5,
            px: 5,
            borderRadius: 3,
            boxShadow: '0 8px 16px rgba(0, 0, 0, 0.2)',
            background: 'linear-gradient(45deg, #6C63FF 30%, #FF6584 90%)',
            '&:hover': {
              transform: 'translateY(-3px)',
              boxShadow: '0 12px 20px rgba(0, 0, 0, 0.3)',
            }
          }}
        >
          Begin Your Astrological Journey
        </Button>

        <Grid 
          container 
          spacing={3} 
          sx={{ 
            mt: { xs: 3, md: 4 },
            maxWidth: '1200px',
            mx: 'auto',
            px: 2,
          }}
        >
          <Grid item xs={12} md={4}>
            <Paper 
              elevation={3} 
              sx={{ 
                p: 2.5, 
                height: '100%',
                backgroundColor: 'rgba(255, 255, 255, 0.95)',
                backdropFilter: 'blur(10px)',
                transition: 'all 0.3s ease-in-out',
                '&:hover': {
                  transform: 'translateY(-10px)',
                  boxShadow: '0 16px 24px rgba(0, 0, 0, 0.2)',
                }
              }}
            >
              <PsychologyIcon sx={{ fontSize: 35, color: 'primary.main', mb: 1.5 }} />
              <Typography variant="h5" gutterBottom color="primary" sx={{ fontWeight: 600, fontSize: '1.1rem' }}>
                Deep Personal Insights
              </Typography>
              <Typography variant="body1" color="text.secondary" sx={{ fontSize: '0.9rem' }}>
                Uncover the hidden aspects of your personality through detailed analysis of your sun, moon, and rising signs.
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} md={4}>
            <Paper 
              elevation={3} 
              sx={{ 
                p: 2.5, 
                height: '100%',
                backgroundColor: 'rgba(255, 255, 255, 0.95)',
                backdropFilter: 'blur(10px)',
                transition: 'all 0.3s ease-in-out',
                '&:hover': {
                  transform: 'translateY(-10px)',
                  boxShadow: '0 16px 24px rgba(0, 0, 0, 0.2)',
                }
              }}
            >
              <AutoGraphIcon sx={{ fontSize: 35, color: 'primary.main', mb: 1.5 }} />
              <Typography variant="h5" gutterBottom color="primary" sx={{ fontWeight: 600, fontSize: '1.1rem' }}>
                Interactive Visualizations
              </Typography>
              <Typography variant="body1" color="text.secondary" sx={{ fontSize: '0.9rem' }}>
                Explore stunning, interactive astrological charts that bring your cosmic blueprint to life.
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} md={4}>
            <Paper 
              elevation={3} 
              sx={{ 
                p: 2.5, 
                height: '100%',
                backgroundColor: 'rgba(255, 255, 255, 0.95)',
                backdropFilter: 'blur(10px)',
                transition: 'all 0.3s ease-in-out',
                '&:hover': {
                  transform: 'translateY(-10px)',
                  boxShadow: '0 16px 24px rgba(0, 0, 0, 0.2)',
                }
              }}
            >
              <RocketLaunchIcon sx={{ fontSize: 35, color: 'primary.main', mb: 1.5 }} />
              <Typography variant="h5" gutterBottom color="primary" sx={{ fontWeight: 600, fontSize: '1.1rem' }}>
                AI-Enhanced Guidance
              </Typography>
              <Typography variant="body1" color="text.secondary" sx={{ fontSize: '0.9rem' }}>
                Receive personalized, AI-powered interpretations that help you understand your unique astrological profile.
              </Typography>
            </Paper>
          </Grid>
        </Grid>
      </Box>
    </Box>
  );
}

export default Home; 