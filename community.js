document.addEventListener('DOMContentLoaded', () => {
  const reportModal = document.getElementById('report-modal');
  const consentModal = document.getElementById('consent-modal');
  const consentDetail = document.getElementById('consent-detail');
  const consentLearnMore = document.getElementById('consent-learn-more');
  const consentAgree = document.getElementById('consent-agree');
  const openReport = document.getElementById('open-report');
  const closeReport = document.getElementById('close-report');
  const form = document.getElementById('issue-form');
  const assistantResponse = document.getElementById('assistant-response');
  const assistantInput = document.getElementById('assistant-input');
  const sendAssistant = document.getElementById('send-assistant');
  const voiceButton = document.getElementById('voice-report');
  const note = document.getElementById('form-note');
  const greeting = document.getElementById('community-greeting');

  function hasConsent() {
    try {
      const stored = JSON.parse(localStorage.getItem('hlokomela_consent'));
      return stored && stored.agreed === true;
    } catch {
      return false;
    }
  }

  function showConsentModal() {
    if (consentModal) consentModal.classList.add('open');
  }

  function hideConsentModal() {
    if (consentModal) consentModal.classList.remove('open');
  }

  if (!hasConsent()) {
    showConsentModal();
  }

  consentLearnMore?.addEventListener('click', () => {
    if (consentDetail) {
      const isHidden = consentDetail.hidden;
      consentDetail.hidden = !isHidden;
      const lang = window.HlokomelaAI?.getLanguage() || 'en';
      const labels = { en: ['Learn more', 'Show less'], zu: ['Funda okuningi', 'Funda okuncane'], af: ['Leer meer', 'Minder'] };
      const pair = labels[lang] || labels.en;
      consentLearnMore.textContent = isHidden ? pair[0] : pair[1];
    }
  });

  consentAgree?.addEventListener('click', () => {
    localStorage.setItem('hlokomela_consent', JSON.stringify({ agreed: true, timestamp: new Date().toISOString() }));
    hideConsentModal();
  });

  consentModal?.addEventListener('click', event => {
    if (event.target === consentModal && hasConsent()) {
      hideConsentModal();
    }
  });

  function updateCommunityGreeting() {
    const ai = window.HlokomelaAI;
    const lang = ai ? ai.getLanguage() : 'en';
    const storedName = localStorage.getItem('hlokomela-community-name')?.trim();
    const name = storedName || 'there';
    const greetings = {
      en: `Hello, ${name} 👋`,
      zu: `Sawubona, ${name} 👋`,
      af: `Hallo, ${name} 👋`
    };

    if (greeting) {
      greeting.textContent = greetings[lang] || greetings.en;
    }
  }

  updateCommunityGreeting();

  openReport?.addEventListener('click', () => {
    if (!hasConsent()) {
      showConsentModal();
      return;
    }
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
    const fileInput = document.getElementById('report-file');
    const locationInput = document.getElementById('report-location');
    const descInput = document.getElementById('report-desc');
    const photoName = fileInput?.files?.[0]?.name || '';
    const location = locationInput?.value?.trim() || '';
    const description = descInput?.value?.trim() || '';
    const report = {
      id: `RPT-${Date.now()}`,
      photo: photoName,
      location,
      description,
      timestamp: new Date().toISOString(),
      status: 'Reported'
    };
    const stored = JSON.parse(localStorage.getItem('hlokomela_reports') || '[]');
    stored.unshift(report);
    localStorage.setItem('hlokomela_reports', JSON.stringify(stored.slice(0, 50)));
    const confirmMsg = ai ? ai.translate('report_confirm', lang) : 'Report received — you\'ll be notified as it\'s reviewed.';
    if (note) note.textContent = confirmMsg;
    form.reset();
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
