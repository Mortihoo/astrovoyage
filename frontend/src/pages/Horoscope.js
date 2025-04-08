import React from 'react';
import { Card, Typography } from '@mui/material';

const Horoscope = () => {
    return (
        <div style={{ padding: '20px' }}>
            <Card sx={{ maxWidth: 800, margin: '0 auto', padding: '20px' }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Horoscope Analysis
                </Typography>
                <Typography variant="body1">
                    This is a protected page that requires authentication to access.
                </Typography>
            </Card>
        </div>
    );
};

export default Horoscope; 