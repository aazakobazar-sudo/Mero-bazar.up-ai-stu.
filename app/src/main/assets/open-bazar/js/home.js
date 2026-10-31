/* Open Bazar customer home: hero + trust row + APK download banner + D3 Roadmap timeline section + category panel */
(function(){
const css=document.createElement('style');css.textContent=`
#catside,#catchips,#hero,#trust,#plinks,#home_roadmap_section,#home_apk_banner{font-family:inherit}
#hero{background:linear-gradient(135deg,var(--ind),var(--ind2));color:#fff;border-radius:16px;padding:20px 18px;margin-bottom:12px;position:relative;overflow:hidden}
#hero:after{content:"";position:absolute;right:-30px;top:-30px;width:140px;height:140px;border-radius:50%;background:var(--saf);opacity:.25}
#hero h1{font-size:25px;line-height:1.2;font-weight:800;letter-spacing:-.3px;margin-bottom:6px}
#hero h1 span{color:#FFC46B;display:block}
#hero p{font-size:14px;opacity:.9;margin-bottom:12px;max-width:34ch}
#hero button{background:var(--saf);color:#fff;border:0;border-radius:10px;padding:10px 16px;font-size:15px;font-weight:700;cursor:pointer}
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

/* Timeline UI styling for Home Section */
.tl-status-pill{display:inline-flex;align-items:center;gap:6px;padding:4px 10px;border-radius:20px;font-size:11.5px;font-weight:700}
.tl-status-completed{background:rgba(16,185,129,0.18);color:#10B981;border:1px solid rgba(16,185,129,0.35)}
.tl-status-current{background:rgba(242,140,15,0.18);color:#F28C0F;border:1px solid rgba(242,140,15,0.4);animation:pulseCur 2s infinite}
.tl-status-future{background:rgba(139,92,246,0.18);color:#A855F7;border:1px solid rgba(139,92,246,0.35)}
@keyframes pulseCur{0%,100%{opacity:1}50%{opacity:0.65}}
.tl-metrics-row{display:grid;grid-template-columns:repeat(auto-fit,minmax(105px,1fr));gap:6px}
.tl-metric-box{background:rgba(255,255,255,0.04);border:1px solid var(--ln,#1E2D4A);border-radius:10px;padding:8px}
.tl-metric-label{font-size:10.5px;color:var(--mu,#94A3B8);font-weight:600;margin-bottom:2px}
.tl-metric-val{font-size:13px;font-weight:800;color:var(--tx,#FFF)}
.tl-deliv-item{display:flex;align-items:flex-start;gap:6px;font-size:12px;line-height:1.4}
.tl-deliv-item .icon{color:#10B981;font-weight:bold;flex-shrink:0}
.tl-tech-tag{display:inline-block;padding:2px 8px;border-radius:6px;font-size:11px;font-weight:600;background:rgba(255,255,255,0.06);border:1px solid var(--ln,#1E2D4A);color:var(--tx,#E2E8F0)}

/* APK Banner */
#home_apk_banner{background:linear-gradient(135deg,rgba(15,76,154,0.18),rgba(242,140,15,0.14));border:1px solid rgba(242,140,15,0.35);border-radius:16px;padding:14px 16px;margin-bottom:14px;display:flex;flex-wrap:wrap;align-items:center;justify-content:space-between;gap:12px}
#home_apk_banner h3{margin:0 0 4px;font-size:15.5px;font-weight:800;color:var(--tx,#0F172A);display:flex;align-items:center;gap:6px}
#home_apk_banner p{margin:0;font-size:12.5px;color:var(--mu,#64748B);line-height:1.4}

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
  ['catside','catchips','hero','trust','plinks','home_roadmap_section','home_apk_banner'].forEach(i=>{const e=document.getElementById(i);if(e)e.remove()});
  document.body.classList.remove('hasside');
  const m=document.querySelector('.main');if(!m||!window.S||!S.u||S.u.role!='c'||S.sub)return;
  const n=ne();

  if(S.tab=='home'){
    const hd=document.querySelector('.hd');if(hd)document.documentElement.style.setProperty('--hdh',Math.round(hd.getBoundingClientRect().bottom+8)+'px');
    const items=[[0,'🛍️',X.all[n?0:1]]].concat(G.map((g,i)=>[i+1,g.e,g.n])),
      btn=a=>`<div class="ci ${S.g==a[0]?'on':''}" onclick="S.g=${a[0]};S.fc=8;R()">${a[1]} ${a[2]}</div>`;
    const side=document.createElement('div');side.id='catside';side.innerHTML='<h4>☰ '+X.cat[n?0:1]+'</h4>'+items.map(btn).join('');document.body.appendChild(side);document.body.classList.add('hasside');
    const ch=document.createElement('div');ch.id='catchips';ch.innerHTML=items.map(btn).join('');
    const f=document.createDocumentFragment();

    if(!S.q.trim()&&!S.g){
      // 1. Hero banner
      const h=document.createElement('div');h.id='hero';const k=n?0:4;
      h.innerHTML=`<h1>${X.hero[k]}<span>${X.hero[k+1]}</span></h1><p>${X.hero[k+2]}</p><button onclick="var e=document.getElementById('hf')||document.getElementById('hb');e&&e.scrollIntoView({behavior:'smooth'})">${X.hero[k+3]}</button>`;
      f.appendChild(h);

      // 2. Trust row
      const tr=document.createElement('div');tr.id='trust';
      tr.innerHTML=X.trust.map(a=>`<div><span class="i">${a[0]}</span><span><b>${n?a[1]:a[2]}</b>${n?a[3]:a[4]}</span></div>`).join('');
      f.appendChild(tr);

      // 3. Official APK Download & Installation Banner
      const apkBanner=document.createElement('div');apkBanner.id='home_apk_banner';
      apkBanner.innerHTML=`
        <div style="flex:1;min-width:240px">
          <h3><span>📱</span> <span>${n?'आजको बजार Android APK फाइल उपलब्ध छ':'Aajako Bazar Android APK Available'}</span></h3>
          <p>${n?'नेपालको आफ्नै स्थानीय बजार मोबाइलमा सिधै इन्स्टल गर्नुहोस्। द्रुत गति, अफलाइन सपोर्ट र रियल-टाइम कलिङ।':'Install the native Android APK directly on your device. Fast performance, offline support & real-time calls.'}</p>
        </div>
        <div style="display:flex;gap:8px;flex-wrap:wrap">
          <button class="btn s" onclick="window.downloadApkDirect()" style="padding:9px 15px;font-weight:700;display:flex;align-items:center;gap:6px">
            <span>📥</span> <span>${n?'APK डाउनलोड गर्नुहोस्':'Download APK'}</span>
          </button>
          <button class="btn g" onclick="window.openApkDownloadModal()" style="padding:9px 13px;font-weight:700;display:flex;align-items:center;gap:6px">
            <span>📖</span> <span>${n?'इन्स्टल गाइड':'Install Guide'}</span>
          </button>
        </div>
      `;
      f.appendChild(apkBanner);

      // 4. Strategic Development Roadmap Section (D3.js dynamic timeline)
      const roadmapSec=document.createElement('div');roadmapSec.id='home_roadmap_section';
      roadmapSec.style.cssText=`
        background:var(--card,#121E34);border:1px solid var(--ln,#1E2D4A);border-radius:18px;
        padding:16px;margin-bottom:14px;box-shadow:var(--sh,0 6px 18px rgba(0,0,0,0.25));
        position:relative;overflow:hidden;
      `;
      roadmapSec.innerHTML=`
        <div style="display:flex;justify-content:space-between;align-items:flex-start;flex-wrap:wrap;gap:10px;margin-bottom:12px;border-bottom:1px solid var(--ln,#1E2D4A);padding-bottom:10px">
          <div>
            <div style="display:inline-flex;align-items:center;gap:6px;background:rgba(242,140,15,0.15);border:1px solid rgba(242,140,15,0.3);padding:3px 10px;border-radius:12px;font-size:11px;font-weight:700;color:var(--saf,#F28C0F);margin-bottom:4px">
              <span>🚀</span> <span>${n?'परियोजना विकास मार्गचित्र (Roadmap)':'Project Roadmap & History'}</span>
            </div>
            <h2 style="font-size:17.5px;font-weight:800;margin:0;color:var(--tx,#FFF);display:flex;align-items:center;gap:8px">
              <span>आजको बजार (Aajako Bazar)</span>
              <span style="font-size:11.5px;font-weight:600;padding:2px 8px;border-radius:8px;background:var(--ind,#0F4C9A);color:#fff">D3.js Timeline</span>
            </h2>
            <p style="font-size:12.5px;color:var(--mu,#94A3B8);margin:3px 0 0">
              ${n?'२०२३ को शुरुआती अवधारणादेखि २०२७+ सम्मका ९ रणनीतिक इन्जिनियरिङ चरणहरू':'9 strategic engineering milestones from 2023 offline genesis to 2027+ mesh networks'}
            </p>
          </div>
          <div style="display:flex;align-items:center;gap:6px;flex-wrap:wrap">
            <button class="btn s" id="btnHomeTour" style="padding:6px 11px;font-size:12px;font-weight:700;border-radius:8px" onclick="window.toggleHomeTimelineTour()">
              ▶ ${n?'स्टोरी टुर (Auto-Tour)':'Auto-Tour'}
            </button>
            <button class="btn g" style="padding:6px 10px;font-size:12px;font-weight:700;border-radius:8px" onclick="if(window.openProjectTimelineModal)window.openProjectTimelineModal()">
              ⛶ ${n?'पूर्ण दृश्य':'Full View'}
            </button>
            <button class="btn g" style="padding:6px 8px;font-size:12px;font-weight:700;border-radius:8px" onclick="if(window._timelineZoom)window._timelineZoom.zoomIn()" title="Zoom In">➕</button>
            <button class="btn g" style="padding:6px 8px;font-size:12px;font-weight:700;border-radius:8px" onclick="if(window._timelineZoom)window._timelineZoom.zoomOut()" title="Zoom Out">➖</button>
          </div>
        </div>

        <!-- D3 Timeline Canvas -->
        <div id="home_timeline_canvas" style="width:100%;height:300px;background:#0B1220;border:1px solid #1E2D4A;border-radius:14px;overflow:hidden;position:relative;"></div>

        <!-- Interactive Milestone Inspection Card -->
        <div id="homeMilestoneDetailCard" class="tl-detail-card" style="margin-top:12px;background:#0B1220;border:1px solid #1E2D4A;border-radius:14px;padding:14px;position:relative">
          <div style="display:flex;justify-content:space-between;align-items:center;border-bottom:1px solid #1E2D4A;padding-bottom:8px;margin-bottom:10px">
            <div style="display:flex;align-items:center;gap:10px">
              <div id="detailIcon" class="tl-detail-icon" style="width:36px;height:36px;border-radius:10px;display:flex;align-items:center;justify-content:center;background:rgba(15,76,154,0.3);font-size:18px">🏛️</div>
              <div>
                <span id="detailPhase" class="tl-phase-badge" style="font-size:11px;font-weight:700;color:var(--saf,#F28C0F)">Phase 1</span>
                <h3 id="detailTitle" class="tl-detail-title" style="font-size:15px;font-weight:800;margin:0;color:#FFF">Genesis</h3>
              </div>
            </div>
            <div id="detailStatusPill" class="tl-status-pill tl-status-completed" style="padding:4px 10px;border-radius:14px;font-size:11px;font-weight:700">✓ Completed</div>
          </div>
          <div id="detailMetrics" class="tl-metrics-row" style="display:grid;grid-template-columns:repeat(auto-fit,minmax(105px,1fr));gap:6px;margin-bottom:10px"></div>
          <ul id="detailDeliverables" class="tl-deliv-list" style="list-style:none;padding:0;margin:0 0 10px;display:flex;flex-direction:column;gap:5px;font-size:12px;color:#E2E8F0"></ul>
          <div id="detailTech" class="tl-tech-row" style="display:flex;flex-wrap:wrap;gap:5px"></div>

          <!-- Stepper Controls -->
          <div style="display:flex;justify-content:space-between;align-items:center;margin-top:12px;border-top:1px solid #1E2D4A;padding-top:10px">
            <button class="btn g" style="padding:5px 10px;font-size:12px;font-weight:700" onclick="window.stepHomeMilestone(-1)">
              ◀ ${n?'अघिल्लो चरण':'Previous'}
            </button>
            <span id="txtStepIndicator" class="tl-step-indicator" style="font-size:11.5px;color:var(--mu,#94A3B8);font-weight:600">Milestone 1 / 9</span>
            <button class="btn g" style="padding:5px 10px;font-size:12px;font-weight:700" onclick="window.stepHomeMilestone(1)">
              ${n?'पछिल्लो चरण':'Next'} ▶
            </button>
          </div>
        </div>

        <!-- Chief Software Architect & Engineer Banner -->
        <div style="margin-top:12px;padding:10px 14px;background:linear-gradient(135deg,rgba(15,76,154,0.2),rgba(16,185,129,0.15));border:1px solid rgba(15,76,154,0.4);border-radius:12px;display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:8px">
          <div style="display:flex;align-items:center;gap:8px">
            <span style="font-size:20px">👨‍💻</span>
            <div>
              <b style="font-size:13px;color:#FFF">${n?'आर्किटेक्ट तथा मुख्य इन्जिनियर: सूर्य काफ्ले':'Chief Architect & Engineer: Surya Kafle'}</b>
              <div style="font-size:11.5px;color:var(--mu,#94A3B8)">suryakafle860@gmail.com · Full-Stack & Android Systems</div>
            </div>
          </div>
          <div style="display:flex;gap:6px">
            <button class="btn s" style="padding:6px 12px;font-size:12px;font-weight:700" onclick="if(window.openResumeModal)window.openResumeModal()">
              📄 ${n?'बायोडाटा (Resume)':'Full Resume'}
            </button>
            <button class="btn g" style="padding:6px 12px;font-size:12px;font-weight:700" onclick="if(window.openContactHiringModal)window.openContactHiringModal();else if(window.openResumeModal)window.openResumeModal()">
              💼 ${n?'हायर गर्नुहोस्':'Hire Engineer'}
            </button>
          </div>
        </div>
      `;
      f.appendChild(roadmapSec);
    }

    f.appendChild(ch);
    m.insertBefore(f,m.firstChild);

    // Auto-render D3 timeline canvas if roadmap present
    if(!S.q.trim()&&!S.g){
      setTimeout(()=>{
        if(typeof window.renderProjectTimeline==='function'){
          const c=document.getElementById('home_timeline_canvas');
          if(c) window.renderProjectTimeline(c,{width:Math.max(c.clientWidth||800,780),height:300,lang:n?'ne':'en'});
        }
      },100);
    }
  }else if(S.tab=='me'){
    const L=[['About Open Bazar','Open Bazar बारे','Open Bazar is a local marketplace for Nepal that connects customers directly with nearby shops.','Open Bazar नेपालको स्थानीय बजार हो, जहाँ ग्राहकले नजिकका पसलसँग सिधै जोडिन पाउँछन्।'],
      ['Android APK Download','APK डाउनलोड र इन्स्टल गाइड','Download the official Aajako Bazar APK directly to your phone and learn how to install unknown apps safely.','आजको बजारको आधिकारिक एन्ड्रोइड APK फाइल डाउनलोड गर्नुहोस् र फोनमा सजिलै इन्स्टल गर्ने गाइड हेर्नुहोस्।'],
      ['FAQ','सोधिने प्रश्न','Browse shops, add items to your cart and place an order. The seller confirms it, packs it and you receive it. Payment follows the method the shop accepts.','पसल हेर्नुहोस्, सामान कार्टमा राख्नुहोस् र अर्डर गर्नुहोस्। पसलले स्वीकार गरी प्याक गर्छ र तपाईंले सामान पाउनुहुन्छ। भुक्तानी पसलले स्वीकार्ने तरिकाअनुसार हुन्छ।'],
      ['Order tracking','अर्डर ट्र्याकिङ','Open the Selection/Orders tab to see the status of every order.','सबै अर्डरको स्थिति हेर्न अर्डर ट्याब खोल्नुहोस्।'],
      ['Contact us','सम्पर्क','Email: aazakobazar@gmail.com','इमेल: aazakobazar@gmail.com'],
      ['Privacy policy','गोपनीयता नीति','We store only the details needed to run your account and orders. Your private details are visible only to you and the administrator.','हामी खाता र अर्डर चलाउन चाहिने विवरण मात्र राख्छौँ। तपाईंको निजी विवरण तपाईं र प्रशासकले मात्र हेर्न सक्छन्।'],
      ['Terms of use','प्रयोगका सर्तहरू','Use the app honestly. Sellers are responsible for their listings; fake or abusive accounts may be suspended.','एपलाई इमानदारीपूर्वक प्रयोग गर्नुहोस्। पसलले राखेको सामानको जिम्मेवारी पसलको हुन्छ; नक्कली वा दुरुपयोग गर्ने खाता निलम्बन हुन सक्छ।']];
    const d=document.createElement('div');d.id='plinks';
    d.innerHTML='<h3>'+(n?'सहयोग र जानकारी':'Help & information')+'</h3>'+
      '<div style="display:flex;flex-direction:column;gap:8px;margin-bottom:12px">'+
        '<button class="btn s" style="width:100%;font-size:14px;display:flex;align-items:center;justify-content:center;gap:6px" onclick="window.openApkDownloadModal()"><span>📥</span> <b>'+(n?'Android APK डाउनलोड र इन्स्टल गर्ने तरिका':'Download Android APK & Install Guide')+'</b></button>'+
        '<button class="btn g" style="width:100%;font-size:14px;display:flex;align-items:center;justify-content:center;gap:6px" onclick="if(window.openOpenBazarHelp)openOpenBazarHelp(\'guide\')"><span>❓</span> <b>'+(n?'सिस्टम प्रयोग गाइड तथा Open Bazar AI':'System User Guide & Open Bazar AI')+'</b></button>'+
      '</div>'+
      L.map(a=>`<details><summary>${n?a[1]:a[0]}</summary><p>${n?a[3]:a[2]}</p></details>`).join('')+
      '<p style="text-align:center;margin-top:10px">© Open Bazar</p>';
    m.appendChild(d);
  }
}

// Global controls for Roadmap Timeline
window.toggleHomeTimelineTour=function(){
  if(window._homeTourInterval){
    clearInterval(window._homeTourInterval);
    window._homeTourInterval=null;
    const b=document.getElementById('btnHomeTour');
    if(b) b.innerHTML=`▶ ${ne()?'स्टोरी टुर (Auto-Tour)':'Auto-Tour'}`;
  }else{
    let cur=0;
    if(window._selectTimelineMilestone) window._selectTimelineMilestone(cur,ne());
    const b=document.getElementById('btnHomeTour');
    if(b) b.innerHTML=`⏸ ${ne()?'रोक्नुहोस् (Pause)':'Pause Tour'}`;
    window._homeTourInterval=setInterval(()=>{
      cur=(cur+1)%9;
      if(window._selectTimelineMilestone) window._selectTimelineMilestone(cur,ne());
    },3800);
  }
};

window.stepHomeMilestone=function(dir){
  const total=9;
  let idx=(window.activeMilestoneIndex!=null?window.activeMilestoneIndex:0)+dir;
  if(idx<0) idx=total-1;
  if(idx>=total) idx=0;
  if(window._selectTimelineMilestone) window._selectTimelineMilestone(idx,ne());
};

// Global APK Direct Download & Interactive Guide
window.downloadApkDirect=function(){
  const isNep=ne();
  // 1. If in native Android WebView bridge, use native bridge
  if(window.AndroidBridge && typeof window.AndroidBridge.downloadApk==='function'){
    window.AndroidBridge.downloadApk('aajako-bazar.apk');
    return;
  }
  // 2. Direct web download link from assets
  const a=document.createElement('a');
  a.href='mero-bazar.apk';
  a.download='aajako-bazar.apk';
  document.body.appendChild(a);
  a.click();
  setTimeout(()=>a.remove(),2000);
  if(typeof window.toast==='function'){
    window.toast(isNep?'📥 APK फाइल डाउनलोड सुरु भयो...':'📥 APK download started...');
  }
};

window.openApkDownloadModal=function(){
  const isNep=ne();
  let m=document.getElementById('obApkModal');
  if(m) m.remove();
  m=document.createElement('div');
  m.id='obApkModal';
  m.style.cssText=`
    position:fixed;top:0;left:0;right:0;bottom:0;
    background:rgba(11,18,32,0.92);z-index:999999;
    display:flex;align-items:center;justify-content:center;
    padding:14px;backdrop-filter:blur(8px);overflow-y:auto;
  `;
  m.innerHTML=`
    <div style="width:100%;max-width:680px;max-height:92vh;overflow-y:auto;background:var(--card,#121E34);color:var(--tx,#F1F5F9);border-radius:20px;padding:20px;border:1px solid var(--ln,#1E2D4A);box-shadow:0 18px 48px rgba(0,0,0,0.6);position:relative;">
      <div style="display:flex;justify-content:space-between;align-items:center;border-bottom:1px solid var(--ln,#1E2D4A);padding-bottom:12px;margin-bottom:16px">
        <div>
          <h2 style="font-size:18px;font-weight:800;color:var(--tx,#FFF);display:flex;align-items:center;gap:8px;margin:0">
            <span>📥</span> <span>${isNep?'APK डाउनलोड र इन्स्टलेसन गाइड':'APK Download & Install Guide'}</span>
          </h2>
          <div style="font-size:12px;color:var(--mu,#94A3B8);margin-top:3px">
            ${isNep?'आजको बजार (Aajako Bazar) Android Application (.apk)':'Official Android Package for phones and tablets'}
          </div>
        </div>
        <button id="btnCloseApkModal" class="btn g" style="padding:6px 12px;border-radius:50%;min-height:36px;min-width:36px;font-weight:bold">✕</button>
      </div>

      <!-- 1-Tap Action Card -->
      <div style="background:linear-gradient(135deg,rgba(15,76,154,0.35),rgba(242,140,15,0.2));border:1px solid var(--saf,#F28C0F);border-radius:14px;padding:16px;margin-bottom:16px;text-align:center">
        <div style="font-size:32px;margin-bottom:6px">📦</div>
        <h3 style="font-size:17px;font-weight:800;color:#FFF;margin:0 0 6px">aajako-bazar.apk (v1.0)</h3>
        <p style="font-size:12.5px;color:var(--mu,#CBD5E1);margin:0 0 14px">
          ${isNep?'सबै एन्ड्रोइड मोबाइल (Android 8.0+) का लागि पूर्ण रूपमा तयार':'Production build compiled with full WebRTC, GPS, and Offline Sync'}
        </p>
        <button class="btn s" onclick="window.downloadApkDirect()" style="padding:12px 24px;font-size:15px;font-weight:800;display:inline-flex;align-items:center;gap:8px;border-radius:10px">
          <span>⬇️</span> <span>${isNep?'अहिले डाउनलोड गर्नुहोस् (Download APK)':'Download APK Now (56 MB)'}</span>
        </button>
      </div>

      <!-- Step-by-Step Installation Guide -->
      <h3 style="font-size:15px;font-weight:800;color:var(--saf,#F28C0F);margin:0 0 10px;display:flex;align-items:center;gap:6px">
        <span>⚙️</span> <span>${isNep?'मोबाइलमा कसरी इन्स्टल गर्ने? (Step-by-Step)':'How to Install on Android Device?'}</span>
      </h3>

      <div style="display:flex;flex-direction:column;gap:10px;margin-bottom:18px">
        <div style="display:flex;gap:10px;background:rgba(255,255,255,0.04);border:1px solid var(--ln,#1E2D4A);border-radius:12px;padding:12px">
          <div style="width:28px;height:28px;border-radius:50%;background:var(--ind,#0F4C9A);color:#fff;display:flex;align-items:center;justify-content:center;font-weight:800;font-size:13px;flex-shrink:0">१</div>
          <div>
            <b style="font-size:13.5px;color:#FFF">${isNep?'APK फाइल डाउनलोड गर्नुहोस्':'Download the APK File'}</b>
            <div style="font-size:12px;color:var(--mu,#94A3B8);margin-top:2px">${isNep?'माथिको "Download APK" बटन थिच्नुहोस्। फाइल तपाईँको मोबाइलको "Downloads" फोल्डरमा सुरक्षित हुन्छ।':'Click the download button above to save aajako-bazar.apk into your device Downloads folder.'}</div>
          </div>
        </div>

        <div style="display:flex;gap:10px;background:rgba(255,255,255,0.04);border:1px solid var(--ln,#1E2D4A);border-radius:12px;padding:12px">
          <div style="width:28px;height:28px;border-radius:50%;background:var(--ind,#0F4C9A);color:#fff;display:flex;align-items:center;justify-content:center;font-weight:800;font-size:13px;flex-shrink:0">२</div>
          <div>
            <b style="font-size:13.5px;color:#FFF">${isNep?'अज्ञात स्रोत अनुमति (Install Unknown Apps)':'Allow Install Unknown Apps'}</b>
            <div style="font-size:12px;color:var(--mu,#94A3B8);margin-top:2px">${isNep?'यदि पहिलो पटक इन्स्टल गर्दै हुनुहुन्छ भने <b>Settings &gt; Security &gt; Install Unknown Apps</b> मा जानुहोस् र Chrome/Files लाई <b>Allow from this source (सक्षम)</b> गर्नुहोस्।':'If prompted, go to Settings &gt; Security &gt; Install Unknown Apps and toggle Allow for your browser or file manager.'}</div>
          </div>
        </div>

        <div style="display:flex;gap:10px;background:rgba(255,255,255,0.04);border:1px solid var(--ln,#1E2D4A);border-radius:12px;padding:12px">
          <div style="width:28px;height:28px;border-radius:50%;background:var(--ind,#0F4C9A);color:#fff;display:flex;align-items:center;justify-content:center;font-weight:800;font-size:13px;flex-shrink:0">३</div>
          <div>
            <b style="font-size:13.5px;color:#FFF">${isNep?'इन्स्टल थिच्नुहोस् (Tap Install)':'Tap Install'}</b>
            <div style="font-size:12px;color:var(--mu,#94A3B8);margin-top:2px">${isNep?'डाउनलोड भएको <b>aajako-bazar.apk</b> फाइल खोल्नुहोस् र <b>Install</b> बटन थिच्नुहोस्। केही सेकेन्डमै इन्स्टल सम्पन्न हुनेछ।':'Open aajako-bazar.apk from notifications or File Manager and tap Install.'}</div>
          </div>
        </div>

        <div style="display:flex;gap:10px;background:rgba(255,255,255,0.04);border:1px solid var(--ln,#1E2D4A);border-radius:12px;padding:12px">
          <div style="width:28px;height:28px;border-radius:50%;background:var(--ind,#0F4C9A);color:#fff;display:flex;align-items:center;justify-content:center;font-weight:800;font-size:13px;flex-shrink:0">४</div>
          <div>
            <b style="font-size:13.5px;color:#FFF">${isNep?'एप सुरु गर्नुहोस् (Open App)':'Open App & Enjoy'}</b>
            <div style="font-size:12px;color:var(--mu,#94A3B8);margin-top:2px">${isNep?'<b>Open</b> थिच्नुहोस् र नेपालको आफ्नै डिजिटल बजारमा किनमेल, पसल व्यवस्थापन र भिडियो कलिङ सुरु गर्नुहोस्!':'Tap Open and begin using the full ecosystem with video calling, maps, and local shops!'}</div>
          </div>
        </div>
      </div>

      <!-- AI Studio Platform Settings Guide -->
      <div style="background:rgba(15,76,154,0.18);border:1px solid rgba(15,76,154,0.35);border-radius:12px;padding:12px">
        <h4 style="font-size:13.5px;color:#38BDF8;margin:0 0 4px">💡 ${isNep?'AI Studio इन्भाइरोनमेन्टबाट डाउनलोड गर्ने तरिका:':'Downloading directly via AI Studio Platform:'}</h4>
        <p style="font-size:12px;color:var(--mu,#CBD5E1);margin:0;line-height:1.4">
          ${isNep?'तपाईँ AI Studio को माथिल्लो दायाँ भागमा रहेको <b>Settings (⚙️)</b> मेनुबाट सिधै <b>Export APK / AAB</b> थिचेर पनि कम्पाइल भएको आधिकारिक APK डाउनलोड गर्न सक्नुहुन्छ।':'You can also click the gear/settings icon in AI Studio to export the production APK / AAB directly.'}
        </p>
      </div>
    </div>
  `;
  document.body.appendChild(m);
  document.getElementById('btnCloseApkModal').onclick=()=>m.remove();
};

const _R=window.R;if(typeof _R!=='function')return;
window.R=function(){const r=_R.apply(this,arguments);try{decorate()}catch(e){console.warn('home',e)}return r};
addEventListener('resize',()=>{try{decorate()}catch(_){}});
setTimeout(()=>{try{decorate()}catch(_){}},300);
})();
