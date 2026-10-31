/* Open Bazar: order flow helpers - auto bill on accept, customer "received", bill share */
(function(){
const ne=()=>(window.DB&&DB.lang)=='ne';
window.rcvO=function(id){const o=DB.orders.find(x=>x.id==id);if(o&&S.u&&o.cid==S.u.id){o.rcv=today();save();toast('✓');R()}};
const _so=window.setO;
if(typeof _so==='function')window.setO=function(id,s){const o=DB.orders.find(x=>x.id==id),b=o&&o.st,r=_so.apply(this,arguments);
  if(s==='Processing'&&o&&o.st==='Processing'&&b!=='Processing'&&S.u&&S.u.role==='m'){S.bi={o:id};S.sub='bill';R();setTimeout(()=>{try{billImg()}catch(_){}},700);try{toast(ne()?'बिल बन्यो र डाउनलोड हुँदैछ':'Bill created and downloading')}catch(_){}}
  return r};
let sh=false;
const ac=HTMLAnchorElement.prototype.click;
HTMLAnchorElement.prototype.click=function(){
  if(sh&&this.download&&/^blob:/.test(this.href)){
    sh=false;
    const a=this,h=a.href,n=a.download;
    if(window.obShare){
      window.obShare({
        dataOrBlobOrUrl: h,
        filename: n,
        mimeType: 'image/png',
        title: ne() ? 'ओपन बजार बिल' : 'Open Bazar Bill',
        text: (ne() ? 'ओपन बजार डिजिटल बिल: ' : 'Open Bazar digital bill: ') + n
      });
      return;
    }
  }
  if(window.obDownload && this.download && (this.href.startsWith('blob:') || this.href.startsWith('data:'))){
    window.obDownload(this.href, this.download);
    return;
  }
  return ac.apply(this,arguments);
};
function decorate(){const o=document.getElementById('obsh');if(o)o.remove();
  if(!window.S||!S.u||S.sub!=='bill'||!S.bi||!S.bi.o)return;const m=document.querySelector('.main');if(!m)return;
  const b=document.createElement('button');b.id='obsh';b.className='btn s';b.style.cssText='width:100%;margin-top:10px;display:flex;align-items:center;justify-content:center;gap:8px';b.innerHTML='<span>📤</span> '+(ne()?'बिल सेयर गर्नुहोस् (WhatsApp / अन्य)':'Share Bill via WhatsApp / Apps');
  b.onclick=()=>{sh=true;try{billImg()}catch(_){sh=false}};m.appendChild(b)}
const _R=window.R;if(typeof _R!=='function')return;
window.R=function(){const r=_R.apply(this,arguments);try{decorate()}catch(e){console.warn('orders',e)}return r};
})();
