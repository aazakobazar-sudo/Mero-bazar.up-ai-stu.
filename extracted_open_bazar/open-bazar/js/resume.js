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
        domain: "Frontend Engineering",
        score: "96%",
        highlights: "Jetpack Compose WebView, React, Modern ES6+, D3.js v7 Interactive Visualizations, HTML5 Canvas, Responsive Design, Offline PWA Service Workers"
      },
      {
        domain: "Backend & Cloud Services",
        score: "95%",
        highlights: "Node.js, Express, Firebase Firestore & Cloud Auth, RESTful API Design, Real-time WebSockets, PBKDF2 WebCrypto Zero-Trust Encryption"
      },
      {
        domain: "UI/UX & Product Design",
        score: "94%",
        highlights: "Material Design 3 (M3), Devanagari Typography, High-Contrast Theming, WCAG 2.1 AA Accessibility, Touch Gestures, Mobile-First Ergonomics"
      },
      {
        domain: "System Architecture",
        score: "93%",
        highlights: "Clean Architecture / MVVM, Offline-First Local DB Sync, Native Android Java/Kotlin Bridge, Modular Services, Fault-Tolerant State Machines"
      },
      {
        domain: "Mobile & Hardware Integration",
        score: "92%",
        highlights: "Android Kotlin App Shell, MediaStore Scoped Storage, Native FileProvider Sharing, Camera & Microphone Access, Geolocation Telemetry"
      },
      {
        domain: "Real-Time Telemetry & VoIP",
        score: "91%",
        highlights: "Metered.ca WebRTC Audio/Video Calling, TURN/STUN Relay traversal over UDP & TCP/TLS, Real-time Audio Spectrum Analysers, Automated Invoicing"
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
              <h2 class="res-sec-head">Technical Proficiency & Architecture</h2>
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
        </div>

        <!-- Bottom Mobile Action Bar -->
        <div class="resume-bottom-bar noprint">
          <button class="btn s" style="flex:1" onclick="window.printResumePDF()">
            🖨️ ${ne ? 'PDF सेभ / प्रिन्ट गर्नुहोस्' : 'Save / Print as PDF'}
          </button>
          <a href="mailto:${PROFILE.email}?subject=Recruitment%20Opportunity" class="btn g" style="flex:1;text-decoration:none;display:flex;align-items:center;justify-content:center">
            ✉️ ${ne ? 'सम्पर्क गर्नुहोस्' : 'Contact Surya'}
          </a>
        </div>
      </div>
    `;

    document.body.appendChild(modal);
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
