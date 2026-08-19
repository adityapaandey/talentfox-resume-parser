// Configuration for different environments
const config = {
    development: {
        API_BASE_URL: 'http://localhost:8080/api/resume-parser'
    },
    production: {
        // Update this with your Railway/Render backend URL after deployment
        API_BASE_URL: 'https://your-backend-url.railway.app/api/resume-parser'
    }
};

// Auto-detect environment
const ENV = window.location.hostname === 'localhost' ? 'development' : 'production';

export const API_BASE_URL = config[ENV].API_BASE_URL;
