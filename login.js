document.querySelectorAll('.login-form').forEach(form => {
  form.addEventListener('submit', event => {
    event.preventDefault();
    const role = form.dataset.role;
    window.location.href = role === 'municipality' ? 'municipality-dashboard.html' : 'community-dashboard.html';
  });
});
