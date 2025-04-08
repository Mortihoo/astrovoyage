import React, { useState } from 'react';
import { 
  Box, 
  TextField, 
  Button, 
  Typography, 
  Grid, 
  FormControl, 
  InputLabel, 
  Select, 
  MenuItem,
  Paper,
  RadioGroup,
  FormControlLabel,
  Radio,
  FormLabel
} from '@mui/material';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider, DateTimePicker } from '@mui/x-date-pickers';
import { countries, cities } from '../data/locations';

const ChartForm = ({ onSubmit, loading = false }) => {
  const [formData, setFormData] = useState({
    birthDateTime: new Date(),
    country: '',
    city: '',
    name: '',
    gender: 'female', // default value
  });

  const [availableCities, setAvailableCities] = useState([]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Update available cities when country changes
    if (name === 'country') {
      setAvailableCities(cities[value] || []);
      setFormData(prev => ({
        ...prev,
        city: '' // Reset city when country changes
      }));
    }
  };

  const handleDateTimeChange = (newValue) => {
    setFormData(prev => ({
      ...prev,
      birthDateTime: newValue
    }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit({
      ...formData,
      birthDateTime: formData.birthDateTime.toISOString(),  // Convert Date to ISO string
      location: `${formData.city}, ${formData.country}`
    });
  };

  return (
    <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
      <Box component="form" onSubmit={handleSubmit}>
        <Typography variant="h6" component="h2" gutterBottom>
          Generate Astrological Chart
        </Typography>
        
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <TextField
              required
              fullWidth
              label="Name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              margin="normal"
            />
          </Grid>
          
          <Grid item xs={12} md={6}>
            <FormControl component="fieldset" margin="normal">
              <FormLabel component="legend">Gender</FormLabel>
              <RadioGroup
                row
                name="gender"
                value={formData.gender}
                onChange={handleChange}
              >
                <FormControlLabel value="female" control={<Radio />} label="Female" />
                <FormControlLabel value="male" control={<Radio />} label="Male" />
                <FormControlLabel value="other" control={<Radio />} label="Other" />
              </RadioGroup>
            </FormControl>
          </Grid>
          
          <Grid item xs={12} md={6}>
            <FormControl fullWidth margin="normal">
              <InputLabel>Country</InputLabel>
              <Select
                required
                name="country"
                value={formData.country}
                onChange={handleChange}
                label="Country"
              >
                {countries.map((country) => (
                  <MenuItem key={country.code} value={country.code}>
                    {country.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          
          <Grid item xs={12} md={6}>
            <FormControl fullWidth margin="normal">
              <InputLabel>City</InputLabel>
              <Select
                required
                name="city"
                value={formData.city}
                onChange={handleChange}
                label="City"
                disabled={!formData.country}
              >
                {availableCities.map((city) => (
                  <MenuItem key={city.name} value={city.name}>
                    {city.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          
          <Grid item xs={12} md={6}>
            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <DateTimePicker
                label="Birth Date and Time"
                value={formData.birthDateTime}
                onChange={handleDateTimeChange}
                renderInput={(params) => <TextField required fullWidth margin="normal" {...params} />}
              />
            </LocalizationProvider>
          </Grid>
        </Grid>
        
        <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
          <Button 
            type="submit" 
            variant="contained" 
            color="primary"
            disabled={loading || !formData.city}
          >
            {loading ? 'Generating...' : 'Generate Chart'}
          </Button>
        </Box>
      </Box>
    </Paper>
  );
};

export default ChartForm; 