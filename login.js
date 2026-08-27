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

/* Tab switching between Sign In and Create Account */
document.querySelectorAll('.auth-tabs').forEach(tabs => {
  const loginForm = tabs.closest('.login-card').querySelector('#login-form');
  const registerForm = tabs.closest('.login-card').querySelector('#register-form');
  tabs.querySelectorAll('.auth-tab').forEach(tab => {
    tab.addEventListener('click', () => {
      tabs.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      const isRegister = tab.dataset.tab === 'register';
      if (loginForm) loginForm.hidden = isRegister;
      if (registerForm) registerForm.hidden = !isRegister;
      const err = tabs.closest('.login-card').querySelector('#login-error');
      if (err) err.hidden = true;
    });
  });
});

/* Register form submission */
document.querySelectorAll('.register-form').forEach(form => {
  form.addEventListener('submit', async event => {
    event.preventDefault();
    const role = form.dataset.role;
    const firstName = form.querySelector('input[name="firstName"]')?.value?.trim();
    const lastName = form.querySelector('input[name="lastName"]')?.value?.trim();
    const email = form.querySelector('input[name="email"]')?.value?.trim();
    const phone = form.querySelector('input[name="phone"]')?.value?.trim() || null;
    const password = form.querySelector('input[name="password"]')?.value;
    const submit = form.querySelector('button[type="submit"]');
    const error = document.getElementById('login-error');

    if (!firstName || !email || !password) {
      if (error) { error.textContent = 'Please fill in all required fields.'; error.hidden = false; }
      return;
    }

    submit.disabled = true;
    submit.textContent = 'Creating account...';
    if (error) error.hidden = true;

    const userRole = role === 'municipality' ? 'MUNICIPAL_OPERATOR' : 'COMMUNITY_MEMBER';

    if (window.HlokomelaAPI) {
      try {
        const auth = await window.HlokomelaAPI.register({ firstName, lastName, email, phone, password, role: userRole });
        localStorage.removeItem('hlokomela-demo-mode');
        localStorage.setItem('hlokomela-community-name', `${auth.user.firstName} ${auth.user.lastName}`.trim());
        window.location.href = role === 'municipality' ? 'municipality-dashboard.html' : 'community-dashboard.html';
        return;
      } catch (regError) {
        const msg = regError.message || '';
        // Fall through to demo mode on any error (network or auth)
        if (error) { error.textContent = 'Entering demo mode...'; error.hidden = false; }
      }
    }

    /* Demo fallback */
    demoLogin(role, email, password, `${firstName} ${lastName}`.trim());
  });
});

/* Login form submission */
document.querySelectorAll('.login-form:not(.register-form)').forEach(form => {
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
        // Fall through to demo mode on any error (network or auth)
        if (error) { error.textContent = 'Entering demo mode...'; error.hidden = false; }
      }
    }

    demoLogin(role, email, password, fullName);
  });
});
