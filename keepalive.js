// Keeps the Render free-tier backend warm by pinging /api/v1/health every 8 minutes.
// This prevents the 50-second cold-start delay on first user action.
(function () {
  const BASE = (window.HLOKOMELA_API_URL || '').replace(/\/$/, '');
  if (!BASE) return;

  function ping() {
    fetch(BASE + '/api/v1/health', { method: 'GET', cache: 'no-store' })
      .catch(function () { /* silent — just keeping it warm */ });
  }

  // Ping immediately on page load then every 8 minutes
  ping();
  setInterval(ping, 8 * 60 * 1000);
})();
