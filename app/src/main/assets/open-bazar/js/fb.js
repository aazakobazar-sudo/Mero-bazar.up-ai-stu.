/* Firebase Auth, Firestore Real-time Multi-Device Sync (Products, Chats, Orders, Khata) & Migration */
(function(){
const cfg = {
  apiKey: "AIzaSyA48YiJe9WQNGcs0_a8ADtDPVn7AqbbkbU",
  authDomain: "aazabo-bazar.firebaseapp.com",
  projectId: "aazabo-bazar",
  storageBucket: "aazabo-bazar.firebasestorage.app",
  messagingSenderId: "813051695868",
  appId: "1:813051695868:web:dd227f036d4dd07b967d79"
};

// Cloud status dot indicator
const bd = document.createElement('div');
bd.style.cssText = 'position:fixed;bottom:6px;left:6px;z-index:99999;width:14px;height:14px;border-radius:50%;border:2px solid #fff;box-shadow:0 0 4px #0006;background:#e53935;cursor:pointer';
const st = s => {
  const ok = /^(online|saved|live)/.test(s);
  bd.style.background = ok ? '#10B981' : '#e53935';
  bd.dataset.s = s;
  bd.title = 'Mero Bazar Cloud Sync: ' + s;
  if (!bd.parentNode && document.documentElement) document.documentElement.appendChild(bd);
};
st(typeof firebase === 'undefined' ? 'Firebase SDK not loaded' : 'connecting...');
bd.onclick = () => {
  try {
    toast(bd.dataset.s.indexOf('online') >= 0 || bd.dataset.s.indexOf('live') >= 0 ? '🟢 रियल-टाइम क्लाउड सिंक सक्रिय छ' : 'सिंक स्थिति: ' + bd.dataset.s);
  } catch (_) {}
  if (window.cloudPull) window.cloudPull();
};

/* Map picker (OpenStreetMap + Leaflet, Nepal only) */
window.pickLoc = function(cb, must) {
  const ld = () => new Promise((ok, no) => {
    if (window.L) return ok();
    const l = document.createElement('link');
    l.rel = 'stylesheet';
    l.href = 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css';
    document.head.appendChild(l);
    const s = document.createElement('script');
    s.src = 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.js';
    s.onload = ok;
    s.onerror = no;
    document.head.appendChild(s);
  });
  const B = [[26.3, 80.0], [30.5, 88.3]];
  const w = document.createElement('div');
  w.style.cssText = 'position:fixed;inset:0;z-index:100000;background:#fff;color:#111;display:flex;flex-direction:column;font-family:system-ui,sans-serif';
  w.innerHTML = '<div style="padding:12px 14px;font-size:17px;font-weight:700">📍 Select your location in Nepal</div><div style="padding:0 14px 8px;font-size:13px;color:#555">Tap the road/place near your shop. A red pin will mark it.</div><div id="pkm" style="flex:1"></div><div style="display:flex;gap:8px;padding:10px"><button id="pkg" style="flex:1;padding:12px;border-radius:10px;border:1px solid #888;background:#fff;font-size:15px">My GPS</button><button id="pks" style="flex:2;padding:12px;border-radius:10px;border:0;background:#c62828;color:#fff;font-size:16px;font-weight:700">Save location</button>' + (must ? '' : '<button id="pkc" style="flex:1;padding:12px;border-radius:10px;border:1px solid #888;background:#fff;font-size:15px">Cancel</button>') + '</div>';
  document.body.appendChild(w);
  let pt = null, mk = null, map;
  const inN = (a, o) => a >= B[0][0] && a <= B[1][0] && o >= B[0][1] && o <= B[1][1];
  const put = (a, o) => {
    if (!inN(a, o)) return toast('Please select a place inside Nepal');
    pt = [a, o];
    if (mk) mk.setLatLng(pt);
    else mk = L.circleMarker(pt, { radius: 11, color: '#fff', weight: 3, fillColor: '#e53935', fillOpacity: 1 }).addTo(map);
  };
  ld().then(() => {
    map = L.map('pkm', { maxBounds: B, minZoom: 6 }).setView([27.7, 85.32], 12);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '© OpenStreetMap' }).addTo(map);
    map.on('click', e => put(e.latlng.lat, e.latlng.lng));
    const u = window.S && S.u;
    if (u && u.la != null && inN(u.la, u.lo)) {
      map.setView([u.la, u.lo], 16);
      put(u.la, u.lo);
    }
  }).catch(() => {
    toast('Map could not load (check internet)');
    w.remove();
  });
  w.querySelector('#pkg').onclick = () => navigator.geolocation && navigator.geolocation.getCurrentPosition(r => {
    if (map) {
      map.setView([r.coords.latitude, r.coords.longitude], 17);
      put(r.coords.latitude, r.coords.longitude);
    }
  }, () => toast('GPS not available'), { enableHighAccuracy: true, timeout: 15000 });
  w.querySelector('#pks').onclick = () => {
    if (!pt) return toast('Tap on the map first');
    w.remove();
    cb(+pt[0].toFixed(6), +pt[1].toFixed(6));
  };
  const c = w.querySelector('#pkc');
  if (c) c.onclick = () => w.remove();
};

let auth = null;
let fs = null;

function tryInitFirebase() {
  try {
    if (typeof firebase !== 'undefined') {
      if (!firebase.apps.length) {
        firebase.initializeApp(cfg);
      }
      auth = firebase.auth();
      fs = firebase.firestore();
      fs.settings({ experimentalAutoDetectLongPolling: true, merge: true });
      try { fs.enablePersistence({ synchronizeTabs: true }).catch(() => {}); } catch (_) {}
      window.fs = fs;
      startRealtimeListeners();
      st('live - connected to cloud');
      return true;
    }
  } catch (e) {
    console.warn('Firebase init error:', e);
  }
  return false;
}

if (!tryInitFirebase()) {
  let attempts = 0;
  const pollTimer = setInterval(() => {
    attempts++;
    if (tryInitFirebase() || attempts > 20) {
      clearInterval(pollTimer);
    }
  }, 400);
}

window.forgot = async function() {
  const e = em($('em') && $('em').value);
  if (!e) return toast('Enter your email first');
  if (!auth) return toast('Firebase connecting, please wait...');
  try {
    await auth.sendPasswordResetEmail(e);
    toast('Reset link sent. Check your email (also Spam).');
  } catch (x) {
    toast('Cloud: ' + (x.code || x));
  }
};

let wt = 0;
const warn = (w, x) => {
  console.warn(w, x);
  st('error: ' + ((x && x.code) || x));
};
let cred = null;
const em = s => String(s || '').trim().toLowerCase();

var mirror = async function mirror(c, u) {
  if (!auth) return;
  try {
    let r;
    const g0 = auth.currentUser;
    if (g0 && g0.email && g0.email.toLowerCase() === c.e && !g0.providerData.some(q => q.providerId == 'password')) {
      await g0.linkWithCredential(firebase.auth.EmailAuthProvider.credential(c.e, c.p));
      r = { user: g0 };
    } else {
      try {
        r = await auth.signInWithEmailAndPassword(c.e, c.p);
      } catch (x) {
        if (['auth/user-not-found', 'auth/invalid-credential', 'auth/invalid-login-credentials'].includes(x.code)) {
          r = await auth.createUserWithEmailAndPassword(c.e, c.p);
          try {
            await r.user.sendEmailVerification();
            toast('Verification email sent. Please check your inbox/Spam.');
          } catch (_) {}
        } else throw x;
      }
    }
    if (u && r.user && u.fu !== r.user.uid) {
      u.fu = r.user.uid;
      save();
    }
    if (u && u.role == 'a' && typeof claimAdmin == 'function') claimAdmin(r.user);
  } catch (x) {
    warn('fb auth', x);
  }
};

const _login = window.login;
window.login = function(e, p) {
  cred = { e: em(e), p: p };
  return _login.apply(this, arguments);
};
const _reg = window.regNext;
window.regNext = function() {
  try {
    rc();
    const d = S.r && S.r.d;
    if (d && d.em2 && d.pw1) cred = { e: em(d.em2), p: d.pw1 };
  } catch (_) {}
  return _reg.apply(this, arguments);
};
const _sess = window.sess;
window.sess = function(u) {
  const c = cred;
  cred = null;
  const r = _sess.apply(this, arguments);
  if (c && S.u === u) mirror(c, u);
  return r;
};
const _lo = window.logout;
window.logout = function() {
  try { if (auth) auth.signOut(); } catch (_) {}
  return _lo.apply(this, arguments);
};

/* ---- FIRESTORE REAL-TIME CORE ---- */
const BADK = new Set(['photo', 'doc', 'tok', 'pass', 'salt', 'fu']);
  const cl = x => {
    if (Array.isArray(x)) return x.map(cl).filter(v => v !== undefined);
    if (x && typeof x == 'object') {
      const o = {};
      for (const k in x) {
        if (BADK.has(k) || k[0] == '_') continue;
        const v = cl(x[k]);
        if (v !== undefined) o[k] = v;
      }
      return o;
    }
    if (typeof x == 'string' && (x.startsWith('data:') || x.length > 3000)) return undefined;
    if (typeof x == 'number' && !isFinite(x)) return 0;
    return x;
  };
  const hh = s => {
    let c = ~0;
    for (let i = 0; i < s.length; i++) {
      c ^= s.charCodeAt(i);
      for (let k = 0; k < 8; k++) c = c >>> 1 ^ (c & 1 ? 0xEDB88320 : 0);
    }
    return 1e6 + ((~c) >>> 0) % 9e8;
  };

  const shrink = (src, maxDim = 480, qual = 0.6) => new Promise(ok => {
    const i = new Image();
    i.onload = () => {
      const k = Math.min(1, maxDim / Math.max(i.width, i.height));
      const c = document.createElement('canvas');
      c.width = Math.round(i.width * k);
      c.height = Math.round(i.height * k);
      c.getContext('2d').drawImage(i, 0, 0, c.width, c.height);
      let q = qual, r;
      for (;;) {
        r = c.toDataURL('image/jpeg', q);
        if (r.length <= 90000 || q <= 0.3) break;
        q -= 0.1;
      }
      ok(r.length <= 110000 ? r : null);
    };
    i.onerror = () => ok(null);
    i.src = src;
  });

  const mine = () => {
    const u = window.S && S.u;
    const cu = auth.currentUser;
    return u && cu && u.fu === cu.uid ? { u, cu } : (u ? { u, cu: { uid: 'u_' + u.id } } : null);
  };

  /* ---- REAL-TIME CHAT SYNC (PDF 4.2.6 CHAT TABLE) ---- */
  window.sendChatMessageToFirestore = async function(msg) {
    if (!fs) return false;
    try {
      const docId = 'msg_' + (msg.id || Date.now() + '_' + Math.random().toString(36).substr(2, 5));
      await fs.collection('chats').doc(docId).set({
        id: msg.id || docId,
        cid: msg.cid,
        mid: msg.mid,
        f: msg.f || (S.u?.role === 'c' ? 'c' : 'm'),
        sender_id: S.u ? S.u.id : (msg.f === 'c' ? msg.cid : msg.mid),
        receiver_id: msg.receiver_id || (S.u?.role === 'c' ? msg.mid : msg.cid),
        sender_name: S.u ? (S.u.shop ? S.u.shop.name : S.u.name) : 'User',
        t: msg.t || '',
        voice: !!msg.voice,
        audioUrl: msg.audioUrl || '',
        img: msg.img || '',
        productLink: msg.productLink || null,
        ts: msg.ts || Date.now(),
        read: false
      }, { merge: true });
      return true;
    } catch (e) {
      console.warn('Realtime chat push error:', e);
      return false;
    }
  };

  /* ---- REAL-TIME PRODUCT SYNC (PDF 4.2.3 PRODUCTS TABLE) ---- */
  window.saveProductToFirestore = async function(product) {
    if (!fs) return false;
    try {
      const pid = 'prod_' + (product.id || Date.now());
      const shopOwner = S.u;
      const cleanProd = {
        id: product.id,
        shop_id: product.mid || (shopOwner ? shopOwner.id : 0),
        shop_name: shopOwner?.shop?.name || '',
        product_name: product.name,
        category: product.type || (product.cat ? product.cat : 'अन्य'),
        price: Number(product.price || 0),
        discount_price: Number(product.dis || 0),
        stock: Number(product.stock || 0),
        weight_specs: product.specs || '',
        delivery_charge: Number(product.deliv || 0),
        delivery_contact: product.delivPhone || '',
        product_image: product.img ? (product.img.length > 50000 ? await shrink(product.img) : product.img) : '',
        created_date: product.d || new Date().toISOString().slice(0, 10),
        ts: Date.now()
      };
      await fs.collection('products').doc(pid).set(cleanProd, { merge: true });
      // Also update merchant pub bundle
      if (shopOwner && shopOwner.role === 'm') {
        window.cloudPush && window.cloudPush();
      }
      return true;
    } catch (e) {
      console.warn('Realtime product save error:', e);
      return false;
    }
  };

  /* ---- REAL-TIME ORDER SYNC (PDF 4.2.4 ORDERS TABLE) ---- */
  window.saveOrderToFirestore = async function(order) {
    if (!fs) return false;
    try {
      const oid = 'ord_' + (order.id || Date.now());
      await fs.collection('orders').doc(oid).set({
        id: order.id,
        cid: order.cid,
        mid: order.mid,
        items: order.items || [],
        total: order.total || 0,
        pay: order.pay || 'COD',
        st: order.st || 'Pending',
        d: order.d || new Date().toISOString().slice(0, 10),
        customer_name: S.u?.name || '',
        customer_phone: S.u?.phone || '',
        ts: Date.now()
      }, { merge: true });
      return true;
    } catch (e) {
      console.warn('Realtime order save error:', e);
      return false;
    }
  };

  /* ---- REAL-TIME KHATA SYNC (PDF 4.2.5 KHATA TABLE) ---- */
  window.saveKhataToFirestore = async function(khata) {
    if (!fs) return false;
    try {
      const kid = 'kht_' + (khata.id || Date.now());
      await fs.collection('khata').doc(kid).set({
        id: khata.id,
        mid: khata.mid || (S.u ? S.u.id : 0),
        name: khata.name || '',
        phone: khata.phone || '',
        amt: Number(khata.amt || 0),
        paid: Number(khata.paid || 0),
        note: khata.note || '',
        due_date: khata.due_date || '',
        ts: Date.now()
      }, { merge: true });
      return true;
    } catch (e) {
      console.warn('Realtime khata save error:', e);
      return false;
    }
  };

  /* ---- SOUND ALERT FOR REALTIME INCOMING UPDATES ---- */
  function playToneAlert(type = 'msg') {
    try {
      const ctx = new (window.AudioContext || window.webkitAudioContext)();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);
      if (type === 'msg') {
        osc.frequency.setValueAtTime(587.33, ctx.currentTime); // D5
        osc.frequency.setValueAtTime(880, ctx.currentTime + 0.1); // A5
        gain.gain.setValueAtTime(0.25, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.35);
        osc.start(ctx.currentTime);
        osc.stop(ctx.currentTime + 0.35);
      } else if (type === 'order') {
        osc.frequency.setValueAtTime(523.25, ctx.currentTime); // C5
        osc.frequency.setValueAtTime(659.25, ctx.currentTime + 0.12); // E5
        osc.frequency.setValueAtTime(783.99, ctx.currentTime + 0.24); // G5
        gain.gain.setValueAtTime(0.3, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.5);
        osc.start(ctx.currentTime);
        osc.stop(ctx.currentTime + 0.5);
      }
    } catch (_) {}
  }

  /* ---- ACTIVE REAL-TIME SNAPSHOT LISTENERS ---- */
  let activeListeners = [];
  function startRealtimeListeners() {
    activeListeners.forEach(unsub => { try { unsub(); } catch (_) {} });
    activeListeners = [];

    // 1. Real-time Chats Listener
    try {
      const chatUnsub = fs.collection('chats').orderBy('ts', 'desc').limit(250).onSnapshot(snapshot => {
        if (!window.DB) return;
        if (!DB.chats) DB.chats = [];
        let hasNew = false;
        let incomingFromOther = null;

        snapshot.docChanges().forEach(change => {
          if (change.type === 'added' || change.type === 'modified') {
            const data = change.doc.data();
            const existsIdx = DB.chats.findIndex(m => m.id === data.id || (m.ts === data.ts && m.cid === data.cid && m.mid === data.mid));
            if (existsIdx >= 0) {
              DB.chats[existsIdx] = Object.assign(DB.chats[existsIdx], data);
            } else {
              DB.chats.push(data);
              hasNew = true;
              // Check if incoming to me from someone else
              const myId = S.u ? S.u.id : null;
              if (myId && data.sender_id !== myId && (data.receiver_id === myId || (S.u.role === 'm' && data.mid === myId) || (S.u.role === 'c' && data.cid === myId))) {
                if (Date.now() - (data.ts || 0) < 15000) {
                  incomingFromOther = data;
                }
              }
            }
          }
        });

        if (incomingFromOther) {
          playToneAlert('msg');
          toast(`💬 ${incomingFromOther.sender_name || 'नयाँ सन्देश'}: ${incomingFromOther.t ? (incomingFromOther.t.slice(0, 35) + '...') : 'भ्वाइस म्यासेज'}`);
        }

        if (hasNew || incomingFromOther) {
          save();
          if (window.S && (S.tab === 'msg' || S.sub === 'chats' || S.sub === 'chat')) {
            R();
          }
        }
      }, err => console.warn('Realtime chat error:', err));
      activeListeners.push(chatUnsub);
    } catch (e) { console.warn('chat listener init err', e); }

    // 2. Real-time Products Listener (All Shops)
    try {
      const prodUnsub = fs.collection('products').onSnapshot(snapshot => {
        if (!window.DB) return;
        if (!DB.products) DB.products = [];
        let updated = false;

        snapshot.docChanges().forEach(change => {
          const d = change.doc.data();
          if (change.type === 'added' || change.type === 'modified') {
            const idx = DB.products.findIndex(p => p.id === d.id);
            const mappedP = {
              id: d.id,
              mid: d.shop_id || 1,
              name: d.product_name,
              type: d.category || 'अन्य',
              price: d.price || 0,
              dis: d.discount_price || 0,
              stock: d.stock || 0,
              specs: d.weight_specs || '',
              deliv: d.delivery_charge || 0,
              delivPhone: d.delivery_contact || '',
              img: d.product_image || '',
              d: d.created_date || today(),
              _cloud: true
            };
            if (idx >= 0) {
              DB.products[idx] = Object.assign(DB.products[idx], mappedP);
            } else {
              DB.products.unshift(mappedP);
            }
            updated = true;
          } else if (change.type === 'removed') {
            DB.products = DB.products.filter(p => p.id !== d.id);
            updated = true;
          }
        });

        if (updated) {
          save();
          if (window.S && (S.tab === 'home' || S.tab === 'cats' || S.tab === 'shops' || S.tab === 'prods')) {
            R();
          }
        }
      }, err => console.warn('Realtime product error:', err));
      activeListeners.push(prodUnsub);
    } catch (e) { console.warn('prod listener init err', e); }

    // 3. Real-time Orders Listener
    try {
      const ordUnsub = fs.collection('orders').onSnapshot(snapshot => {
        if (!window.DB) return;
        if (!DB.orders) DB.orders = [];
        let updated = false;
        let newOrderForMe = false;

        snapshot.docChanges().forEach(change => {
          const d = change.doc.data();
          if (change.type === 'added' || change.type === 'modified') {
            const idx = DB.orders.findIndex(o => o.id === d.id);
            const ord = {
              id: d.id,
              cid: d.cid,
              mid: d.mid,
              items: d.items || [],
              total: d.total || 0,
              st: d.st || 'Pending',
              pay: d.pay || 'COD',
              d: d.d || today(),
              _cloud: true
            };
            if (idx >= 0) {
              if (DB.orders[idx].st !== ord.st) {
                toast(`📦 अर्डर #${ord.id} स्थिति: ${ord.st}`);
              }
              DB.orders[idx] = Object.assign(DB.orders[idx], ord);
            } else {
              DB.orders.unshift(ord);
              if (S.u && S.u.role === 'm' && ord.mid === S.u.id && Date.now() - (d.ts || 0) < 30000) {
                newOrderForMe = true;
              }
            }
            updated = true;
          }
        });

        if (newOrderForMe) {
          playToneAlert('order');
          toast('🔔 नयाँ अर्डर प्राप्त भयो! तुरुन्त अर्डर सूची हेर्नुहोस्।');
        }

        if (updated) {
          save();
          if (window.S && (S.tab === 'orders' || S.sub === 'orders' || S.tab === 'home')) {
            R();
          }
        }
      }, err => console.warn('Realtime orders error:', err));
      activeListeners.push(ordUnsub);
    } catch (e) { console.warn('order listener init err', e); }

    // 4. Real-time Digital Khata Listener
    try {
      const khtUnsub = fs.collection('khata').onSnapshot(snapshot => {
        if (!window.DB) return;
        if (!DB.khata) DB.khata = [];
        let updated = false;

        snapshot.docChanges().forEach(change => {
          const d = change.doc.data();
          if (change.type === 'added' || change.type === 'modified') {
            const idx = DB.khata.findIndex(k => k.id === d.id);
            const item = {
              id: d.id,
              mid: d.mid,
              name: d.name,
              phone: d.phone || '',
              amt: d.amt || 0,
              paid: d.paid || 0,
              note: d.note || '',
              due_date: d.due_date || '',
              _cloud: true
            };
            if (idx >= 0) {
              DB.khata[idx] = Object.assign(DB.khata[idx], item);
            } else {
              DB.khata.unshift(item);
            }
            updated = true;
          }
        });

        if (updated) {
          save();
          if (window.S && (S.tab === 'khata' || S.sub === 'khata')) {
            R();
          }
        }
      }, err => console.warn('Realtime khata error:', err));
      activeListeners.push(khtUnsub);
    } catch (e) { console.warn('khata listener init err', e); }

    // 5. Merchant Shop Bundles (pub collection)
    try {
      const pubUnsub = fs.collection('pub').onSnapshot(snapshot => {
        const me = auth.currentUser && auth.currentUser.uid;
        snapshot.docChanges().forEach(change => {
          if (change.doc.id === me) return;
          try {
            const o = JSON.parse(change.doc.data().j);
            if (!o.u || !o.u.shop) return;
            const b = hh(change.doc.id);
            const u0 = Object.assign({}, o.u, { id: b, role: 'm', on: 1, _r: 1, _uid: change.doc.id });
            const existUIdx = DB.users.findIndex(x => x._uid === change.doc.id || x.id === b);
            if (existUIdx >= 0) DB.users[existUIdx] = u0;
            else DB.users.push(u0);

            (o.p || []).forEach(p => {
              const q = Object.assign({}, p, { id: b * 10000 + (p.id % 10000), mid: b, _r: 1 });
              const existPIdx = DB.products.findIndex(x => x.id === q.id);
              if (existPIdx >= 0) DB.products[existPIdx] = q;
              else DB.products.push(q);
            });
          } catch (_) {}
        });
        st('live - ' + DB.users.filter(x => x.role === 'm').length + ' shops connected');
      }, err => console.warn('pub listener err', err));
      activeListeners.push(pubUnsub);
    } catch (e) { console.warn('pub listener err', e); }
  }

  // Start real-time listeners immediately
  startRealtimeListeners();

  /* ---- PUSH & PULL SYSTEM ---- */
  var push = async function push() {
    const m = mine();
    if (!m) return;
    const { u, cu } = m;
    let er = null;

    try {
      // 1. Sync user profile
      await fs.collection('priv').doc(cu.uid).set({ j: JSON.stringify(cl(u)), ts: Date.now() });
      await fs.collection('dir').doc(cu.uid).set({
        j: JSON.stringify({ n: u.name || '', r: u.role, p: u.phone || '', d: [u.dist, u.local].filter(Boolean).join(', ') }),
        ts: Date.now()
      });

      // 2. If merchant, sync shop & products
      if (u.role === 'm' && u.shop) {
        let o = {
          u: cl(u),
          p: cl(DB.products.filter(x => x.mid == u.id)),
          n: cl((DB.notices || []).filter(x => x.mid == u.id))
        };
        let j = JSON.stringify(o);
        while (j.length > 55000 && (o.n.length || o.p.length)) {
          (o.n.length ? o.n : o.p).pop();
          j = JSON.stringify(o);
        }
        await fs.collection('pub').doc(cu.uid).set({ j, ts: Date.now() });

        // Also push each product individually to products collection
        for (const p of DB.products.filter(x => x.mid == u.id)) {
          window.saveProductToFirestore(p);
        }
      }

      st('saved to cloud');
    } catch (x) {
      er = x;
      warn('push', x);
    }
  };

  window.cloudPush = push;
  window.cloudPull = () => {
    startRealtimeListeners();
    toast('🔄 क्लाउड डेटा सिन्क्रोनाइज भयो');
  };

  const _save = window.save;
  let pt;
  window.save = function() {
    const r = _save.apply(this, arguments);
    clearTimeout(pt);
    pt = setTimeout(push, 1000);
    return r;
  };

  /* ---- OLD DATA MIGRATION & MAPPING ROUTINE (PDF Architecture) ---- */
  window.migrateOldDataToNewStructure = function() {
    if (!window.DB) return;

    // 1. Map old category numbers (1-5) to 8 standard categories in PDF Page 1
    const catMap = { 1: 1, 2: 4, 3: 5, 4: 8, 5: 8 };
    (DB.products || []).forEach(p => {
      if (typeof p.type === 'number' && p.type >= 1 && p.type <= 5) {
        p.type = ['खाद्यान्न, कृषि तथा खाद्य परिकार', 'लत्ताकपडा, फेसन तथा बजार', 'खाना, होटल तथा रेस्टुरेन्ट', 'उद्योग, कलकारखाना तथा उत्पादन', 'यातायात तथा सवारी'][p.type - 1] || 'अन्य विविध सेवा तथा व्यवसाय';
      }
    });

    // 2. Ensure default groups exist
    if (!DB.groups || !DB.groups.length) {
      DB.groups = [
        { id: 'g_all_bazar', name: 'नेपाल खुल्ला बजार साझा चौतारी', icon: '🏪', desc: 'सबै व्यापारी र ग्राहकहरूको साझा छलफल', membersCount: 48 },
        { id: 'g_grocery', name: 'किराना तथा दैनिक उपभोग्य बजार', icon: '🧺', desc: 'ताजा खाद्यान्न तथा दैनिक उपभोग्य वस्तु', membersCount: 32 },
        { id: 'g_support', name: 'अर्डर तथा डेलिभरी सहायता समूह', icon: '🚚', desc: 'डेलिभरी समन्वय तथा ग्राहक सहायता', membersCount: 24 }
      ];
    }

    // 3. Migrate old navigation tab if pointing to obsolete 'sel' or 'vid'
    if (window.S) {
      if (S.tab === 'sel') S.tab = 'orders';
      if (S.tab === 'vid') S.tab = 'home';
    }

    // 4. Ensure DB collections arrays exist
    ['users', 'products', 'orders', 'chats', 'khata', 'videos', 'pay', 'bills', 'notices'].forEach(k => {
      if (!DB[k]) DB[k] = [];
    });

    save();
  };

  // Run migration on boot
  setTimeout(window.migrateOldDataToNewStructure, 800);

  // Direct Firestore Inquiries
  window.saveInquiryToFirestore = async function(inquiry) {
    if (!fs) return false;
    try {
      const docId = inquiry.ticketId || ('INQ_' + Date.now());
      await fs.collection('inquiries').doc(docId).set({
        ticketId: inquiry.ticketId || docId,
        name: inquiry.name || '',
        email: inquiry.email || '',
        phone: inquiry.phone || '',
        category: inquiry.category || 'collaboration',
        categoryName: inquiry.categoryName || 'Collaboration & Consulting',
        subject: inquiry.subject || '',
        message: inquiry.message || '',
        targetEmail: inquiry.targetEmail || 'aazakobazar@gmail.com',
        date: inquiry.date || new Date().toISOString().slice(0, 10),
        type: inquiry.type || 'collaboration_consulting',
        uid: (auth && auth.currentUser) ? auth.currentUser.uid : (inquiry.uid || null),
        status: 'pending',
        ts: Date.now()
      }, { merge: true });
      return true;
    } catch (e) {
      console.warn('Firestore inquiry error:', e);
      return false;
    }
  };
})();

