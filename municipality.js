document.addEventListener('DOMContentLoaded', async () => {
  const api = window.HlokomelaAPI;
  if (!api?.isAuthenticated()) return;
  const user = api.currentUser();
  if (!user || !['MUNICIPAL_OPERATOR', 'ADMIN'].includes(user.role)) {
    window.location.replace('municipality-login.html');
    return;
  }

  const setText = (selector, value) => {
    const element = document.querySelector(selector);
    if (element && value !== undefined && value !== null) element.textContent = value;
  };

  async function refreshDashboard() {
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

    const priority = summary.priorityIncidents?.[0];
    if (priority) {
      setText('#recommendation-title', `${priority.recommendedAction} (${priority.pipeCode || priority.reference})`);
      setText('#recommendation-risk', priority.riskLevel);
      setText('#recommendation-loss', `${Math.round(priority.estimatedWaterLossLitres).toLocaleString()} L`);
      setText('#recommendation-window', priority.riskLevel === 'CRITICAL' ? 'Immediately' : priority.riskLevel === 'HIGH' ? 'Within 4 hours' : 'Within 24 hours');
      setText('#recommendation-reason', priority.description);
    }

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
        item.innerHTML = `<strong>${report.location}</strong><p>${report.description}</p><p>${report.reference} · <span class="report-status">${report.status.replaceAll('_', ' ')}</span></p><div class="report-actions"><button type="button" class="tiny-btn report-action" data-status="ASSIGNED" data-report-ref="${report.reference}">Assign</button><button type="button" class="tiny-btn report-action" data-status="IN_PROGRESS" data-report-ref="${report.reference}">Investigating</button><button type="button" class="tiny-btn report-action" data-status="RESOLVED" data-report-ref="${report.reference}">Resolved</button></div>`;
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
  }

  await refreshDashboard();
  window.setInterval(refreshDashboard, 15000);

  document.getElementById('generate-report')?.addEventListener('click', () => {
    const report = [
      'Hlokomela AI Weekly Water Intelligence Report',
      `Generated: ${new Date().toISOString()}`,
      `High-risk pipes: ${document.getElementById('ai-risk-count')?.textContent || 0}`,
      `Predicted bursts: ${document.getElementById('ai-burst-count')?.textContent || 0}`,
      `Community reports today: ${document.getElementById('ai-reports-count')?.textContent || 0}`
    ].join('\n');
    const link = document.createElement('a');
    link.href = URL.createObjectURL(new Blob([report], { type: 'text/plain' }));
    link.download = 'hlokomela-weekly-report.txt';
    link.click();
    URL.revokeObjectURL(link.href);
  });

  document.getElementById('view-predictions')?.addEventListener('click', () => {
    document.getElementById('map')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    document.querySelector('[data-pipe="P-101"]')?.click();
  });

  document.addEventListener('click', async event => {
    const button = event.target.closest('.report-action');
    if (!button || !button.dataset.reportRef) return;
    button.disabled = true;
    try {
      await api.request(`/api/v1/community-reports/${encodeURIComponent(button.dataset.reportRef)}/status`, {
        method: 'PATCH',
        body: JSON.stringify({ status: button.dataset.status })
      });
      const container = button.closest('.report');
      container?.querySelectorAll('.report-action').forEach(action => { action.style.background = ''; });
      button.style.background = 'var(--mint)';
      const status = container?.querySelector('.report-status');
      if (status) status.textContent = button.dataset.status.replaceAll('_', ' ');
    } catch (error) {
      button.title = error.message;
    } finally {
      button.disabled = false;
    }
  });
});
