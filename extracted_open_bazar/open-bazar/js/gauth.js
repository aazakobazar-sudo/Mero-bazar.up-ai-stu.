/* Open Bazar: Google email verification on registration + wide-screen (laptop/desktop) layout */
(function(){
const css=document.createElement('style');css.textContent=`
#gbtn{width:100%;display:flex;align-items:center;justify-content:center;gap:8px;padding:12px;border-radius:10px;border:1px solid var(--ln,#ccc);background:var(--card,#fff);color:inherit;font-size:15px;font-weight:700;margin-bottom:10px;cursor:pointer}
#gnote{font-size:13px;margin:-4px 0 8px;opacity:.8}
@media(min-width:900px){
 body.ob-auth .main{max-width:520px;margin:0 auto;width:100%}
 body.ob-in .main{max-width:820px;margin:0 auto;width:100%}
 body.ob-home #hf{display:grid;grid-template-columns:repeat(auto-fill,minmax(230px,1fr));gap:12px;align-items:start}
 .nav{justify-content:center}.nav>*{max-width:150px}
}`;document.head.appendChild(css);
const ne=()=>(window.DB&&DB.lang)=='ne';
async function google(){
  try{if(!window.firebase)throw{code:'sdk-not-loaded'};const p=new firebase.auth.GoogleAuthProvider();p.setCustomParameters({prompt:'select_account'});
    const r=await firebase.auth().signInWithPopup(p),u=r.user;
    if(!u.emailVerified){await firebase.auth().signOut();return toast(ne()?'यो Google इमेल प्रमाणित छैन':'This Google email is not verified')}
    const n=$('r_nm'),e=$('r_em2');if(n&&!n.value)n.value=u.displayName||'';if(e){e.value=u.email;e.readOnly=true}
    try{rc()}catch(_){}S.r.gv=u.email;toast(ne()?'✓ Google बाट इमेल प्रमाणित भयो':'✓ Email verified with Google');decorate()}
  catch(x){toast('Google: '+(x.code||x.message||x))}}
function decorate(){
  const b=document.body;b.classList.remove('ob-auth','ob-in','ob-home');
  if(!window.S||!window.DB)return;
  b.classList.add(!S.u?'ob-auth':(S.tab=='home'&&!S.sub?'ob-home':'ob-in'));
  const old=document.getElementById('gbtn');if(old)old.remove();const on=document.getElementById('gnote');if(on)on.remove();
  const nm=document.getElementById('r_nm');if(!nm||!document.getElementById('r_em2')||!S.r)return;
  const bt=document.createElement('button');bt.id='gbtn';bt.type='button';
  bt.innerHTML=S.r.gv?('✓ '+(ne()?'Google प्रमाणित: ':'Google verified: ')+S.r.gv):('<b style="color:#4285F4;font-size:18px">G</b> '+(ne()?'Google बाट इमेल प्रमाणित गर्नुहोस्':'Verify email with Google'));
  bt.onclick=S.r.gv?null:google;nm.before(bt)}

/* ---- Forgot password screen (email reset link + resend) ---- */
function ov(html){const w=document.createElement('div');w.style.cssText='position:fixed;inset:0;z-index:100000;background:var(--bg,#fff);color:var(--tx,#111);overflow:auto;padding:16px;font-family:inherit';w.innerHTML='<div style="max-width:460px;margin:0 auto">'+html+'</div>';document.body.appendChild(w);return w}
const IN='width:100%;padding:12px;border:1px solid var(--ln,#ccc);border-radius:10px;font-size:15px;margin:6px 0;background:var(--card,#fff);color:inherit',BT='width:100%;padding:12px;border:0;border-radius:10px;font-size:15px;font-weight:700;background:var(--ind,#1E2B6E);color:#fff;margin-top:6px';
window.forgot=function(){const n=ne(),pre=(document.getElementById('em')||{}).value||'';
  const w=ov('<button id="fx" style="float:right;padding:8px 14px;border:1px solid var(--ln);border-radius:8px;background:none;color:inherit">'+(n?'बन्द':'Close')+'</button><h2 style="margin:0 0 6px">🔑 '+(n?'पासवर्ड बिर्सनुभयो?':'Forgot password?')+'</h2><div style="font-size:14px;line-height:1.55;opacity:.85;margin-bottom:8px">'+(n?'आफ्नो दर्ता गरेको इमेल राख्नुहोस्। हामी त्यहाँ पासवर्ड बदल्ने लिङ्क पठाउँछौँ। इमेलको लिङ्क खोलेर नयाँ पासवर्ड राख्नुहोस्, अनि यहाँ फर्केर लगइन गर्नुहोस्।':'Enter your registered email. We will send a link to set a new password. Open the link in the email, set a new password, then come back here and log in.')+'</div><input id="fe" type="email" style="'+IN+'" placeholder="Email" value="'+pre.replace(/"/g,'')+'"><button id="fs" style="'+BT+'">'+(n?'रिसेट लिङ्क पठाउनुहोस्':'Send reset link')+'</button><div id="fm" style="margin-top:10px;font-size:14px;line-height:1.5"></div>');
  w.querySelector('#fx').onclick=()=>w.remove();let cd=0,tm=null;const b=w.querySelector('#fs'),m=w.querySelector('#fm');
  const tick=()=>{if(cd>0){b.disabled=true;b.style.opacity='.55';b.textContent=(n?'फेरि पठाउन ':'Resend in ')+cd+'s';cd--;tm=setTimeout(tick,1000)}else{b.disabled=false;b.style.opacity='1';b.textContent=sent?(n?'फेरि पठाउनुहोस् (Resend)':'Resend link'):(n?'रिसेट लिङ्क पठाउनुहोस्':'Send reset link')}};let sent=false;
  b.onclick=async()=>{const e=w.querySelector('#fe').value.trim().toLowerCase();if(!/\S+@\S+\.\S+/.test(e)){m.textContent=n?'सही इमेल राख्नुहोस्':'Enter a valid email';return}
    if(!window.firebase){m.textContent=n?'क्लाउड लोड भएको छैन। इन्टरनेट जाँचेर पृष्ठ रिफ्रेस गर्नुहोस्।':'Cloud not loaded. Check internet and refresh the page.';return}
    b.disabled=true;m.textContent='...';
    try{await firebase.auth().sendPasswordResetEmail(e);sent=true;m.innerHTML='✅ '+(n?'यदि यो इमेल क्लाउडमा दर्ता छ भने लिङ्क पठाइयो। इनबक्स र <b>Spam</b> फोल्डर हेर्नुहोस्।':'If this email is registered in the cloud, a link was sent. Check your inbox and the <b>Spam</b> folder.')+'<div style="opacity:.75;margin-top:6px">'+(n?'इमेल नआए: यो इमेलले नयाँ संस्करणमा एकपटक लगइन गर्नुभएको छैन होला। पुरानो पासवर्डले एकपटक लगइन गरेपछि फेरि प्रयास गर्नुहोस्, वा admin लाई सम्पर्क गर्नुहोस्।':'No email? This address may not have logged in on this version yet. Log in once with your old password, then try again, or contact the admin.')+'</div>';cd=60;tick()}
    catch(x){b.disabled=false;m.textContent=(n?'त्रुटि: ':'Error: ')+(x.code||x.message)}};
  setTimeout(()=>{const f=w.querySelector('#fe');f&&!f.value&&f.focus()},100)};
/* ---- Shop location + directions (customers) ---- */
const loadL=()=>new Promise((ok,no)=>{if(window.L)return ok();const l=document.createElement('link');l.rel='stylesheet';l.href='https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css';document.head.appendChild(l);const s=document.createElement('script');s.src='https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.js';s.onload=ok;s.onerror=no;document.head.appendChild(s)});
function showLoc(s){const n=ne(),has=s.la!=null&&s.lo!=null&&isFinite(s.la)&&isFinite(s.lo);
  const w=ov('<button id="lx" style="float:right;padding:8px 14px;border:1px solid var(--ln);border-radius:8px;background:none;color:inherit">'+(n?'बन्द':'Close')+'</button><h2 style="margin:0 0 4px">📍 '+String(s.shop.name).replace(/</g,'&lt;')+'</h2><div style="font-size:13px;opacity:.8;margin-bottom:8px">'+(has?(n?'रातो पिनमा पसल छ।':'The red pin is the shop.'):(n?'यो पसलले अझै स्थान राखेको छैन।':'This shop has not set its location yet.'))+'</div>'+(has?'<div id="lm" style="height:55vh;border-radius:12px;overflow:hidden;border:1px solid var(--ln)"></div><button id="ld" style="'+BT+'">🧭 '+(n?'बाटो देखाउनुहोस् (Directions)':'Get directions')+'</button>':''));
  w.querySelector('#lx').onclick=()=>w.remove();if(!has)return;
  w.querySelector('#ld').onclick=()=>window.open('https://www.openstreetmap.org/directions?engine=fossgis_osrm_car&route=%3B'+(+s.la)+'%2C'+(+s.lo),'_blank','noopener');
  loadL().then(()=>{const m=L.map('lm',{minZoom:6}).setView([s.la,s.lo],16);L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png',{maxZoom:19,attribution:'© OpenStreetMap'}).addTo(m);L.circleMarker([s.la,s.lo],{radius:12,color:'#fff',weight:3,fillColor:'#e53935',fillOpacity:1}).addTo(m);
    navigator.geolocation&&navigator.geolocation.getCurrentPosition(r=>{L.circleMarker([r.coords.latitude,r.coords.longitude],{radius:8,color:'#fff',weight:2,fillColor:'#1a73e8',fillOpacity:1}).addTo(m);m.fitBounds([[s.la,s.lo],[r.coords.latitude,r.coords.longitude]],{padding:[40,40],maxZoom:16})},()=>{},{timeout:8000})}).catch(()=>{const e=w.querySelector('#lm');if(e)e.textContent=n?'नक्सा लोड भएन (इन्टरनेट?)':'Map could not load (internet?)'})}
function locBtn(){['lbtn','cbtn'].forEach(i=>{const o=document.getElementById(i);if(o)o.remove()});if(!window.S||!S.u||S.sub!=='shop'||!S.sid)return;const s=U(S.sid),m=document.querySelector('.main');if(!s||!s.shop||!m)return;
  const b=document.createElement('button');b.id='lbtn';b.style.cssText='width:100%;padding:12px;border:0;border-radius:10px;font-size:15px;font-weight:700;background:var(--saf,#E8820C);color:#fff;margin-bottom:10px';b.textContent='📍 '+(ne()?'स्थान र बाटो हेर्नुहोस्':'View location & directions');b.onclick=()=>showLoc(s);m.insertBefore(b,m.firstChild);
  const ph=String(s.phone||'').replace(/\D/g,'');if(/^9\d{9}$/.test(ph)){const c=document.createElement('button');c.id='cbtn';c.style.cssText='width:100%;padding:12px;border:0;border-radius:10px;font-size:15px;font-weight:700;background:#2e7d32;color:#fff;margin-bottom:10px';c.textContent='📞 '+(ne()?'पसललाई फोन गर्नुहोस्':'Call the shop')+' · '+ph;c.onclick=()=>{location.href='tel:+977'+ph};m.insertBefore(c,m.firstChild)}}
const _R=window.R;if(typeof _R!=='function')return;
window.R=function(){const r=_R.apply(this,arguments);try{decorate();locBtn()}catch(e){console.warn('gauth',e)}return r};
setTimeout(()=>{try{decorate()}catch(_){}},400);
})();
