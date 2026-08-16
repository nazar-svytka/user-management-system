import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

import { ToastContainer } from 'react-toastify';

import 'react-toastify/dist/ReactToastify.css';

import './index.css';
import App from './App.jsx';
import { AuthProvider } from './context/AuthContext.jsx';
import "./styles/theme.css";
import "./styles/buttons.css";
import "./styles/table.css";
import "./styles/dashboard.css";

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <BrowserRouter>
            <AuthProvider>
                <App />

                <ToastContainer
                    position="top-right"
                    autoClose={3000}
                    hideProgressBar={false}
                    newestOnTop
                    closeOnClick
                    pauseOnHover
                    draggable
                    theme="colored"
                />
            </AuthProvider>
        </BrowserRouter>
    </StrictMode>
);