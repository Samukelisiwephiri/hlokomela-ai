(function () {
  const configuredBase = window.HLOKOMELA_API_URL || localStorage.getItem('hlokomela-api-url');
  const API_BASE = (configuredBase || 'http://localhost:8080').replace(/\/$/, '');
  const TOKEN_KEY = 'hlokomela-access-token';
  const USER_KEY = 'hlokomela-user';

  async function request(path, options = {}) {
    const headers = new Headers(options.headers || {});
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) headers.set('Authorization', 'Bearer ' + token);
    if (options.body && !(options.body instanceof FormData) && !headers.has('Content-Type')) {
      headers.set('Content-Type', 'application/json');
    }

    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), 12000);
    let response;
    try {
      response = await fetch(API_BASE + path, { ...options, headers, signal: controller.signal });
    } finally { clearTimeout(timer); }
    const contentType = response.headers.get('content-type') || '';
    const payload = contentType.includes('application/json') ? await response.json() : await response.text();
    if (response.status === 401) {
      const isAuthEndpoint = path.includes('/auth/login') || path.includes('/auth/register');
      if (!isAuthEndpoint) {
        clearSession();
        throw new Error('Your session has expired. Please sign in again.');
      }
      const errPayload = typeof payload === 'object' ? payload : {};
      throw new Error(errPayload && errPayload.message ? errPayload.message : 'Invalid email or password.');
        ? errPayload.message
        : 'Invalid email or password. Please check your credentials.';
      throw new Error(message);
    }

    if (!response.ok) {
      const message = typeof payload === 'object' && payload?.message
        ? payload.message
        : `Request failed (${response.status})`;
      throw new Error(message);
    }
    return payload;
  }

  function clearSession() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }

  function saveSession(auth) {
    localStorage.setItem(TOKEN_KEY, auth.accessToken);
    localStorage.setItem(USER_KEY, JSON.stringify(auth.user));
    return auth;
  }

  function currentUser() {
    try { return JSON.parse(localStorage.getItem(USER_KEY) || 'null'); } catch { return null; }
  }

  function isAuthenticated() {
    return Boolean(localStorage.getItem(TOKEN_KEY));
  }

  async function login(email, password) {
    return saveSession(await request('/api/v1/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    }));
  }

  async function register(details) {
    return saveSession(await request('/api/v1/auth/register', {
      method: 'POST',
      body: JSON.stringify(details)
    }));
  }

  async function submitReport(report, photo) {
    if (!photo) {
      return request('/api/v1/community-reports', { method: 'POST', body: JSON.stringify(report) });
    }
    const form = new FormData();
    form.append('report', new Blob([JSON.stringify(report)], { type: 'application/json' }));
    form.append('photo', photo);
    return request('/api/v1/community-reports', { method: 'POST', body: form });
  }

  async function submitTelemetry(reading, deviceKey) {
    return request('/api/v1/telemetry/readings', {
      method: 'POST',
      headers: { 'X-Device-Key': deviceKey },
      body: JSON.stringify(reading)
    });
  }

  async function askAssistant(question, language) {
    return request('/api/v1/ai/assistant', {
      method: 'POST',
      body: JSON.stringify({ question, language })
    });
  }

  window.HlokomelaAPI = {
    API_BASE, request, login, register, submitReport, submitTelemetry, askAssistant, currentUser, isAuthenticated, clearSession
  };
})();
