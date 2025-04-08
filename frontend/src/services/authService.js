const authService = {
  isAuthenticated: () => {
    // Check if user is authenticated by looking for token in localStorage
    const token = localStorage.getItem('token');
    return !!token;
  },

  login: (token) => {
    // Store the authentication token
    localStorage.setItem('token', token);
  },

  logout: () => {
    // Remove the authentication token
    localStorage.removeItem('token');
    localStorage.removeItem('username');
  },

  getToken: () => {
    // Get the stored token
    return localStorage.getItem('token');
  },

  getUsername: () => {
    // Get the stored username
    return localStorage.getItem('username');
  }
};

export default authService; 