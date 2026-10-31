/* Open Bazar: Taxpayer Incentive Prize (IRD) helper - prepares bill details and opens the OFFICIAL portal. The app cannot upload for the user. */
(function(){
const PORTAL='https://prize.ird.gov.np/';
const ne=()=>(window.DB&&DB.lang)=='ne',esc=s=>String(s==null?'':s).replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const BT='padding:10px 14px;border:0;border-radius:10px;font-size:14px;font-weight:700;background:var(--ind,#1E2B6E);color:#fff;cursor:pointer';
function rows(){const u=S.u,R=[];
  DB.orders.forEach(o=>{if(!['Processing','Packed','Shipped','Delivered'].includes(o.st))return;const m=U(o.mid);if(u.role=='c'&&o.cid!=u.id)return;if(u.role=='m'&&o.mid!=u.id)return;
    R.push({k:'o'+o.id,ref:o,no:'O'+o.id,pan:(m&&m.shop&&m.shop.pan)||'',d:o.d,amt:o.total,who:u.role=='c'?(m&&m.shop?m.shop.name:''):((U(o.cid)||{}).name||'')})});
  if(u.role=='m')(DB.bills||[]).forEach(b=>{if(b.mid!=u.id)return;R.push({k:'b'+b.id,ref:b,no:'B'+b.id,pan:(u.shop&&u.shop.pan)||'',d:b.d,amt:(b.items||[]).reduce((s,i)=>s+i.q*i.p,0),who:b.cname||''})});
  return R.sort((a,b)=>String(b.d).localeCompare(String(a.d)))}
function copy(t){(navigator.clipboard&&navigator.clipboard.writeText?navigator.clipboard.writeText(t):Promise.reject()).then(()=>toast(ne()?'कपी भयो':'Copied'),()=>{prompt('Copy:',t)})}
function open(){const n=ne(),u=S.u,w=document.createElement('div');w.style.cssText='position:fixed;inset:0;z-index:100000;background:var(--bg,#fff);color:var(--tx,#111);overflow:auto;padding:14px;font-family:inherit';document.body.appendChild(w);
  const draw=()=>{const R=rows(),nopan=u.role=='m'&&!(u.shop&&u.shop.pan);
  w.innerHTML='<div style="max-width:720px;margin:0 auto"><button id="tx" style="float:right;padding:8px 14px;border:1px solid var(--ln);border-radius:8px;background:none;color:inherit">'+(n?'बन्द':'Close')+'</button><h2 style="margin:0 0 6px">🎁 '+(n?'करदाता प्रोत्साहन उपहार (IRD)':'Taxpayer Incentive Prize (IRD)')+'</h2>'+
  '<div style="background:var(--card);border:1px solid var(--ln);border-radius:12px;padding:12px;font-size:14px;line-height:1.6;margin-bottom:10px">'+(n?
   '<b>यो एपले IRD को पोर्टलमा आफैं अपलोड गर्न सक्दैन।</b> यहाँ तपाईंको बिलको विवरण (बिल नं., विक्रेताको PAN, मिति, रकम) तयार छ। <b>कपी</b> गरेर आधिकारिक पोर्टलमा टाँस्नुहोस्।<ul style="margin:6px 0 0 18px"><li>नगद खरिदमा बिल आफैं दर्ता गर्नुपर्छ। डिजिटल भुक्तानी (eSewa/Khalti/QR) अक्सर आफैं सहभागी हुन्छ।</li><li>रु १०० वा सोभन्दा बढीको PAN/VAT बिल चाहिन्छ। सक्कल बिल सुरक्षित राख्नुहोस्।</li><li>नियम र म्याद पोर्टलमै हेर्नुहोस्; यो एपको बिल योजनाका लागि मान्य हुन्छ कि हुँदैन भन्ने कुरा विक्रेताको PAN/VAT दर्ता र IRD को नियममा भर पर्छ।</li></ul>':
   '<b>This app cannot upload to the IRD portal for you.</b> Your bill details (invoice no., seller PAN, date, amount) are prepared here. <b>Copy</b> them and paste into the official portal.<ul style="margin:6px 0 0 18px"><li>Cash purchases must be registered manually. Digital payments (eSewa/Khalti/QR) usually enter automatically.</li><li>A PAN/VAT bill of Rs 100 or more is needed. Keep the original bill.</li><li>Check rules and deadlines on the portal; whether an app-generated bill qualifies depends on the seller\'s PAN/VAT registration and IRD rules.</li></ul>')+'</div>'+
  (nopan?'<div style="background:#fff3cd;color:#664d03;border-radius:10px;padding:10px;margin-bottom:10px;font-size:14px">⚠️ '+(n?'तपाईंको पसलको PAN राखिएको छैन। PAN बिनाको बिल प्रयोग गर्न नमिल्न सक्छ। पसलको प्रोफाइलमा PAN राख्नुहोस्।':'Your shop PAN is not set. Bills without a PAN may not be usable. Add your PAN in the shop profile.')+'</div>':'')+
  '<button id="tp" style="'+BT+';width:100%;margin-bottom:12px">🌐 '+(n?'आधिकारिक पोर्टल खोल्नुहोस् (prize.ird.gov.np)':'Open official portal (prize.ird.gov.np)')+'</button>'+
  (R.length?R.map((r,i)=>'<div style="border:1px solid var(--ln);border-radius:12px;padding:10px;margin-bottom:8px;background:var(--card)"><div style="display:flex;justify-content:space-between;gap:8px"><b>'+esc(r.no)+'</b><span>Rs '+esc(r.amt)+'</span></div><div style="font-size:13px;opacity:.85;margin:4px 0">'+esc(r.who)+' · '+esc(r.d)+'<br>'+(n?'विक्रेताको PAN: ':'Seller PAN: ')+(esc(r.pan)||'—')+'</div><div style="display:flex;gap:6px;flex-wrap:wrap"><button data-c="'+i+'" style="'+BT+'">'+(n?'विवरण कपी':'Copy details')+'</button><button data-u="'+i+'" style="'+BT+';background:'+(r.ref.irdUp?'#2e7d32':'#777')+'">'+(r.ref.irdUp?'✓ '+(n?'अपलोड गरियो':'Uploaded')+' '+esc(r.ref.irdUp):(n?'अपलोड गरेँ भनी जनाउनुहोस्':'Mark as uploaded'))+'</button></div></div>').join(''):'<div style="opacity:.7;padding:10px">'+(n?'स्वीकृत अर्डर वा बिल अहिले छैन।':'No accepted orders or bills yet.')+'</div>')+'</div>';
  w.querySelector('#tx').onclick=()=>w.remove();
  w.querySelector('#tp').onclick=()=>window.open(PORTAL,'_blank','noopener');
  w.querySelectorAll('[data-c]').forEach(b=>b.onclick=()=>{const r=R[b.dataset.c];copy('Invoice no: '+r.no+'\nSeller PAN: '+r.pan+'\nDate: '+r.d+'\nAmount: '+r.amt)});
  w.querySelectorAll('[data-u]').forEach(b=>b.onclick=()=>{const r=R[b.dataset.u];r.ref.irdUp=r.ref.irdUp?'':today();save();draw()})};draw()}
window.obTax=open;
function decorate(){['obtax','obtaxb'].forEach(i=>{const e=document.getElementById(i);if(e)e.remove()});
  if(!window.S||!S.u||S.u.role=='a')return;const m=document.querySelector('.main');if(!m)return;const n=ne();
  const mk=(id,css)=>{const b=document.createElement('button');b.id=id;b.style.cssText=css;b.textContent='🎁 '+(n?'कर पुरस्कार (IRD) — बिल दर्ता':'Tax prize (IRD) — register bills');b.onclick=open;return b};
  if(S.tab=='me'&&!S.sub)m.appendChild(mk('obtax','width:100%;margin-top:12px;'+BT+';background:var(--saf,#E8820C)'));
  if(S.sub=='bill'&&S.bi)m.appendChild(mk('obtaxb','width:100%;margin-top:8px;'+BT+';background:var(--saf,#E8820C)'))}
const _R=window.R;if(typeof _R!=='function')return;
window.R=function(){const r=_R.apply(this,arguments);try{decorate()}catch(e){console.warn('tax',e)}return r};
setTimeout(()=>{try{decorate()}catch(_){}},600);
})();
