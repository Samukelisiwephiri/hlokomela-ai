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

    if (!window.HlokomelaAPI) return;
    submit.disabled = true;
    if (error) error.hidden = true;

    try {
      const auth = await window.HlokomelaAPI.login(email, password);
      const user = auth.user;
      localStorage.setItem('hlokomela-community-name', `${user.firstName} ${user.lastName}`.trim());
      if (role === 'municipality' && !['MUNICIPAL_OPERATOR', 'ADMIN'].includes(user.role)) {
        throw new Error('This account does not have municipality access.');
      }
      if (role === 'community' && user.role !== 'COMMUNITY_MEMBER') {
        throw new Error('This account does not have community access.');
      }
      window.location.href = role === 'municipality' ? 'municipality-dashboard.html' : 'community-dashboard.html';
    } catch (loginError) {
      if (error) {
        error.textContent = loginError.message;
        error.hidden = false;
      }
      submit.disabled = false;
    }
  });
});
