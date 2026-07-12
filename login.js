document.querySelectorAll('.login-form').forEach(form => {
  form.addEventListener('submit', event => {
    event.preventDefault();
    const role = form.dataset.role;
    const fullNameInput = form.querySelector('#full-name-input, input[name="fullName"]');
    const fullName = fullNameInput?.value?.trim();

    if (fullName) {
      localStorage.setItem('hlokomela-community-name', fullName);
    } else {
      localStorage.removeItem('hlokomela-community-name');
    }

    window.location.href = role === 'municipality' ? 'municipality-dashboard.html' : 'community-dashboard.html';
  });
});
