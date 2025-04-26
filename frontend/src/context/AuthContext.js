import React, { createContext, useState, useContext, useEffect } from 'react';
import axios from 'axios';
import config from '../config/config';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [username, setUsername] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    const validateToken = async (token) => {
        try {
            const response = await axios.get(`${config.API_BASE_URL}/auth/validate`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.status === 200;
        } catch (error) {
            if (error.response?.status === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('username');
            }
            return false;
        }
    };

    useEffect(() => {
        const checkAuth = async () => {
            const token = localStorage.getItem('token');
            const storedUsername = localStorage.getItem('username');
            
            if (token && storedUsername) {
                const isValid = await validateToken(token);
                if (isValid) {
                    setIsAuthenticated(true);
                    setUsername(storedUsername);
                } else {
                    setIsAuthenticated(false);
                    setUsername(null);
                }
            } else {
                setIsAuthenticated(false);
                setUsername(null);
            }
            setIsLoading(false);
        };

        checkAuth();
    }, []);

    const login = (token) => {
        const storedUsername = localStorage.getItem('username');
        localStorage.setItem('token', token);
        setIsAuthenticated(true);
        setUsername(storedUsername);
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        setIsAuthenticated(false);
        setUsername(null);
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, username, login, logout, isLoading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}; 