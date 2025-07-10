import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import keycloak from './Components/services/keycloak';

keycloak.init({ onLoad: 'login-required' })
  .then(authenticated => {
    if (authenticated) {
      localStorage.setItem('jwtToken', keycloak.token);

      setInterval(() => {
        keycloak.updateToken(30).then(refreshed => {
          if (refreshed) {
            localStorage.setItem('jwtToken', keycloak.token);
          }
        }).catch((err) => {
          console.error('🔁 Token refresh error:', err);
          keycloak.logout();
        });
      }, 10000);

      ReactDOM.createRoot(document.getElementById('root')).render(
        <React.StrictMode>
          <App />
        </React.StrictMode>
      );
    } else {
      console.warn('Not authenticated');
      keycloak.logout();
    }
  })
  .catch((err) => {
    console.error('Keycloak init failed:', err);
  });
