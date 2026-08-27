/* Demo credentials — no backend required for the public GitHub Pages demo */
const DEMO_ACCOUNTS = {
  'municipality': { email: 'demo@municipality.gov.za', password: 'demo1234', firstName: 'Demo', lastName: 'Municipality', role: 'MUNICIPAL_OPERATOR' },
  'community':   { email: 'demo@community.co.za',     password: 'demo1234', firstName: 'Demo', lastName: 'User',         role: 'COMMUNITY_MEMBER'   }
};

function demoLogin(role, email, password, fullName) {
  const acc = DEMO_ACCOUNTS[role];
  const name = fullName || (acc ? `${acc.firstName} ${acc.lastName}` : 'Demo User');
  localStorage.setItem('hlokomela-access-token', 'demo-token');
  localStorage.setItem('hlokomela-demo-mode', 'true');
  localStorage.setItem('hlokomela-user', JSON.stringify({ firstName: name.split(' ')[0], lastName: name.split(' ').slice(1).join(' ') || '', role: role === 'municipality' ? 'MUNICIPAL_OPERATOR' : 'COMMUNITY_MEMBER' }));
  localStorage.setItem('hlokomela-community-name', name);
  window.location.href = role === 'municipality' ? 'municipality-dashboard.html' : 'community-dashboard.html';
}

document.querySelectorAll('.login-form').forEach(form => {
  form.addEventListener('submit', async event => {
    event.preventDefault();
    const role = form.dataset.role;
    const fullNameInput = form.querySelector('#full-name-input, input[name="fullName"]');
    const fullName = fullNameInput?.value?.trim();
    const email = form.querySelector('input[name="email"]')?.value?.trim();
    const password = form.querySelector('input[name="password"]')?.value;
    const submit = form.querySelector('button[type="submit"]');
    const error = document.getElementById('login-error');

    if (!email || !password) {
      if (error) { error.textContent = 'Please enter your email and password.'; error.hidden = false; }
      return;
    }

    submit.disabled = true;
    submit.textContent = 'Signing in...';
    if (error) error.hidden = true;

    /* Try the real API first; fall back to demo mode if it is unreachable */
    if (window.HlokomelaAPI) {
      try {
        const auth = await window.HlokomelaAPI.login(email, password);
        const user = auth.user;
        localStorage.removeItem('hlokomela-demo-mode');
        localStorage.setItem('hlokomela-community-name', `${user.firstName} ${user.lastName}`.trim());
        if (role === 'municipality' && !['MUNICIPAL_OPERATOR', 'ADMIN'].includes(user.role)) {
          throw new Error('This account does not have municipality access.');
        }
        if (role === 'community' && user.role !== 'COMMUNITY_MEMBER') {
          throw new Error('This account does not have community access.');
        }
        window.location.href = role === 'municipality' ? 'municipality-dashboard.html' : 'community-dashboard.html';
        return;
      } catch (loginError) {
        const msg = loginError.message || '';
        const isNetworkError = msg.includes('Failed to fetch') || msg.includes('NetworkError') || msg.includes('Request failed (5') || msg.includes('Load failed') || msg.includes('503') || msg.includes('waking');
        if (!isNetworkError) {
          if (error) { error.textContent = msg; error.hidden = false; }
          submit.disabled = false;
          submit.textContent = submit.dataset.i18n === 'login_button_text' ? 'Login to community dashboard' : 'Login to municipality dashboard';
          return;
        }
        /* Network/server error — show message then fall through to demo */
        if (error) { error.textContent = 'Server starting up — entering demo mode. Your data will sync when the server is ready.'; error.hidden = false; }
      }
    }

    /* Demo mode: accept any non-empty credentials */
    demoLogin(role, email, password, fullName);
  });
});
