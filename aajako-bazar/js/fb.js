/* Firebase Auth (stage 1): mirrors local login/register into Firebase Auth. Local login stays as fallback. */
(function(){
const cfg={apiKey:"AIzaSyA48YiJe9WQNGcs0_a8ADtDPVn7AqbbkbU",authDomain:"aazabo-bazar.firebaseapp.com",projectId:"aazabo-bazar",storageBucket:"aazabo-bazar.firebasestorage.app",messagingSenderId:"813051695868",appId:"1:813051695868:web:dd227f036d4dd07b967d79"};
const bd=document.createElement('div');bd.style.cssText='position:fixed;bottom:3px;left:3px;z-index:99999;font:11px sans-serif;background:#000b;color:#fff;padding:2px 7px;border-radius:9px;max-width:70%';
const st=s=>{bd.textContent='\u2601 '+s;if(!bd.parentNode)document.documentElement.appendChild(bd)};
st(typeof firebase==='undefined'?'Firebase SDK not loaded':'connecting...');
bd.onclick=()=>window.cloudPull&&window.cloudPull();
/* Bill: saving a new bill downloads it automatically as an image */
(function(){const m=window.mkBill;if(typeof m!=='function')return;
window.mkBill=function(){const r=m.apply(this,arguments);setTimeout(()=>{try{if(S.sub==='bill'&&S.bi&&typeof billImg==='function')billImg()}catch(_){}},500);return r}})();
/* Map picker (OpenStreetMap + Leaflet, free, Nepal only): tap to drop a red pin, Save */
window.pickLoc=function(cb,must){
  const ld=()=>new Promise((ok,no)=>{if(window.L)return ok();const l=document.createElement('link');l.rel='stylesheet';l.href='https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css';document.head.appendChild(l);const s=document.createElement('script');s.src='https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.js';s.onload=ok;s.onerror=no;document.head.appendChild(s)});
  const B=[[26.3,80.0],[30.5,88.3]],w=document.createElement('div');
  w.style.cssText='position:fixed;inset:0;z-index:100000;background:#fff;color:#111;display:flex;flex-direction:column;font-family:system-ui,sans-serif';
  w.innerHTML='<div style="background:#0F4C9A;color:#fff;padding:14px 16px;display:flex;justify-content:space-between;align-items:center"><b style="font-size:16px">📍 नेपालको नक्सामा एरिया छान्नुहोस् (रातो पिन)</b>'+(must?'':'<button id="pkc" style="border:0;background:none;color:#fff;font-size:20px;cursor:pointer">✕</button>')+'</div><div style="padding:8px 16px;background:#FEF2F2;border-bottom:1px solid #FCA5A5;font-size:12px;color:#991B1B;font-weight:700">🔴 नक्सामा आफू रहेको ठाउँ वा पसलमा ट्याप गर्नुहोस्। त्यहाँ रातो पिन बस्नेछ (अनिवार्य)।</div><div id="pkm" style="flex:1;min-height:300px"></div><div style="display:flex;gap:8px;padding:12px;background:#F8FAFC;border-top:1px solid #E2E8F0"><button id="pkg" type="button" style="flex:1;padding:11px;border-radius:10px;border:1px solid #CBD5E1;background:#fff;font-size:13px;font-weight:700;cursor:pointer">📡 मेरो GPS</button><button id="pks" type="button" style="flex:2;padding:11px;border-radius:10px;border:0;background:#DC2626;color:#fff;font-size:14px;font-weight:800;cursor:pointer;box-shadow:0 3px 10px rgba(220,38,38,.4)">🔴 रातो पिन सुरक्षित गर्नुहोस्</button>'+(must?'<button id="pkc" type="button" style="flex:1;padding:11px;border-radius:10px;border:1px solid #CBD5E1;background:#fff;font-size:13px;cursor:pointer">बन्द</button>':'')+'</div>';
  document.body.appendChild(w);let pt=null,mk=null,map;
  const inN=(a,o)=>a>=B[0][0]&&a<=B[1][0]&&o>=B[0][1]&&o<=B[1][1];
  const put=(a,o)=>{if(!inN(a,o))return toast('कृपया नेपालभित्रको स्थान मात्र छान्नुहोस्');pt=[a,o];if(mk)mk.setLatLng(pt);else mk=L.circleMarker(pt,{radius:12,color:'#ffffff',weight:3,fillColor:'#DC2626',fillOpacity:1}).addTo(map);mk.bindPopup('<b>📍 तपाईंको छनोट गरिएको स्थान</b><br>Lat: '+a.toFixed(4)+', Lon: '+o.toFixed(4)).openPopup()};
  ld().then(()=>{map=L.map('pkm',{maxBounds:B,minZoom:6}).setView([27.7045,85.312],13);L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png',{maxZoom:19,attribution:'\u00a9 OpenStreetMap'}).addTo(map);map.on('click',e=>put(e.latlng.lat,e.latlng.lng));
    const u=window.S&&S.u;if(u&&u.la!=null&&inN(u.la,u.lo)){map.setView([u.la,u.lo],16);put(u.la,u.lo)}}).catch(()=>{toast('नक्सा लोड हुन सकेन (इन्टरनेट जाँच्नुहोस्)');w.remove()});
  w.querySelector('#pkg').onclick=()=>navigator.geolocation&&navigator.geolocation.getCurrentPosition(r=>{if(map){map.setView([r.coords.latitude,r.coords.longitude],17);put(r.coords.latitude,r.coords.longitude)}},()=>toast('GPS उपलब्ध भएन'),{enableHighAccuracy:true,timeout:15000});
  w.querySelector('#pks').onclick=()=>{if(!pt)return toast('पहिले नक्सामा कतै थिचेर रातो पिन लगाउनुहोस्');w.remove();cb(+pt[0].toFixed(6),+pt[1].toFixed(6))};
  const c=w.querySelector('#pkc');if(c)c.onclick=()=>w.remove()};
let auth=null;
try{if(typeof firebase!=='undefined'){firebase.initializeApp(cfg);auth=firebase.auth()}}catch(e){console.warn('fb init',e)}
if(!auth)return;
window.forgot=async function(){const e=em($('em')&&$('em').value);if(!e)return toast('Enter your email first');try{await auth.sendPasswordResetEmail(e);toast('Reset link sent. Check your email (also Spam).')}catch(x){toast('Cloud: '+(x.code||x))}};
let wt=0;const warn=(w,x)=>{console.warn(w,x);st('error: '+((x&&x.code)||x));if(Date.now()-wt>60000){wt=Date.now();try{toast('Cloud: '+((x&&x.code)||x))}catch(_){}}};
let cred=null;
const em=s=>String(s||'').trim().toLowerCase();
var mirror=async function mirror(c,u){
  try{
    let r;
    try{r=await auth.signInWithEmailAndPassword(c.e,c.p)}
    catch(x){
      if(['auth/user-not-found','auth/invalid-credential','auth/invalid-login-credentials'].includes(x.code)){r=await auth.createUserWithEmailAndPassword(c.e,c.p);try{await r.user.sendEmailVerification();toast('Verification email sent. Please check your inbox/Spam.')}catch(_){}}
      else throw x}
    if(u&&r.user&&u.fu!==r.user.uid){u.fu=r.user.uid;save()}
    if(r.user&&!r.user.emailVerified&&u&&u.role!='a'){try{st('email not verified - check inbox')}catch(_){}}
    if(u&&u.role=='a'&&typeof claimAdmin=='function')claimAdmin(r.user);
  }catch(x){warn('fb auth',x)}
}
const _login=window.login;
window.login=function(e,p){cred={e:em(e),p:p};return _login.apply(this,arguments)};
const _reg=window.regNext;
window.regNext=function(){try{rc();const d=S.r&&S.r.d;if(d&&d.em2&&d.pw1)cred={e:em(d.em2),p:d.pw1}}catch(_){}return _reg.apply(this,arguments)};
const _sess=window.sess;
window.sess=function(u){const c=cred;cred=null;const r=_sess.apply(this,arguments);if(c&&S.u===u)mirror(c,u);return r};
const _lo=window.logout;
window.logout=function(){try{auth.signOut()}catch(_){}return _lo.apply(this,arguments)};
const _ch=window.chPw;
window.chPw=async function(){
  const op=$('op')&&$('op').value,np=$('np')&&$('np').value,before=S.u&&S.u.pass;
  const r=await _ch.apply(this,arguments);
  try{const cu=auth.currentUser;if(cu&&np&&S.u&&S.u.pass!==before){await cu.reauthenticateWithCredential(firebase.auth.EmailAuthProvider.credential(cu.email,op));await cu.updatePassword(np)}}
  catch(x){console.warn('fb pw',x.code||x)}
  return r};

/* ---- pub/{uid}: merchant shop+items+notices (~<=20KB, all signed-in users read). priv/{uid}: own full profile (owner only). ---- */
let fs=null;try{fs=firebase.firestore();fs.settings({experimentalAutoDetectLongPolling:true,merge:true})}catch(e){console.warn('fs',e)}
if(fs){
const BADK=new Set(['photo','doc','tok','pass','salt','img','pic','image','fu']);
const cl=x=>{if(Array.isArray(x))return x.map(cl).filter(v=>v!==undefined);if(x&&typeof x=='object'){const o={};for(const k in x){if(BADK.has(k)||k[0]=='_')continue;const v=cl(x[k]);if(v!==undefined)o[k]=v}return o}if(typeof x=='string'&&(x.startsWith('data:')||x.length>1500))return undefined;if(typeof x=='number'&&!isFinite(x))return 0;return x};
const hh=s=>{let c=~0;for(let i=0;i<s.length;i++){c^=s.charCodeAt(i);for(let k=0;k<8;k++)c=c>>>1^(c&1?0xEDB88320:0)}return 1e6+((~c)>>>0)%9e8};
const IC={};
const shrink=src=>new Promise(ok=>{const i=new Image();i.onload=()=>{const k=Math.min(1,480/Math.max(i.width,i.height)),c=document.createElement('canvas');c.width=Math.round(i.width*k);c.height=Math.round(i.height*k);c.getContext('2d').drawImage(i,0,0,c.width,c.height);let q=.6,r;for(;;){r=c.toDataURL('image/jpeg',q);if(r.length<=90000||q<=.3)break;q-=.1}ok(r.length<=110000?r:null)};i.onerror=()=>ok(null);i.src=src});
async function upImgs(u){if(u.role!='m')return false;let ch=false;
  for(const p of DB.products){if(p.mid!=u.id||!p.img||!p.img.startsWith('data:')||(p.ih&&p._il===p.img.length))continue;
    const d=await shrink(p.img);if(!d)continue;
    try{await fs.collection('imgs').doc(auth.currentUser.uid+'_'+p.id).set({d,ts:Date.now()});p.ih=Date.now();p._il=p.img.length;ch=true}catch(x){console.warn('img',x.code||x);break}}
  return ch}
async function pullImgs(){let ch=false;const need=DB.products.filter(p=>p._r&&p.ih&&!(IC[p._k]&&IC[p._k].v==p.ih)).slice(0,30);
  await Promise.all(need.map(async p=>{try{const d=await fs.collection('imgs').doc(p._k).get();if(d.exists){IC[p._k]={v:p.ih,d:d.data().d};ch=true}}catch(x){console.warn('imgget',x.code||x)}}));return ch}
let AP={},APs='';
async function pushApprovals(){const m=mine();if(!m||m.u.role!='a')return;
  for(const x of DB.users){if(!x._r||!x._uid)continue;const h=x.shop||{},g=x.shop?[h.ok,h.exp,x.on,h.fake,h.slots].join('|'):(x.on?1:0)+'|||0|0';if(x._as===g)continue;
    await fs.collection('approvals').doc(x._uid).set({ok:h.ok?1:0,exp:h.exp||'',on:x.on?1:0,fake:h.fake||'',slots:h.slots||0,ts:Date.now()});x._as=g}}
async function claimAdmin(cu){try{const r=fs.doc('cfg/admin');if(!(await r.get()).exists)await r.set({uid:cu.uid})}catch(x){warn('admin',x)}}
async function pushPays(){const m=mine();if(!m)return false;const{u,cu}=m;let ch=false;
  for(const o of DB.pay){const asM=o.mid==u.id;if(!asM&&u.role!='a')continue;const id=o._o||(asM?cu.uid+'_'+o.id:'');if(!id||(o._o&&o._s===o.st))continue;
    await fs.collection('pays').doc(id).set(asM&&!o._o?{muid:cu.uid,j:JSON.stringify(cl(o)),st:o.st,ts:Date.now()}:{st:o.st,ts:Date.now()},{merge:true});o._o=id;o._s=o.st;ch=true}
  return ch}
async function pullPays(){const m=mine();if(!m)return false;const{u,cu}=m;let ch=false;
  const s=await(u.role=='a'?fs.collection('pays'):fs.collection('pays').where('muid','==',cu.uid)).get();
  s.forEach(d=>{const x=d.data();let o;try{o=JSON.parse(x.j)}catch(_){return}const l=DB.pay.find(y=>y._o===d.id);
    if(!l){if(u.role=='a'){DB.pay.unshift(Object.assign({},o,{id:hh(d.id),mid:hh(x.muid),st:x.st,_o:d.id,_s:x.st}));ch=true}return}
    if(x.st!==l._s){l.st=x.st;l._s=x.st;ch=true}});
  if(ch)save();return ch}
async function pushOrders(){const m=mine();if(!m)return false;const{u,cu}=m;let ch=false;
  for(const o of DB.orders){const asC=o.cid==u.id,asM=o.mid==u.id;if(!asC&&!asM)continue;
    const id=o._o||(asC?cu.uid+'_'+o.id:'');if(!id||(o._o&&o._s===o.st))continue;
    const mu=U(o.mid),muid=asM?cu.uid:mu&&mu._uid;if(!muid)continue;
    const x=asC&&!o._o?{cuid:cu.uid,muid,j:JSON.stringify(cl(Object.assign({},o,{cn:u.name,cp:u.phone,ca:[u.dist,u.local,u.ward].join(', ')}))),st:o.st,ts:Date.now()}:{st:o.st,ts:Date.now()};
    await fs.collection('orders').doc(id).set(x,{merge:true});o._o=id;o._s=o.st;ch=true}
  return ch}
async function pullOrders(){const m=mine();if(!m)return false;const{u,cu}=m;let ch=false;
  const s=await fs.collection('orders').where(u.role=='m'?'muid':'cuid','==',cu.uid).get();
  s.forEach(d=>{const x=d.data();let o;try{o=JSON.parse(x.j)}catch(_){return}let l=DB.orders.find(y=>y._o===d.id);
    if(u.role=='m'){const cid=hh(x.cuid);if(!DB.users.some(y=>y.id==cid))DB.users.push({id:cid,name:o.cn||'Customer',phone:o.cp||'',dist:'',local:o.ca||'',ward:'',email:'c'+cid+'@remote.invalid',role:'c',on:1,salt:'',pass:'',wallet:0,pts:0,_c:1});
}
    if(!l){DB.orders.unshift(Object.assign({},o,{id:hh(d.id),mid:u.role=='m'?u.id:hh(x.muid),cid:u.role=='m'?hh(x.cuid):u.id,st:x.st,_o:d.id,_s:x.st}));ch=true;return}
    if(l&&x.st!==l._s){l.st=x.st;l._s=x.st;ch=true}});
  if(ch)save();return ch}
let pt,last='',DIR=null;
const mine=()=>{const u=S.u,cu=auth.currentUser;return u&&cu&&u.fu===cu.uid?{u,cu}:null};
var push=async function push(){const m=mine();if(!m)return;const{u,cu}=m;let er=null;
  const step=async f=>{try{await f()}catch(x){er=er||x;warn('push',x)}};
  await step(async()=>{if(await upImgs(u))save()});
  await step(async()=>{if(await pushOrders())save()});
  await step(()=>fs.collection('priv').doc(cu.uid).set({j:JSON.stringify(cl(u)),ts:Date.now()}));
  if(u.role!='a')await step(()=>fs.collection('dir').doc(cu.uid).set({j:JSON.stringify({n:u.name||'',r:u.role,p:u.phone||'',d:[u.dist,u.local].filter(Boolean).join(', ')}),ts:Date.now()}));
  if(u.role=='m'&&u.shop)await step(()=>{let o={u:cl(u),p:cl(DB.products.filter(x=>x.mid==u.id)),n:cl(DB.notices.filter(x=>x.mid==u.id))},j=JSON.stringify(o);
    while(j.length>20000&&(o.n.length||o.p.length)){(o.n.length?o.n:o.p).pop();j=JSON.stringify(o)}
    return fs.collection('pub').doc(cu.uid).set({j,ts:Date.now()})});
  if(!er)st('saved to cloud')}
function merge(s){const me=auth.currentUser&&auth.currentUser.uid;
  ['users','products','notices'].forEach(k=>DB[k]=DB[k].filter(x=>!x._r));
  if(DIR)DIR.forEach(d=>{if(d.id===me)return;let x;try{x=JSON.parse(d.data().j)}catch(_){return}if(x.r=='m')return;const a=AP[d.id]||{};DB.users.push({id:hh(d.id),name:x.n,phone:x.p,dist:x.d,local:'',ward:'',email:'r'+d.id+'@remote.invalid',role:'c',on:a.on===0?0:1,salt:'',pass:'',wallet:0,pts:0,_r:1,_uid:d.id,_as:'|||0|0'.replace(/^/,(a.on===0?0:1)+'')})});
  s.forEach(d=>{if(d.id===me)return;let o;try{o=JSON.parse(d.data().j)}catch(_){return}if(!o.u||!o.u.shop)return;const b=hh(d.id);
    const U0=Object.assign({},o.u,{id:b,email:'r'+d.id+'@remote.invalid',role:'m',on:1,salt:'',pass:'',_r:1,_uid:d.id}),a=AP[d.id]||{};U0.shop=Object.assign({},o.u.shop,{ok:a.ok||0,exp:a.exp||'',fake:a.fake||'',slots:a.slots||0});U0.on=a.on===0?0:1;U0._as=[U0.shop.ok,U0.shop.exp,U0.on,U0.shop.fake,U0.shop.slots].join('|');DB.users.push(U0);
    (o.p||[]).forEach(p=>{const k=d.id+'_'+p.id,q=Object.assign({},p,{id:b*1e4+p.id%1e4,mid:b,_r:1,_k:k});Object.defineProperty(q,'img',{get(){const c=IC[k];return c&&c.v==q.ih?c.d:''},enumerable:false});DB.products.push(q)});
    (o.n||[]).forEach(n=>DB.notices.push(Object.assign({},n,{id:b*1e4+n.id%1e4,mid:b,_r:1})))})}
async function pull(){const m=mine();if(!m)return;let ch=false,er=null;
  const step=async f=>{try{if(await f())ch=true}catch(x){er=er||x;console.warn('pull',x.code||x)}};
  if(m.u.role=='a'){await step(()=>claimAdmin(m.cu));await step(async()=>{await pushApprovals()})}
  await step(async()=>{const ap=await fs.collection('approvals').get(),g=JSON.stringify(ap.docs.map(d=>[d.id,d.data().ts]));if(g===APs)return false;APs=g;AP={};ap.forEach(d=>AP[d.id]=d.data());last='';return true});
  const my=AP[m.cu.uid];
  if(m.u.role!='a'&&my&&my.on===0&&m.u.on){m.u.on=0;save();logout();return}
  if(m.u.role=='m'&&my){const h=m.u.shop||{};if(h.ok!=my.ok||h.exp!==my.exp||(m.u.on?1:0)!==my.on||(h.slots||0)!==my.slots||(h.fake||'')!==my.fake){Object.assign(h,{ok:my.ok,exp:my.exp,slots:my.slots,fake:my.fake});m.u.on=my.on;ch=true;save()}}
  await step(()=>pushPays());await step(()=>pullPays());
  if(m.u.role=='a')await step(async()=>{DIR=await fs.collection('dir').get()});
  await step(async()=>{const s=await fs.collection('pub').get(),sig=s.docs.filter(d=>d.id!==m.cu.uid).map(d=>d.id+d.data().ts).join()+(DIR?DIR.docs.map(d=>d.id+d.data().ts).join():'');if(sig===last)return false;last=sig;merge(s);return true});
  await step(()=>pullImgs());await step(()=>pullOrders());
  const a=document.activeElement;if(ch&&!(a&&/INPUT|TEXTAREA|SELECT/.test(a.tagName)))R();
  const n=DB.users.filter(x=>x._r&&x.role=='m').length,c=DB.users.filter(x=>x._r&&x.role=='c').length;
  st((er?'error: '+(er.code||er)+' - ':'online - ')+n+' shops'+(m.u.role=='a'?', '+c+' customers':''))}
window.cloudPull=pull;
const _save=window.save;window.save=function(){const r=_save.apply(this,arguments);clearTimeout(pt);pt=setTimeout(push,1500);return r};
const _m=mirror;mirror=async function(c,u){await _m(c,u);await push();await pull();if(mine())try{toast('Cloud connected')}catch(_){}};
setTimeout(()=>{if(S.u&&!mine()){st('sign out and sign in again');try{toast('Cloud sync off: log out and log in again')}catch(_){}}else if(!S.u)st('ready - please log in')},6000);
async function restore(e,p){const r=await auth.signInWithEmailAndPassword(e,p),uid=r.user.uid,pv=await fs.collection('priv').doc(uid).get();if(!pv.exists)return;
  const u=JSON.parse(pv.data().j);u.id=nid();u.salt=rnd(16);u.pass=await hp(p,u.salt);u.on=1;u.fu=uid;DB.users.push(u);
  if(u.role=='m'){const pb=await fs.collection('pub').doc(uid).get();if(pb.exists){const o=JSON.parse(pb.data().j);(o.p||[]).forEach(x=>DB.products.push(Object.assign(x,{id:nid(),mid:u.id})));(o.n||[]).forEach(x=>DB.notices.push(Object.assign(x,{id:nid(),mid:u.id})))}}
  save()}
const _l2=window.login;window.login=async function(e,p){try{const u0=DB.users.find(x=>canon(x.email)===canon(e));if(!u0)await restore(em(e),p);else if(u0.role!='a'&&(await hp(p||'',u0.salt))!==u0.pass){await auth.signInWithEmailAndPassword(em(e),p);u0.salt=rnd(16);u0.pass=await hp(p,u0.salt);u0.fu=auth.currentUser.uid;save()}}catch(x){console.warn('restore',x.code||x)}return _l2.apply(this,arguments)};

async function upProfs(u,cu){const k=(u.photo||'').length+'|'+((u.shop&&u.shop.doc)||'').length;if(u._pk===k)return;
  const o={ts:Date.now()};if(u.photo&&u.photo.startsWith('data:'))o.p=await shrink(u.photo,480,.6);if(u.shop&&u.shop.doc&&u.shop.doc.startsWith('data:'))o.d=await shrink(u.shop.doc,640,.6);
  if(o.p||o.d){await fs.collection('profs').doc(cu.uid).set(o);u._pk=k;save()}}
const _p0=push;push=async function(){await _p0();const m=mine();if(m&&m.u.role!='a')try{await upProfs(m.u,m.cu)}catch(x){warn('profs',x)}};
const _m3=mirror;mirror=async function(c,u){await _m3(c,u);const m=mine();if(m)try{await fs.collection('logins').add({uid:m.cu.uid,n:m.u.name||'',r:m.u.role,e:m.u.email||'',t:Date.now()})}catch(x){warn('log',x)}};
let pk=false;setInterval(()=>{const u=window.S&&S.u;if(u&&u.role=='m'&&u.la==null&&!pk&&!document.getElementById('pkm')){pk=true;pickLoc((a,o)=>{u.la=a;u.lo=o;save();R();pk=false},true)}},4000);
let sb=null;setInterval(()=>{const u=window.S&&S.u,show=u&&u.role=='a'&&mine();if(show&&!sb){sb=document.createElement('button');sb.textContent='\ud83d\udccb Statement';sb.style.cssText='position:fixed;bottom:34px;right:10px;z-index:99998;padding:9px 14px;border-radius:20px;border:0;background:#c62828;color:#fff;font-weight:700';sb.onclick=statement;document.body.appendChild(sb)}else if(!show&&sb){sb.remove();sb=null}},2000);
async function statement(){const w=document.createElement('div');w.style.cssText='position:fixed;inset:0;z-index:100000;background:#fff;color:#111;overflow:auto;padding:14px;font:14px system-ui,sans-serif';
  w.innerHTML='<button style="float:right;padding:8px 14px" onclick="this.parentNode.remove()">Close</button><h2 style="margin:0 0 8px">Statement</h2><div id="stb">Loading...</div>';document.body.appendChild(w);
  const e=s=>String(s==null?'':s).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c])),D=ts=>new Date(ts).toLocaleString();let h='';
  try{const [dr,lg,pf,pb]=await Promise.all([fs.collection('dir').get(),fs.collection('logins').orderBy('t','desc').limit(150).get(),fs.collection('profs').get(),fs.collection('pub').get()]);
    const P={},L={};pf.forEach(d=>P[d.id]=d.data());pb.forEach(d=>{try{const u=JSON.parse(d.data().j).u;L[d.id]=u&&u.la!=null?u.la+','+u.lo:''}catch(_){}});
    h+='<h3>Registered ('+dr.size+')</h3>';dr.forEach(d=>{let x={};try{x=JSON.parse(d.data().j)}catch(_){}const q=P[d.id]||{};
      h+='<div style="border:1px solid #ddd;border-radius:10px;padding:8px;margin:6px 0"><b>'+e(x.n)+'</b> \u00b7 '+(x.r=='m'?'Merchant':'Customer')+'<br>'+e(x.p)+' \u00b7 '+e(x.d)+(L[d.id]?'<br><a target="_blank" href="https://www.openstreetmap.org/?mlat='+L[d.id].split(',')[0]+'&mlon='+L[d.id].split(',')[1]+'#map=17/'+L[d.id].replace(',','/')+'">\ud83d\udccd '+e(L[d.id])+'</a>':'')+'<br>'+(q.p?'<img src="'+q.p+'" style="height:70px;border-radius:8px;margin:4px 4px 0 0">':'')+(q.d?'<img src="'+q.d+'" style="height:70px;border-radius:8px;margin-top:4px">':'')+'</div>'});
    h+='<h3>Login statement (latest 150)</h3>';lg.forEach(d=>{const x=d.data();h+='<div style="padding:5px 0;border-bottom:1px solid #eee">'+D(x.t)+' \u00b7 <b>'+e(x.n)+'</b> ('+(x.r=='m'?'Merchant':x.r=='a'?'Admin':'Customer')+') '+e(x.e)+'</div>'})}
  catch(x){h='Error: '+e(x.code||x)}
  w.querySelector('#stb').innerHTML=h}
let kt,ul=[],lon=false;const kick=()=>{clearTimeout(kt);kt=setTimeout(pull,1200)};
const listen=m=>{const f=()=>{};const add=q=>ul.push(q.onSnapshot(kick,f));
  add(fs.collection('pub'));add(fs.collection('approvals'));
  add(fs.collection('orders').where(m.u.role=='m'?'muid':'cuid','==',m.cu.uid));
  if(m.u.role=='a'){add(fs.collection('pays'));add(fs.collection('dir'))}else if(m.u.role=='m')add(fs.collection('pays').where('muid','==',m.cu.uid))};
setInterval(()=>{const m=mine();if(m&&!lon){lon=true;try{listen(m)}catch(x){warn('live',x)}}else if(!m&&lon){lon=false;ul.forEach(f=>{try{f()}catch(_){}});ul=[]}},2000);
setInterval(pull,60000);
}
})();
