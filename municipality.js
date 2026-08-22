document.addEventListener('DOMContentLoaded', async () => {
  const api = window.HlokomelaAPI;
  if (!api?.isAuthenticated()) return;

  const setText = (selector, value) => {
    const element = document.querySelector(selector);
    if (element && value !== undefined && value !== null) element.textContent = value;
  };

  try {
    const [summary, pipes, incidents, workOrders, reports] = await Promise.all([
      api.request('/api/v1/dashboard/summary'),
      api.request('/api/v1/pipes'),
      api.request('/api/v1/incidents?size=20'),
      api.request('/api/v1/maintenance/work-orders'),
      api.request('/api/v1/community-reports/municipal?size=20')
    ]);

    setText('#ai-risk-count', summary.highRiskPipes);
    setText('#ai-burst-count', summary.priorityIncidents?.length || 0);
    setText('#ai-reports-count', summary.reportsToday);
    setText('#ai-confidence-value', summary.priorityIncidents?.length
      ? `${Math.round(summary.priorityIncidents[0].confidence * 100)}%` : '0%');
    setText('#notification-feed', summary.recentAlerts?.[0]?.message || 'No new alerts.');

    const pipeByCode = Object.fromEntries(pipes.map(pipe => [pipe.code, pipe]));
    document.querySelectorAll('[data-pipe]').forEach(marker => {
      const pipe = pipeByCode[marker.dataset.pipe];
      if (pipe) marker.title = `${pipe.code}: ${pipe.currentRiskLevel || 'LOW'} risk`;
    });

    const reportList = document.querySelector('#reports .report');
    if (reportList && reports.content?.length) {
      const reportsContainer = reportList.parentElement;
      reportsContainer.querySelectorAll('.report').forEach(item => item.remove());
      reports.content.slice(0, 5).forEach(report => {
        const item = document.createElement('div');
        item.className = 'report';
        item.innerHTML = `<strong>${report.location}</strong><p>${report.description}</p><p>${report.reference} · ${report.status.replaceAll('_', ' ')}</p>`;
        reportsContainer.appendChild(item);
      });
    }

    const queue = document.querySelector('#maintenance .table tbody');
    if (queue) {
      queue.innerHTML = workOrders.length ? workOrders.map(order =>
        `<tr><td>${order.pipeCode || '-'}</td><td>${order.incidentReference || '-'}</td><td>${order.assignedTeam}</td><td>${order.status.replaceAll('_', ' ')}</td></tr>`
      ).join('') : '<tr><td colspan="4">No maintenance work orders.</td></tr>';
    }

    const activeIncident = incidents.content?.find(incident => incident.status !== 'RESOLVED' && incident.status !== 'CLOSED');
    const dispatchButton = document.getElementById('dispatch');
    if (dispatchButton && activeIncident) {
      dispatchButton.onclick = async () => {
        dispatchButton.disabled = true;
        try {
          await api.request(`/api/v1/incidents/${encodeURIComponent(activeIncident.reference)}/dispatch`, {
            method: 'POST',
            body: JSON.stringify({ assignedTeam: 'Municipal response team', notes: 'Dispatched from the live dashboard.' })
          });
          dispatchButton.textContent = 'Team assigned';
        } catch (error) {
          dispatchButton.disabled = false;
          dispatchButton.textContent = error.message;
        }
      };
    }
  } catch (error) {
    console.warn('Live dashboard data unavailable:', error.message);
  }
});
