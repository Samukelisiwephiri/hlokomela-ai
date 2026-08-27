(function () {
  const translations = {
    en: {
      nav_platform: 'Platform',
      nav_how: 'How it works',
      nav_municipalities: 'Municipalities',
      nav_iot: 'Live IoT',
      login_button: 'Login',
      hero_title: 'Stop leaks before they become crises.',
      hero_description: 'Hlokomela AI combines smart pipe monitoring and artificial intelligence to help water teams protect every drop.',
      hero_cta: 'Open command centre',
      hero_secondary: 'Connect live IoT data',
      role_title: 'Welcome to Hlokomela AI',
      role_intro: 'Select your role to continue.',
      role_municipality: 'Municipality',
      role_municipality_desc: 'Full network visibility and incident control',
      role_community: 'Community Member',
      role_community_desc: 'Report issues and track local water updates',
      continue_button: 'Continue',
      community_welcome: 'Hello, User 👋',
      community_subtitle: 'Welcome back. Your water network is being monitored in real time.',
      community_status: 'Live updates',
      card_status_title: 'Water status near you',
      card_status_normal: 'Normal',
      card_status_text: 'No leaks detected nearby.',
      card_report_title: 'Report a water problem',
      card_report_button: '+ Report issue',
      card_ai_title: 'AI assistant',
      assistant_hint: 'Ask the assistant anything about your local water service.',
      card_tips_title: 'Water saving tips',
      card_reports_title: 'My reports',
      card_alerts_title: 'Nearby alerts',
      card_usage_title: 'Community water usage',
      card_points_title: 'Earn eco points',
      report_modal_title: 'Report a problem',
      report_type: 'Problem type',
      report_location: 'Location',
      report_photo: 'Upload photo',
      report_description: 'Description',
      report_submit: 'Submit',
      report_note: 'AI is analysing your report and the municipality will be notified.',
      voice_button: '🎤 Report using your voice',
      chat_placeholder: 'Ask the assistant',
      ai_center_title: '🤖 Hlokomela AI Insights',
      ai_high_risk: 'High Risk Pipes',
      ai_predicted_bursts: 'Predicted Bursts',
      ai_reports_today: 'Community Reports Today',
      ai_confidence: 'AI Confidence',
      ai_priority: 'Recommended Priority',
      ai_generate_report: 'Generate Weekly Report',
      ai_view_predictions: 'View Predictions',
      ai_risk_prediction_title: 'Leak Risk Prediction',
      ai_risk_prediction_text: 'Pipe P-101 has an 87% probability of bursting within the next 48 hours.',
      ai_explanation: 'Reason',
      ai_explanation_points: 'Pressure has fallen 30%; Flow has increased unexpectedly; Strong vibration detected; Similar pattern preceded previous failures',
      ai_recommendation: 'Maintenance Recommendation',
      ai_recommendation_text: 'Replace the damaged pipe section within 24 hours.',
      trend_title: 'This Month',
      trend_water_loss: 'Water Loss',
      trend_burst_pipes: 'Burst Pipes',
      trend_repair_time: 'Average Repair Time',
      trend_highest_area: 'Highest Risk Area',
      notification_title: 'Smart Notification',
      notification_text: 'High risk of a burst pipe on Main Street. Please avoid the area while municipal teams carry out repairs.',
      login_title: 'Welcome back',
      login_intro: 'Sign in to report issues, track alerts, and see local updates.',
      municipality_login_title: 'Command centre login',
      municipality_login_intro: 'Secure sign-in for ward supervisors and municipal operations teams.',
      community_login_title: 'Community access',
      community_login_intro: 'Sign in to report issues, track alerts, and see local updates.',
      incident_reason_burst: 'Large visible crack and rapid water loss detected.',
      incident_reason_low_pressure: 'Pressure drop reported by the community and nearby pipes are unstable.',
      incident_reason_leak: 'Leak reported in a high-traffic area with visible water loss.',
      priority_immediate: 'Immediate response',
      priority_monitor: 'Monitor today',
      priority_schedule: 'Schedule today',
      assistant_pressure: 'A burst pipe has been detected 2.3 km away. Repairs are underway. Estimated restoration: 3 hours.',
      assistant_report: 'Click Report Issue, upload a photo, and share your location. The municipality will receive the report immediately.',
      assistant_save: 'Water early morning or late evening, fix leaking taps promptly, and reuse greywater where appropriate.',
      assistant_fallback: 'I can help with low pressure, repairs, water saving, and reporting issues.',
      login_button_text: 'Login to community dashboard',
      municipality_login_button_text: 'Enter municipality dashboard',
      language_label: 'Language',
      consent_title: 'Before you report an issue',
      consent_body: 'Hlokomela AI collects the details you submit (photo, location, description) to help your municipality respond to water issues faster. Your report may be shared with municipal water teams. We don\u2019t sell your data or share it with third parties outside this purpose. By continuing, you agree to this use of your information, in line with South Africa\u2019s POPIA.',
      consent_learn_more: 'Learn more',
      consent_agree: 'Agree & Continue',
      consent_detail_title: 'Your data, your rights',
      consent_detail_text: 'Hlokomela AI stores your reports for up to 24 months to improve service delivery and detect patterns. After this period, personal data is anonymised.',
      consent_detail_retention: 'Data retention: Report data (including photos and location) is retained for 24 months. Anonymised data may be kept longer for network analysis.',
      consent_detail_deletion: 'Right to deletion: You may request deletion of your personal data at any time by emailing privacy@hlokomela.ai. We will respond within 30 days.',
      consent_detail_contact: 'Contact our Information Officer: privacy@hlokomela.ai for any POPIA-related queries or data access requests.',
      report_urgency_label: 'Urgency',
      report_urgency_text: 'AI will assess urgency after submission',
      report_confirm: 'Report received \u2014 you\u2019ll be notified as it\u2019s reviewed.'
    },
    zu: {
      nav_platform: 'I-Platform',
      nav_how: 'Indlela esebenza ngayo',
      nav_municipalities: 'Omasipala',
      nav_iot: 'I-IoT ephilayo',
      login_button: 'Ngena ngemvume',
      hero_title: 'Vimbela ukuvuza ngaphambi kokuba kube yinkinga.',
      hero_description: 'IHlokomela AI ihlanganisa ukugadwa kwamapayipi namandla obuhlakani ukuze isize amaqembu amanzi avikele wonke amanzi.',
      hero_cta: 'Vula isikhungo sokulawula',
      hero_secondary: 'Xhuma idatha ye-IoT ephilayo',
      role_title: 'Siyakwamukela ku-Hlokomela AI',
      role_intro: 'Khetha indima yakho ukuze uqhubeke.',
      role_municipality: 'Umasipala',
      role_municipality_desc: 'Ukubona okuphelele kwenethiwekhi nokulawulwa kwezimo',
      role_community: 'Ilungu lomphakathi',
      role_community_desc: 'Bika izinkinga ulandelele izibuyekezo zendawo',
      continue_button: 'Qhubeka',
      community_welcome: 'Sawubona, HlokomelaAI 👋',
      community_subtitle: 'Siyakwamukela. Inethiwekhi yakho yamanzi igadwa ngesikhathi sangempela.',
      community_status: 'Izibuyekezo eziphilayo',
      card_status_title: 'Isimo samanzi eduze kwakho',
      card_status_normal: 'Kujwayelekile',
      card_status_text: 'Awekho amanzi avuzayo eduze.',
      card_report_title: 'Bika inkinga yamanzi',
      card_report_button: '+ Bika inkinga',
      card_ai_title: 'Umsizi we-AI',
      assistant_hint: 'Buza umsizi nganoma yikuphi mayelana nensizakalo yamanzi endaweni yakho.',
      card_tips_title: 'Amaphuzu okonga amanzi',
      card_reports_title: 'Izibiko zami',
      card_alerts_title: 'Izexwayiso eziseduze',
      card_usage_title: 'Ukusetshenziswa kwamanzi komphakathi',
      card_points_title: 'Thola amaphuzu e-eco',
      report_modal_title: 'Bika inkinga',
      report_type: 'Uhlobo lwenkinga',
      report_location: 'Indawo',
      report_photo: 'Layisha isithombe',
      report_description: 'Incazelo',
      report_submit: 'Thumela',
      report_note: 'I-AI ihlaziya isibiko sakho futhi umasipala uzokwaziswa.',
      voice_button: '🎤 Bika ngokusebenzisa izwi',
      chat_placeholder: 'Buza umsizi',
      ai_center_title: '🤖 Imininingwane ye-Hlokomela AI',
      ai_high_risk: 'Amapayipi Anengozi Ephezulu',
      ai_predicted_bursts: 'Ukuqhuma Okubaliwe',
      ai_reports_today: 'Izibiko Zomphakathi Namuhla',
      ai_confidence: 'Ukuzethemba kwe-AI',
      ai_priority: 'Ukubeka phambili okukhuthaziwe',
      ai_generate_report: 'Khiqiza Umbiko Wephepha Wavikela',
      ai_view_predictions: 'Buka Ukuqagela',
      ai_risk_prediction_title: 'Ukuqagela Kwezingozi Zokuvuza',
      ai_risk_prediction_text: 'Ipayipi P-101 linethuba elingu-87% lokuqhuma phakathi namahora angu-48 alandelayo.',
      ai_explanation: 'Isizathu',
      ai_explanation_points: 'U pressão uhile 30%; Ukugeleza kukhule ngokungalindelekile; Ukudlidliza okukhulu kutholakale; Iphethini efanayo ibihlelekile ngaphambilini',
      ai_recommendation: 'Isiphakamiso Sokulungisa',
      ai_recommendation_text: 'Shintsha ingxenye yepayipi elonakele phakathi namahora angu-24.',
      trend_title: 'Kule nyanga',
      trend_water_loss: 'Ukulahleka Kwamanzi',
      trend_burst_pipes: 'Amapayipi Aqhume',
      trend_repair_time: 'Isikhathi Sokulungisa Esijwayelekile',
      trend_highest_area: 'Indawo Enengozi Ephezulu',
      notification_title: 'Isaziso Esihlakaniphile',
      notification_text: 'Ingozi ephezulu yokuphuka kwepayipi eMain Street. Sicela ugweme lendawo ngesikhathi amaqembu omasipala elungisa.',
      login_title: 'Siyakwamukela futhi',
      login_intro: 'Ngena ngemvume ukubika izinkinga, ukulandelela izixwayiso, futhi ubone izibuyekezo zendawo.',
      municipality_login_title: 'Ukungena kwesikhungo sokulawula',
      municipality_login_intro: 'Ukungena okuvikelekile kwabalawuli bamaphara namaqembu okusebenza.',
      community_login_title: 'Ukufinyelela komphakathi',
      community_login_intro: 'Ngena ngemvume ukubika izinkinga, ukulandelela izixwayiso, futhi ubone izibuyekezo zendawo.',
      incident_reason_burst: 'Kubonakele ukwephuka okuphawulekayo kanye nokulahleka kwamanzi ngokushesha.',
      incident_reason_low_pressure: 'Ukwehla kwengcindezi kubikiwe futhi amanye amapayipi aseduze awodwa.',
      incident_reason_leak: 'Ukuvuza kubikiwe endaweni enabantu abaningi futhi kukhonjisiwe ukulahleka kwamanzi.',
      priority_immediate: 'Uphendulo olusheshayo',
      priority_monitor: 'Gada namuhla',
      priority_schedule: 'Hlela namuhla',
      assistant_pressure: 'Ipayipi eliqhumile litholakele 2.3 km kude. Ukulungiswa kuyaqhubeka. Isikhathi sokubuyisela: 3 amahora.',
      assistant_report: 'Chofoza u-Bika Inkinga, layisha isithombe, futhi wabelane nendawo. Umasipala uzothola umbiko ngokushesha.',
      assistant_save: 'Nisela ekuseni kakhulu noma ntambama, lungisa amapayipi avuzayo ngokushesha, futhi sebenzisa amanzi aseduze kabusha.',
      assistant_fallback: 'Ngingakusiza mayelana nengcindezi ephansi, ukulungiswa, ukonga amanzi, nokubika izinkinga.',
      login_button_text: 'Ngena kwimodi yomphakathi',
      municipality_login_button_text: 'Faka idashibhohi yomasipala',
      language_label: 'Ulimi',
      consent_title: 'Ngaphambi kokubika inkinga',
      consent_body: 'IHlokomela AI iqoqa imininingwane oyithumelayo (isithombe, indawo, incazelo) ukusiza umasipala wakho uphendule ezinkingeni zamanzi ngokushesha. Isibiko sakho sangashayiselwana namaqembu amanzi omasipala. Asithengisi idatha yakho futhi asiyihlanganisi nabantu abangaphandle kolaka lokhu. Ngiqhubeka, uyavuma ukusetshenziswa kolunye ulwazi lwakho, ngokuhambisana ne-POPIA yaseNingizimu Afrika.',
      consent_learn_more: 'Funda okuningi',
      consent_agree: 'Vumelana & Qhubeka',
      consent_detail_title: 'Idatha yakho, amalungelo akho',
      consent_detail_text: 'IHlokomela AI igcina izibiko zakho iminyaka engu-24 ukuze ithuthukise ukulethwa kwenkonzo futhi ibone izimo. Ngemva kwaleso sikhathi, imininingwane yokubona icutshungulwa.',
      consent_detail_retention: 'Ukugcina idatha: Idatha yesibiko (kufaka isithombe nendawo) igcinwe iminyaka engu-24. Idatha elungisiwe ingagcinwa isikhathi eside ukuze icutshungulwe.',
      consent_detail_deletion: 'Amalungelo okususa: Ungacela ukususwa kwedatha yakho noma nini ngokuthumela i-email ku-privacy@hlokomela.ai. Sizophendula phakathi kwezinsuku ezingu-30.',
      consent_detail_contact: 'Xhumana noMphathi weData: privacy@hlokomela.ai noma yiziphi izibuzo ezihlobene ne-POPIA noma izicelo zokufinyelela kwedatha.',
      report_urgency_label: 'Urgency',
      report_urgency_text: 'I-AI izohlola ubudingo ngemva kokuthunyelwa',
      report_confirm: 'Isibiko samukelwe \u2014 uzokwaziswa uma sihlolwa.'
    },
    af: {
      nav_platform: 'Platform',
      nav_how: 'Hoe dit werk',
      nav_municipalities: 'Munisipaliteite',
      nav_iot: 'Lewende IoT',
      login_button: 'Teken aan',
      hero_title: 'Stop lekke voordat dit krisisse word.',
      hero_description: 'Hlokomela AI kombineer slim pypmonitering en kunsmatige intelligensie om waterspanne te help om elke druppel te beskerm.',
      hero_cta: 'Open bevelsentrum',
      hero_secondary: 'Koppel lewende IoT-data',
      role_title: 'Welkom by Hlokomela AI',
      role_intro: 'Kies jou rol om voort te gaan.',
      role_municipality: 'Munisipaliteit',
      role_municipality_desc: 'Volle netwerkbesigtiging en insidentbeheer',
      role_community: 'Gemeenskapslid',
      role_community_desc: 'Rapporteer probleme en volg plaaslike opdaterings',
      continue_button: 'Gaan voort',
      community_welcome: 'Hallo, HlokomelaAI 👋',
      community_subtitle: 'Welkom terug. Jou waternetwerk word intyds gemonitor.',
      community_status: 'Lewende opdaterings',
      card_status_title: 'Waterstatus naby jou',
      card_status_normal: 'Normaal',
      card_status_text: 'Geen lekkasies naby nie.',
      card_report_title: 'Rapporteer ’n waterprobleem',
      card_report_button: '+ Rapporteer probleem',
      card_ai_title: 'KI-assistent',
      assistant_hint: 'Vra die assistent enigiets oor jou plaaslike watersdiens.',
      card_tips_title: 'Waterbesparingstips',
      card_reports_title: 'My verslae',
      card_alerts_title: 'Naby waarskuwings',
      card_usage_title: 'Gemeenskapswaterverbruik',
      card_points_title: 'Verdien ekopunte',
      report_modal_title: 'Rapporteer ’n probleem',
      report_type: 'Probleemtipe',
      report_location: 'Ligging',
      report_photo: 'Laai foto op',
      report_description: 'Beskrywing',
      report_submit: 'Stuur',
      report_note: 'KI ontleed jou verslag en die munisipaliteit sal onmiddellik in kennis gestel word.',
      voice_button: '🎤 Rapporteer met jou stem',
      chat_placeholder: 'Vra die assistent',
      ai_center_title: '🤖 Hlokomela KI-insigte',
      ai_high_risk: 'Hoë-risiko-pype',
      ai_predicted_bursts: 'Voorspelde bars',
      ai_reports_today: 'Gemeenskapsverslae vandag',
      ai_confidence: 'KI-selfvertroue',
      ai_priority: 'Aanbevole prioriteit',
      ai_generate_report: 'Genereer weeklikse verslag',
      ai_view_predictions: 'Bekyk voorspellings',
      ai_risk_prediction_title: 'Lekrisikovoorspelling',
      ai_risk_prediction_text: 'Pyp P-101 het ’n 87% kans om binne die volgende 48 uur te bars.',
      ai_explanation: 'Rede',
      ai_explanation_points: 'Druk het met 30% gedaal; vloei het onverwagtes toegeneem; sterk vibrasie waargeneem; soortgelyke patroon het vorige foute voorafgegaan',
      ai_recommendation: 'Onderhoudsaanbeveling',
      ai_recommendation_text: 'Vervang die beskadigde pypgedeelte binne 24 uur.',
      trend_title: 'Hierdie maand',
      trend_water_loss: 'Waterverlies',
      trend_burst_pipes: 'Barste pype',
      trend_repair_time: 'Gemiddelde hersteltyd',
      trend_highest_area: 'Hoogste risiko-area',
      notification_title: 'Slim kennisgewing',
      notification_text: 'Hoë risiko van ’n bars in die hoofstraat. Verduidelik die gebied terwyl munisipale spanne herstelwerk doen.',
      login_title: 'Welkom terug',
      login_intro: 'Teken aan om probleme te rapporteer, waarskuwings te volg en plaaslike opdaterings te sien.',
      municipality_login_title: 'Bevelsentrum-aanmelding',
      municipality_login_intro: 'Veilige aanmelding vir gebiedsopsieners en munisipale bedryfslede.',
      community_login_title: 'Gemeenskaps toegang',
      community_login_intro: 'Teken aan om probleme te rapporteer, waarskuwings te volg en plaaslike opdaterings te sien.',
      incident_reason_burst: 'Sienbare bars en vinnige waterverlies waargeneem.',
      incident_reason_low_pressure: 'Drukval gerapporteer en aangrensende pype is onstabiel.',
      incident_reason_leak: 'Lek gerapporteer in ’n gebied met hoë verkeer en sigbare waterverlies.',
      priority_immediate: 'Onmiddellike reaksie',
      priority_monitor: 'Monitor vandag',
      priority_schedule: 'Skeduleer vandag',
      assistant_pressure: '’n Bars pyp is 2.3 km weg opgespoor. Herstelwerk is aan die gang. Geskatte hersteltyd: 3 uur.',
      assistant_report: 'Klik Rapporteer probleem, laai ’n foto op en deel jou ligging. Die munisipaliteit sal die verslag onmiddellik ontvang.',
      assistant_save: 'Water vroegoggend of laatmiddag, herstel lekkende krane onmiddellik, en hergebruik gryswater waar moontlik.',
      assistant_fallback: 'Ek kan help met lae druk, herstelwerk, watersbesparing en probleemrapportering.',
      login_button_text: 'Teken aan tot gemeenskapsdashboard',
      municipality_login_button_text: 'Betree munisipale dashboard',
      language_label: 'Taal',
      consent_title: 'Voordat jy \'n probleem rapporteer',
      consent_body: 'Hlokomela AI versamel die besonderhede wat jy indien (foto, ligging, beskrywing) om jou munisipaliteit te help om vinniger op waterprobleme te reageer. Jou verslag mag met munisipale waterspanne gedeel word. Ons verkoop nie jou data nie en deel dit nie met derde partye buiten hierdie doel nie. Deur voort te gaan, stem jy in met hierdie gebruik van jou inligting, in lyn met Suid-Afrika se POPIA.',
      consent_learn_more: 'Leer meer',
      consent_agree: 'Stem in & Gaan voort',
      consent_detail_title: 'Jou data, jou regte',
      consent_detail_text: 'Hlokomela AI stoor jou verslae vir tot 24 maande om dienslewering te verbeter en patrone op te spoor. Na hierdie tydperk word persoonlike data geanonimiseer.',
      consent_detail_retention: 'Data-berging: Verslagdata (insluitend foto\'s en ligging) word vir 24 maande gestoor. Geanonimiseerde data mag langer bewaar word vir netwerkanalise.',
      consent_detail_deletion: 'Reg tot verwydering: Jy mag enige tyd verwydering van jou persoonlike data versoek deur \'n e-pos te stuur na privacy@hlokomela.ai. Ons sal binne 30 dae reageer.',
      consent_detail_contact: 'Kontak ons Inligtingsbeampte: privacy@hlokomela.ai vir enige POPIA-verwante navrae of datatoegangversoeke.',
      report_urgency_label: 'Dringendheid',
      report_urgency_text: 'KI sal dringendheid assesseer na indiening',
      report_confirm: 'Verslag ontvang \u2014 jy sal in kennis gestel word sodra dit hersien word.'
    }
  };

  const defaultLanguage = 'en';

  function getLanguage() {
    return localStorage.getItem('hlokomela-language') || defaultLanguage;
  }

  function setLanguage(lang) {
    const next = translations[lang] ? lang : defaultLanguage;
    localStorage.setItem('hlokomela-language', next);
    applyTranslations(next);
    return next;
  }

  function translate(key, lang) {
    const dictionary = translations[lang] || translations[defaultLanguage];
    return dictionary[key] || translations[defaultLanguage][key] || key;
  }

  function applyTranslations(lang) {
    document.querySelectorAll('[data-i18n]').forEach((element) => {
      const key = element.getAttribute('data-i18n');
      const value = translate(key, lang);
      if (value) {
        if (element.tagName === 'INPUT' || element.tagName === 'TEXTAREA' || element.tagName === 'SELECT') {
          element.placeholder = value;
        } else if (element.children.length > 0) {
          const textNode = [...element.childNodes].find(node =>
            node.nodeType === Node.TEXT_NODE && node.textContent.trim());
          if (textNode) textNode.textContent = value;
        } else {
          element.textContent = value;
        }
      }
    });
    document.querySelectorAll('[data-i18n-html]').forEach((element) => {
      const key = element.getAttribute('data-i18n-html');
      const value = translate(key, lang);
      if (value) {
        element.innerHTML = value;
      }
    });
    const selector = document.getElementById('language-selector');
    if (selector) {
      selector.value = lang;
    }
  }

  function createIncidentReport(text, lang) {
    const cleanText = text || '';
    const lowered = cleanText.toLowerCase();
    let severity = 'Medium';
    let reasonKey = 'incident_reason_leak';
    let priorityKey = 'priority_monitor';

    if (lowered.includes('burst') || lowered.includes('qhuma') || lowered.includes('break') || lowered.includes('phatlohile')) {
      severity = 'High';
      reasonKey = 'incident_reason_burst';
      priorityKey = 'priority_immediate';
    } else if (lowered.includes('pressure') || lowered.includes('pressure')) {
      severity = 'Medium';
      reasonKey = 'incident_reason_low_pressure';
      priorityKey = 'priority_monitor';
    }

    const id = `#${Math.floor(1000 + Math.random() * 9000)}`;
    return {
      id,
      text: cleanText,
      severity,
      reason: translate(reasonKey, lang),
      priority: translate(priorityKey, lang),
      language: lang,
      createdAt: new Date().toLocaleString()
    };
  }

  function getStoredReports() {
    try {
      return JSON.parse(localStorage.getItem('hlokomela-reports') || '[]');
    } catch (error) {
      return [];
    }
  }

  function saveReport(report) {
    const reports = getStoredReports();
    reports.unshift(report);
    localStorage.setItem('hlokomela-reports', JSON.stringify(reports.slice(0, 12)));
    return reports;
  }

  function getAiAssistantReply(question, lang) {
    const value = (question || '').toLowerCase();
    if (value.includes('pressure') || value.includes('amanzi')) {
      return translate('assistant_pressure', lang);
    }
    if (value.includes('report') || value.includes('bika') || value.includes('rapport')) {
      return translate('assistant_report', lang);
    }
    if (value.includes('fix') || value.includes('repair') || value.includes('today') || value.includes('lungisa')) {
      return 'A repair team will assess the incident based on its risk level. Check the latest municipal update for the expected restoration time.';
    }
    if (value.includes('save') || value.includes('water') || value.includes('ong')) {
      return translate('assistant_save', lang);
    }
    return translate('assistant_fallback', lang);
  }

  function renderMunicipalityInsights(lang) {
    const reports = getStoredReports();
    const highRiskCount = Math.min(12, 8 + reports.filter((report) => report.severity === 'High').length);
    const predictedBursts = Math.min(5, 2 + reports.filter((report) => report.severity === 'High').length);
    const todayReports = reports.length || 24;
    const confidence = 96;

    const riskValue = document.getElementById('ai-risk-count');
    if (riskValue) riskValue.textContent = highRiskCount;

    const burstValue = document.getElementById('ai-burst-count');
    if (burstValue) burstValue.textContent = predictedBursts;

    const reportsValue = document.getElementById('ai-reports-count');
    if (reportsValue) reportsValue.textContent = todayReports;

    const confidenceValue = document.getElementById('ai-confidence-value');
    if (confidenceValue) confidenceValue.textContent = `${confidence}%`;

    const priorityList = document.getElementById('ai-priority-list');
    if (priorityList) {
      priorityList.innerHTML = ['Main Street', 'Clinic Road', 'Mamelodi East', 'Water Tower'].map((item) => `<li>${item}</li>`).join('');
    }

    const notificationFeed = document.getElementById('notification-feed');
    if (notificationFeed) {
      notificationFeed.innerHTML = '';
      const latest = reports[0];
      if (latest) {
        notificationFeed.innerHTML = `
          <div class="ai-notification">
            <strong>${translate('notification_title', lang)}</strong>
            <p>${latest.severity === 'High' ? `${translate('notification_text', lang)} (${latest.id})` : translate('notification_text', lang)}</p>
          </div>`;
      } else {
        notificationFeed.innerHTML = `<div class="ai-notification"><strong>${translate('notification_title', lang)}</strong><p>${translate('notification_text', lang)}</p></div>`;
      }
    }

    const incidentFeed = document.getElementById('incident-feed');
    if (incidentFeed) {
      incidentFeed.innerHTML = '';
      const feedReports = reports.slice(0, 4);
      feedReports.forEach((report) => {
        const item = document.createElement('div');
        item.className = 'incident-item';
        item.innerHTML = `<strong>${report.id}</strong><span>${report.severity}</span><p>${report.reason}</p>`;
        incidentFeed.appendChild(item);
      });
    }
  }

  function initialiseLanguageControls() {
    const selector = document.getElementById('language-selector');
    if (selector) {
      selector.addEventListener('change', (event) => {
        setLanguage(event.target.value);
      });
    }

    const currentLanguage = getLanguage();
    applyTranslations(currentLanguage);
  }

  document.addEventListener('DOMContentLoaded', () => {
    initialiseLanguageControls();
    renderMunicipalityInsights(getLanguage());
  });

  window.HlokomelaAI = {
    setLanguage,
    applyTranslations,
    createIncidentReport,
    saveReport,
    getStoredReports,
    getAiAssistantReply,
    renderMunicipalityInsights,
    getLanguage,
    translate
  };
})();
