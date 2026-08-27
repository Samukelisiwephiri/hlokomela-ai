(function () {
  var configuredBase = window.HLOKOMELA_API_URL || localStorage.getItem('hlokomela-api-url');
  var API_BASE = (configuredBase || 'http://localhost:8080').replace(/\/$/, '');
  var TOKEN_KEY = 'hlokomela-access-token';
  var USER_KEY = 'hlokomela-user';

  function getToken() { return localStorage.getItem(TOKEN_KEY); }

  function request(path, options) {
    options = options || {};
    var headers = new Headers(options.headers || {});
    var token = getToken();
    if (token) headers.set('Authorization', 'Bearer ' + token);
    if (options.body && !(options.body instanceof FormData) && !headers.has('Content-Type')) {
      headers.set('Content-Type', 'application/json');
    }

    var controller = new AbortController();
    var timer = setTimeout(function () { controller.abort(); }, 12000);

    return fetch(API_BASE + path, Object.assign({}, options, { headers: headers, signal: controller.signal }))
      .then(function (response) {
        clearTimeout(timer);
        var contentType = response.headers.get('content-type') || '';
        var payloadPromise = contentType.indexOf('application/json') !== -1
          ? response.json()
          : response.text();
        return payloadPromise.then(function (payload) {
          if (response.status === 401) {
            var isAuth = path.indexOf('/auth/login') !== -1 || path.indexOf('/auth/register') !== -1;
            if (!isAuth) {
              clearSession();
              throw new Error('Your session has expired. Please sign in again.');
            }
            var msg = (payload && typeof payload === 'object' && payload.message)
              ? payload.message
              : 'Invalid email or password. Please check your credentials.';
            throw new Error(msg);
          }
          if (!response.ok) {
            var errMsg = (payload && typeof payload === 'object' && payload.message)
              ? payload.message
              : 'Request failed (' + response.status + ')';
            throw new Error(errMsg);
          }
          return payload;
        });
      })
      .catch(function (err) {
        clearTimeout(timer);
        throw err;
      });
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
    try { return JSON.parse(localStorage.getItem(USER_KEY) || 'null'); } catch (e) { return null; }
  }

  function isAuthenticated() {
    return Boolean(getToken());
  }

  function login(email, password) {
    return request('/api/v1/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email: email, password: password })
    }).then(saveSession);
  }

  function register(details) {
    return request('/api/v1/auth/register', {
      method: 'POST',
      body: JSON.stringify(details)
    }).then(saveSession);
  }

  function submitReport(report, photo) {
    if (!photo) {
      return request('/api/v1/community-reports', { method: 'POST', body: JSON.stringify(report) });
    }
    var form = new FormData();
    form.append('report', new Blob([JSON.stringify(report)], { type: 'application/json' }));
    form.append('photo', photo);
    return request('/api/v1/community-reports', { method: 'POST', body: form });
  }

  function submitTelemetry(reading, deviceKey) {
    return request('/api/v1/telemetry/readings', {
      method: 'POST',
      headers: { 'X-Device-Key': deviceKey },
      body: JSON.stringify(reading)
    });
  }

  function askAssistant(question, language) {
    return request('/api/v1/ai/assistant', {
      method: 'POST',
      body: JSON.stringify({ question: question, language: language })
    });
  }

  window.HlokomelaAPI = {
    API_BASE: API_BASE,
    request: request,
    login: login,
    register: register,
    submitReport: submitReport,
    submitTelemetry: submitTelemetry,
    askAssistant: askAssistant,
    currentUser: currentUser,
    isAuthenticated: isAuthenticated,
    clearSession: clearSession
  };
})();
