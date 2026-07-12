document.addEventListener('DOMContentLoaded', () => {
  const reportModal = document.getElementById('report-modal');
  const openReport = document.getElementById('open-report');
  const closeReport = document.getElementById('close-report');
  const form = document.getElementById('issue-form');
  const assistantResponse = document.getElementById('assistant-response');
  const assistantInput = document.getElementById('assistant-input');
  const sendAssistant = document.getElementById('send-assistant');
  const voiceButton = document.getElementById('voice-report');
  const note = document.getElementById('form-note');

  openReport?.addEventListener('click', () => {
    reportModal.classList.add('open');
  });

  closeReport?.addEventListener('click', () => {
    reportModal.classList.remove('open');
  });

  reportModal?.addEventListener('click', event => {
    if (event.target === reportModal) {
      reportModal.classList.remove('open');
    }
  });

  form?.addEventListener('submit', event => {
    event.preventDefault();
    const ai = window.HlokomelaAI;
    const lang = ai ? ai.getLanguage() : 'en';
    const reportText = form.querySelector('textarea')?.value || 'Leak reported';
    const incident = ai ? ai.createIncidentReport(reportText, lang) : { id: '#458', severity: 'High', reason: 'Leak reported', priority: 'Immediate response' };
    const message = `${incident.id} · ${incident.severity} · ${incident.priority}`;
    if (note) note.textContent = message;
    if (ai) ai.saveReport(incident);
    if (ai) ai.renderMunicipalityInsights(lang);
    reportModal.classList.remove('open');
  });

  function handleAssistantReply(question) {
    const ai = window.HlokomelaAI;
    const lang = ai ? ai.getLanguage() : 'en';
    const response = ai ? ai.getAiAssistantReply(question, lang) : question;
    if (assistantResponse) assistantResponse.textContent = response;
  }

  sendAssistant?.addEventListener('click', () => {
    const question = assistantInput?.value?.trim();
    if (question) handleAssistantReply(question);
  });

  assistantInput?.addEventListener('keydown', (event) => {
    if (event.key === 'Enter') {
      const question = assistantInput.value.trim();
      if (question) handleAssistantReply(question);
    }
  });

  document.querySelectorAll('.prompt-chip').forEach(button => {
    button.addEventListener('click', () => {
      handleAssistantReply(button.textContent);
    });
  });

  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (SpeechRecognition && voiceButton) {
    const recognition = new SpeechRecognition();
    recognition.lang = 'en-ZA';
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;
    recognition.onresult = (event) => {
      const transcript = event.results[0][0].transcript;
      if (assistantInput) assistantInput.value = transcript;
      handleAssistantReply(transcript);
    };
    recognition.onerror = () => {
      if (assistantResponse) assistantResponse.textContent = 'Voice input is unavailable right now.';
    };
    voiceButton.addEventListener('click', () => recognition.start());
  }
});
