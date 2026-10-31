/* Open Bazar AI Assistant: Contextual Help, Navigation, Live Analytics & Gemini API (100% Free System) */
(function(){
const CFG = {
  apiKey: "AIzaSyAX8kJhxEevQitGnO1XTt88ZcTW7K2YHes",
  authDomain: "aazabo-bazar.firebaseapp.com",
  projectId: "aazabo-bazar"
};
const MODELS = ['gemini-2.5-flash', 'gemini-1.5-flash', 'gemini-3.8-flash', 'gemini-1.5-flash-8b'];

function getActiveApiKey() {
  if (window.AndroidBridge && typeof window.AndroidBridge.getGeminiApiKey === 'function') {
    const k = window.AndroidBridge.getGeminiApiKey();
    if (k && k.trim() && !k.startsWith('MY_')) return k.trim();
  }
  if (window.GEMINI_API_KEY && window.GEMINI_API_KEY.trim() && !window.GEMINI_API_KEY.startsWith('MY_')) {
    return window.GEMINI_API_KEY.trim();
  }
  const custom = localStorage.getItem('user_gemini_api_key');
  if (custom && custom.trim()) return custom.trim();
  return CFG.apiKey;
}
const ne = () => (window.DB && DB.lang) === 'ne';
const esc = s => String(s == null ? '' : s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const rs = n => '₨' + Number(n || 0).toLocaleString('en-IN');

let chatHistory = [];
let assistantModal = null;
let floatingBtn = null;

/* ---------------- 1. Context & Analytics Engine ---------------- */

function getScreenContext() {
  const u = window.S && S.u;
  const isNep = ne();
  
  if (!u) {
    if (S && S.r) {
      const step = (S.r.i || 0) + 1;
      return {
        screenId: 'register',
        name: isNep ? `नयाँ दर्ता (चरण ${step})` : `Registration (Step ${step})`,
        role: S.r.d && S.r.d.rl === 'm' ? 'merchant_registration' : 'customer_registration',
        description: isNep ? 'प्रयोगकर्ता खाता खोल्ने प्रक्रियामा हुनुहुन्छ।' : 'User is filling out the registration form.'
      };
    }
    return {
      screenId: 'login',
      name: isNep ? 'लगइन तथा प्रवेश' : 'Login / Welcome',
      role: 'guest',
      description: isNep ? 'प्रयोगकर्ता लगइन पृष्ठमा हुनुहुन्छ।' : 'User is on the login screen.'
    };
  }

  // Logged-in contexts
  if (u.role === 'c') {
    if (S.sub === 'shop') return { screenId: 'shop_detail', name: isNep ? 'पसल विवरण तथा सामान' : 'Shop Details', role: 'customer' };
    if (S.sub === 'acct') return { screenId: 'account_settings', name: isNep ? 'खाता सेटिङ' : 'Account Settings', role: 'customer' };
    if (S.sub === 'cmp') return { screenId: 'complaint', name: isNep ? 'उजुरी दर्ता' : 'Report Issue', role: 'customer' };
    if (S.sub === 'bill') return { screenId: 'bill_view', name: isNep ? 'भ्याट/पान बिल' : 'View Bill', role: 'customer' };
    if (S.tab === 'cart') return { screenId: 'cart', name: isNep ? 'मेरो कार्ट (सामान खरिद)' : 'Shopping Cart', role: 'customer' };
    if (S.tab === 'sel') return { screenId: 'orders', name: isNep ? 'मेरो अर्डरहरू' : 'Customer Orders', role: 'customer' };
    if (S.tab === 'shops') return { screenId: 'shops', name: isNep ? 'पसलहरूको सूची' : 'Nearby Shops', role: 'customer' };
    if (S.tab === 'vids') return { screenId: 'videos', name: isNep ? 'भिडियो हब' : 'Video Showcase', role: 'customer' };
    if (S.tab === 'me') return { screenId: 'profile', name: isNep ? 'ग्राहक प्रोफाइल' : 'Customer Profile', role: 'customer' };
    return { screenId: 'home', name: isNep ? 'बजार गृहपृष्ठ' : 'Marketplace Home', role: 'customer' };
  }

  if (u.role === 'm') {
    if (S.sub === 'add') return { screenId: 'add_product', name: isNep ? 'नयाँ सामान थप्ने' : 'Add New Product', role: 'merchant' };
    if (S.sub === 'bnew') return { screenId: 'new_bill', name: isNep ? 'नयाँ बिल बनाउने' : 'Create New Bill', role: 'merchant' };
    if (S.sub === 'bill') return { screenId: 'bill_view', name: isNep ? 'बिल विवरण र सेयर' : 'Bill Invoice', role: 'merchant' };
    if (S.sub === 'set') return { screenId: 'shop_settings', name: isNep ? 'पसल समय र डेलिभरी सेटिङ' : 'Shop Settings', role: 'merchant' };
    if (S.sub === 'pay') return { screenId: 'plan_payment', name: isNep ? 'मासिक प्लान तथा भुक्तानी' : 'Subscription & Renewal', role: 'merchant' };
    if (S.sub === 'acct') return { screenId: 'merchant_profile', name: isNep ? 'व्यापारी प्रोफाइल' : 'Merchant Profile', role: 'merchant' };
    if (S.tab === 'prod') return { screenId: 'products', name: isNep ? 'सामान तथा स्टक व्यवस्थापन' : 'Products & Stock', role: 'merchant' };
    if (S.tab === 'not') return { screenId: 'notices', name: isNep ? 'सूचना तथा विज्ञापन' : 'Merchant Notices', role: 'merchant' };
    if (S.tab === 'kht') return { screenId: 'khata', name: isNep ? 'उधारो खाता (Khata)' : 'Khata Ledger', role: 'merchant' };
    return { screenId: 'dashboard', name: isNep ? 'व्यापारी ड्यासबोर्ड' : 'Merchant Dashboard', role: 'merchant' };
  }

  if (u.role === 'a') {
    if (S.tab === 'u') return { screenId: 'admin_users', name: isNep ? 'प्रयोगकर्ता व्यवस्थापन' : 'Admin Users', role: 'admin' };
    if (S.tab === 'sh') return { screenId: 'admin_shops', name: isNep ? 'पसल स्वीकृति' : 'Shop Approvals', role: 'admin' };
    if (S.tab === 'comp') return { screenId: 'admin_comp', name: isNep ? 'उजुरी अनुगमन' : 'Complaints Monitor', role: 'admin' };
    if (S.tab === 'mon') return { screenId: 'admin_mon', name: isNep ? 'सामान/सूचना निगरानी' : 'System Monitor', role: 'admin' };
    if (S.tab === 'set') return { screenId: 'admin_set', name: isNep ? 'सिस्टम सेटिङ' : 'Admin Settings', role: 'admin' };
    return { screenId: 'admin_ov', name: isNep ? 'केन्द्रीय एडमिन सारांश' : 'Admin Overview', role: 'admin' };
  }

  return { screenId: 'general', name: 'Open Bazar', role: 'user' };
}

function calculateAnalytics() {
  const u = window.S && S.u;
  const db = window.DB || { users: [], products: [], orders: [], khata: [], pay: [] };
  
  if (!u) {
    const liveShops = db.users.filter(x => x.shop && x.shop.ok && x.on);
    return {
      type: 'public',
      totalShops: liveShops.length,
      totalProducts: db.products.length,
      demoShops: db.users.filter(x => x.shop && x.shop.demo).length
    };
  }

  if (u.role === 'm') {
    const myProds = db.products.filter(p => p.mid === u.id);
    const myOrders = db.orders.filter(o => o.mid === u.id);
    const myKhata = (db.khata || []).filter(k => k.mid === u.id && !k.paid);
    const deliveredOrders = myOrders.filter(o => o.st === 'Delivered' || o.st === 'Packed' || o.st === 'Shipped');
    const totalRev = deliveredOrders.reduce((sum, o) => sum + (o.total || 0), 0);
    const totalReceivable = myKhata.reduce((sum, k) => sum + (k.amt || 0), 0);
    const lowStock = myProds.filter(p => p.stock > 0 && p.stock < 10);
    const outOfStock = myProds.filter(p => p.stock <= 0);

    return {
      type: 'merchant',
      totalProducts: myProds.length,
      lowStockCount: lowStock.length,
      outOfStockCount: outOfStock.length,
      totalOrders: myOrders.length,
      pendingOrders: myOrders.filter(o => o.st === 'Pending').length,
      totalRevenue: totalRev,
      receivableKhata: totalReceivable,
      shopExpiry: u.shop ? u.shop.exp : 'N/A',
      slots: u.shop ? (100 + (u.shop.slots || 0)) : 100
    };
  }

  if (u.role === 'c') {
    const myOrders = db.orders.filter(o => o.cid === u.id);
    const totalSpent = myOrders.filter(o => o.st === 'Delivered').reduce((sum, o) => sum + (o.total || 0), 0);
    const activeOrders = myOrders.filter(o => ['Pending', 'Processing', 'Packed', 'Shipped'].includes(o.st));
    const cartCount = Object.keys(S.cart || {}).length;

    return {
      type: 'customer',
      cartItems: cartCount,
      activeOrders: activeOrders.length,
      totalOrders: myOrders.length,
      totalSpent: totalSpent,
      wallet: u.wallet || 0,
      points: u.pts || 0
    };
  }

  if (u.role === 'a') {
    const pendingShops = db.users.filter(x => x.shop && !x.shop.ok);
    const pendingPays = (db.pay || []).filter(p => p.st === 'Pending');
    return {
      type: 'admin',
      totalUsers: db.users.length,
      totalMerchants: db.users.filter(x => x.role === 'm').length,
      totalCustomers: db.users.filter(x => x.role === 'c').length,
      pendingShopApprovals: pendingShops.length,
      pendingPaymentApprovals: pendingPays.length,
      totalComplaints: (db.comp || []).length
    };
  }

  return {};
}

/* ---------------- 2. Navigation Actions ---------------- */

const NAV_TARGETS = [
  { id: 'home', titleNp: 'गृहपृष्ठ', titleEn: 'Home', action: () => { S.tab = 'home'; S.sub = null; R(); } },
  { id: 'shops', titleNp: 'पसलहरू', titleEn: 'Shops', action: () => { S.tab = 'shops'; S.sub = null; R(); } },
  { id: 'cart', titleNp: 'कार्ट हेर्नुहोस्', titleEn: 'View Cart', action: () => { S.tab = 'cart'; S.sub = null; R(); } },
  { id: 'orders', titleNp: 'मेरो अर्डरहरू', titleEn: 'My Orders', action: () => { S.tab = 'sel'; S.sub = null; R(); } },
  { id: 'profile', titleNp: 'मेरो प्रोफाइल', titleEn: 'Profile', action: () => { S.tab = 'me'; S.sub = null; R(); } },
  { id: 'dashboard', titleNp: 'ड्यासबोर्ड', titleEn: 'Dashboard', action: () => { S.tab = 'dash'; S.sub = null; R(); } },
  { id: 'products', titleNp: 'सामान तथा स्टक', titleEn: 'Products', action: () => { S.tab = 'prod'; S.sub = null; R(); } },
  { id: 'add_product', titleNp: 'सामान थप्नुहोस्', titleEn: 'Add Product', action: () => { S.tab = 'prod'; S.sub = 'add'; R(); } },
  { id: 'khata', titleNp: 'उधारो खाता', titleEn: 'Khata', action: () => { S.tab = 'kht'; S.sub = null; R(); } },
  { id: 'bills', titleNp: 'बिलहरूको सूची', titleEn: 'Bills', action: () => { S.tab = 'home'; S.sub = 'bills'; R(); } },
  { id: 'new_bill', titleNp: 'नयाँ बिल बनाउने', titleEn: 'Create Bill', action: () => { S.sub = 'bnew'; R(); } },
  { id: 'vlink', titleNp: 'भिडियो व्यवस्थापन', titleEn: 'Video Links', action: () => { if (window.videoMgr) videoMgr(); } },
  { id: 'pay_qr', titleNp: 'अनलाइन भुक्तानी QR', titleEn: 'Payment QR', action: () => { if (window.payMgr) payMgr(); } },
  { id: 'ird_tax', titleNp: 'कर पुरस्कार (IRD)', titleEn: 'IRD Tax Prize', action: () => { if (window.obTax) obTax(); } },
  { id: 'd3_chart', titleNp: 'बजार तथ्याङ्क चार्ट', titleEn: 'Market Growth Chart', action: () => { if (window.openBazarD3Chart) openBazarD3Chart(); } },
  { id: 'radar_chart', titleNp: 'प्रविधि रडार चार्ट', titleEn: 'Technical Radar Chart', action: () => { if (window.openRadarChart) openRadarChart(); } },
  { id: 'help_guide', titleNp: 'प्रणाली चलाउने गाइड', titleEn: 'System Guide', action: () => { if (window.openOpenBazarHelp) openOpenBazarHelp('guide'); } },
  { id: 'register_merchant', titleNp: 'व्यापारी दर्ता', titleEn: 'Register Merchant', action: () => { S.r = { i: 0, d: { rl: 'm' } }; R(); } },
  { id: 'register_customer', titleNp: 'ग्राहक दर्ता', titleEn: 'Register Customer', action: () => { S.r = { i: 0, d: { rl: 'c' } }; R(); } },
  { id: 'login', titleNp: 'लगइन गर्नुहोस्', titleEn: 'Login Screen', action: () => { S.r = null; R(); } }
];

function executeNav(targetId) {
  const target = NAV_TARGETS.find(t => t.id === targetId);
  if (target) {
    if (assistantModal) assistantModal.remove();
    target.action();
    toast(ne() ? `🚀 ${target.titleNp} मा गइयो` : `🚀 Navigated to ${target.titleEn}`);
  }
}
window.obAiNav = executeNav;

/* ---------------- 3. Contextual Suggested Questions ---------------- */

function getContextualSuggestions() {
  const ctx = getScreenContext();
  const isNep = ne();

  if (ctx.role === 'guest') {
    return isNep ? [
      { q: 'व्यापारीको रूपमा पसल कसरी दर्ता गर्ने?', target: 'register_merchant' },
      { q: 'पासवर्ड बिर्सनुभयो भने के गर्ने?', action: 'forgot' },
      { q: 'नेपालको नक्सामा स्थान कसरी राख्ने?', info: 'map' },
      { q: 'Open Bazar को नियम तथा शुल्क के हो?', info: 'rules' }
    ] : [
      { q: 'How to register a new shop?', target: 'register_merchant' },
      { q: 'How to reset forgotten password?', action: 'forgot' },
      { q: 'How does Nepal map location work?', info: 'map' },
      { q: 'What are Open Bazar fees and terms?', info: 'rules' }
    ];
  }

  if (ctx.role === 'merchant') {
    return isNep ? [
      { q: '📊 मेरो व्यापार र बिक्री विश्लेषण हेरौँ', action: 'analytics' },
      { q: '📦 नयाँ सामान कसरी थप्ने र स्टक मिलाउने?', target: 'add_product' },
      { q: '🧾 ग्राहकलाई भ्याट/पान बिल कसरी बनाउने?', target: 'new_bill' },
      { q: '🎬 YouTube वा TikTok भिडियो कसरी राख्ने?', target: 'vlink' }
    ] : [
      { q: '📊 Show my sales & store analytics', action: 'analytics' },
      { q: '📦 How to add new products & manage stock?', target: 'add_product' },
      { q: '🧾 How to create VAT / PAN invoices?', target: 'new_bill' },
      { q: '🎬 How to share social video links?', target: 'vlink' }
    ];
  }

  if (ctx.role === 'customer') {
    return isNep ? [
      { q: '🛍️ मेरो नजिकैका पसलहरू कहाँ छन्?', target: 'shops' },
      { q: '🚚 अर्डर कसरी गर्ने र डेलिभरी नियम के छ?', info: 'delivery' },
      { q: '🎁 IRD करदाता प्रोत्साहन उपहार बिल कसरी दर्ता गर्ने?', target: 'ird_tax' },
      { q: '🛒 मेरो कार्ट र अर्डर स्थिति देखाउनुहोस्', target: 'orders' }
    ] : [
      { q: '🛍️ Find nearest local shops in my area', target: 'shops' },
      { q: '🚚 How to order & what are delivery terms?', info: 'delivery' },
      { q: '🎁 How to claim IRD Tax lottery prizes?', target: 'ird_tax' },
      { q: '🛒 View my cart and active orders', target: 'orders' }
    ];
  }

  // Admin
  return isNep ? [
    { q: '📊 केन्द्रीय सिस्टम विश्लेषण र तथ्याङ्क', action: 'analytics' },
    { q: '🏪 पसल स्वीकृति तथा प्यानल', target: 'admin_shops' },
    { q: '👥 प्रयोगकर्ता तथा व्यापारी अनुगमन', target: 'admin_users' }
  ] : [
    { q: '📊 View full marketplace analytics', action: 'analytics' },
    { q: '🏪 Review pending shop approvals', target: 'admin_shops' },
    { q: '👥 Manage active users and merchants', target: 'admin_users' }
  ];
}

/* ---------------- 4. Gemini API & Intelligent Fallback ---------------- */

async function callGeminiApi(prompt, systemInstruction) {
  const currentKey = getActiveApiKey();
  for (const model of MODELS) {
    try {
      const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${currentKey}`;
      const payload = {
        contents: [
          { role: 'user', parts: [{ text: prompt }] }
        ],
        systemInstruction: {
          parts: [{ text: systemInstruction }]
        },
        generationConfig: {
          temperature: 0.3,
          maxOutputTokens: 800
        }
      };

      const resp = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!resp.ok) continue;
      const data = await resp.json();
      const text = data.candidates?.[0]?.content?.parts?.[0]?.text;
      if (text) return text.trim();
    } catch (_) {}
  }
  return null;
}

function generateLocalSmartResponse(query) {
  const q = query.toLowerCase();
  const isNep = ne();
  const ctx = getScreenContext();
  const an = calculateAnalytics();

  // 1. Analytics Query
  if (q.includes('analytic') || q.includes('विश्लेषण') || q.includes('बिक्री') || q.includes('कमाई') || q.includes('व्यापार') || q.includes('revenue') || q.includes('sales')) {
    if (an.type === 'merchant') {
      return isNep ?
        `📊 **तपाईंको पसलको प्रत्यक्ष विश्लेषण (Live Analytics):**\n` +
        `• कुल सामान (Products): **${an.totalProducts} वटा** (सीमा: ${an.slots})\n` +
        `• थोरै स्टक बाँकी (<10): **${an.lowStockCount} वटा**\n` +
        `• सकिएको स्टक (Out of stock): **${an.outOfStockCount} वटा**\n` +
        `• कुल अर्डरहरू: **${an.totalOrders} वटा** (पर्खाइमा: ${an.pendingOrders})\n` +
        `• सम्पन्न बिक्री रकम: **${rs(an.totalRevenue)}**\n` +
        `• उधारो लिन बाँकी (Khata): **${rs(an.receivableKhata)}**\n` +
        `• पसल म्याद (Plan expiry): **${an.shopExpiry}**\n\n` +
        `💡 *सुझाव: कम स्टक भएका सामानहरू तुरुन्तै थप्नुहोस्।*` :
        `📊 **Live Store Analytics:**\n` +
        `• Total Products: **${an.totalProducts}** (limit: ${an.slots})\n` +
        `• Low stock (<10): **${an.lowStockCount}**\n` +
        `• Out of stock: **${an.outOfStockCount}**\n` +
        `• Total Orders: **${an.totalOrders}** (Pending: ${an.pendingOrders})\n` +
        `• Completed Revenue: **${rs(an.totalRevenue)}**\n` +
        `• Receivable Khata: **${rs(an.receivableKhata)}**\n` +
        `• Subscription expiry: **${an.shopExpiry}**`;
    } else if (an.type === 'customer') {
      return isNep ?
        `📊 **तपाईंको किनमेल सारांश (Shopping Analytics):**\n` +
        `• कार्टमा रहेका सामान: **${an.cartItems} वटा**\n` +
        `• सक्रिय अर्डरहरू: **${an.activeOrders} वटा**\n` +
        `• कुल सम्पन्न खरिद: **${rs(an.totalSpent)}**\n` +
        `• वालेट ब्यालेन्स: **${rs(an.wallet)}** | पोइन्ट: **${an.points}**` :
        `📊 **Your Shopping Overview:**\n` +
        `• Cart Items: **${an.cartItems}**\n` +
        `• Active Orders: **${an.activeOrders}**\n` +
        `• Total Purchases: **${rs(an.totalSpent)}**\n` +
        `• Wallet Balance: **${rs(an.wallet)}** | Points: **${an.points}**`;
    }
  }

  // 2. D3 Visualization & Market Growth Intent
  if (q.includes('radar') || q.includes('रडार') || q.includes('दक्षता') || q.includes('proficiency') || q.includes('backend') || q.includes('frontend') || q.includes('architecture') || q.includes('ui/ux')) {
    return isNep ?
      `🕸️ **Open Bazar प्राविधिक दक्षता रडार चार्ट (D3.js Radar Chart):**\n` +
      `Backend (95%), Frontend (96%), UI/UX (94%), Architecture (93%), Mobile (92%), र Data Analytics (91%) को प्रत्यक्ष D3 रडार चार्ट हेर्न तल थिच्नुहोस्:\n[NAV:radar_chart]` :
      `🕸️ **Open Bazar Technical Proficiency Radar Chart (D3.js):**\n` +
      `Interactive D3 Radar Chart showing domain mastery across Backend, Frontend, UI/UX, Architecture, Mobile & Data:\n[NAV:radar_chart]`;
  }
  if (q.includes('help') || q.includes('सहयोग') || q.includes('गाइड') || q.includes('guide') || q.includes('कसरी चलाउने') || q.includes('system') || q.includes('प्रणाली')) {
    return isNep ?
      `📘 **Open Bazar प्रणाली चलाउने विस्तृत गाइड:**\n` +
      `ग्राहक र व्यापारीका सम्पूर्ण सुविधाहरू कसरी प्रयोग गर्ने भन्ने पूर्ण जानकारी हेर्न तल थिच्नुहोस्:\n[NAV:help_guide]` :
      `📘 **Open Bazar Complete System User Guide:**\n` +
      `Explore step-by-step guidance on browsing, shopping, order fulfillment, invoicing, and khata:\n[NAV:help_guide]`;
  }
  if (q.includes('chart') || q.includes('d3') || q.includes('चार्ट') || q.includes('वृद्धि') || q.includes('growth') || q.includes('तथ्याङ्क')) {
    return isNep ?
      `📈 **Open Bazar बजार वृद्धि तथा प्रयोगकर्ता संलग्नता (D3.js Chart):**\n` +
      `कारोबार रकम (GMV), सम्पन्न अर्डरहरू, पसल वृद्धि र वर्ग अनुसारको मागको प्रत्यक्ष अन्तरक्रियात्मक D3 चार्ट हेर्न र साँच्चिकै PNG/CSV डाउनलोड वा सेयर गर्न तल थिच्नुहोस्:\n[NAV:d3_chart]` :
      `📈 **Open Bazar Market Growth & User Engagement Metrics (D3.js):**\n` +
      `Interactive D3 charts for GMV volume, orders, shop onboarding & category demands with real download and share:\n[NAV:d3_chart]`;
  }

  // 3. Navigation Intent
  if (q.includes('cart') || q.includes('कार्ट')) {
    return isNep ?
      `🛒 तपाईंको कार्ट खोल्न तल क्लिक गर्नुहोस्:\n[NAV:cart]` :
      `🛒 Open your shopping cart below:\n[NAV:cart]`;
  }
  if (q.includes('order') || q.includes('अर्डर')) {
    return isNep ?
      `📦 अर्डर हेर्न वा ट्र्याक गर्न तल जानुहोस्:\n[NAV:orders]` :
      `📦 View and track your orders here:\n[NAV:orders]`;
  }
  if (q.includes('shop') || q.includes('पसल')) {
    return isNep ?
      `🏪 नजिकका पसलहरूको सूची यहाँ हेर्नुहोस्:\n[NAV:shops]` :
      `🏪 Browse registered nearby shops:\n[NAV:shops]`;
  }
  if (q.includes('bill') || q.includes('बिल')) {
    return isNep ?
      `🧾 भ्याट/पान बिल तयार गर्न वा हेर्न तल थिच्नुहोस्:\n[NAV:new_bill]` :
      `🧾 Create or view PAN/VAT bills:\n[NAV:new_bill]`;
  }
  if (q.includes('tax') || q.includes('ird') || q.includes('कर') || q.includes('पुरस्कार')) {
    return isNep ?
      `🎁 IRD करदाता प्रोत्साहन उपहारका लागि बिल विवरण यहाँबाट कपी गर्नुहोस्:\n[NAV:ird_tax]` :
      `🎁 Open IRD Taxpayer Incentive Prize Portal:\n[NAV:ird_tax]`;
  }

  // 3. Common Queries
  if (q.includes('register') || q.includes('दर्ता') || q.includes('खाता')) {
    return isNep ?
      `📝 **Open Bazar मा दर्ता प्रक्रिया:**\n` +
      `१. ग्राहक दर्ता पूर्ण रूपमा **निःशुल्क** छ।\n` +
      `२. व्यापारी दर्ताका लागि ३५ दिनको मासिक शुल्क रु १०० लाग्छ (२० सामान समावेश)। थप १० सामानको ठाउँका लागि रु ५० पर्छ।\n` +
      `३. दर्ता गर्दा मोबाइल नम्बर, ठेगाना, र नेपालको नक्सामा पसलको रातो पिन छनोट अनिवार्य हुन्छ।\n\n` +
      `दर्ता सुरु गर्न तल थिच्नुहोस्:\n[NAV:register_merchant]` :
      `📝 **Open Bazar Registration Guide:**\n` +
      `1. Customers register for **free**.\n` +
      `2. Merchants pay Rs 100 per 35 days (20 items included) + Rs 50 for 10 extra slots.\n` +
      `3. Mobile, address and dropping a red pin on Nepal map are required.\n\n[NAV:register_merchant]`;
  }

  if (q.includes('password') || q.includes('पासवर्ड') || q.includes('forgot')) {
    return isNep ?
      `🔑 **पासवर्ड सहायता:**\n` +
      `• नयाँ पासवर्ड कम्तिमा ८ अक्षर, जसमा अंग्रेजी अक्षर र अंक दुबै हुनुपर्छ।\n` +
      `• यदि पासवर्ड बिर्सनुभयो भने "Forgot password" मा गएर दर्ता गरेको इमेल लेख्नुहोस्, इमेलमा पासवर्ड रिसेट लिङ्क आउँछ।` :
      `🔑 **Password Assistance:**\n` +
      `• Minimum 8 characters with both letters and numbers.\n` +
      `• Use 'Forgot Password' on login screen to receive a secure reset link.`;
  }

  // Default contextual welcome
  return isNep ?
    `नमस्ते! म Open Bazar को निःशुल्क AI सहायक हुँ। म तपाईंलाई:\n` +
    `• पसल, सामान तथा अर्डर खोज्न\n` +
    `• एपका कुनै पनि सेक्सनमा तुरुन्त जान (Navigation)\n` +
    `• व्यापार, स्टक र आम्दानी विश्लेषण (Analytics) हेर्न\n` +
    `• बिल, भिडियो र कर पुरस्कार दर्ता गर्न सहयोग गर्न सक्छु।\n\n` +
    `तपाईं अहिले **${ctx.name}** मा हुनुहुन्छ। के मद्दत गरौँ?` :
    `Hello! I am your free Open Bazar AI Assistant. I can help you:\n` +
    `• Search local shops and products\n` +
    `• Navigate to any screen or feature\n` +
    `• Calculate real-time sales and store analytics\n` +
    `• Create bills and share video links.\n\n` +
    `You are currently on **${ctx.name}**. How can I assist you?`;
}

async function queryAssistant(userMessage) {
  const ctx = getScreenContext();
  const an = calculateAnalytics();
  const isNep = ne();

  // Build high-context prompt for Gemini
  let productsSnippet = '';
  try {
    if (window.liveP) {
      productsSnippet = liveP().slice(0, 40).map(p => {
        const s = window.U ? U(p.mid) : null;
        return `${p.name} (Rs ${p.price}, ${s?.shop?.name || ''}, ${s?.dist || ''})`;
      }).join('; ');
    }
  } catch (_) {}

  const sysInstruction = 
    `You are the official contextual AI Assistant for Open Bazar, a local marketplace platform in Nepal. ` +
    `Current Screen: "${ctx.name}" (Role: ${ctx.role}). ` +
    `Language preference: ${isNep ? 'Nepali' : 'English'}. ` +
    `Live Analytics: ${JSON.stringify(an)}. ` +
    `Available Products: ${productsSnippet || 'None'}. ` +
    `Rules:\n` +
    `1. Answer in the same language the user writes (Nepali or English) in a warm, polite, and practical tone.\n` +
    `2. Keep responses concise and formatted with clean bullet points.\n` +
    `3. If the user wants to go to a screen or do an action, include a navigation tag at the end, for example: [NAV:cart], [NAV:orders], [NAV:shops], [NAV:add_product], [NAV:new_bill], [NAV:ird_tax], [NAV:dashboard], [NAV:register_merchant].\n` +
    `4. If asked about analytics, calculate and present numbers accurately from the provided data.\n` +
    `5. Do not invent fake products, shops or policies. Respect Nepal currency (Rs / ₨).`;

  const conversationHistory = chatHistory.slice(-4).map(h => `${h.role === 'user' ? 'User' : 'Assistant'}: ${h.text}`).join('\n');
  const fullPrompt = `${conversationHistory}\nUser: ${userMessage}\nAssistant:`;

  // 1. Try Gemini API
  const aiResponse = await callGeminiApi(fullPrompt, sysInstruction);
  if (aiResponse) {
    return aiResponse;
  }

  // 2. Fallback to smart local contextual rule engine (100% free & resilient)
  return generateLocalSmartResponse(userMessage);
}

/* ---------------- 5. Widget UI (Floating Button & Modal) ---------------- */

function formatBubbleText(rawText) {
  let text = esc(rawText);
  // Markdown bold
  text = text.replace(/\*\*(.*?)\*\*/g, '<b>$1</b>');
  // Markdown italic
  text = text.replace(/\*(.*?)\*/g, '<i>$1</i>');
  // Markdown bullet points
  text = text.replace(/^[•\-] (.*)$/gm, '<li style="margin-left:14px">$1</li>');

  // Parse [NAV:target_id] into interactive buttons
  text = text.replace(/\[NAV:([a-z_]+)\]/g, (match, navId) => {
    const target = NAV_TARGETS.find(t => t.id === navId);
    if (!target) return '';
    const label = ne() ? target.titleNp : target.titleEn;
    return `<div style="margin-top:8px"><button class="btn j sm" onclick="obAiNav('${navId}')" style="display:inline-flex;align-items:center;gap:4px">🚀 ${label} खोल्नुहोस्</button></div>`;
  });

  return text;
}

function openHelpModal(initialSlide = 'guide', initialQuery = '') {
  if (assistantModal) assistantModal.remove();

  const isNep = ne();
  const ctx = getScreenContext();
  const w = document.createElement('div');
  w.id = 'ob_help_modal';
  w.style.cssText = 'position:fixed;inset:0;z-index:100005;background:rgba(10,22,40,0.72);backdrop-filter:blur(6px);display:flex;flex-direction:column;font-family:"Noto Sans Devanagari","Segoe UI",system-ui,sans-serif;color:var(--tx,#0A1628)';

  let activeSlide = initialSlide; // 'guide' or 'ai'

  w.innerHTML = `
    <div style="flex:1;max-width:580px;width:100%;margin:0 auto;background:var(--bg,#F6F9FC);display:flex;flex-direction:column;height:100%;box-shadow:0 0 35px rgba(0,0,0,0.5);overflow:hidden">
      
      <!-- Top Brand Header -->
      <div style="background:linear-gradient(135deg,var(--ind,#0F4C9A),var(--ind2,#1B6AC9));color:#fff;padding:12px 16px;display:flex;align-items:center;justify-content:space-between;box-shadow:0 2px 10px rgba(0,0,0,0.25)">
        <div style="display:flex;align-items:center;gap:10px">
          <img src="assets/logo-160.png" style="width:36px;height:36px;border-radius:50%;background:#fff;padding:2px;box-shadow:0 2px 6px rgba(0,0,0,0.3)" alt="Mero Bazar">
          <div>
            <div style="font-weight:800;font-size:16px;letter-spacing:-0.2px">मेरो बजार सहयोग तथा AI केन्द्र</div>
            <div style="font-size:11.5px;opacity:0.9;display:flex;align-items:center;gap:6px">
              <span style="background:var(--saf,#F28C0F);color:#fff;padding:1px 6px;border-radius:4px;font-weight:700;font-size:10px">Help & AI</span>
              <span>📍 ${esc(ctx.name)}</span>
            </div>
          </div>
        </div>
        <div style="display:flex;align-items:center;gap:6px">
          <button id="ai_clear" style="background:rgba(255,255,255,0.18);border:0;color:#fff;border-radius:8px;padding:6px 9px;font-size:12px;font-weight:700;cursor:pointer" title="Clear Chat">🧹</button>
          <button id="help_modal_close" style="background:rgba(255,255,255,0.25);border:0;color:#fff;border-radius:50%;width:34px;height:34px;font-size:16px;font-weight:800;cursor:pointer;display:flex;align-items:center;justify-content:center">✕</button>
        </div>
      </div>

      <!-- Slide Switcher Navigation -->
      <div style="display:flex;background:var(--card,#fff);border-bottom:1px solid var(--ln,#E1E8F0);padding:8px 12px;gap:8px">
        <button id="btn_slide_guide" class="btn ${activeSlide === 'guide' ? 's' : 'g'}" style="flex:1;border-radius:12px;font-size:13.5px;padding:10px 12px;display:flex;align-items:center;justify-content:center;gap:6px">
          <span>📘</span> <b>${isNep ? 'कसरी चलाउने? (System Guide)' : 'How to use (System Guide)'}</b>
        </button>
        <button id="btn_slide_ai" class="btn ${activeSlide === 'ai' ? 's' : 'g'}" style="flex:1;border-radius:12px;font-size:13.5px;padding:10px 12px;display:flex;align-items:center;justify-content:center;gap:6px">
          <span>🤖</span> <b>मेरो बजार AI</b>
        </button>
      </div>

      <!-- Container: Slide 1 (System Guide) -->
      <div id="slide_guide_container" style="flex:1;overflow-y:auto;padding:14px;display:${activeSlide === 'guide' ? 'flex' : 'none'};flex-direction:column;gap:12px">
        
        <!-- Welcome Banner -->
        <div class="c" style="background:linear-gradient(135deg,rgba(15,76,154,0.08),rgba(242,140,15,0.12));border:1px solid var(--ind2);padding:14px;margin-bottom:0">
          <div style="display:flex;align-items:center;gap:10px">
            <span style="font-size:26px">💡</span>
            <div>
              <b style="font-size:15px;color:var(--ind2)">मेरो बजार प्रणाली प्रयोग मार्गदर्शिका</b>
              <div style="font-size:12.5px;color:var(--mu);margin-top:2px">एपका सम्पूर्ण सुविधाहरू कसरी सहजै चलाउने भन्ने जानकारी तल दिइएको छ।</div>
            </div>
          </div>
        </div>

        <!-- Section 1: Customer System -->
        <div class="c" style="padding:14px;margin-bottom:0">
          <div style="display:flex;align-items:center;gap:8px;margin-bottom:8px">
            <span style="font-size:20px">🛍️</span>
            <b style="font-size:15px;color:var(--tx)">१. ग्राहक किनमेल प्रणाली (Customer Shopping)</b>
          </div>
          <div style="font-size:13px;line-height:1.6;color:var(--tx)">
            • <b>पसल खोज्ने:</b> होम स्क्रिन वा 'पसलहरू' ट्याबमा गएर आफ्नै जिल्लाका प्रमाणित स्थानीय पसलहरू नक्सामा हेर्नुहोस्।<br>
            • <b>सामान छान्ने र अर्डर:</b> मन परेको सामानको परिमाण छानी 'कार्टमा थप्नुहोस्' र डेलिभरी ठेगाना राखी अर्डर गर्नुहोस्।<br>
            • <b>अर्डर ट्र्याकिङ:</b> 'अर्डरहरू' ट्याबमा अर्डर स्वीकृत (Processing) भएको हेर्नुहोस् र सामान हात परेपछि 'सामान प्राप्त भयो' थिच्नुहोस्।<br>
            • <b>IRD कर लटरी पुरस्कार:</b> भ्याट बिल नम्बरबाट १०% कर फिर्ता र सरकारी लटरी उपहार दाबी गर्न सकिन्छ।
          </div>
          <div style="display:flex;gap:8px;margin-top:10px">
            <button class="btn sm" onclick="obAiNav('shops')">🏪 पसलहरू हेर्नुहोस्</button>
            <button class="btn sm s" onclick="obAiNav('orders')">📦 मेरो अर्डरहरू</button>
          </div>
        </div>

        <!-- Section 2: Merchant System -->
        <div class="c" style="padding:14px;margin-bottom:0">
          <div style="display:flex;align-items:center;gap:8px;margin-bottom:8px">
            <span style="font-size:20px">🏪</span>
            <b style="font-size:15px;color:var(--tx)">२. पसल तथा बिक्रेता प्रणाली (Merchant & Billing)</b>
          </div>
          <div style="font-size:13px;line-height:1.6;color:var(--tx)">
            • <b>सामान र स्टक थप्ने:</b> पसल ड्यासबोर्डमा गई सामानको नाम, मूल्य, छुट, र मौज्दात (स्टक) तुरुन्तै अपडेट गर्नुहोस्।<br>
            • <b>ग्राहकको अर्डर व्यवस्थापन:</b> नयाँ अर्डर आउनासाथ 'Processing' मा स्वीकृत गर्दा स्वतः बिल बन्छ।<br>
            • <b>भ्याट/पान डिजिटल बिल:</b> आधिकारिक बिललाई '🖼 फोटो डाउनलोड' गरी आफ्ना ग्राहकलाई सिधै WhatsApp/Viber मा सेयर गर्नुहोस्।<br>
            • <b>उधारो खाता (Khata):</b> ग्राहकको बाँकी हिसाब र भुक्तानी ट्र्याक गर्नुहोस्।<br>
            • <b>Reels/भिडियो प्रचार:</b> YouTube, TikTok र Facebook भिडियो लिङ्क जोडेर आफ्ना उत्पादनको प्रचार गर्नुहोस्।
          </div>
          <div style="display:flex;gap:8px;margin-top:10px">
            <button class="btn sm j" onclick="obAiNav('add_product')">📦 सामान थप्ने</button>
            <button class="btn sm s" onclick="obAiNav('new_bill')">🧾 नयाँ बिल</button>
            <button class="btn sm" onclick="obAiNav('khata')">📒 खाता</button>
          </div>
        </div>

        <!-- Section 3: Radar Chart & Analytics -->
        <div class="c" style="padding:14px;margin-bottom:0">
          <div style="display:flex;align-items:center;gap:8px;margin-bottom:8px">
            <span style="font-size:20px">🕸️</span>
            <b style="font-size:15px;color:var(--tx)">३. प्राविधिक दक्षता रडार तथा बजार विश्लेषण</b>
          </div>
          <div style="font-size:13px;line-height:1.6;color:var(--tx)">
            • <b>D3.js रडार चार्ट:</b> Backend, Frontend, UI/UX, Architecture, Mobile, र Data Analytics को टेक्निकल दक्षता रडार हेर्नुहोस्।<br>
            • <b>बजार वृद्धि मेट्रिक्स:</b> कुल बजार कारोबार (GMV), सम्पन्न अर्डर दर, र वर्ग अनुसारको माग प्रत्यक्ष हेर्नुहोस्।
          </div>
          <div style="display:flex;gap:8px;margin-top:10px">
            <button class="btn sm s" onclick="if(window.openRadarChart){const m=document.getElementById('ob_help_modal');if(m)m.remove();openRadarChart();}">🕸️ D3 रडार चार्ट खोल्नुहोस्</button>
            <button class="btn sm" onclick="if(window.openBazarD3Chart){const m=document.getElementById('ob_help_modal');if(m)m.remove();openBazarD3Chart();}">📈 बजार तथ्याङ्क</button>
          </div>
        </div>

        <!-- Switch to AI Call-To-Action -->
        <div style="background:linear-gradient(135deg,var(--ind2,#1B6AC9),var(--ind,#0F4C9A));color:#fff;border-radius:14px;padding:14px;display:flex;align-items:center;justify-content:space-between;gap:10px;cursor:pointer" id="cta_switch_to_ai">
          <div style="display:flex;align-items:center;gap:8px">
            <span style="font-size:24px">🤖</span>
            <div>
              <b style="font-size:14px">थप प्रश्न वा तत्काल सहयोग चाहियो?</b>
              <div style="font-size:12px;opacity:0.9">Open Bazar AI सँग च्याट गरी सिधै नेभिगेट गर्नुहोस्</div>
            </div>
          </div>
          <span style="background:var(--saf,#F28C0F);padding:6px 12px;border-radius:99px;font-size:12px;font-weight:800;white-space:nowrap">AI खोल्नुहोस् ➔</span>
        </div>

      </div>

      <!-- Container: Slide 2 (Open Bazar AI) -->
      <div id="slide_ai_container" style="flex:1;overflow:hidden;display:${activeSlide === 'ai' ? 'flex' : 'none'};flex-direction:column">
        
        <!-- Quick Action Chips -->
        <div id="ai_chips" style="padding:8px 12px;background:var(--card,#fff);border-bottom:1px solid var(--ln,#E1E8F0);display:flex;gap:6px;overflow-x:auto;scrollbar-width:none">
        </div>

        <!-- Chat Stream -->
        <div id="ai_messages" style="flex:1;overflow-y:auto;padding:12px;display:flex;flex-direction:column;gap:10px">
        </div>

        <!-- Input Bar -->
        <div style="padding:10px 12px;background:var(--card,#fff);border-top:1px solid var(--ln,#E1E8F0);display:flex;gap:8px;align-items:center">
          <input id="ai_input" type="text" placeholder="${isNep ? 'नेपाली वा English मा सोध्नुहोस्...' : 'Ask Open Bazar AI in Nepali or English...'}" style="flex:1;padding:12px 14px;border:1.5px solid var(--ln,#E1E8F0);border-radius:12px;font-size:14.5px;background:var(--bg,#F6F9FC);color:inherit;outline:none;font-family:inherit">
          <button id="ai_send" style="background:var(--ind,#0F4C9A);color:#fff;border:0;border-radius:12px;padding:12px 18px;font-weight:800;font-size:14px;cursor:pointer;display:flex;align-items:center;gap:4px">
            ${isNep ? 'पठाउनुहोस्' : 'Send'}
          </button>
        </div>
      </div>

    </div>
  `;

  document.body.appendChild(w);
  assistantModal = w;

  const guideContainer = w.querySelector('#slide_guide_container');
  const aiContainer = w.querySelector('#slide_ai_container');
  const btnGuide = w.querySelector('#btn_slide_guide');
  const btnAi = w.querySelector('#btn_slide_ai');
  const msgContainer = w.querySelector('#ai_messages');
  const inputEl = w.querySelector('#ai_input');
  const sendBtn = w.querySelector('#ai_send');
  const chipsContainer = w.querySelector('#ai_chips');

  function setSlide(slide) {
    activeSlide = slide;
    if (slide === 'guide') {
      guideContainer.style.display = 'flex';
      aiContainer.style.display = 'none';
      btnGuide.classList.remove('g');
      btnGuide.classList.add('s');
      btnAi.classList.remove('s');
      btnAi.classList.add('g');
    } else {
      guideContainer.style.display = 'none';
      aiContainer.style.display = 'flex';
      btnAi.classList.remove('g');
      btnAi.classList.add('s');
      btnGuide.classList.remove('s');
      btnGuide.classList.add('g');
      setTimeout(() => inputEl.focus(), 100);
    }
  }

  btnGuide.onclick = () => setSlide('guide');
  btnAi.onclick = () => setSlide('ai');
  const ctaBtn = w.querySelector('#cta_switch_to_ai');
  if (ctaBtn) ctaBtn.onclick = () => setSlide('ai');

  w.querySelector('#help_modal_close').onclick = () => {
    w.remove();
    assistantModal = null;
  };

  w.onclick = e => {
    if (e.target === w) {
      w.remove();
      assistantModal = null;
    }
  };

  // Render contextual suggestion chips in Slide 2
  const suggestions = getContextualSuggestions();
  suggestions.forEach(sug => {
    const chip = document.createElement('div');
    chip.style.cssText = 'white-space:nowrap;background:var(--bg,#F6F9FC);border:1px solid var(--ln,#E1E8F0);border-radius:18px;padding:6px 12px;font-size:12.5px;font-weight:700;color:var(--ind2,#1B6AC9);cursor:pointer';
    chip.textContent = sug.q;
    chip.onclick = () => {
      setSlide('ai');
      inputEl.value = sug.q;
      handleSend();
    };
    chipsContainer.appendChild(chip);
  });

  function appendBubble(text, isUser = false) {
    const bubble = document.createElement('div');
    bubble.style.cssText = `max-width:85%;padding:10px 14px;border-radius:14px;font-size:13.5px;line-height:1.55;white-space:pre-wrap;word-break:break-word;${
      isUser ? 'margin-left:auto;background:var(--ind,#0F4C9A);color:#fff;border-bottom-right-radius:4px' :
      'margin-right:auto;background:var(--card,#fff);border:1px solid var(--ln,#E1E8F0);color:inherit;border-bottom-left-radius:4px;box-shadow:0 1px 3px rgba(0,0,0,0.05)'
    }`;
    bubble.innerHTML = isUser ? esc(text) : formatBubbleText(text);
    msgContainer.appendChild(bubble);
    msgContainer.scrollTop = msgContainer.scrollHeight;
    return bubble;
  }

  // Welcome message if chat history empty
  if (chatHistory.length === 0) {
    const welcomeText = generateLocalSmartResponse('welcome');
    appendBubble(welcomeText, false);
  } else {
    chatHistory.forEach(h => appendBubble(h.text, h.role === 'user'));
  }

  async function handleSend() {
    const q = inputEl.value.trim();
    if (!q) return;
    inputEl.value = '';
    appendBubble(q, true);
    chatHistory.push({ role: 'user', text: q });

    const loadingBubble = appendBubble(isNep ? 'सोच्दैछ...' : 'Thinking...', false);
    loadingBubble.style.opacity = '0.7';

    try {
      const reply = await queryAssistant(q);
      loadingBubble.innerHTML = formatBubbleText(reply);
      loadingBubble.style.opacity = '1';
      chatHistory.push({ role: 'assistant', text: reply });
    } catch (e) {
      const fallback = generateLocalSmartResponse(q);
      loadingBubble.innerHTML = formatBubbleText(fallback);
      loadingBubble.style.opacity = '1';
    }
  }

  sendBtn.onclick = handleSend;
  inputEl.onkeydown = e => { if (e.key === 'Enter') handleSend(); };
  w.querySelector('#ai_clear').onclick = () => {
    chatHistory = [];
    msgContainer.innerHTML = '';
    appendBubble(generateLocalSmartResponse('welcome'), false);
    toast(isNep ? 'च्याट खाली गरियो' : 'Chat cleared');
  };

  if (initialQuery) {
    setSlide('ai');
    inputEl.value = initialQuery;
    handleSend();
  }
}

window.openOpenBazarHelp = openHelpModal;
window.openOpenBazarAi = function(query) { openHelpModal('ai', query); };
window.openAssistantModal = function(query) { openHelpModal('ai', query); };

/* ---------------- 6. Persistent Help & AI Button ---------------- */

function ensureHelpWidget() {
  // If user dismissed/deleted the floating AI widget, do not create it
  if (localStorage.getItem('mero_bazar_hide_floating_ai') === '1') {
    const existing = document.getElementById('ob_floating_help_btn');
    if (existing) existing.remove();
    return;
  }

  if (document.getElementById('ob_floating_help_btn')) return;

  const btn = document.createElement('div');
  btn.id = 'ob_floating_help_btn';
  
  // Retrieve saved drag position if available
  let savedPos = null;
  try {
    savedPos = JSON.parse(localStorage.getItem('mero_bazar_floating_ai_pos') || 'null');
  } catch (_) {}

  btn.style.cssText = 'position:fixed;z-index:99996;display:flex;align-items:center;gap:6px;background:linear-gradient(135deg,var(--saf,#F28C0F),#F59E0B);color:#fff;padding:8px 12px;border-radius:99px;box-shadow:0 6px 20px rgba(0,0,0,0.35);border:2px solid #fff;user-select:none;touch-action:none;cursor:grab;transition:box-shadow .2s,opacity .2s;';

  if (savedPos && typeof savedPos.left === 'number' && typeof savedPos.top === 'number') {
    btn.style.left = Math.max(8, Math.min(window.innerWidth - 140, savedPos.left)) + 'px';
    btn.style.top = Math.max(50, Math.min(window.innerHeight - 80, savedPos.top)) + 'px';
    btn.style.right = 'auto';
    btn.style.bottom = 'auto';
  } else {
    btn.style.bottom = '78px';
    btn.style.right = '14px';
  }

  btn.innerHTML = `
    <div id="ob_float_drag_area" style="display:flex;align-items:center;gap:6px;cursor:grab">
      <span style="font-size:17px;line-height:1">❓</span>
      <span style="font-size:13.5px;font-weight:750;letter-spacing:0.2px">Help</span>
    </div>
    <button id="ob_float_delete_btn" type="button" title="हटाउनुहोस् / Close" style="background:rgba(0,0,0,0.28);border:0;color:#fff;border-radius:50%;width:22px;height:22px;font-size:12px;cursor:pointer;display:inline-flex;align-items:center;justify-content:center;margin-left:2px;font-weight:700">✕</button>
  `;

  // Delete/dismiss action
  const delBtn = btn.querySelector('#ob_float_delete_btn');
  if (delBtn) {
    delBtn.onclick = function(e) {
      e.stopPropagation();
      e.preventDefault();
      localStorage.setItem('mero_bazar_hide_floating_ai', '1');
      btn.style.transition = 'transform 0.2s, opacity 0.2s';
      btn.style.transform = 'scale(0.5)';
      btn.style.opacity = '0';
      setTimeout(() => btn.remove(), 200);
      if (typeof window.toast === 'function') {
        window.toast('❓ Help बटन हटाइयो (माथिको ❓ बाट फेरि खोल्न सकिन्छ)');
      }
    };
  }

  // Touch and mouse drag logic
  let isDragging = false;
  let startX = 0, startY = 0;
  let initialLeft = 0, initialTop = 0;
  let moved = false;

  function onPointerDown(e) {
    // If clicked on delete button, do nothing
    if (e.target.closest('#ob_float_delete_btn')) return;

    isDragging = true;
    moved = false;
    btn.style.cursor = 'grabbing';
    const clientX = e.touches ? e.touches[0].clientX : e.clientX;
    const clientY = e.touches ? e.touches[0].clientY : e.clientY;
    startX = clientX;
    startY = clientY;
    const rect = btn.getBoundingClientRect();
    initialLeft = rect.left;
    initialTop = rect.top;

    btn.style.right = 'auto';
    btn.style.bottom = 'auto';
    btn.style.left = initialLeft + 'px';
    btn.style.top = initialTop + 'px';
  }

  function onPointerMove(e) {
    if (!isDragging) return;
    const clientX = e.touches ? e.touches[0].clientX : e.clientX;
    const clientY = e.touches ? e.touches[0].clientY : e.clientY;
    const dx = clientX - startX;
    const dy = clientY - startY;

    if (Math.hypot(dx, dy) > 5) {
      moved = true;
    }

    if (moved) {
      const maxLeft = Math.max(8, window.innerWidth - btn.offsetWidth - 8);
      const maxTop = Math.max(50, window.innerHeight - btn.offsetHeight - 65);
      const newLeft = Math.max(8, Math.min(maxLeft, initialLeft + dx));
      const newTop = Math.max(50, Math.min(maxTop, initialTop + dy));

      btn.style.left = newLeft + 'px';
      btn.style.top = newTop + 'px';

      if (e.cancelable) e.preventDefault();
    }
  }

  function onPointerUp(e) {
    if (!isDragging) return;
    isDragging = false;
    btn.style.cursor = 'grab';

    if (moved) {
      const rect = btn.getBoundingClientRect();
      try {
        localStorage.setItem('mero_bazar_floating_ai_pos', JSON.stringify({
          left: Math.round(rect.left),
          top: Math.round(rect.top)
        }));
      } catch (_) {}
    } else {
      // It was a tap, open Help & AI modal!
      if (!e.target.closest('#ob_float_delete_btn')) {
        openHelpModal('guide');
      }
    }
  }

  // Touch event listeners
  btn.addEventListener('touchstart', onPointerDown, { passive: true });
  window.addEventListener('touchmove', onPointerMove, { passive: false });
  window.addEventListener('touchend', onPointerUp, { passive: true });

  // Mouse event listeners
  btn.addEventListener('mousedown', onPointerDown);
  window.addEventListener('mousemove', onPointerMove);
  window.addEventListener('mouseup', onPointerUp);

  document.body.appendChild(btn);
  floatingBtn = btn;
}

// Function to allow user to toggle floating button back on
window.toggleFloatingAiWidget = function(show) {
  if (show) {
    localStorage.removeItem('mero_bazar_hide_floating_ai');
    ensureHelpWidget();
    if (typeof window.toast === 'function') {
      window.toast('❓ तैरिने Help बटन सक्रिय गरियो');
    }
  } else {
    localStorage.setItem('mero_bazar_hide_floating_ai', '1');
    const existing = document.getElementById('ob_floating_help_btn');
    if (existing) existing.remove();
  }
};

// Check every 2 seconds
setInterval(() => {
  ensureHelpWidget();
}, 2000);

// Initialize on script load
setTimeout(() => {
  ensureHelpWidget();
}, 400);

})();

