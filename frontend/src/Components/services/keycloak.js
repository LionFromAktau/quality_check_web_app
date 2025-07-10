// src/services/keycloak.js
import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8081',
  realm: 'oauth2-realm',
  clientId: 'oauth2-realm-client',
});

export default keycloak;
