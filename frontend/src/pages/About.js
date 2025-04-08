import React from 'react';
import { Box, Typography, Paper, Container } from '@mui/material';

function About() {
  return (
    <Container maxWidth="md">
      <Box sx={{ py: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          About Astrology Chart
        </Typography>
        <Paper elevation={3} sx={{ p: 4, mt: 2 }}>
          <Typography variant="body1" paragraph>
            Astrology Chart is a modern web application that helps you explore and understand your astrological chart.
            Our platform uses advanced algorithms and astronomical data to generate accurate birth charts and provide
            meaningful insights about your cosmic influences.
          </Typography>
          <Typography variant="body1" paragraph>
            Whether you're new to astrology or an experienced practitioner, our tools and visualizations make it easy
            to explore the positions of planets, houses, and aspects in your chart.
          </Typography>
          <Typography variant="body1">
            We believe in making astrology accessible and understandable to everyone, while maintaining the depth and
            accuracy that serious practitioners expect.
          </Typography>
        </Paper>
      </Box>
    </Container>
  );
}

export default About; 