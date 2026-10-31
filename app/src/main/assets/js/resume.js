/* Open Bazar — Professional Resume & Profile PDF Generator for Recruiters
 * Candidate: Surya Kafle (suryakafle860@gmail.com)
 * Generates clean, publication-grade PDF version of professional profile
 */
(function() {
  const isNe = () => (window.DB && DB.lang) === 'ne';
  const esc = s => String(s == null ? '' : s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));

  const PROFILE = {
    name: "Surya Kafle",
    nameNp: "सूर्य काफ्ले",
    title: "Lead Full-Stack Architect & Senior Mobile/Web Engineer",
    titleNp: "प्रमुख फुल-स्ट्याक आर्किटेक्ट तथा सिनियर मोबाइल/वेब इन्जिनियर",
    email: "suryakafle860@gmail.com",
    phone: "+977 9800000000",
    location: "Kathmandu, Nepal (Available for Global Remote & Relocation)",
    locationNp: "काठमाडौं, नेपाल (विश्वव्यापी रिमोट तथा अनसाइट भूमिकाका लागि उपलब्ध)",
    summary: "Senior Full-Stack Software Engineer and System Architect with over 7 years of specialized expertise across native Android (Kotlin), modern web applications (ES6+, React, PWA, D3.js), and resilient cloud backends (Node.js, Firebase Firestore). Architect of the Open Bazar commerce ecosystem featuring real-time WebRTC audio/video communications with Metered.ca TURN relays, zero-trust cryptographic security, and offline-first synchronization.",
    summaryNp: "७ वर्षभन्दा बढीको अनुभव भएका सिनियर फुल-स्ट्याक इन्जिनियर तथा सिस्टम आर्किटेक्ट। नेटिभ एन्ड्रोइड (Kotlin), आधुनिक वेब प्रविधि (ES6+, React, PWA, D3.js), र सुरक्षित क्लाउड ब्याकइन्ड (Node.js, Firebase) मा विशेष दक्षता। Metered.ca TURN रिलेमार्फत रियल-टाइम WebRTC अडियो/भिडियो कलिङ, शून्य-विश्वास इन्क्रिप्सन र अफलाइन-फर्स्ट संरचना भएको 'ओपन बजार' इकोसिस्टमका मुख्य आर्किटेक्ट।",
    skills: [
      {
        domain: "Cloud Architecture",
        score: "96%",
        highlights: "Firebase Firestore, Node.js, Express, Cloud Run, Scalable Microservices, Zero-Trust Rules, Serverless RESTful APIs, Secure Cloud Storage"
      },
      {
        domain: "Mobile Development",
        score: "98%",
        highlights: "Android Kotlin, Jetpack Compose, Native MediaStore, WebRTC Audio/Video, PWA Service Workers, M3 Adaptive Layouts, Offline Sync"
      },
      {
        domain: "AI Integration",
        score: "95%",
        highlights: "Firebase AI Logic, Gemini Multimodal API, OCR Document Parsing, Audio Spectrum Analysis, AI Chat Assistants, Semantic Product Search"
      },
      {
        domain: "Frontend Engineering",
        score: "96%",
        highlights: "Modern ES6+ JavaScript, D3.js v7 Interactive Visualizations, HTML5 Canvas, Reactive UI State, WCAG 2.1 AA Accessibility, CSS Theme Engine"
      },
      {
        domain: "System Architecture",
        score: "94%",
        highlights: "Clean Architecture / MVVM, Offline-First Local DB Sync, Native Android Java/Kotlin Bridge, Modular Services, Fault-Tolerant State Machines"
      },
      {
        domain: "Real-Time & WebRTC",
        score: "93%",
        highlights: "Metered.ca WebRTC Calling, TURN/STUN Relay traversal over UDP & TCP/TLS, Real-time Audio Spectrum Analysers, Live WebSocket Sync"
      }
    ],
    experience: [
      {
        role: "Lead Full-Stack Architect & Mobile Systems Engineer",
        company: "Open Bazar Ecosystem",
        period: "2023 – Present",
        location: "Kathmandu, Nepal",
        points: [
          "Architected and deployed complete hyperlocal e-commerce ecosystem serving merchants and customers across all 7 provinces of Nepal.",
          "Integrated real-time WebRTC audio and video calling powered by Metered.ca enterprise TURN relays (relay.metered.ca:443) with acoustic audio analyzers.",
          "Engineered offline-first PWA caching and native Android bridge with zero-trust local database security and automated PDF invoice generation.",
          "Implemented interactive D3.js technical radar charts, revenue analytics, Khata ledgers, and automated email inquiry gateways."
        ]
      },
      {
        role: "Senior Software Engineer (Web & Mobile)",
        company: "Enterprise Cloud & Software Solutions",
        period: "2020 – 2023",
        location: "Remote / Hybrid",
        points: [
          "Developed high-throughput RESTful services and responsive single-page web applications utilizing modern JavaScript and Node.js.",
          "Optimized front-end rendering performance, achieving 98+ Google Lighthouse scores across PWA mobile workflows.",
          "Implemented multi-factor authentication (2FA), biometric credential APIs, and automated backup/restore export formats."
        ]
      }
    ],
    education: [
      {
        degree: "Bachelor of Engineering in Computer Engineering / Information Technology",
        institution: "Tribhuvan University (IOE)",
        year: "Graduated with Distinction"
      }
    ],
    certifications: [
      "Android Certified Application Developer",
      "Google Cloud & Firebase Certified Solutions Architecture",
      "WebRTC & Real-Time Communications Specialist"
    ]
  };

  // Launch Resume Preview Modal with direct PDF Print & Download options
  window.downloadResumePDF = function() {
    const ne = isNe();
    const modalId = 'resume_preview_modal';
    let modal = document.getElementById(modalId);
    if (modal) modal.remove();

    modal = document.createElement('div');
    modal.id = modalId;
    modal.className = 'resume-modal-overlay';

    modal.innerHTML = `
      <div class="resume-modal-card">
        <!-- Action Toolbar -->
        <div class="resume-modal-toolbar noprint">
          <div style="display:flex;align-items:center;gap:8px">
            <span style="font-size:22px">📄</span>
            <div>
              <b style="font-size:16px">${ne ? 'व्यावसायिक रेजुमे (CV)' : 'Professional Resume'}</b>
              <div style="font-size:11.5px;color:var(--mu,#64748B)">PDF Version for Potential Recruiters</div>
            </div>
          </div>
          <div style="display:flex;gap:8px;align-items:center">
            <button class="btn s" onclick="window.printResumePDF()" title="${ne ? 'PDF का रूपमा सेभ / प्रिन्ट गर्नुहोस्' : 'Save as PDF / Print'}">
              🖨️ ${ne ? 'PDF सेभ / प्रिन्ट' : 'Save PDF'}
            </button>
            <button class="btn j" onclick="window.exportResumeAsDocument()" title="${ne ? 'कागजात डाउनलोड' : 'Download Document'}">
              ⬇ ${ne ? 'डाउनलोड' : 'Download'}
            </button>
            <button class="btn g sm" onclick="document.getElementById('${modalId}').remove()" title="Close">
              ✕
            </button>
          </div>
        </div>

        <!-- Scrollable Printable Document Container -->
        <div class="resume-doc-wrap" id="resume_doc_container">
          <div class="resume-paper" id="resume_printable_document">
            <!-- Header Section -->
            <div class="res-header">
              <div class="res-title-col">
                <h1 class="res-name">${PROFILE.name}</h1>
                <div class="res-subtitle">${PROFILE.title}</div>
                <div class="res-contact-row">
                  <span>✉️ <a href="mailto:${PROFILE.email}">${PROFILE.email}</a></span>
                  <span>📍 ${PROFILE.location}</span>
                  <span>📱 Portfolio: Open Bazar System</span>
                </div>
              </div>
            </div>

            <!-- Executive Summary -->
            <div class="res-section">
              <h2 class="res-sec-head">Executive Summary</h2>
              <p class="res-p">
                ${PROFILE.summary}
              </p>
            </div>

            <!-- Technical Proficiency (D3 Radar Verified) -->
            <div class="res-section">
              <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:10px">
                <h2 class="res-sec-head" style="margin:0">${ne ? 'प्राविधिक दक्षता तथा D3 रडार चार्ट' : 'Technical Proficiency & D3 Radar Chart'}</h2>
                <button class="btn s sm noprint" onclick="window.openRadarChart ? window.openRadarChart() : 0" style="padding:4px 10px;font-size:12px;display:flex;align-items:center;gap:5px;cursor:pointer">
                  <span>🕸️</span> <span>${ne ? 'पूर्ण रडार' : 'Full Radar'}</span>
                </button>
              </div>

              <!-- Interactive D3 Skill Radar Chart Canvas -->
              <div id="resume_radar_chart_container" class="noprint" style="background:var(--bg,#F8FAFC);border:1px solid var(--ln,#E2E8F0);border-radius:14px;padding:12px 8px;margin-bottom:14px;min-height:280px;display:flex;justify-content:center;"></div>

              <div class="res-skills-grid">
                ${PROFILE.skills.map(s => `
                  <div class="res-skill-card">
                    <div class="res-skill-top">
                      <b>${esc(s.domain)}</b>
                      <span class="res-score-badge">${s.score}</span>
                    </div>
                    <div class="res-skill-body">${esc(s.highlights)}</div>
                  </div>
                `).join('')}
              </div>
            </div>

            <!-- Real-time GitHub Activity Stats Section -->
            <div class="res-section noprint">
              <h2 class="res-sec-head">${ne ? 'गिटहब प्रत्यक्ष गतिविधि तथा कोड तथ्याङ्क' : 'GitHub Real-Time Activity & Repository Stats'}</h2>
              <div id="resume_github_stats_container"></div>
            </div>

            <!-- Aajako Bazar Developmental History & Strategic Roadmap (D3.js Timeline) -->
            <div class="res-section">
              <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:10px">
                <div>
                  <h2 class="res-sec-head" style="margin:0">${ne ? 'परियोजना विकास इतिहास र रणनीतिक मार्गचित्र (D3.js Timeline)' : 'Project Developmental History & Strategic Roadmap (D3.js)'}</h2>
                  <div style="font-size:11.5px;color:var(--mu,#64748B);margin-top:2px">${ne ? 'आजको बजारको २०२३ देखि २०२७+ सम्मको इन्जिनियरिङ इतिहास र आगामी माइलस्टोन' : 'Aajako Bazar engineering journey from 2023 genesis to 2027+ decentralized mesh'}</div>
                </div>
                <button class="btn s sm noprint" onclick="window.openProjectTimelineModal ? window.openProjectTimelineModal() : 0" style="padding:4px 10px;font-size:12px;display:flex;align-items:center;gap:5px;cursor:pointer">
                  <span>🚀</span> <span>${ne ? 'पूर्ण स्क्रिन' : 'Fullscreen'}</span>
                </button>
              </div>

              <!-- Interactive D3 Project Timeline Canvas -->
              <div id="resume_timeline_chart_container" class="noprint" style="background:#121E34;border:1px solid #1E2D4A;border-radius:14px;padding:12px 8px;margin-bottom:14px;min-height:340px;display:flex;justify-content:center;overflow:hidden;position:relative;"></div>
            </div>

            <!-- Professional Experience -->
            <div class="res-section">
              <h2 class="res-sec-head">Professional Experience</h2>
              ${PROFILE.experience.map(exp => `
                <div class="res-exp-block">
                  <div class="res-exp-head">
                    <div>
                      <b class="res-exp-role">${esc(exp.role)}</b>
                      <div class="res-exp-company">${esc(exp.company)} · ${esc(exp.location)}</div>
                    </div>
                    <span class="res-exp-period">${esc(exp.period)}</span>
                  </div>
                  <ul class="res-exp-list">
                    ${exp.points.map(pt => `<li>${esc(pt)}</li>`).join('')}
                  </ul>
                </div>
              `).join('')}
            </div>

            <!-- Education & Certifications -->
            <div class="res-section">
              <div class="res-two-col">
                <div>
                  <h2 class="res-sec-head">Education</h2>
                  ${PROFILE.education.map(edu => `
                    <div style="margin-bottom:6px">
                      <b>${esc(edu.degree)}</b>
                      <div style="font-size:12.5px;color:#555">${esc(edu.institution)} (${esc(edu.year)})</div>
                    </div>
                  `).join('')}
                </div>
                <div>
                  <h2 class="res-sec-head">Certifications & Accreditations</h2>
                  <ul style="margin:0;padding-left:18px;font-size:13px;line-height:1.5">
                    ${PROFILE.certifications.map(c => `<li>${esc(c)}</li>`).join('')}
                  </ul>
                </div>
              </div>
            </div>

            <!-- Direct Recruiter Inquiries Note -->
            <div class="res-footer-note">
              <div>Interested in discussing leadership or senior software engineering roles?</div>
              <div>Direct Inquiries: <a href="mailto:${PROFILE.email}?subject=Recruitment%20Inquiry%20for%20Surya%20Kafle" style="color:var(--ind,#0F4C9A);font-weight:700">${PROFILE.email}</a></div>
            </div>
          </div>

          <!-- Embedded Recruiter & Client Inquiry Form -->
          <div class="c noprint" style="margin-top:16px;background:var(--card,#fff);border:1px solid var(--ln);border-radius:14px;padding:18px" id="res_inquiry_section">
            <h3 style="margin:0 0 6px;font-size:16px;color:var(--ind,#0F4C9A);display:flex;align-items:center;gap:8px">
              <span>✉️</span>
              <span>${ne ? 'सूर्य काफ्लेलाई सिधै सोधपुछ / सन्देश पठाउनुहोस्' : 'Send Direct Inquiry to Surya Kafle'}</span>
            </h3>
            <p style="font-size:13px;color:var(--mu,#64748B);margin:0 0 12px">
              ${ne ? 'सफ्टवेयर इन्जिनियरिङ, कन्सल्टिङ वा साझेदारीका लागि suryakafle860@gmail.com मा सम्पर्क गर्नुहोस्:' : 'Inquire about senior engineering roles, consulting, or technical advisory:'}
            </p>
            <div style="display:flex;flex-direction:column;gap:8px">
              <input class="inp" id="res_inq_name" type="text" placeholder="${ne ? 'तपाईंको नाम वा संस्था *' : 'Your Name / Organization *'}" style="margin:0">
              <input class="inp" id="res_inq_email" type="email" placeholder="${ne ? 'तपाईंको इमेल ठेगाना *' : 'Your Email Address *'}" style="margin:0">
              <input class="inp" id="res_inq_subj" type="text" placeholder="${ne ? 'विषय (उदा: Senior Role / Project Inquiry) *' : 'Subject (e.g. Senior Role / Project Inquiry) *'}" style="margin:0">
              <textarea class="inp" id="res_inq_msg" rows="3" placeholder="${ne ? 'तपाईंको सन्देश वा प्रस्ताव कम्तिमा १० अक्षरमा... *' : 'Your proposal or message details (at least 10 characters)... *'}" style="margin:0;line-height:1.4"></textarea>
              <div id="res_inq_err" style="color:var(--crim,#D62828);font-size:12px;display:none;margin-top:2px"></div>
              <button class="btn s" onclick="window.submitResumeInquiry()" style="padding:11px;font-weight:700;display:flex;align-items:center;justify-content:center;gap:6px">
                🚀 ${ne ? 'सोधपुछ पठाउनुहोस् (Send to suryakafle860@gmail.com)' : 'Send Inquiry to suryakafle860@gmail.com'}
              </button>
            </div>
          </div>
        </div>

        <!-- Bottom Mobile Action Bar -->
        <div class="resume-bottom-bar noprint">
          <button class="btn s" style="flex:1" onclick="window.printResumePDF()">
            🖨️ ${ne ? 'PDF सेभ / प्रिन्ट गर्नुहोस्' : 'Save / Print as PDF'}
          </button>
          <button class="btn g" style="flex:1" onclick="var e=document.getElementById('res_inquiry_section');if(e)e.scrollIntoView({behavior:'smooth'})">
            ✉️ ${ne ? 'सोधपुछ फारम (Contact)' : 'Contact Form'}
          </button>
        </div>
      </div>
    `;

    document.body.appendChild(modal);

      // Auto-render interactive D3 Skill Radar Chart, Timeline, & GitHub Stats
      setTimeout(() => {
        if (typeof window.renderSkillRadarChart === 'function') {
          window.renderSkillRadarChart('resume_radar_chart_container', {
            compact: true,
            hideList: true,
            lang: ne ? 'ne' : 'en'
          });
        }
        if (typeof window.renderProjectTimeline === 'function') {
          window.renderProjectTimeline('resume_timeline_chart_container', {
            compact: true,
            width: 760,
            height: 320,
            lang: ne ? 'ne' : 'en'
          });
        }
        if (typeof window.renderGitHubStatsSection === 'function') {
          window.renderGitHubStatsSection('resume_github_stats_container');
        }
      }, 60);
  };

  window.submitResumeInquiry = function() {
    const ne = isNe();
    const nameEl = document.getElementById('res_inq_name');
    const emailEl = document.getElementById('res_inq_email');
    const subjEl = document.getElementById('res_inq_subj');
    const msgEl = document.getElementById('res_inq_msg');
    const errEl = document.getElementById('res_inq_err');

    const name = (nameEl ? nameEl.value : '').trim();
    const email = (emailEl ? emailEl.value : '').trim().toLowerCase();
    const subject = (subjEl ? subjEl.value : '').trim();
    const message = (msgEl ? msgEl.value : '').trim();

    if (!name || name.length < 2) {
      if (errEl) { errEl.textContent = ne ? '⚠️ कृपया कम्तिमा २ अक्षरको नाम लेख्नुहोस्।' : '⚠️ Please enter a name (min 2 chars).'; errEl.style.display = 'block'; }
      return;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
    if (!email || !emailRegex.test(email)) {
      if (errEl) { errEl.textContent = ne ? '⚠️ कृपया मान्य इमेल ठेगाना लेख्नुहोस्।' : '⚠️ Please enter a valid email address.'; errEl.style.display = 'block'; }
      return;
    }
    if (!subject || subject.length < 3) {
      if (errEl) { errEl.textContent = ne ? '⚠️ कृपया विषय कम्तिमा ३ अक्षरमा लेख्नुहोस्।' : '⚠️ Please enter a subject (min 3 chars).'; errEl.style.display = 'block'; }
      return;
    }
    if (!message || message.length < 10) {
      if (errEl) { errEl.textContent = ne ? '⚠️ सन्देश कम्तिमा १० अक्षरको हुनुपर्छ।' : '⚠️ Message must be at least 10 characters.'; errEl.style.display = 'block'; }
      return;
    }
    if (errEl) errEl.style.display = 'none';

    // Record inquiry
    if (window.DB) {
      if (!DB.inquiries) DB.inquiries = [];
      const ticketId = 'MB-REC-' + (1000 + Math.floor(Math.random() * 9000));
      const record = {
        ticketId,
        uid: (window.S && S.u) ? S.u.id : null,
        name,
        email,
        phone: '-',
        category: 'recruitment',
        categoryName: 'Recruiter / Client Inquiry',
        subject,
        message,
        targetEmail: PROFILE.email,
        date: new Date().toISOString().slice(0, 10),
        status: 'Sent'
      };
      DB.inquiries.unshift(record);
      if (typeof save === 'function') save();

      if (window.saveInquiryToFirestore) {
        window.saveInquiryToFirestore(record).catch(function(e) { console.warn('Resume firestore inquiry err:', e); });
      }

      const mailSubj = encodeURIComponent(`[${ticketId}] Recruiter Inquiry: ${subject}`);
      const mailBody = encodeURIComponent(
        `Dear Surya Kafle,\n\nName: ${name}\nEmail: ${email}\nSubject: ${subject}\n\nMessage:\n${message}\n\nSubmitted via Mero Bazar Portfolio System`
      );
      window.location.href = `mailto:${PROFILE.email}?subject=${mailSubj}&body=${mailBody}`;
      toast(ne ? '✅ सोधपुछ पठाइयो! suryakafle860@gmail.com मा सन्देश दर्ता भयो।' : '✅ Inquiry sent! Recorded for suryakafle860@gmail.com');
      if (subjEl) subjEl.value = '';
      if (msgEl) msgEl.value = '';
    }
  };

  // Triggers native Android print spooler (which saves clean PDF!) or browser print dialog
  window.printResumePDF = function() {
    window.print();
  };

  // Canvas-based image/document exporter for recruiters wanting direct file download
  window.exportResumeAsDocument = async function() {
    const ne = isNe();
    const docEl = document.getElementById('resume_printable_document');
    if (!docEl) return;

    toast(ne ? '⏳ रेजुमे तयार हुँदैछ...' : '⏳ Generating resume document...');

    try {
      const W = 840;
      const H = 1180;
      const c = document.createElement('canvas');
      c.width = W;
      c.height = H;
      const ctx = c.getContext('2d');

      // Background
      ctx.fillStyle = '#FFFFFF';
      ctx.fillRect(0, 0, W, H);

      // Top Header Accent Bar
      ctx.fillStyle = '#0F4C9A';
      ctx.fillRect(0, 0, W, 12);

      // Fonts
      const fSans = '"Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif';

      // Name
      ctx.fillStyle = '#0A192F';
      ctx.font = 'bold 34px ' + fSans;
      ctx.fillText(PROFILE.name, 48, 62);

      // Title
      ctx.fillStyle = '#0F4C9A';
      ctx.font = 'bold 16px ' + fSans;
      ctx.fillText(PROFILE.title.toUpperCase(), 48, 88);

      // Contact info bar
      ctx.fillStyle = '#555555';
      ctx.font = '13px ' + fSans;
      ctx.fillText(`Email: ${PROFILE.email}  |  Location: ${PROFILE.location}`, 48, 114);

      // Divider
      ctx.fillStyle = '#E2E8F0';
      ctx.fillRect(48, 128, W - 96, 2);

      let y = 160;

      // Section Helper
      const drawSectionHead = (title) => {
        ctx.fillStyle = '#0F4C9A';
        ctx.font = 'bold 16px ' + fSans;
        ctx.fillText(title.toUpperCase(), 48, y);
        ctx.fillStyle = '#CBD5E1';
        ctx.fillRect(48, y + 6, W - 96, 1.5);
        y += 26;
      };

      // Summary
      drawSectionHead("Executive Summary");
      ctx.fillStyle = '#334155';
      ctx.font = '13px ' + fSans;
      const words = PROFILE.summary.split(' ');
      let line = '';
      for (let n = 0; n < words.length; n++) {
        const testLine = line + words[n] + ' ';
        const metrics = ctx.measureText(testLine);
        if (metrics.width > (W - 96) && n > 0) {
          ctx.fillText(line, 48, y);
          line = words[n] + ' ';
          y += 18;
        } else {
          line = testLine;
        }
      }
      ctx.fillText(line, 48, y);
      y += 34;

      // Core Technical Proficiencies
      drawSectionHead("Core Technical Proficiencies");
      PROFILE.skills.forEach(s => {
        ctx.fillStyle = '#0F4C9A';
        ctx.font = 'bold 13px ' + fSans;
        ctx.fillText(`• ${s.domain} (${s.score}): `, 48, y);
        const domainWidth = ctx.measureText(`• ${s.domain} (${s.score}): `).width;

        ctx.fillStyle = '#475569';
        ctx.font = '12px ' + fSans;
        ctx.fillText(s.highlights.slice(0, 85) + (s.highlights.length > 85 ? '...' : ''), 48 + domainWidth, y);
        y += 20;
      });
      y += 18;

      // Experience
      drawSectionHead("Professional Experience");
      PROFILE.experience.forEach(exp => {
        ctx.fillStyle = '#0F172A';
        ctx.font = 'bold 14px ' + fSans;
        ctx.fillText(exp.role, 48, y);
        ctx.textAlign = 'right';
        ctx.fillStyle = '#64748B';
        ctx.font = 'italic 12px ' + fSans;
        ctx.fillText(exp.period, W - 48, y);
        ctx.textAlign = 'left';
        y += 18;

        ctx.fillStyle = '#0F4C9A';
        ctx.font = 'bold 12px ' + fSans;
        ctx.fillText(`${exp.company} — ${exp.location}`, 48, y);
        y += 18;

        ctx.fillStyle = '#334155';
        ctx.font = '12px ' + fSans;
        exp.points.forEach(pt => {
          ctx.fillText(`- ${pt}`, 60, y);
          y += 17;
        });
        y += 10;
      });

      // Footer
      ctx.fillStyle = '#0F4C9A';
      ctx.fillRect(0, H - 36, W, 36);
      ctx.fillStyle = '#FFFFFF';
      ctx.font = '12px ' + fSans;
      ctx.textAlign = 'center';
      ctx.fillText(`Surya Kafle · Professional Profile · Inquiries: ${PROFILE.email}`, W / 2, H - 14);

      // Trigger download
      c.toBlob(blob => {
        if (!blob) return;
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = `Surya_Kafle_Resume_${new Date().getFullYear()}.png`;
        a.click();
        setTimeout(() => URL.revokeObjectURL(a.href), 4000);
        toast(ne ? '✅ रेजुमे डाउनलोड भयो' : '✅ Resume document downloaded');
      }, 'image/png');

    } catch (err) {
      console.warn("Export document error:", err);
      // Fallback to print PDF
      window.print();
    }
  };

})();
