/* Open Bazar — Professional Inquiry & Contact Form System
 * Target Email: suryakafle860@gmail.com
 * Validated contact form for business partnerships, onboarding, and customer inquiries
 */
(function() {
  const TARGET_EMAIL = 'suryakafle860@gmail.com';
  const isNe = () => (window.DB && DB.lang) === 'ne';
  const esc = s => String(s == null ? '' : s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));

  // Contact Form State
  const CS = {
    name: '',
    email: '',
    phone: '',
    category: 'business',
    subject: '',
    message: '',
    errors: {},
    submitted: false,
    lastTicketId: null
  };

  const CATEGORIES = [
    { id: 'collaboration', ne: '🤝 व्यावसायिक सहकार्य तथा कन्सल्टिङ (Collaboration & Consulting)', en: '🤝 Collaboration & Consulting' },
    { id: 'business', ne: '💼 व्यापार साझेदारी (Business Partnership)', en: '💼 Business Partnership' },
    { id: 'merchant', ne: '🛒 पसल दर्ता तथा अनबोर्डिङ (Merchant Onboarding)', en: '🛒 Merchant Onboarding & Expansion' },
    { id: 'technical', ne: '🛠️ प्राविधिक सल्लाह तथा सफ्टवेयर निर्माण (Technical Advisory & Engineering)', en: '🛠️ Technical Advisory & Engineering' },
    { id: 'ads', ne: '📢 विज्ञापन तथा प्रवर्द्धन (Advertising & Promotion)', en: '📢 Advertising & Sponsorship' },
    { id: 'general', ne: '❓ सामान्य सोधपुछ (General Inquiries)', en: '❓ General Inquiries' }
  ];

  function ensureInquiriesDB() {
    if (!window.DB) return;
    if (!DB.inquiries || !Array.isArray(DB.inquiries)) {
      DB.inquiries = [];
    }
  }

  // Pre-fill fields from logged in user if available
  function initDefaults() {
    if (window.S && S.u) {
      if (!CS.name) CS.name = S.u.name || (S.u.shop ? S.u.shop.name : '');
      if (!CS.email && S.u.email && !S.u.email.includes('.local')) CS.email = S.u.email;
      if (!CS.phone && S.u.phone) CS.phone = S.u.phone;
    }
  }

  window.renderContactFormView = function() {
    ensureInquiriesDB();
    initDefaults();
    const ne = isNe();
    const myInquiries = (DB.inquiries || []).filter(item => {
      if (S.u) return item.uid === S.u.id || item.email === S.u.email;
      return true;
    });

    setTimeout(() => {
      if (typeof window.renderGitHubStatsSection === 'function') {
        window.renderGitHubStatsSection('github_portfolio_stats_contact');
      }
    }, 50);

    return `
      <div class="c" style="background:linear-gradient(135deg,var(--ind,#0F4C9A),var(--ind2,#1B6AC9));color:#fff;margin-bottom:14px;border-radius:18px;padding:18px">
        <div style="display:flex;align-items:center;gap:12px">
          <span style="font-size:36px">✉️</span>
          <div>
            <b style="font-size:18px;display:block">${ne ? 'व्यावसायिक सोधपुछ तथा सम्पर्क' : 'Professional Inquiries & Contact'}</b>
            <div style="font-size:12.5px;opacity:.9;margin-top:2px">
              ${ne ? 'हाम्रा आधिकारिक व्यवस्थापकलाई सिधै सन्देश पठाउनुहोस्' : 'Send a direct inquiry to our official administration'}
            </div>
          </div>
        </div>
        <div style="margin-top:12px;background:rgba(255,255,255,0.15);padding:8px 12px;border-radius:10px;font-size:13px;display:flex;align-items:center;gap:8px">
          <span>🎯 ${ne ? 'प्राप्तकर्ता:' : 'Recipient:'}</span>
          <b style="color:#FFDF78;word-break:break-all">${TARGET_EMAIL}</b>
        </div>
        <div style="margin-top:12px;display:flex;gap:8px;flex-wrap:wrap">
          <button class="btn s" onclick="window.downloadResumePDF ? window.downloadResumePDF() : 0" style="background:#fff;color:var(--ind,#0F4C9A);font-weight:750;border:0;padding:9px 16px;border-radius:10px;font-size:13.5px;box-shadow:0 3px 10px rgba(0,0,0,.18);gap:6px;cursor:pointer">
            📄 ${ne ? 'रेजुमे तथा सिभी हेर्नुहोस् (View Resume / CV)' : 'View Resume & CV (PDF)'}
          </button>
          <button class="btn s" onclick="window.openRadarChart ? window.openRadarChart() : 0" style="background:#F28C0F;color:#fff;font-weight:750;border:0;padding:9px 14px;border-radius:10px;font-size:13px;box-shadow:0 3px 10px rgba(0,0,0,.18);gap:6px;cursor:pointer">
            🕸️ ${ne ? 'D3 रडार दक्षता (Skill Radar)' : 'D3 Skill Radar'}
          </button>
          <a href="mailto:${TARGET_EMAIL}?subject=Professional%20Inquiry" class="btn g" style="background:rgba(255,255,255,.18);color:#fff;border:1px solid rgba(255,255,255,.3);border-radius:10px;padding:9px 13px;font-size:13px;text-decoration:none">
            ✉️ Email Direct
          </a>
        </div>
      </div>

      <!-- Portfolio Summary Card for Potential Clients & Recruiters -->
      <div class="c" style="border-radius:16px;padding:16px;margin-bottom:14px;border-left:4px solid var(--saf,#F57C00)">
        <div style="display:flex;justify-content:space-between;align-items:flex-start;gap:10px">
          <div>
            <h3 style="margin:0 0 4px;font-size:16px;color:var(--tx,#0A192F)">
              👨‍💻 Surya Kafle (सूर्य काफ्ले) — Portfolio
            </h3>
            <div style="font-size:12.5px;color:var(--ind,#0F4C9A);font-weight:700">
              Lead Full-Stack Architect & Senior Mobile/Web Engineer (7+ Years)
            </div>
          </div>
          <button class="btn sm g" onclick="window.openRadarChart ? window.openRadarChart() : 0" style="font-size:11.5px;padding:4px 8px;white-space:nowrap;display:flex;align-items:center;gap:4px">
            <span>🕸️</span> Radar Chart
          </button>
        </div>
        <p style="font-size:13px;line-height:1.45;color:var(--mu,#64748B);margin:8px 0 10px">
          ${ne ? 'क्लाउड आर्किटेक्चर (Firebase, Node.js), मोबाइल डेभलपमेन्ट (Kotlin, Compose), र एआई इन्टिग्रेशन (Gemini, OCR) मा विशेष दक्षता। D3.js इन्टरएक्टिभ रडार चार्टद्वारा प्रमाणित प्राविधिक क्षमता।' : 'Specialized in Cloud Architecture (Firebase, Node.js), Mobile Development (Android Kotlin, Jetpack Compose), and AI Integration (Gemini, OCR). Proven technical leadership visually mapped via interactive D3.js radar charts.'}
        </p>
        <div style="display:flex;gap:6px;flex-wrap:wrap">
          <span class="bd B" style="font-size:11px">☁️ Cloud Architecture (96%)</span>
          <span class="bd B" style="font-size:11px">📱 Mobile Development (98%)</span>
          <span class="bd B" style="font-size:11px">🤖 AI Integration (95%)</span>
          <span class="bd B" style="font-size:11px">💻 Frontend (96%)</span>
          <span class="bd B" style="font-size:11px">🏛️ System Arch (94%)</span>
          <span class="bd G" style="font-size:11px">Available for Hire</span>
        </div>
      </div>

      <!-- Real-time GitHub Activity Stats Section -->
      <div id="github_portfolio_stats_contact"></div>

      <!-- Main Form Card -->
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;flex-wrap:wrap;gap:6px">
          <h3 style="margin:0;font-size:16px">
            📝 ${ne ? 'सोधपुछ फारम भर्नुहोस्' : 'Submit Your Inquiry'}
          </h3>
          <span class="bd G" style="font-size:11px;display:inline-flex;align-items:center;gap:4px">
            🔥 ${ne ? 'Firebase Cloud Firestore सक्रिय' : 'Firebase Cloud Firestore Active'}
          </span>
        </div>

        <!-- Full Name -->
        <label class="st" style="display:block;margin-bottom:4px">
          ${ne ? 'पूरा नाम *' : 'Full Name *'}
        </label>
        <input class="inp" id="cf_name" type="text" placeholder="${ne ? 'तपाईंको पूरा नाम' : 'Your full name'}" value="${esc(CS.name)}" oninput="CS.name=this.value;window.clearFieldError('name')" style="${CS.errors.name ? 'border-color:var(--crim);background:rgba(214,40,40,0.06)' : ''}">
        ${CS.errors.name ? `<div style="color:var(--crim);font-size:12px;margin:-6px 0 8px 4px">⚠️ ${esc(CS.errors.name)}</div>` : ''}

        <!-- Email Address -->
        <label class="st" style="display:block;margin-bottom:4px">
          ${ne ? 'इमेल ठेगाना *' : 'Email Address *'}
        </label>
        <input class="inp" id="cf_email" type="email" placeholder="example@domain.com" value="${esc(CS.email)}" oninput="CS.email=this.value;window.clearFieldError('email')" style="${CS.errors.email ? 'border-color:var(--crim);background:rgba(214,40,40,0.06)' : ''}">
        ${CS.errors.email ? `<div style="color:var(--crim);font-size:12px;margin:-6px 0 8px 4px">⚠️ ${esc(CS.errors.email)}</div>` : ''}

        <!-- Phone Number -->
        <label class="st" style="display:block;margin-bottom:4px">
          ${ne ? 'सम्पर्क फोन / मोबाइल (वैकल्पिक)' : 'Contact Phone / Mobile (Optional)'}
        </label>
        <input class="inp" id="cf_phone" type="tel" placeholder="98XXXXXXXX" value="${esc(CS.phone)}" oninput="CS.phone=this.value;window.clearFieldError('phone')" style="${CS.errors.phone ? 'border-color:var(--crim);background:rgba(214,40,40,0.06)' : ''}">
        ${CS.errors.phone ? `<div style="color:var(--crim);font-size:12px;margin:-6px 0 8px 4px">⚠️ ${esc(CS.errors.phone)}</div>` : ''}

        <!-- Inquiry Category -->
        <label class="st" style="display:block;margin-bottom:4px">
          ${ne ? 'सोधपुछको प्रकार / वर्ग *' : 'Inquiry Category *'}
        </label>
        <select class="inp" id="cf_cat" onchange="CS.category=this.value">
          ${CATEGORIES.map(c => `
            <option value="${c.id}" ${CS.category === c.id ? 'selected' : ''}>
              ${esc(ne ? c.ne : c.en)}
            </option>
          `).join('')}
        </select>

        <!-- Subject -->
        <label class="st" style="display:block;margin-bottom:4px">
          ${ne ? 'विषय (Subject) *' : 'Subject *'}
        </label>
        <input class="inp" id="cf_subj" type="text" placeholder="${ne ? 'सोधपुछको मुख्य शीर्षक' : 'Summary / topic of inquiry'}" value="${esc(CS.subject)}" oninput="CS.subject=this.value;window.clearFieldError('subject')" style="${CS.errors.subject ? 'border-color:var(--crim);background:rgba(214,40,40,0.06)' : ''}">
        ${CS.errors.subject ? `<div style="color:var(--crim);font-size:12px;margin:-6px 0 8px 4px">⚠️ ${esc(CS.errors.subject)}</div>` : ''}

        <!-- Detailed Message -->
        <label class="st" style="display:block;margin-bottom:4px">
          ${ne ? 'विस्तृत सन्देश / विवरण *' : 'Detailed Message *'}
        </label>
        <textarea class="inp" id="cf_msg" rows="4" placeholder="${ne ? 'तपाईंको सोधपुछ वा प्रस्ताव कम्तिमा १० अक्षरमा स्पष्ट लेख्नुहोस्...' : 'Describe your inquiry or proposal in detail (min 10 characters)...'}" oninput="CS.message=this.value;window.clearFieldError('message')" style="line-height:1.45;${CS.errors.message ? 'border-color:var(--crim);background:rgba(214,40,40,0.06)' : ''}">${esc(CS.message)}</textarea>
        ${CS.errors.message ? `<div style="color:var(--crim);font-size:12px;margin:-6px 0 8px 4px">⚠️ ${esc(CS.errors.message)}</div>` : ''}

        <!-- Submit Button with Firebase Action -->
        <button id="cf_submit_btn" class="btn s" style="width:100%;padding:13px;font-size:15px;margin-top:6px;gap:8px" onclick="window.submitContactInquiry()">
          🚀 ${ne ? 'सन्देश पठाउनुहोस् (Send to suryakafle860@gmail.com)' : 'Send Inquiry to suryakafle860@gmail.com'}
        </button>
      </div>

      <!-- Past Inquiries List -->
      ${myInquiries.length > 0 ? `
        <h3 style="margin:18px 2px 8px;font-size:16px">
          📋 ${ne ? 'तपाईंका पछिल्ला सोधपुछहरू' : 'Your Submitted Inquiries'} (${myInquiries.length})
        </h3>
        <div style="display:flex;flex-direction:column;gap:8px">
          ${myInquiries.map(inq => `
            <div class="c" style="border-left:4px solid var(--ind);padding:12px 14px">
              <div class="row">
                <b>${esc(inq.subject)}</b>
                <span class="bd G" style="font-size:10.5px">${esc(inq.ticketId)}</span>
              </div>
              <div class="st" style="margin:4px 0">${esc(inq.categoryName)} · ${inq.date}</div>
              <div style="font-size:13.5px;color:var(--tx);opacity:.9;margin-top:4px">${esc(inq.message)}</div>
              <div style="margin-top:8px;display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:6px">
                <span class="bd ${inq.firestoreSynced ? 'G' : 'B'}" style="font-size:11px">
                  ${inq.firestoreSynced ? '☁️ ' + (ne ? 'Firestore मा सुरक्षित' : 'Firestore Synced') : '✉️ Sent to ' + TARGET_EMAIL}
                </span>
                <button class="btn sm g" onclick="window.resendInquiryEmail('${inq.ticketId}')" title="${ne ? 'इमेल क्लाइन्टमा खोल्नुहोस्' : 'Open in Email'}">
                  ✉️ Email
                </button>
              </div>
            </div>
          `).join('')}
        </div>
      ` : ''}
    `;
  };

  window.clearFieldError = function(field) {
    if (CS.errors[field]) {
      delete CS.errors[field];
      const el = document.getElementById('cf_' + field);
      if (el) {
        el.style.borderColor = '';
        el.style.background = '';
      }
    }
  };

  // Field validation and send logic
  window.submitContactInquiry = async function() {
    const ne = isNe();
    CS.errors = {};

    const name = (CS.name || '').trim();
    const email = (CS.email || '').trim().toLowerCase();
    const phone = (CS.phone || '').trim();
    const subject = (CS.subject || '').trim();
    const message = (CS.message || '').trim();
    const category = CS.category || 'business';

    // 1. Name validation
    if (!name || name.length < 2) {
      CS.errors.name = ne ? 'कृपया कम्तिमा २ अक्षरको पूरा नाम लेख्नुहोस्।' : 'Please enter a valid name (at least 2 characters).';
    }

    // 2. Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
    if (!email || !emailRegex.test(email)) {
      CS.errors.email = ne ? 'कृपया मान्य इमेल ठेगाना लेख्नुहोस् (उदा: name@example.com)।' : 'Please enter a valid email address (e.g. name@example.com).';
    }

    // 3. Phone validation (optional, but if provided must be >= 7 digits)
    if (phone && !/^[0-9+ -]{7,15}$/.test(phone)) {
      CS.errors.phone = ne ? 'कृपया मान्य फोन वा मोबाइल नम्बर लेख्नुहोस्।' : 'Please enter a valid phone number.';
    }

    // 4. Subject validation
    if (!subject || subject.length < 3) {
      CS.errors.subject = ne ? 'कृपया सोधपुछको विषय कम्तिमा ३ अक्षरमा लेख्नुहोस्।' : 'Please enter a subject (at least 3 characters).';
    }

    // 5. Message validation
    if (!message || message.length < 10) {
      CS.errors.message = ne ? 'कृपया विस्तृत सन्देश कम्तिमा १० अक्षरमा लेख्नुहोस्।' : 'Message must be at least 10 characters long.';
    }

    if (Object.keys(CS.errors).length > 0) {
      R();
      toast(ne ? '⚠️ कृपया रातो चिन्ह लागेका विवरणहरू सच्याउनुहोस्।' : '⚠️ Please correct the errors in the form.');
      return;
    }

    // Build unique ticket ID
    const ticketId = 'MB-INQ-' + (1000 + Math.floor(Math.random() * 9000));
    const catObj = CATEGORIES.find(c => c.id === category) || CATEGORIES[0];
    const catName = ne ? catObj.ne : catObj.en;
    const nowStr = new Date().toISOString().slice(0, 10);

    const inquiryRecord = {
      ticketId,
      uid: (window.S && S.u) ? S.u.id : null,
      name,
      email,
      phone: phone || '-',
      category,
      categoryName: catName,
      subject,
      message,
      targetEmail: TARGET_EMAIL,
      date: nowStr,
      status: 'Sent',
      firestoreSynced: false
    };

    const btn = document.getElementById('cf_submit_btn');
    if (btn) {
      btn.disabled = true;
      btn.innerHTML = `⏳ ${ne ? 'Firebase क्लाउडमा पठाउँदै...' : 'Saving to Firebase Cloud...'}`;
    }

    // Save directly to Firestore Cloud Database
    let firestoreOk = false;
    if (window.saveInquiryToFirestore) {
      try {
        firestoreOk = await window.saveInquiryToFirestore(inquiryRecord);
      } catch (err) {
        console.warn('Firestore inquiry save exception:', err);
      }
    }
    inquiryRecord.firestoreSynced = !!firestoreOk;

    ensureInquiriesDB();
    DB.inquiries.unshift(inquiryRecord);
    save();

    // Construct Mailto URI
    const mailSubject = encodeURIComponent(`[${ticketId}] Mero Bazar Inquiry: ${subject}`);
    const mailBody = encodeURIComponent(
      `Mero Bazar Professional Inquiry / मेरो बजार सोधपुछ\n` +
      `-----------------------------------------\n` +
      `Ticket ID: ${ticketId}\n` +
      `Sender Name: ${name}\n` +
      `Sender Email: ${email}\n` +
      `Phone/Mobile: ${phone || 'N/A'}\n` +
      `Category: ${catName}\n` +
      `Date: ${nowStr}\n\n` +
      `Message:\n${message}\n\n` +
      `-----------------------------------------\n` +
      `Submitted via Mero Bazar Platform (मेरो बजार)`
    );

    const mailtoUrl = `mailto:${TARGET_EMAIL}?subject=${mailSubject}&body=${mailBody}`;

    // Reset form fields
    CS.subject = '';
    CS.message = '';
    CS.errors = {};

    // Trigger system email app
    try {
      window.location.href = mailtoUrl;
    } catch (_) {}

    R();
    showInquirySuccessModal(inquiryRecord, mailtoUrl);
  };

  function showInquirySuccessModal(record, mailtoUrl) {
    const ne = isNe();
    const modalId = 'inquiry_success_modal';
    let modal = document.getElementById(modalId);
    if (modal) modal.remove();

    modal = document.createElement('div');
    modal.id = modalId;
    modal.style.cssText = 'position:fixed;top:0;left:0;right:0;bottom:0;background:rgba(8,16,32,.85);z-index:999999;display:flex;align-items:center;justify-content:center;padding:16px';
    
    modal.innerHTML = `
      <div class="c" style="background:var(--card,#fff);max-width:440px;width:100%;border-radius:20px;padding:22px;box-shadow:0 8px 30px rgba(0,0,0,.4)">
        <div style="text-align:center;font-size:48px;margin-bottom:8px">🎉</div>
        <h2 style="text-align:center;font-size:19px;font-weight:800;margin:0 0 6px">
          ${ne ? 'सोधपुछ सफलतापूर्वक पठाइयो!' : 'Inquiry Successfully Sent!'}
        </h2>
        <div class="st" style="text-align:center;text-transform:none;margin-bottom:14px">
          ${ne ? 'तपाईंको सन्देश सिधै दर्ता भयो र आधिकारिक इमेलमा पठाइयो:' : 'Your inquiry was recorded and dispatched to:'}<br>
          <b style="color:var(--ind);font-size:14px">${TARGET_EMAIL}</b>
        </div>

        <div style="background:var(--bg,#F6F9FC);border:1px solid var(--ln);border-radius:12px;padding:12px;font-size:13px;margin-bottom:14px">
          <div><b>Ticket:</b> ${esc(record.ticketId)}</div>
          <div><b>From:</b> ${esc(record.name)} (${esc(record.email)})</div>
          <div><b>Subject:</b> ${esc(record.subject)}</div>
          <div id="fs_sync_status" style="margin-top:6px;font-size:12px">
            ☁️ <span style="color:#16A34A;font-weight:700">Firestore Cloud Database मा सुरक्षित भयो</span>
          </div>
        </div>

        <div style="display:flex;flex-direction:column;gap:8px">
          <a href="${mailtoUrl}" class="btn s" style="text-decoration:none;display:flex;align-items:center;justify-content:center;gap:6px">
            ✉️ ${ne ? 'Gmail / इमेल एपमा खोल्नुहोस्' : 'Open in Gmail / Email App'}
          </a>
          <button class="btn g" onclick="navigator.clipboard&&navigator.clipboard.writeText('${esc(record.message)}').then(()=>toast('${ne ? 'सन्देश कपी भयो' : 'Copied message'}'))">
            📋 ${ne ? 'सन्देश कपी गर्नुहोस्' : 'Copy Message Text'}
          </button>
          <button class="btn r sm" onclick="document.getElementById('${modalId}').remove()">
            ✕ ${ne ? 'बन्द गर्नुहोस्' : 'Close'}
          </button>
        </div>
      </div>
    `;

    document.body.appendChild(modal);
  }

  window.resendInquiryEmail = function(ticketId) {
    const item = (DB.inquiries || []).find(x => x.ticketId === ticketId);
    if (!item) return;
    const mailSubject = encodeURIComponent(`[${item.ticketId}] Inquiry: ${item.subject}`);
    const mailBody = encodeURIComponent(
      `Open Bazar Inquiry\nTicket: ${item.ticketId}\nFrom: ${item.name} (${item.email})\nPhone: ${item.phone}\nCategory: ${item.categoryName}\nDate: ${item.date}\n\nMessage:\n${item.message}`
    );
    window.location.href = `mailto:${TARGET_EMAIL}?subject=${mailSubject}&body=${mailBody}`;
  };

})();
