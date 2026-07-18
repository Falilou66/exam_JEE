// Environnement de PRODUCTION (build Docker). L'API est servie derrière Nginx
// sur la même origine → URL relative.
export const environment = {
  production: true,
  apiUrl: '/api/v1',
};
