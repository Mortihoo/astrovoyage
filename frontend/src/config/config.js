const config = {
    API_BASE_URL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
    API_ENDPOINTS: {
        LOGIN: '/auth/login',
        REGISTER: '/auth/register',
        INTERPRET: '/interpret',
        BIRTH_CHART: '/birth-chart'
    }
};

export default config; 