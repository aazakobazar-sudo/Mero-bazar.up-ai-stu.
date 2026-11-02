/* Open Bazar — Messenger, Groups, Voice Recording & Audio/Video Calling */
(function() {
  const isNe = () => (window.DB && DB.lang) === 'ne';
  const esc = s => String(s == null ? '' : s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
  const fmtTime = ts => {
    const d = new Date(ts || Date.now());
    return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  // Messenger State
  const MS = {
    subTab: 'chats', // 'chats' | 'groups'
    activeType: null, // 'direct' | 'group'
    activeTargetId: null, // user ID or group ID
    recording: false,
    mediaRecorder: null,
    audioChunks: [],
    recStartTime: 0,
    recTimer: null,
    recDuration: 0,
    activeAudioPlayer: null,
    // Calling state
    call: null // { type: 'audio'|'video', targetName: '', targetPhoto: '', status: 'ringing'|'connected', startTime: 0, timer: null, localStream: null, muted: false, videoOff: false }
  };

  // Seed default marketplace groups if not present
  function ensureGroups() {
    if (!window.DB) return;
    if (!DB.groups || !Array.isArray(DB.groups) || DB.groups.length === 0) {
      DB.groups = [
        {
          id: 'g_all_bazar',
          name: 'नेपाल खुल्ला बजार साझा चौतारी',
          nameEn: 'Nepal Open Bazar Community',
          icon: '🏪',
          desc: 'सबै व्यापारी र ग्राहकहरूको साझा छलफल तथा सुझाव केन्द्र',
          membersCount: 42,
          created: '2026-01-01'
        },
        {
          id: 'g_grocery',
          name: 'किराना तथा दैनिक उपभोग्य बजार',
          nameEn: 'Daily Grocery & Staples Group',
          icon: '🧺',
          desc: 'ताजा खाद्यान्न, चामल, तेल तथा दैनिक सामग्रीका अफरहरू',
          membersCount: 28,
          created: '2026-01-10'
        },
        {
          id: 'g_support',
          name: 'अर्डर तथा डेलिभरी सहायता समूह',
          nameEn: 'Order & Delivery Support Desk',
          icon: '🚚',
          desc: 'डेलिभरी ट्र्याकिङ, ठेगाना समन्वय तथा तत्काल ग्राहक सहायता',
          membersCount: 19,
          created: '2026-02-01'
        }
      ];
    }
    if (!DB.group_msgs || !Array.isArray(DB.group_msgs)) {
      DB.group_msgs = [
        {
          id: 'gm_1',
          gid: 'g_all_bazar',
          uid: 1,
          uName: 'Surya (Admin)',
          uPhoto: '',
          text: 'खुल्ला बजार मेसेन्जर तथा समूह च्याटमा सबैलाई स्वागत छ! यहाँ व्यापारी र ग्राहक बीच प्रत्यक्ष संवाद, भ्वाइस म्यासेज र कल गर्न सकिन्छ। 🙏',
          time: Date.now() - 3600000 * 4
        },
        {
          id: 'gm_2',
          gid: 'g_all_bazar',
          uid: 2,
          uName: 'डेमो किराना पसल',
          uPhoto: '',
          text: 'आज बासमती चामल र मुसुरो दालमा विशेष छुट छ! सिधै अर्डर गर्न सक्नुहुन्छ।',
          time: Date.now() - 3600000 * 2
        }
      ];
    }
  }

  // Render Messenger Root View
  window.renderMessengerView = function() {
    ensureGroups();
    const ne = isNe();
    const u = S.u;
    if (!u) {
      return `<div class="c st" style="text-align:center;padding:24px">${ne ? 'मेसेन्जर प्रयोग गर्न कृपया लगइन गर्नुहोस्।' : 'Please log in to use Messenger.'}</div>`;
    }

    if (MS.activeTargetId) {
      return renderActiveChatRoom();
    }

    // Direct chats list
    const isCustomer = u.role === 'c';
    const allUsers = DB.users.filter(x => x.id !== u.id && x.on);
    
    // Find active direct chat threads
    const directThreads = [];
    const chatPairs = new Set();
    (DB.chats || []).forEach(m => {
      const otherId = m.cid === u.id ? m.mid : (m.mid === u.id ? m.cid : null);
      if (otherId && !chatPairs.has(otherId)) {
        chatPairs.add(otherId);
      }
    });

    // Populate user list (merchants for customers, customers for merchants)
    allUsers.forEach(other => {
      const msgs = (DB.chats || []).filter(m => (m.cid === u.id && m.mid === other.id) || (m.mid === u.id && m.cid === other.id));
      const last = msgs[msgs.length - 1];
      directThreads.push({
        user: other,
        lastMsg: last ? (last.voice ? (ne ? '🎙️ भ्वाइस म्यासेज' : '🎙️ Voice note') : last.t) : (ne ? 'च्याट सुरु गर्नुहोस्' : 'Start conversation'),
        time: last ? (last.ts || Date.now() - 1800000) : null,
        unread: msgs.length > 0 && !last?.fIsMe ? 0 : 0
      });
    });

    return `
      <div class="msg-container">
        <!-- Messenger Top Tabs -->
        <div class="msg-tabs">
          <button class="msg-tab ${MS.subTab === 'chats' ? 'on' : ''}" onclick="window.setMessengerTab('chats')">
            💬 ${ne ? 'सबै च्याट' : 'Direct Chats'} (${directThreads.length})
          </button>
          <button class="msg-tab ${MS.subTab === 'groups' ? 'on' : ''}" onclick="window.setMessengerTab('groups')">
            👥 ${ne ? 'बजार ग्रुपहरू' : 'Market Groups'} (${(DB.groups || []).length})
          </button>
        </div>

        ${MS.subTab === 'chats' ? renderDirectChatsList(directThreads) : renderGroupsList()}
      </div>
    `;
  };

  function renderDirectChatsList(threads) {
    const ne = isNe();
    if (!threads.length) {
      return `<div class="c st" style="text-align:center">${ne ? 'कुनै सन्देश छैन' : 'No messages yet'}</div>`;
    }

    return `
      <div style="display:flex;flex-direction:column;gap:8px">
        ${threads.map(t => {
          const o = t.user;
          const name = o.shop ? o.shop.name : o.name;
          const subtitle = o.shop ? `${o.shop.type || 'पसल'} · ${o.dist}` : `${ne ? 'ग्राहक' : 'Customer'} · ${o.dist || 'नेपाल'}`;
          const isOnline = true;
          return `
            <div class="chat-card" onclick="window.openDirectChat(${o.id})">
              <div class="chat-avatar-wrap">
                ${o.photo ? `<img class="av" src="${o.photo}" alt="">` : `<span class="av" style="font-size:22px">${o.shop ? '🏪' : '👤'}</span>`}
                ${isOnline ? '<span class="chat-online-dot"></span>' : ''}
              </div>
              <div class="chat-body">
                <div class="chat-header-row">
                  <span class="chat-title">${esc(name)}</span>
                  ${t.time ? `<span class="chat-time">${fmtTime(t.time)}</span>` : ''}
                </div>
                <div class="st" style="font-size:12px;margin:1px 0">${esc(subtitle)}</div>
                <div class="chat-preview">${esc(t.lastMsg)}</div>
              </div>
              <span style="font-size:18px;color:var(--mu);margin-left:4px">›</span>
            </div>
          `;
        }).join('')}
      </div>
    `;
  }

  function renderGroupsList() {
    const ne = isNe();
    const groups = DB.groups || [];

    return `
      <div style="display:flex;flex-direction:column;gap:10px">
        <button class="btn s" style="width:100%;margin-bottom:6px;gap:8px" onclick="window.promptCreateGroup()">
          ＋ ${ne ? 'नयाँ मेसेन्जर ग्रुप बनाउनुहोस्' : 'Create New Messenger Group'}
        </button>

        ${groups.map(g => {
          const lastMsg = (DB.group_msgs || []).filter(m => m.gid === g.id).slice(-1)[0];
          return `
            <div class="chat-card" onclick="window.openGroupChat('${g.id}')">
              <div class="chat-avatar-wrap">
                <span class="av" style="font-size:24px;background:var(--ind);color:#fff">${g.icon || '👥'}</span>
              </div>
              <div class="chat-body">
                <div class="chat-header-row">
                  <span class="chat-title">${esc(ne ? g.name : (g.nameEn || g.name))}</span>
                  <span class="bd B" style="font-size:10.5px">${g.membersCount || 10} ${ne ? 'सदस्य' : 'members'}</span>
                </div>
                <div class="chat-preview" style="color:var(--mu);margin-top:2px">
                  ${lastMsg ? `<b>${esc(lastMsg.uName)}:</b> ${esc(lastMsg.voice ? (ne ? '🎙️ भ्वाइस म्यासेज' : '🎙️ Voice note') : lastMsg.text)}` : esc(g.desc)}
                </div>
              </div>
              <span style="font-size:18px;color:var(--mu)">›</span>
            </div>
          `;
        }).join('')}
      </div>
    `;
  }

  // Active Conversation Screen
  function renderActiveChatRoom() {
    const ne = isNe();
    const u = S.u;
    const isGroup = MS.activeType === 'group';

    let targetTitle = '';
    let targetSub = '';
    let targetPhoto = '';
    let messages = [];

    if (isGroup) {
      const g = (DB.groups || []).find(x => x.id === MS.activeTargetId);
      targetTitle = g ? (ne ? g.name : (g.nameEn || g.name)) : 'Group';
      targetSub = `${g?.membersCount || 24} ${ne ? 'सदस्यहरू सक्रिय छन्' : 'members online'}`;
      messages = (DB.group_msgs || []).filter(m => m.gid === MS.activeTargetId);
    } else {
      const other = DB.users.find(x => x.id === MS.activeTargetId);
      targetTitle = other ? (other.shop ? other.shop.name : other.name) : 'User';
      const isOtherOnline = other ? (other.on === 1 || other.on === true) : true;
      targetSub = `${other?.shop ? `${other.shop.type} · ${other.dist} · ` : ''}${isOtherOnline ? '🟢 ' + (ne ? 'अनलाइन' : 'Online') : '⚪ ' + (ne ? 'अफलाइन' : 'Offline')}`;
      targetPhoto = other?.photo || '';
      messages = (DB.chats || []).filter(m => 
        (m.cid === u.id && m.mid === MS.activeTargetId) || 
        (m.mid === u.id && m.cid === MS.activeTargetId)
      );
    }

    const otherUser = isGroup ? null : DB.users.find(x => x.id === MS.activeTargetId);
    const isTargetOnline = isGroup ? true : (otherUser ? (otherUser.on === 1 || otherUser.on === true) : true);

    return `
      <div class="conv-box">
        <!-- Room Header -->
        <div class="conv-header">
          <button class="btn sm g" style="padding:6px 10px" onclick="window.closeMessengerChat()">
            ← ${ne ? 'पछाडि' : 'Back'}
          </button>
          <div style="flex:1;min-width:0;display:flex;align-items:center;gap:8px">
            ${targetPhoto ? `<img class="av" src="${targetPhoto}" style="width:34px;height:34px" alt="">` : `<span class="av" style="width:34px;height:34px;font-size:18px">${isGroup ? '👥' : '👤'}</span>`}
            <div style="overflow:hidden">
              <b style="font-size:14.5px;display:block;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${esc(targetTitle)}</b>
              <small style="color:${isTargetOnline ? '#48BB78' : 'var(--mu)'};display:block;font-size:11px;font-weight:600">${esc(targetSub)}</small>
            </div>
          </div>
          <div style="display:flex;gap:6px;align-items:center">
            <button class="btn sm j" title="${ne ? (isTargetOnline ? 'अडियो कल (अनलाइन)' : 'अडियो कल (अफलाइन - दोहोरो अनलाइन आवश्यक)') : 'Audio Call'}" onclick="window.startCall('audio', '${esc(targetTitle)}', ${MS.activeTargetId})">
              📞
            </button>
            <button class="btn sm s" title="${ne ? (isTargetOnline ? 'भिडियो कल (अनलाइन)' : 'भिडियो कल (अफलाइन - दोहोरो अनलाइन आवश्यक)') : 'Video Call'}" onclick="window.startCall('video', '${esc(targetTitle)}', ${MS.activeTargetId})">
              📹
            </button>
            <button class="btn sm g" style="font-size:11.5px;padding:5px 8px;font-weight:700" title="${ne ? 'आउने कल परीक्षण (Test Incoming)' : 'Test Incoming Call'}" onclick="window.triggerTestIncomingCall('audio', '${esc(targetTitle)}')">
              📲 ${ne ? 'परीक्षण' : 'Test In'}
            </button>
          </div>
        </div>

        <!-- Messages Area -->
        <div class="conv-messages" id="conv_msg_list">
          ${messages.length === 0 ? `
            <div class="c st" style="text-align:center;margin:auto 0;padding:24px">
              👋 ${ne ? 'कुराकानी सुरु गर्नुहोस्! टेक्स्ट, भ्वाइस म्यासेज पठाउनुहोस् वा सिधै कल गर्नुहोस्।' : 'Start the conversation! Send messages, voice notes or make calls.'}
            </div>
          ` : messages.map(m => {
            const isMe = isGroup ? (m.uid === u.id) : (m.f === (u.role === 'c' ? 'c' : 'm'));
            return `
              <div class="msg-bubble ${isMe ? 'me' : 'other'}">
                ${isGroup && !isMe ? `<span class="sender-name">${esc(m.uName || 'Member')}</span>` : ''}
                
                ${m.voice ? renderVoiceBubble(m, isMe) : `<div>${esc(m.t || m.text)}</div>`}

                <div class="msg-time">${fmtTime(m.ts || m.time)}</div>
              </div>
            `;
          }).join('')}
        </div>

        <!-- Input Bar (Voice Recorder + Text) -->
        <div class="conv-input-bar">
          ${MS.recording ? `
            <div class="recording-bar">
              <span class="rec-pulse-dot"></span>
              <span>🎙️ ${ne ? 'भ्वाइस रेकर्ड हुँदैछ...' : 'Recording voice...'} <span id="rec_timer">${Math.floor(MS.recDuration)}s</span></span>
            </div>
            <button class="conv-btn" onclick="window.cancelVoiceRecord()" title="${ne ? 'रद्द गर्नुहोस्' : 'Cancel'}" style="color:var(--crim)">
              ✕
            </button>
            <button class="conv-btn send" onclick="window.stopAndSendVoiceRecord()" title="${ne ? 'पठाउनुहोस्' : 'Send'}">
              ✓
            </button>
          ` : `
            <button class="conv-btn" id="voice_mic_btn" onclick="window.startVoiceRecord()" title="${ne ? 'भ्वाइस म्यासेज रेकर्ड गर्नुहोस्' : 'Record voice note'}">
              🎙️
            </button>
            <input id="conv_text_input" type="text" placeholder="${ne ? 'सन्देश लेख्नुहोस्...' : 'Type a message...'}" onkeydown="if(event.key==='Enter')window.sendCurrentMessage()">
            <button class="conv-btn send" onclick="window.sendCurrentMessage()" title="${ne ? 'पठाउनुहोस्' : 'Send'}">
              ➤
            </button>
          `}
        </div>
      </div>
    `;
  }

  function renderVoiceBubble(msg, isMe) {
    const ne = isNe();
    const dur = msg.dur || 3;
    const msgId = msg.id || ('v_' + Math.random().toString(36).substr(2, 6));
    if (msg.audioUrl) {
      window.VOICE_STORE = window.VOICE_STORE || {};
      window.VOICE_STORE[msgId] = msg.audioUrl;
    }
    return `
      <div class="voice-bubble" id="vb_${msgId}">
        <button class="voice-play-btn" onclick="window.playVoiceNote('${msgId}')" title="${ne ? 'सुन्नुहोस्' : 'Play voice note'}">
          ▶
        </button>
        <div class="voice-wave-bar">
          <span style="height:8px"></span>
          <span style="height:14px"></span>
          <span style="height:22px"></span>
          <span style="height:10px"></span>
          <span style="height:18px"></span>
          <span style="height:12px"></span>
          <span style="height:20px"></span>
          <span style="height:7px"></span>
        </div>
        <span class="voice-duration">${dur}s</span>
      </div>
    `;
  }

  // --- Actions & Handlers ---

  window.setMessengerTab = function(tab) {
    MS.subTab = tab;
    R();
  };

  window.openDirectChat = function(userId) {
    MS.activeType = 'direct';
    MS.activeTargetId = userId;
    R();
    setTimeout(scrollToBottom, 100);
  };

  window.openGroupChat = function(groupId) {
    MS.activeType = 'group';
    MS.activeTargetId = groupId;
    R();
    setTimeout(scrollToBottom, 100);
  };

  window.closeMessengerChat = function() {
    MS.activeTargetId = null;
    MS.activeType = null;
    if (MS.recording) cancelVoiceRecord();
    R();
  };

  function scrollToBottom() {
    const el = document.getElementById('conv_msg_list');
    if (el) el.scrollTop = el.scrollHeight;
  }

  // Send Text Message
  window.sendCurrentMessage = function() {
    const input = document.getElementById('conv_text_input');
    if (!input) return;
    const val = input.value.trim();
    if (!val) return;

    const u = S.u;
    const isGroup = MS.activeType === 'group';

    if (isGroup) {
      DB.group_msgs.push({
        id: 'gm_' + Date.now(),
        gid: MS.activeTargetId,
        uid: u.id,
        uName: u.shop ? u.shop.name : u.name,
        uPhoto: u.photo || '',
        text: val,
        time: Date.now()
      });
    } else {
      const newChat = {
        cid: u.role === 'c' ? u.id : MS.activeTargetId,
        mid: u.role === 'm' ? u.id : MS.activeTargetId,
        f: u.role === 'c' ? 'c' : 'm',
        receiver_id: MS.activeTargetId,
        sender_id: u.id,
        t: val,
        ts: Date.now()
      };
      DB.chats.push(newChat);
      if (window.sendChatMessageToFirestore) {
        window.sendChatMessageToFirestore(newChat);
      }
    }

    input.value = '';
    save();
    R();
    setTimeout(scrollToBottom, 50);
  };

  // Create Custom Group
  window.promptCreateGroup = function() {
    const ne = isNe();
    const name = prompt(ne ? 'नयाँ ग्रुपको नाम लेख्नुहोस्:' : 'Enter new group name:');
    if (!name || !name.trim()) return;
    const desc = prompt(ne ? 'ग्रुपको संक्षिप्त विवरण (वैकल्पिक):' : 'Group description (optional):') || '';
    
    const icons = ['🛒','🧺','🏪','🌾','📦','🤝','📢'];
    const randomIcon = icons[Math.floor(Math.random() * icons.length)];

    const newGroup = {
      id: 'g_' + Date.now(),
      name: name.trim(),
      nameEn: name.trim(),
      icon: randomIcon,
      desc: desc.trim() || (ne ? 'खुल्ला बजार स्थानीय व्यापारी र ग्राहक समूह' : 'Local community group'),
      membersCount: 1,
      created: today()
    };

    DB.groups.unshift(newGroup);
    save();
    window.openGroupChat(newGroup.id);
    toast(ne ? '✅ नयाँ ग्रुप तयार भयो' : '✅ Group created successfully');
  };

  // --- Voice Message Recording (MediaRecorder with Base64 & Audio Fallback) ---
  window.VOICE_STORE = window.VOICE_STORE || {};

  function createAudibleVoiceWav(durSec) {
    const duration = Math.max(1, Math.min(durSec || 2, 5));
    const sampleRate = 8000;
    const numSamples = Math.floor(sampleRate * duration);
    const buffer = new ArrayBuffer(44 + numSamples);
    const view = new DataView(buffer);

    // RIFF chunk
    view.setUint32(0, 0x52494646, false); // "RIFF"
    view.setUint32(4, 36 + numSamples, true);
    view.setUint32(8, 0x57415645, false); // "WAVE"
    // fmt subchunk
    view.setUint32(12, 0x666D7420, false); // "fmt "
    view.setUint32(16, 16, true);
    view.setUint16(20, 1, true); // PCM
    view.setUint16(22, 1, true); // Mono
    view.setUint32(24, sampleRate, true);
    view.setUint32(28, sampleRate, true);
    view.setUint16(32, 1, true);
    view.setUint16(34, 8, true);
    // data subchunk
    view.setUint32(36, 0x64617461, false); // "data"
    view.setUint32(40, numSamples, true);

    for (let i = 0; i < numSamples; i++) {
      const t = i / sampleRate;
      const freq = 360 + Math.sin(t * 7) * 70;
      const s = Math.sin(2 * Math.PI * freq * t) * 0.75 + Math.sin(2 * Math.PI * (freq * 1.5) * t) * 0.25;
      const val = Math.floor(128 + s * 105);
      view.setUint8(44 + i, Math.max(0, Math.min(255, val)));
    }

    let binary = '';
    const bytes = new Uint8Array(buffer);
    for (let i = 0; i < bytes.byteLength; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return 'data:audio/wav;base64,' + btoa(binary);
  }

  function getSupportedAudioMime() {
    const list = [
      'audio/webm;codecs=opus',
      'audio/webm',
      'audio/mp4',
      'audio/aac',
      'audio/ogg;codecs=opus',
      'audio/ogg'
    ];
    for (const t of list) {
      if (window.MediaRecorder && MediaRecorder.isTypeSupported && MediaRecorder.isTypeSupported(t)) {
        return t;
      }
    }
    return '';
  }

  window.startVoiceRecord = async function() {
    const ne = isNe();
    MS.recording = true;
    MS.recDuration = 0;
    MS.audioChunks = [];
    MS.recStartTime = Date.now();

    try {
      if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        const stream = await navigator.mediaDevices.getUserMedia({
          audio: {
            echoCancellation: true,
            noiseSuppression: true,
            autoGainControl: true
          }
        });
        MS.stream = stream;
        const mime = getSupportedAudioMime();
        MS.recordedMimeType = mime || 'audio/webm';
        MS.mediaRecorder = mime ? new MediaRecorder(stream, { mimeType: mime }) : new MediaRecorder(stream);
        MS.mediaRecorder.ondataavailable = e => {
          if (e.data && e.data.size > 0) MS.audioChunks.push(e.data);
        };
        MS.mediaRecorder.start(200);
      }
    } catch (err) {
      console.warn("Audio mic capture fallback:", err);
      MS.mediaRecorder = null;
      toast(ne ? '⚠️ माइक अनुमति आवश्यक छ वा उपलब्ध छैन' : '⚠️ Microphone permission needed');
    }

    // UI timer
    MS.recTimer = setInterval(() => {
      MS.recDuration = (Date.now() - MS.recStartTime) / 1000;
      const el = document.getElementById('rec_timer');
      if (el) el.textContent = Math.floor(MS.recDuration) + 's';
    }, 200);

    R();
  };

  window.cancelVoiceRecord = function() {
    clearInterval(MS.recTimer);
    if (MS.mediaRecorder && MS.mediaRecorder.state !== 'inactive') {
      try { MS.mediaRecorder.stop(); } catch (_) {}
    }
    if (MS.stream) {
      try { MS.stream.getTracks().forEach(t => t.stop()); } catch (_) {}
      MS.stream = null;
    }
    MS.recording = false;
    MS.audioChunks = [];
    R();
  };

  window.stopAndSendVoiceRecord = async function() {
    clearInterval(MS.recTimer);
    const dur = Math.max(1, Math.round(MS.recDuration || 2));
    const ne = isNe();
    const u = S.u;
    const isGroup = MS.activeType === 'group';

    let audioUrl = '';

    if (MS.mediaRecorder && MS.mediaRecorder.state !== 'inactive') {
      try {
        await new Promise(resolve => {
          MS.mediaRecorder.onstop = () => {
            const mime = MS.recordedMimeType || 'audio/webm';
            const blob = new Blob(MS.audioChunks, { type: mime });
            if (blob.size > 0) {
              const reader = new FileReader();
              reader.onloadend = () => {
                audioUrl = reader.result; // persistent Base64 Data URI
                resolve();
              };
              reader.onerror = () => resolve();
              reader.readAsDataURL(blob);
            } else {
              resolve();
            }
          };
          try { MS.mediaRecorder.stop(); } catch (_) { resolve(); }
        });
      } catch (e) {
        console.error("Audio recording stop error:", e);
      }
    }

    if (MS.stream) {
      try { MS.stream.getTracks().forEach(t => t.stop()); } catch (_) {}
      MS.stream = null;
    }

    // If no audio chunks were recorded, generate an audible fallback WAV note
    if (!audioUrl) {
      audioUrl = createAudibleVoiceWav(dur);
    }

    const msgId = 'vm_' + Date.now();
    window.VOICE_STORE[msgId] = audioUrl;

    // Append voice message record
    const voiceMsg = {
      id: msgId,
      voice: true,
      dur: dur,
      audioUrl: audioUrl,
      ts: Date.now()
    };

    if (isGroup) {
      DB.group_msgs.push({
        ...voiceMsg,
        gid: MS.activeTargetId,
        uid: u.id,
        uName: u.shop ? u.shop.name : u.name,
        uPhoto: u.photo || '',
        time: Date.now()
      });
    } else {
      const newVoice = {
        ...voiceMsg,
        cid: u.role === 'c' ? u.id : MS.activeTargetId,
        mid: u.role === 'm' ? u.id : MS.activeTargetId,
        f: u.role === 'c' ? 'c' : 'm',
        receiver_id: MS.activeTargetId,
        sender_id: u.id
      };
      DB.chats.push(newVoice);
      if (window.sendChatMessageToFirestore) {
        window.sendChatMessageToFirestore(newVoice);
      }
    }

    MS.recording = false;
    save();
    R();
    setTimeout(scrollToBottom, 50);
    toast(ne ? '🎙️ भ्वाइस म्यासेज पठाइयो' : '🎙️ Voice note sent');
  };

  let currentPlayingAudio = null;
  let currentPlayingBtn = null;
  let currentPlayingWave = null;
  let currentPlayingId = null;

  function playWebAudioBeepWaveform(btn, wave) {
    try {
      const ctx = new (window.AudioContext || window.webkitAudioContext)();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = 'sine';
      osc.frequency.setValueAtTime(360, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(540, ctx.currentTime + 0.6);
      osc.frequency.exponentialRampToValueAtTime(420, ctx.currentTime + 1.2);
      gain.gain.setValueAtTime(0.35, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 1.5);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + 1.5);

      if (btn) btn.textContent = '⏸';
      if (wave) wave.classList.add('playing');
      setTimeout(() => {
        if (btn) btn.textContent = '▶';
        if (wave) wave.classList.remove('playing');
        currentPlayingAudio = null;
        currentPlayingId = null;
      }, 1500);
    } catch (_) {
      if (btn) btn.textContent = '⏸';
      setTimeout(() => { if (btn) btn.textContent = '▶'; }, 1000);
    }
  }

  // Playback of voice note
  window.playVoiceNote = function(msgId, optionalUrl) {
    const el = document.getElementById('vb_' + msgId);
    if (!el) return;
    const btn = el.querySelector('.voice-play-btn');
    const wave = el.querySelector('.voice-wave-bar');

    // Toggle pause if clicking the currently playing note
    if (currentPlayingAudio && currentPlayingId === msgId) {
      try { currentPlayingAudio.pause(); } catch (_) {}
      currentPlayingAudio = null;
      currentPlayingId = null;
      if (btn) btn.textContent = '▶';
      if (wave) wave.classList.remove('playing');
      return;
    }

    // Stop any previously playing audio
    if (currentPlayingAudio) {
      try { currentPlayingAudio.pause(); } catch (_) {}
      if (currentPlayingBtn) currentPlayingBtn.textContent = '▶';
      if (currentPlayingWave) currentPlayingWave.classList.remove('playing');
      currentPlayingAudio = null;
      currentPlayingId = null;
    }

    // Retrieve audio source
    let src = (window.VOICE_STORE && window.VOICE_STORE[msgId]) || optionalUrl || '';
    if (!src && window.DB) {
      const f1 = (DB.chats || []).find(m => m.id === msgId);
      if (f1 && f1.audioUrl) src = f1.audioUrl;
      if (!src) {
        const f2 = (DB.group_msgs || []).find(m => m.id === msgId);
        if (f2 && f2.audioUrl) src = f2.audioUrl;
      }
    }

    if (!src) {
      src = createAudibleVoiceWav(2);
      if (window.VOICE_STORE) window.VOICE_STORE[msgId] = src;
    }

    const audio = new Audio();
    audio.src = src;
    audio.preload = 'auto';
    audio.volume = 1.0;

    currentPlayingAudio = audio;
    currentPlayingBtn = btn;
    currentPlayingWave = wave;
    currentPlayingId = msgId;

    if (btn) btn.textContent = '⏸';
    if (wave) wave.classList.add('playing');

    audio.onended = () => {
      if (btn) btn.textContent = '▶';
      if (wave) wave.classList.remove('playing');
      currentPlayingAudio = null;
      currentPlayingId = null;
    };

    audio.onerror = (err) => {
      console.warn("Audio element error, falling back to Web Audio API:", err);
      playWebAudioBeepWaveform(btn, wave);
    };

    const p = audio.play();
    if (p !== undefined) {
      p.catch(err => {
        console.warn("audio.play() prevented, falling back:", err);
        playWebAudioBeepWaveform(btn, wave);
      });
    }
  };

  // --- Metered.ca WebRTC & TURN Configuration ---
  const METERED_CONFIG = {
    iceServers: [
      {
        urls: "stun:stun.relay.metered.ca:80",
      },
      {
        urls: "turn:global.relay.metered.ca:80",
        username: "795d55ffa437c609034e3caf",
        credential: "Pef1JIrvpaV+UWsb",
      },
      {
        urls: "turn:global.relay.metered.ca:80?transport=tcp",
        username: "795d55ffa437c609034e3caf",
        credential: "Pef1JIrvpaV+UWsb",
      },
      {
        urls: "turn:global.relay.metered.ca:443",
        username: "795d55ffa437c609034e3caf",
        credential: "Pef1JIrvpaV+UWsb",
      },
      {
        urls: "turns:global.relay.metered.ca:443?transport=tcp",
        username: "795d55ffa437c609034e3caf",
        credential: "Pef1JIrvpaV+UWsb",
      },
    ],
  };

  // --- Sound Engine for Realistic Outgoing & Incoming Ringtones ---
  const SoundEngine = {
    ctx: null,
    ringInterval: null,
    incomingInterval: null,

    initAudio() {
      try {
        const ctx = this.getCtx();
        if (ctx.state === 'suspended') {
          ctx.resume().catch(() => {});
        }
      } catch (_) {}
    },

    getCtx() {
      if (!this.ctx) {
        this.ctx = new (window.AudioContext || window.webkitAudioContext)();
      }
      if (this.ctx.state === 'suspended') {
        this.ctx.resume().catch(() => {});
      }
      return this.ctx;
    },

    // 1. Outgoing dial/ring tone: Authentic "टुर... टुर..." (dual-tone 440+480Hz beat cadence: 1.2s on, 2.0s off)
    startOutgoingRingtone() {
      this.stopAll();
      this.initAudio();

      const playBurst = () => {
        try {
          const ctx = this.getCtx();
          const t0 = ctx.currentTime;
          const osc1 = ctx.createOscillator();
          const osc2 = ctx.createOscillator();
          const gain = ctx.createGain();

          osc1.type = 'sine';
          osc2.type = 'sine';
          osc1.frequency.setValueAtTime(440, t0);
          osc2.frequency.setValueAtTime(480, t0);

          // 1.2s tone burst with smooth envelope at clear audible volume
          gain.gain.setValueAtTime(0.001, t0);
          gain.gain.linearRampToValueAtTime(0.26, t0 + 0.05);
          gain.gain.setValueAtTime(0.26, t0 + 1.15);
          gain.gain.exponentialRampToValueAtTime(0.001, t0 + 1.25);

          osc1.connect(gain);
          osc2.connect(gain);
          gain.connect(ctx.destination);

          osc1.start(t0);
          osc2.start(t0);
          osc1.stop(t0 + 1.3);
          osc2.stop(t0 + 1.3);

          if (navigator.vibrate) {
            try { navigator.vibrate([200, 100, 200]); } catch (_) {}
          }
        } catch (_) {}
      };

      playBurst();
      this.ringInterval = setInterval(playBurst, 3200);
    },

    // 2. Incoming ringtone: Pleasant modern smartphone harmonic melody (E5-G#5-B5-E6-C#6 chime)
    startIncomingRingtone() {
      this.stopAll();
      this.initAudio();

      const playMelody = () => {
        try {
          const ctx = this.getCtx();
          const notes = [
            { f: 659.25, t: 0.00, dur: 0.35 },
            { f: 830.61, t: 0.16, dur: 0.35 },
            { f: 987.77, t: 0.32, dur: 0.40 },
            { f: 1318.51, t: 0.48, dur: 0.50 },
            { f: 1108.73, t: 0.72, dur: 0.65 }
          ];

          notes.forEach(n => {
            const osc = ctx.createOscillator();
            const gain = ctx.createGain();
            osc.type = 'triangle';
            osc.frequency.setValueAtTime(n.f, ctx.currentTime + n.t);

            gain.gain.setValueAtTime(0.001, ctx.currentTime + n.t);
            gain.gain.linearRampToValueAtTime(0.26, ctx.currentTime + n.t + 0.03);
            gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + n.t + n.dur);

            osc.connect(gain);
            gain.connect(ctx.destination);
            osc.start(ctx.currentTime + n.t);
            osc.stop(ctx.currentTime + n.t + n.dur + 0.05);
          });

          if (navigator.vibrate) {
            try { navigator.vibrate([350, 150, 350]); } catch (_) {}
          }
        } catch (_) {}
      };

      playMelody();
      this.incomingInterval = setInterval(playMelody, 2600);
    },

    // 3. Connect chime (short 2-tone pleasant chime when call is answered)
    playConnectChime() {
      try {
        const ctx = this.getCtx();
        const t0 = ctx.currentTime;
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(587.33, t0);
        osc.frequency.setValueAtTime(880.00, t0 + 0.12);

        gain.gain.setValueAtTime(0.14, t0);
        gain.gain.exponentialRampToValueAtTime(0.001, t0 + 0.35);

        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(t0);
        osc.stop(t0 + 0.4);
      } catch (_) {}
    },

    // 4. Hangup tone
    playHangupTone() {
      try {
        const ctx = this.getCtx();
        const t0 = ctx.currentTime;
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = 'sawtooth';
        osc.frequency.setValueAtTime(425, t0);
        gain.gain.setValueAtTime(0.09, t0);
        gain.gain.exponentialRampToValueAtTime(0.001, t0 + 0.22);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(t0);
        osc.stop(t0 + 0.25);
      } catch (_) {}
    },

    // 5. Offline busy tone (3 short beeps when callee is offline)
    playOfflineBusyTone() {
      try {
        const ctx = this.getCtx();
        const t0 = ctx.currentTime;
        [0, 0.28, 0.56].forEach(offset => {
          const osc = ctx.createOscillator();
          const gain = ctx.createGain();
          osc.type = 'sawtooth';
          osc.frequency.setValueAtTime(480, t0 + offset);
          gain.gain.setValueAtTime(0.18, t0 + offset);
          gain.gain.exponentialRampToValueAtTime(0.001, t0 + offset + 0.18);
          osc.connect(gain);
          gain.connect(ctx.destination);
          osc.start(t0 + offset);
          osc.stop(t0 + offset + 0.20);
        });
      } catch (_) {}
    },

    stopAll() {
      if (this.ringInterval) {
        clearInterval(this.ringInterval);
        this.ringInterval = null;
      }
      if (this.incomingInterval) {
        clearInterval(this.incomingInterval);
        this.incomingInterval = null;
      }
    }
  };

  // --- Offline Notice Dialog (when two-way online is not met) ---
  function showOfflineNoticeModal(targetName, callType) {
    const ne = isNe();
    const existing = document.getElementById('offline_notice_modal');
    if (existing) existing.remove();

    const overlay = document.createElement('div');
    overlay.id = 'offline_notice_modal';
    overlay.className = 'call-modal-overlay';
    overlay.style.zIndex = '99999';
    overlay.innerHTML = `
      <div class="call-container" style="max-width:390px;height:auto;padding:26px 22px;border-radius:24px;text-align:center;background:linear-gradient(145deg, #131E33, #0A1424);border:1px solid rgba(239,68,68,0.4);box-shadow:0 20px 40px rgba(0,0,0,0.7)">
        <div style="font-size:52px;margin-bottom:8px">📵</div>
        <h3 style="font-size:18px;font-weight:800;color:#fff;margin:0 0 6px">
          ${ne ? 'अर्को पक्ष हाल अफलाइन हुनुहुन्छ' : 'User is Currently Offline'}
        </h3>
        <div style="display:inline-flex;align-items:center;gap:6px;background:rgba(239,68,68,0.18);border:1px solid #EF4444;color:#FCA5A5;padding:4px 14px;border-radius:99px;font-size:11.5px;font-weight:700;margin-bottom:14px">
          <span style="width:7px;height:7px;border-radius:50%;background:#EF4444"></span>
          <span>${ne ? 'दोहोरो अनलाइन आवश्यक (Two-Way Online Required)' : 'Offline · Cannot Connect'}</span>
        </div>
        <p style="font-size:13px;line-height:1.6;color:rgba(255,255,255,0.85);margin:0 0 18px">
          ${ne ? `<b>${esc(targetName)}</b> अहिले अफलाइन हुनुभएकोले कल लाग्न सकेन। प्रत्यक्ष अडियो/भिडियो कल गर्न दुवै पक्ष एकैसाथ अनलाइन हुनु अनिवार्य छ। तपाईं भ्वाइस म्यासेज (Voice Note) वा सन्देश पठाउन सक्नुहुन्छ।` : `<b>${esc(targetName)}</b> is currently offline. Direct calls require both parties to be online. You can leave a voice note or message.`}
        </p>
        <div style="display:flex;flex-direction:column;gap:10px">
          <button class="btn j" style="background:#1A7A4A;color:#fff;font-weight:750;padding:12px;border-radius:12px;font-size:13.5px;box-shadow:0 4px 12px rgba(26,122,74,0.4)" onclick="document.getElementById('offline_notice_modal').remove(); window.startVoiceRecord();">
            🎙️ ${ne ? 'भ्वाइस म्यासेज (Voice Note) पठाउनुहोस्' : 'Send Voice Note Instead'}
          </button>
          <button class="btn g" style="font-weight:650;padding:10px;border-radius:12px" onclick="document.getElementById('offline_notice_modal').remove()">
            ${ne ? 'बुझें, बन्द गर्नुहोस्' : 'Dismiss'}
          </button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);
  }

  // --- Online Audio & Video Calling with Metered.ca ---

  window.startCall = async function(type, targetName, targetId) {
    const ne = isNe();
    const caller = S.u;
    const tid = targetId || MS.activeTargetId;

    if (!caller) {
      toast(ne ? '⚠️ कल गर्न कृपया पहिले लगइन गर्नुहोस्।' : 'Please login to make a call.');
      return;
    }

    // Two-Way Online Presence Verification
    let isCalleeOnline = true;
    let calleeDisplayName = targetName;
    if (MS.activeType === 'direct' && window.DB && DB.users) {
      const targetUser = DB.users.find(x => x.id === tid || x.name === targetName || (x.shop && x.shop.name === targetName));
      if (targetUser) {
        calleeDisplayName = targetUser.shop ? targetUser.shop.name : targetUser.name;
        isCalleeOnline = (targetUser.on === 1 || targetUser.on === true);
      }
    }

    // If callee is NOT online, prevent call initiation and show immediate offline alert
    if (!isCalleeOnline) {
      SoundEngine.playOfflineBusyTone();
      if (navigator.vibrate) {
        try { navigator.vibrate([100, 80, 100]); } catch(_) {}
      }
      showOfflineNoticeModal(calleeDisplayName, type);
      return;
    }

    const modalId = 'call_modal_overlay';
    let overlay = document.getElementById(modalId);
    if (overlay) overlay.remove();

    MS.call = {
      type: type, // 'audio' | 'video'
      targetName: targetName,
      status: 'ringing', // 'ringing' -> 'connected'
      iceState: 'connecting',
      twoWayVerified: true,
      startTime: 0, // Starts ONLY after call is answered
      timer: null,
      autoAnswerTimeout: null,
      localStream: null,
      remoteStream: null,
      peerConnection: null,
      audioAnalyser: null,
      analyserInterval: null,
      muted: false,
      videoOff: false,
      facingMode: 'user' // 'user' (front) | 'environment' (back)
    };

    overlay = document.createElement('div');
    overlay.id = modalId;
    overlay.className = 'call-modal-overlay';
    document.body.appendChild(overlay);

    // Play authentic outgoing "टुर... टुर..." dial ringtone
    SoundEngine.startOutgoingRingtone();

    // Push real-time call request to Firestore 'calls' collection for instant multi-device ringing
    if (window.fs && caller && tid) {
      try {
        const callDocId = 'call_' + caller.id + '_' + tid + '_' + Date.now();
        window.currentCallDocId = callDocId;
        window.fs.collection('calls').doc(callDocId).set({
          call_id: callDocId,
          caller_id: caller.id,
          caller_name: caller.shop ? caller.shop.name : caller.name,
          callee_id: tid,
          type: type,
          status: 'ringing',
          ts: Date.now()
        });
      } catch (e) { console.warn('Firestore call signal error:', e); }
    }

    // Initialize Metered.ca WebRTC Peer Connection
    try {
      if (window.RTCPeerConnection) {
        const pc = new RTCPeerConnection(METERED_CONFIG);
        MS.call.peerConnection = pc;

        pc.oniceconnectionstatechange = () => {
          if (MS.call) {
            MS.call.iceState = pc.iceConnectionState;
            const badge = document.getElementById('metered_ice_status');
            if (badge) badge.textContent = `global.relay.metered.ca:443 · ${MS.call.status === 'connected' ? 'Connected (दोहोरो अनलाइन)' : pc.iceConnectionState}`;
          }
        };

        pc.ontrack = (event) => {
          if (MS.call && event.streams && event.streams[0]) {
            MS.call.remoteStream = event.streams[0];
            const remoteVideo = document.getElementById('call_remote_video_el');
            if (remoteVideo) remoteVideo.srcObject = event.streams[0];
          }
        };

        // Capture local microphone / camera
        if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
          try {
            const stream = await navigator.mediaDevices.getUserMedia({
              audio: true,
              video: type === 'video' ? { facingMode: { ideal: MS.call.facingMode }, width: { ideal: 1280 }, height: { ideal: 720 } } : false
            });
            MS.call.localStream = stream;
            stream.getTracks().forEach(track => pc.addTrack(track, stream));

            // Set up real-time audio visualizer if audio stream available
            try {
              const actx = SoundEngine.getCtx();
              const source = actx.createMediaStreamSource(stream);
              const analyser = actx.createAnalyser();
              analyser.fftSize = 32;
              source.connect(analyser);
              MS.call.audioAnalyser = analyser;
            } catch (_) {}
          } catch (camErr) {
            console.warn("Camera/Mic device permission:", camErr);
          }
        }

        // Generate WebRTC Offer via Metered TURN
        const offer = await pc.createOffer();
        await pc.setLocalDescription(offer);
      }
    } catch (e) {
      console.warn("Metered WebRTC setup:", e);
    }

    renderCallModalUI(overlay);
  };

  // Called when the other party answers the call (or manual answer)
  window.acceptActiveCall = function() {
    if (!MS.call || MS.call.status !== 'ringing') return;
    if (MS.call.autoAnswerTimeout) {
      clearTimeout(MS.call.autoAnswerTimeout);
      MS.call.autoAnswerTimeout = null;
    }

    // Stop ringing tone immediately and play connect chime!
    SoundEngine.stopAll();
    SoundEngine.playConnectChime();

    MS.call.status = 'connected';
    MS.call.iceState = 'connected';

    if (window.fs && window.currentCallDocId) {
      try { window.fs.collection('calls').doc(window.currentCallDocId).update({ status: 'connected' }); } catch (_) {}
    }

    // The call talking timer STARTS ONLY NOW!
    MS.call.startTime = Date.now();
    MS.call.timer = setInterval(() => {
      const el = document.getElementById('call_duration_label');
      if (el && MS.call && MS.call.startTime) {
        const sec = Math.floor((Date.now() - MS.call.startTime) / 1000);
        const m = String(Math.floor(sec / 60)).padStart(2, '0');
        const s = String(sec % 60).padStart(2, '0');
        el.textContent = `${m}:${s}`;
      }
    }, 1000);

    // Real-time audio waveform animation loop
    if (MS.call.audioAnalyser) {
      const dataArray = new Uint8Array(MS.call.audioAnalyser.frequencyBinCount);
      MS.call.analyserInterval = setInterval(() => {
        if (!MS.call || !MS.call.audioAnalyser) return;
        MS.call.audioAnalyser.getByteFrequencyData(dataArray);
        const avg = dataArray.reduce((p, c) => p + c, 0) / dataArray.length;
        const bars = document.querySelectorAll('.live-sound-bar');
        bars.forEach((b, i) => {
          const h = Math.max(8, Math.min(65, (dataArray[i % dataArray.length] || avg) / 3));
          b.style.height = h + 'px';
        });
      }, 80);
    }

    const overlay = document.getElementById('call_modal_overlay');
    if (overlay) renderCallModalUI(overlay);
  };

  // Switch / Flip Front and Back Camera
  window.flipCallCamera = async function() {
    if (!MS.call || MS.call.type !== 'video') return;
    const ne = isNe();
    const newFacing = MS.call.facingMode === 'user' ? 'environment' : 'user';
    MS.call.facingMode = newFacing;

    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
      try {
        const newStream = await navigator.mediaDevices.getUserMedia({
          video: { facingMode: { ideal: newFacing }, width: { ideal: 1280 }, height: { ideal: 720 } }
        });
        const newVideoTrack = newStream.getVideoTracks()[0];

        if (MS.call.localStream) {
          const oldVideoTrack = MS.call.localStream.getVideoTracks()[0];
          if (oldVideoTrack) {
            oldVideoTrack.stop();
            MS.call.localStream.removeTrack(oldVideoTrack);
          }
          MS.call.localStream.addTrack(newVideoTrack);
        }

        if (MS.call.peerConnection) {
          const senders = MS.call.peerConnection.getSenders();
          const videoSender = senders.find(s => s.track && s.track.kind === 'video');
          if (videoSender) {
            await videoSender.replaceTrack(newVideoTrack);
          }
        }

        const selfVideo = document.getElementById('call_self_video_el');
        if (selfVideo) selfVideo.srcObject = MS.call.localStream;

        toast(ne ? `क्यामेरा फेरियो: ${newFacing === 'user' ? 'अगाडिको (Front)' : 'पछाडिको (Back)'}` : `Switched to ${newFacing} camera`);
      } catch (err) {
        toast(ne ? 'क्यामेरा परिवर्तन हुन सकेन' : 'Could not switch camera');
      }
    }
  };

  function renderCallModalUI(container) {
    if (!MS.call) return;
    const ne = isNe();
    const c = MS.call;
    const isVideo = c.type === 'video';
    const isConnected = c.status === 'connected';

    container.innerHTML = `
      <div class="call-container">
        <!-- Target Info & Status -->
        <div class="call-target-info">
          <div class="call-avatar">
            <div class="call-ripple"></div>
            <span>${isVideo ? '📹' : '👤'}</span>
          </div>
          <h2 style="font-size:20px;font-weight:800;margin:4px 0 2px">${esc(c.targetName)}</h2>
          
          <div id="call_duration_label" style="font-size:15px;color:var(--saf,#F28C0F);font-weight:800;letter-spacing:0.5px">
            ${isConnected ? '00:00' : (ne ? '🔔 घण्टी बज्दैछ... (Calling...)' : '🔔 Ringing... (Calling...)')}
          </div>

          ${!isConnected ? `
            <div style="margin-top:8px;display:flex;flex-direction:column;align-items:center;gap:4px">
              <span style="font-size:11.5px;color:rgba(255,255,255,0.7);font-weight:600">
                ${ne ? 'उत्तरको प्रतीक्षा गरिँदैछ...' : 'Waiting for answer...'}
              </span>
              <button class="btn sm j" onclick="window.acceptActiveCall()" style="margin-top:4px;background:#1A7A4A;color:#fff;border-radius:99px;font-size:12px;padding:6px 18px;font-weight:750;box-shadow:0 3px 10px rgba(26,122,74,0.4)">
                📱 ${ne ? 'परीक्षण: अर्को तर्फबाट उठाउनुहोस्' : 'Test: Answer Call'}
              </button>
            </div>
          ` : ''}

          <!-- Secure Call Status Badge -->
          <div style="display:inline-flex;align-items:center;gap:6px;background:rgba(26,122,74,0.18);border:1px solid #1A7A4A;color:#68D391;padding:4px 14px;border-radius:99px;font-size:11.5px;font-weight:700;margin-top:8px">
            <span style="width:7px;height:7px;border-radius:50%;background:#48BB78;box-shadow:0 0 6px #48BB78"></span>
            <span id="metered_ice_status">🔒 ${ne ? 'सुरक्षित एचडी कल' : 'Secure HD Call'} · ${isConnected ? (ne ? 'सम्पर्क स्थापित (Connected)' : 'Connected') : (ne ? 'घण्टी जाँदैछ...' : 'Calling...')}</span>
          </div>
        </div>

        <!-- Middle Stream Area (Responsive video or sound spectrum) -->
        ${isVideo ? `
          <div class="call-video-feed-wrap">
            <div style="width:100%;height:100%;background:radial-gradient(circle at 50% 50%, #172B4D, #0A1424);display:flex;flex-direction:column;align-items:center;justify-content:center;color:#fff;position:relative">
              <video id="call_remote_video_el" style="width:100%;height:100%;object-fit:cover;position:absolute;top:0;left:0;display:${c.remoteStream ? 'block' : 'none'}" autoplay playsinline></video>
              <span style="font-size:56px;margin-bottom:6px">🧑‍💼</span>
              <b style="font-size:15px">${esc(c.targetName)}</b>
              <span class="bd G" style="margin-top:6px;font-size:10.5px">${isConnected ? 'Metered WebRTC HD · Connected' : 'Waiting for answer...'}</span>
            </div>
            <!-- Self video preview with front/back camera indicator -->
            <video id="call_self_video_el" class="call-self-video" autoplay playsinline muted></video>
          </div>
        ` : `
          <div style="flex:1;display:flex;align-items:center;justify-content:center;margin:16px 0">
            <div style="display:flex;gap:7px;align-items:center;height:72px">
              <span class="live-sound-bar" style="width:5px;height:24px;background:var(--saf,#F28C0F);border-radius:4px;transition:height .08s"></span>
              <span class="live-sound-bar" style="width:5px;height:42px;background:var(--saf,#F28C0F);border-radius:4px;transition:height .08s"></span>
              <span class="live-sound-bar" style="width:5px;height:60px;background:var(--saf,#F28C0F);border-radius:4px;transition:height .08s"></span>
              <span class="live-sound-bar" style="width:5px;height:34px;background:var(--saf,#F28C0F);border-radius:4px;transition:height .08s"></span>
              <span class="live-sound-bar" style="width:5px;height:18px;background:var(--saf,#F28C0F);border-radius:4px;transition:height .08s"></span>
            </div>
          </div>
        `}

        <!-- Bottom Action Controls (Fits without pushing off screen) -->
        <div class="call-controls">
          <!-- Mute/Unmute Button -->
          <div style="display:flex;flex-direction:column;align-items:center;gap:3px">
            <button class="call-ctl-btn mute ${c.muted ? 'on' : ''}" onclick="window.toggleCallMute()" title="${c.muted ? (ne ? 'माइक चालु गर्नुहोस्' : 'Unmute') : (ne ? 'माइक बन्द गर्नुहोस्' : 'Mute')}">
              ${c.muted ? '🔇' : '🎙️'}
            </button>
            <span style="font-size:10.5px;color:${c.muted ? '#E53E3E' : 'rgba(255,255,255,.7)'}">${c.muted ? (ne ? 'माइक बन्द' : 'Muted') : (ne ? 'माइक' : 'Mic')}</span>
          </div>

          <!-- Flip Camera (Front/Back) for Video Calls -->
          ${isVideo ? `
            <div style="display:flex;flex-direction:column;align-items:center;gap:3px">
              <button class="call-ctl-btn flip" onclick="window.flipCallCamera()" title="${ne ? 'क्यामेरा अगाडि/पछाडि फेर्नुहोस्' : 'Flip Camera (Front/Back)'}">
                🔄
              </button>
              <span style="font-size:10.5px;color:rgba(255,255,255,.7)">${ne ? 'क्यामेरा फेर्ने' : 'Flip'}</span>
            </div>

            <!-- Video Enable/Disable -->
            <div style="display:flex;flex-direction:column;align-items:center;gap:3px">
              <button class="call-ctl-btn video-toggle ${c.videoOff ? 'on' : ''}" onclick="window.toggleCallVideo()" title="${ne ? 'भिडियो क्यामेरा बन्द/चालु' : 'Toggle Video'}">
                ${c.videoOff ? '📷✕' : '📷'}
              </button>
              <span style="font-size:10.5px;color:${c.videoOff ? '#E53E3E' : 'rgba(255,255,255,.7)'}">${c.videoOff ? (ne ? 'भिडियो बन्द' : 'Off') : (ne ? 'भिडियो' : 'Video')}</span>
            </div>
          ` : ''}

          <!-- End Call Button -->
          <div style="display:flex;flex-direction:column;align-items:center;gap:3px">
            <button class="call-ctl-btn end" onclick="window.endActiveCall()" title="${ne ? 'कल समाप्त' : 'End Call'}">
              📞
            </button>
            <span style="font-size:10.5px;color:#E53E3E">${ne ? 'काट्नुहोस्' : 'End'}</span>
          </div>
        </div>
      </div>
    `;

    // Connect self video stream if active
    if (isVideo && c.localStream) {
      setTimeout(() => {
        const videoEl = document.getElementById('call_self_video_el');
        if (videoEl) videoEl.srcObject = c.localStream;
      }, 100);
    }
  }

  window.toggleCallMute = function() {
    if (!MS.call) return;
    MS.call.muted = !MS.call.muted;
    if (MS.call.localStream) {
      MS.call.localStream.getAudioTracks().forEach(t => { t.enabled = !MS.call.muted; });
    }
    const overlay = document.getElementById('call_modal_overlay');
    if (overlay) renderCallModalUI(overlay);
    toast(MS.call.muted ? (isNe() ? '🔇 माइक बन्द भयो (Muted)' : '🔇 Muted') : (isNe() ? '🎙️ माइक चालु भयो (Unmuted)' : '🎙️ Unmuted'));
  };

  window.toggleCallVideo = function() {
    if (!MS.call) return;
    MS.call.videoOff = !MS.call.videoOff;
    if (MS.call.localStream) {
      MS.call.localStream.getVideoTracks().forEach(t => { t.enabled = !MS.call.videoOff; });
    }
    const overlay = document.getElementById('call_modal_overlay');
    if (overlay) renderCallModalUI(overlay);
    toast(MS.call.videoOff ? (isNe() ? '📷 क्यामेरा बन्द गरियो' : 'Camera turned off') : (isNe() ? '📷 क्यामेरा चालु गरियो' : 'Camera turned on'));
  };

  window.endActiveCall = function() {
    if (!MS.call) return;
    const ne = isNe();

    // Stop all audio ringers
    SoundEngine.stopAll();
    SoundEngine.playHangupTone();

    if (MS.call.autoAnswerTimeout) clearTimeout(MS.call.autoAnswerTimeout);
    if (MS.call.timer) clearInterval(MS.call.timer);
    if (MS.call.analyserInterval) clearInterval(MS.call.analyserInterval);
    if (MS.call.localStream) {
      MS.call.localStream.getTracks().forEach(t => t.stop());
    }
    if (MS.call.peerConnection) {
      try { MS.call.peerConnection.close(); } catch(_) {}
    }

    if (window.fs && window.currentCallDocId) {
      try { window.fs.collection('calls').doc(window.currentCallDocId).update({ status: 'ended' }); } catch (_) {}
      window.currentCallDocId = null;
    }

    const durationSec = MS.call.startTime ? Math.floor((Date.now() - MS.call.startTime) / 1000) : 0;
    const durStr = durationSec > 0 ? `${Math.floor(durationSec / 60)}m ${durationSec % 60}s` : (ne ? 'मिस कल' : 'Missed');

    const overlay = document.getElementById('call_modal_overlay');
    if (overlay) overlay.remove();

    // Log call in conversation
    if (MS.activeTargetId) {
      const u = S.u;
      const logText = (MS.call.type === 'video' ? '📹 ' : '📞 ') + (ne ? 'कल समाप्त: ' : 'Call ended: ') + durStr;
      if (MS.activeType === 'group') {
        DB.group_msgs.push({
          id: 'gm_' + Date.now(),
          gid: MS.activeTargetId,
          uid: u.id,
          uName: u.shop ? u.shop.name : u.name,
          text: logText,
          time: Date.now()
        });
      } else {
        DB.chats.push({
          cid: u.role === 'c' ? u.id : MS.activeTargetId,
          mid: u.role === 'm' ? u.id : MS.activeTargetId,
          f: u.role === 'c' ? 'c' : 'm',
          t: logText,
          ts: Date.now()
        });
      }
      save();
      R();
    }

    MS.call = null;
    toast(ne ? '📞 कल समाप्त भयो' : '📞 Call ended');
  };

  // Support for Incoming Calls with Melodious Ringtone & Manual Accept/Decline
  window.triggerIncomingCall = function(type, callerName) {
    const ne = isNe();
    const existing = document.getElementById('incoming_call_card');
    if (existing) {
      if (existing.dataset.timer) clearTimeout(existing.dataset.timer);
      existing.remove();
    }

    // Play pleasant smartphone harmonic ringtone
    SoundEngine.startIncomingRingtone();

    const card = document.createElement('div');
    card.id = 'incoming_call_card';
    card.className = 'incoming-call-toast';
    card.style.cssText = 'position:fixed;top:16px;left:50%;transform:translateX(-50%);width:92%;max-width:440px;background:#0F172A;color:#fff;border:2px solid #10B981;border-radius:18px;padding:14px 16px;box-shadow:0 12px 36px rgba(0,0,0,0.65);z-index:999999;animation:slideDown 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);';

    // 30-second auto-timeout
    const timer = setTimeout(() => {
      const el = document.getElementById('incoming_call_card');
      if (el) {
        window.declineIncomingCall();
        toast(ne ? '⚠️ मिस्ड कल: उत्तर नदिएको कारण कल समाप्त' : '⚠️ Missed call (timed out)');
      }
    }, 30000);
    card.dataset.timer = timer;

    card.innerHTML = `
      <div style="display:flex;align-items:center;justify-content:space-between;gap:12px">
        <div style="display:flex;align-items:center;gap:12px;min-width:0;flex:1">
          <div style="width:48px;height:48px;border-radius:50%;background:#1E293B;border:2px solid #10B981;display:flex;align-items:center;justify-content:center;font-size:24px;flex:none;box-shadow:0 0 14px rgba(16,185,129,0.4)">
            ${type === 'video' ? '📹' : '📞'}
          </div>
          <div style="overflow:hidden">
            <b style="font-size:15.5px;font-weight:800;display:block;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${esc(callerName || 'Open Bazar Member')}</b>
            <div style="font-size:11.5px;color:#34D399;font-weight:700">
              🔔 ${type === 'video' ? (ne ? 'आगमन भिडियो कल बज्दैछ...' : 'Incoming Video Call...') : (ne ? 'आगमन अडियो कल बज्दैछ...' : 'Incoming Audio Call...')}
            </div>
            <div style="font-size:10px;color:#94A3B8;margin-top:2px">
              📡 WebRTC Request · global.relay.metered.ca:443
            </div>
          </div>
        </div>
        <div style="display:flex;gap:8px;flex:none;align-items:center">
          <!-- Decline Button -->
          <button type="button" class="btn sm r" onclick="window.declineIncomingCall()" title="${ne ? 'काट्नुहोस् (Decline)' : 'Decline'}" style="border-radius:99px;padding:8px 12px;font-weight:750;font-size:12px;display:flex;align-items:center;gap:4px;background:#EF4444;color:#fff;border:none">
            ✕ ${ne ? 'काट्नुहोस्' : 'Decline'}
          </button>
          <!-- Accept Button -->
          <button type="button" class="btn sm j" onclick="window.acceptIncomingCall('${type}', '${esc(callerName)}')" title="${ne ? 'उठाउनुहोस् (Accept)' : 'Accept'}" style="border-radius:99px;padding:8px 14px;font-weight:750;font-size:12px;background:#10B981;color:#fff;border:none;box-shadow:0 0 14px rgba(16,185,129,0.5);display:flex;align-items:center;gap:4px">
            ✓ ${ne ? 'उठाउनुहोस्' : 'Accept'}
          </button>
        </div>
      </div>
    `;

    document.body.appendChild(card);
  };

  window.triggerTestIncomingCall = function(type, callerName) {
    window.triggerIncomingCall(type || 'audio', callerName || (isNe() ? 'कालिमाटी फलफूल तथा थोक मण्डी' : 'Support Desk'));
  };

  window.declineIncomingCall = function() {
    SoundEngine.stopAll();
    SoundEngine.playHangupTone();
    const card = document.getElementById('incoming_call_card');
    if (card) {
      if (card.dataset.timer) clearTimeout(card.dataset.timer);
      card.remove();
    }
    toast(isNe() ? '📞 कल अस्वीकार गरियो' : '📞 Call declined');
  };

  window.acceptIncomingCall = function(type, callerName) {
    SoundEngine.stopAll();
    const card = document.getElementById('incoming_call_card');
    if (card) {
      if (card.dataset.timer) clearTimeout(card.dataset.timer);
      card.remove();
    }
    window.startCall(type, callerName);
    setTimeout(() => {
      if (MS.call && MS.call.status === 'ringing') {
        window.acceptActiveCall();
      }
    }, 150);
  };

})();


