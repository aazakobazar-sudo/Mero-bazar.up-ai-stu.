/* Open Bazar customer home: hero + trust row + category panel (desktop sidebar / mobile chips); info links live in Profile */
(function(){
const css=document.createElement('style');css.textContent=`
#catside,#catchips,#hero,#trust,#plinks{font-family:inherit}
#hero{background:linear-gradient(135deg,var(--ind),var(--ind2));color:#fff;border-radius:16px;padding:20px 18px;margin-bottom:12px;position:relative;overflow:hidden}
#hero:after{content:"";position:absolute;right:-30px;top:-30px;width:140px;height:140px;border-radius:50%;background:var(--saf);opacity:.25}
#hero h1{font-size:25px;line-height:1.2;font-weight:800;letter-spacing:-.3px;margin-bottom:6px}
#hero h1 span{color:#FFC46B;display:block}
#hero p{font-size:14px;opacity:.9;margin-bottom:12px;max-width:34ch}
#hero button{background:var(--saf);color:#fff;border:0;border-radius:10px;padding:10px 16px;font-size:15px;font-weight:700}
#trust{display:grid;grid-template-columns:repeat(2,1fr);gap:8px;margin-bottom:12px}
#trust div{background:var(--card);border:1px solid var(--ln);border-radius:12px;padding:10px;font-size:13px;display:flex;gap:8px;align-items:center}
#trust b{display:block;font-size:13.5px}#trust span.i{font-size:22px}
#catchips{display:flex;gap:8px;overflow-x:auto;padding:2px 0 10px;position:sticky;top:-14px;background:var(--bg);z-index:3;scrollbar-width:none}
.ci{white-space:nowrap;border:1px solid var(--ln);background:var(--card);border-radius:20px;padding:8px 13px;font-size:14px;font-weight:600;cursor:pointer}
.ci.on{background:var(--ind);color:#fff;border-color:var(--ind)}
#catside{display:none}
#plinks{margin-top:18px}#plinks h3{font-size:17px;margin:0 0 8px}
#plinks details{background:var(--card);border:1px solid var(--ln);border-radius:12px;padding:11px 13px;margin-bottom:8px}
#plinks summary{font-weight:700;font-size:15px;cursor:pointer}
#plinks p{margin-top:8px;font-size:14px;line-height:1.5;color:var(--mu)}
@media(min-width:900px){
 body{max-width:1180px}
 #trust{grid-template-columns:repeat(4,1fr)}
 #hero{padding:34px 30px}#hero h1{font-size:38px}#hero p{font-size:17px}
 #catchips{display:none}
 #catside{display:block;position:fixed;top:var(--hdh,76px);left:max(8px,calc(50% - 590px));width:232px;max-height:calc(100% - var(--hdh,76px) - 78px);overflow:auto;background:var(--card);border:1px solid var(--ln);border-radius:14px;box-shadow:var(--sh);padding:8px;z-index:5}
 #catside h4{background:var(--ind);color:#fff;border-radius:10px;padding:10px 12px;font-size:15px;margin-bottom:6px}
 #catside .ci{display:block;border:0;border-radius:10px;margin:2px 0;padding:10px 12px;font-size:15px}
 #catside .ci:hover{background:var(--bg)}#catside .ci.on{background:var(--ind);color:#fff}
 body.hasside .main{padding-left:262px}
}`;document.head.appendChild(css);
const X={
 hero:['Open Bazar मा','अब किनमेल अझै सजिलो','आफ्नै जिल्लाका भरपर्दा पसलबाट सामान किनौँ, सिधै पसलसँग कुरा गरौँ।','किनमेल सुरु गर्नुहोस्','Shop on Open Bazar','Local shopping made easy','Buy from trusted shops in your own district and talk to the seller directly.','Start shopping'],
 trust:[['🏪','स्थानीय पसल','Local shops','प्रमाणित व्यापारी','Verified sellers'],['🔒','सुरक्षित भुक्तानी','Safe payment','eSewa / COD','eSewa / COD'],['📍','नजिकैको पसल','Nearby shops','नक्सामा स्थान','Location on map'],['💬','सिधै सम्पर्क','Direct contact','कल र सन्देश','Call & message']],
 all:['सबै श्रेणी','All categories'],cat:['श्रेणी छान्नुहोस्','Shop by category']
};
const ne=()=>(window.DB&&DB.lang)=='ne';
function decorate(){
  ['catside','catchips','hero','trust','plinks'].forEach(i=>{const e=document.getElementById(i);if(e)e.remove()});document.body.classList.remove('hasside');
  const m=document.querySelector('.main');if(!m||!window.S||!S.u||S.u.role!='c'||S.sub)return;const n=ne();
  if(S.tab=='home'){
    const hd=document.querySelector('.hd');if(hd)document.documentElement.style.setProperty('--hdh',Math.round(hd.getBoundingClientRect().bottom+8)+'px');
    const items=[[0,'🛍️',X.all[n?0:1]]].concat(G.map((g,i)=>[i+1,g.e,g.n])),
      btn=a=>`<div class="ci ${S.g==a[0]?'on':''}" onclick="S.g=${a[0]};S.fc=8;R()">${a[1]} ${a[2]}</div>`;
    const side=document.createElement('div');side.id='catside';side.innerHTML='<h4>☰ '+X.cat[n?0:1]+'</h4>'+items.map(btn).join('');document.body.appendChild(side);document.body.classList.add('hasside');
    const ch=document.createElement('div');ch.id='catchips';ch.innerHTML=items.map(btn).join('');
    const f=document.createDocumentFragment();
    if(!S.q.trim()&&!S.g){
      const h=document.createElement('div');h.id='hero';const k=n?0:4;h.innerHTML=`<h1>${X.hero[k]}<span>${X.hero[k+1]}</span></h1><p>${X.hero[k+2]}</p><button onclick="var e=document.getElementById('hf')||document.getElementById('hb');e&&e.scrollIntoView({behavior:'smooth'})">${X.hero[k+3]}</button>`;f.appendChild(h);
      const tr=document.createElement('div');tr.id='trust';tr.innerHTML=X.trust.map(a=>`<div><span class="i">${a[0]}</span><span><b>${n?a[1]:a[2]}</b>${n?a[3]:a[4]}</span></div>`).join('');f.appendChild(tr)}
    f.appendChild(ch);m.insertBefore(f,m.firstChild)
  }else if(S.tab=='me'){
    const L=[['About Open Bazar','Open Bazar बारे','Open Bazar is a local marketplace for Nepal that connects customers directly with nearby shops.','Open Bazar नेपालको स्थानीय बजार हो, जहाँ ग्राहकले नजिकका पसलसँग सिधै जोडिन पाउँछन्।'],
      ['FAQ','सोधिने प्रश्न','Browse shops, add items to your cart and place an order. The seller confirms it, packs it and you receive it. Payment follows the method the shop accepts.','पसल हेर्नुहोस्, सामान कार्टमा राख्नुहोस् र अर्डर गर्नुहोस्। पसलले स्वीकार गरी प्याक गर्छ र तपाईंले सामान पाउनुहुन्छ। भुक्तानी पसलले स्वीकार्ने तरिकाअनुसार हुन्छ।'],
      ['Order tracking','अर्डर ट्र्याकिङ','Open the Selection/Orders tab to see the status of every order.','सबै अर्डरको स्थिति हेर्न अर्डर ट्याब खोल्नुहोस्।'],
      ['Contact us','सम्पर्क','Email: aazakobazar@gmail.com','इमेल: aazakobazar@gmail.com'],
      ['Privacy policy','गोपनीयता नीति','We store only the details needed to run your account and orders. Your private details are visible only to you and the administrator.','हामी खाता र अर्डर चलाउन चाहिने विवरण मात्र राख्छौँ। तपाईंको निजी विवरण तपाईं र प्रशासकले मात्र हेर्न सक्छन्।'],
      ['Terms of use','प्रयोगका सर्तहरू','Use the app honestly. Sellers are responsible for their listings; fake or abusive accounts may be suspended.','एपलाई इमानदारीपूर्वक प्रयोग गर्नुहोस्। पसलले राखेको सामानको जिम्मेवारी पसलको हुन्छ; नक्कली वा दुरुपयोग गर्ने खाता निलम्बन हुन सक्छ।']];
    const d=document.createElement('div');d.id='plinks';d.innerHTML='<h3>'+(n?'सहयोग र जानकारी':'Help & information')+'</h3><button class="btn s" style="width:100%;margin-bottom:12px;font-size:14px;display:flex;align-items:center;justify-content:center;gap:6px" onclick="if(window.openOpenBazarHelp)openOpenBazarHelp(\'guide\')"><span>❓</span> <b>'+(n?'सिस्टम प्रयोग गाइड तथा Open Bazar AI':'System User Guide & Open Bazar AI')+'</b></button>'+L.map(a=>`<details><summary>${n?a[1]:a[0]}</summary><p>${n?a[3]:a[2]}</p></details>`).join('')+'<p style="text-align:center;margin-top:10px">© Open Bazar</p>';m.appendChild(d)}
}
const _R=window.R;if(typeof _R!=='function')return;
window.R=function(){const r=_R.apply(this,arguments);try{decorate()}catch(e){console.warn('home',e)}return r};
addEventListener('resize',()=>{try{decorate()}catch(_){}});
setTimeout(()=>{try{decorate()}catch(_){}},300);
})();
