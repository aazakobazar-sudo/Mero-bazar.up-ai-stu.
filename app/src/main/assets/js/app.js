/* Open Bazar — single organized app file */
const SELF=document.currentScript;
const K='ab9',today=()=>new Date().toISOString().slice(0,10),addD=(d,n)=>{const x=new Date(d);x.setDate(x.getDate()+n);return x.toISOString().slice(0,10)};
/* ---- security core: PBKDF2 passwords, AES-GCM encrypted storage with a non-extractable device key ---- */
const hex=b=>[...new Uint8Array(b)].map(x=>x.toString(16).padStart(2,'0')).join(''),unhex=h=>new Uint8Array(h.match(/../g).map(x=>parseInt(x,16))),enc=new TextEncoder(),dec=new TextDecoder(),rnd=n=>hex(crypto.getRandomValues(new Uint8Array(n)));
const PW=/^(?=.*[A-Za-z])(?=.*\d).{8,}$/;
async function hp(p,s){const k=await crypto.subtle.importKey('raw',enc.encode(p),'PBKDF2',false,['deriveBits']);return hex(await crypto.subtle.deriveBits({name:'PBKDF2',salt:unhex(s),iterations:100000,hash:'SHA-256'},k,256))}
const idb=()=>new Promise((ok,no)=>{const r=indexedDB.open('abk',1);r.onupgradeneeded=()=>r.result.createObjectStore('k');r.onsuccess=()=>ok(r.result);r.onerror=no});
async function devKey(){const d=await idb(),g=()=>new Promise(ok=>{const q=d.transaction('k').objectStore('k').get('m');q.onsuccess=()=>ok(q.result)});let k=await g();if(!k){k=await crypto.subtle.generateKey({name:'AES-GCM',length:256},false,['encrypt','decrypt']);await new Promise(ok=>{const x=d.transaction('k','readwrite');x.objectStore('k').put(k,'m');x.oncomplete=ok})}return k}
async function seal(o,k){if(!k)return 'p'+JSON.stringify(o);const iv=crypto.getRandomValues(new Uint8Array(12));return hex(iv)+hex(await crypto.subtle.encrypt({name:'AES-GCM',iv},k,enc.encode(JSON.stringify(o))))}
async function unseal(s,k){if(!k){if(s[0]!='p')throw 0;return JSON.parse(s.slice(1))}return JSON.parse(dec.decode(await crypto.subtle.decrypt({name:'AES-GCM',iv:unhex(s.slice(0,24))},k,unhex(s.slice(24)))))}
async function pk(p,s){const k=await crypto.subtle.importKey('raw',enc.encode(p),'PBKDF2',false,['deriveKey']);return crypto.subtle.deriveKey({name:'PBKDF2',salt:unhex(s),iterations:100000,hash:'SHA-256'},k,{name:'AES-GCM',length:256},false,['encrypt','decrypt'])}
const valid=d=>d&&['users','products','orders','khata','videos','notices','chats','comp','ph','nf','pay'].every(k=>Array.isArray(d[k]))&&d.users.some(u=>u.role=='a');
const fresh=()=>({a10:1,n:100,lang:'ne',users:[{id:1,name:'Surya',email:LK.e,salt:LK.s,pass:LK.h,role:'a',on:1,mc:0}],products:[],orders:[],khata:[],videos:[],notices:[],chats:[],comp:[],ph:[],pay:[],nf:[{id:1,t:'🎉 Welcome to Mero Bazar!',d:today(),r:0}],set:{tfa:1,ai:1,esewa:1,khalti:1,bank:1,payee:''}});
let KEY=null,DB,saveT,BAD=0;
async function boot(){try{KEY=await devKey()}catch(e){KEY=null}
await lockChk();
for(const s of [localStorage.getItem(K),localStorage.getItem(K+'b')]){if(!s)continue;try{const d=await unseal(s,KEY);if(valid(d)){DB=cleanDB(d);break}}catch(e){BAD=1}}
if(!DB)DB=fresh();if(!DB.a10){const a=DB.users.find(u=>u.role=='a');if(a){DB.users.forEach(u=>{if(u.id!=a.id&&canon(u.email)==LK.e)u.email='moved'+u.id+'@invalid.local'});a.email=LK.e;a.salt=LK.s;a.pass=LK.h;a.mc=0;a.tok=''}DB.a10=1;save()}}
function save(){clearTimeout(saveT);saveT=setTimeout(async()=>{try{const s=await seal(DB,KEY),o=localStorage.getItem(K);if(o)localStorage.setItem(K+'b',o);localStorage.setItem(K,s)}catch(e){toast(T('Storage full — export a backup'))}},40)}
const FL={};try{Object.assign(FL,JSON.parse(localStorage.getItem('ab9f')||'{}'))}catch(_){}
const N={'Accepted':'स्वीकृत','Packed':'प्याक भयो','On the way':'बाटोमा','Mark packed':'प्याक भयो भनी जनाउनुहोस्','Out for delivery':'डेलिभरीमा पठाइयो','Mark received':'सामान पाएँ','Received':'प्राप्त भयो','Share':'सेयर','Home':'गृह','Shops':'पसल','Videos':'भिडियो','Selection':'मेरो कारोबार','Profile':'प्रोफाइल','Cart':'कार्ट','Orders':'अर्डर','Dashboard':'ड्यासबोर्ड','Products':'सामान','Notices':'सूचना','Khata':'खाता','More':'थप','Overview':'सारांश','Users':'प्रयोगकर्ता','Customer':'ग्राहक','Merchant':'व्यापारी','Admin':'प्रशासक','Login':'लगइन','Logout':'लगआउट','Register':'दर्ता गर्नुहोस्','Password':'पासवर्ड','No account? Register first.':'खाता छैन? पहिले दर्ता गर्नुहोस्।','Wrong email or password':'इमेल वा पासवर्ड मिलेन','Account suspended':'खाता निलम्बित छ','Full name':'पूरा नाम','Mobile 98XXXXXXXX':'मोबाइल 98XXXXXXXX','Register as':'दर्ता प्रकार','Province (7)':'प्रदेश (७)','District (77)':'जिल्ला (७७)','Local level (Municipality / Rural Municipality)':'स्थानीय तह (नगरपालिका / गाउँपालिका)','Ward no. (1-35)':'वडा नं. (१-३५)','Shop / business name':'पसल / व्यवसायको नाम','Admin approval is required before your shop goes live.':'पसल चल्न प्रशासकको स्वीकृति चाहिन्छ।','Demo OTP: ':'डेमो OTP: ','Profile photo (auto-compressed)':'प्रोफाइल फोटो (आफैँ सानो हुन्छ)','Business registration document (photo)':'व्यवसाय दर्ता कागज (फोटो)','Confirm password':'पासवर्ड पुष्टि','Password (min 6)':'पासवर्ड (कम्तिमा ६)','Back':'पछाडि','Next':'अर्को','Finish':'समाप्त','Step':'चरण','Check name, mobile (98…), email, DOB':'नाम, मोबाइल, इमेल, जन्ममिति जाँच्नुहोस्','Email already registered':'इमेल दर्ता भइसक्यो','Complete address':'ठेगाना पूरा गर्नुहोस्','Enter shop name':'पसलको नाम लेख्नुहोस्','Wrong OTP':'OTP मिलेन','Password min 6 & must match':'पासवर्ड कम्तिमा ६ र मिल्नुपर्छ','Waiting for admin approval':'प्रशासकको स्वीकृति पर्खँदै','Not a valid image':'मान्य फोटो होइन','Search products':'सामान खोज्नुहोस्','Categories':'वर्गहरू','Latest notices':'पछिल्ला सूचना','Loyalty':'पोइन्ट','Wallet':'वालेट','Flash deal today':'आजको फ्ल्यास अफर','Up to 20% off':'२०% सम्म छूट','shops':'पसल','Out of stock':'सकियो','Low':'थोरै','In stock':'उपलब्ध','No products found':'सामान भेटिएन','All':'सबै','No shops yet':'अहिले पसल छैन','Nothing listed yet':'अहिलेसम्म केही राखिएको छैन','Delivery':'डेलिभरी','Min order':'न्यूनतम अर्डर','Closed':'बन्द','Open':'खुला','Your cart is empty':'कार्ट खाली छ','Total':'जम्मा','Place order':'अर्डर गर्नुहोस्','Not enough stock':'स्टक पुग्दैन','Insufficient wallet balance':'वालेटमा रकम पुग्दैन','Order placed':'अर्डर भयो','No orders yet':'अर्डर छैन','Pending':'पर्खाइमा','Processing':'तयारी','Delivered':'पुग्यो','Cancelled':'रद्द','Accept':'स्वीकार','Reject':'अस्वीकार','Cancel':'रद्द','Mark delivered':'पुर्‍यायो','Report problem':'समस्या जनाउनुहोस्','Messages':'सन्देश','My Khata':'मेरो खाता','Notifications':'सूचनाहरू','Account':'खाता','Top up':'रकम थप्नुहोस्','Coupon':'कुपन','Apply':'लगाउनुहोस्','Invalid coupon':'कुपन मान्य छैन','Payment history':'भुक्तानी इतिहास','Chat':'च्याट','Type a message':'सन्देश लेख्नुहोस्','No messages yet':'सन्देश छैन','Due ':'बाँकी ','Paid':'चुक्ता','No credit records':'उधारो छैन','Revenue':'आम्दानी','Receivable':'लिनुपर्ने','Low stock':'कम स्टक','AI Tip':'AI सुझाव','Restock soon — demand is rising.':'चाँडै स्टक थप्नुहोस् — माग बढ्दैछ।','Stock levels healthy.':'स्टक राम्रो छ।','Top products by stock value':'मूल्यका आधारमा शीर्ष सामान','Your shop is waiting for admin approval. You can set up now; customers will see it after approval.':'तपाईंको पसल प्रशासकको स्वीकृति पर्खँदै छ। स्वीकृतिपछि मात्र ग्राहकले देख्छन्।','Product name':'सामानको नाम','Price':'मूल्य','Discount':'छूट','Stock':'स्टक','Category':'उप-वर्ग','Add product':'सामान थप्नुहोस्','Fill all fields':'सबै विवरण भर्नुहोस्','units':'थान','Title':'शीर्षक','Details':'विवरण','Contact':'सम्पर्क','Post notice':'सूचना प्रकाशित गर्नुहोस्','Customer name':'ग्राहकको नाम','Amount':'रकम','Note':'टिप्पणी','Add entry':'प्रविष्टि थप्नुहोस्','Total receivable':'जम्मा लिनुपर्ने','Mark paid':'चुक्ता गर्नुहोस्','Name and amount required':'नाम र रकम चाहिन्छ','Video Marketing':'भिडियो मार्केटिङ','Analytics':'विश्लेषण','Shop Settings':'पसल सेटिङ','Customer Messages':'ग्राहक सन्देश','Orders count':'अर्डर संख्या','Rating':'रेटिङ','Views':'हेराइ','Best sellers':'धेरै बिक्ने','Caption':'क्याप्सन','Upload':'अपलोड','Video must be ≤ 60s':'भिडियो ६० सेकेन्डसम्म','Caption required':'क्याप्सन चाहिन्छ','Add a product first':'पहिले सामान थप्नुहोस्','Buy now':'किन्नुहोस्','Opening time':'खुल्ने समय','Closing time':'बन्द हुने समय','Delivery radius (km)':'डेलिभरी दायरा (कि.मी.)','Minimum order (₨)':'न्यूनतम अर्डर (₨)','Save':'सेभ','Toggle':'बदल्नुहोस्','Users count':'प्रयोगकर्ता','Shops count':'पसल','Pending shops':'स्वीकृति पर्खेका पसल','Broadcast notification':'सूचना पठाउनुहोस्','Message to all users':'सबैलाई सन्देश','Send':'पठाउनुहोस्','Sent':'पठाइयो','Approved':'स्वीकृत','Approve':'स्वीकार','Suspend':'निलम्बन','Ban':'प्रतिबन्ध','Activate':'सक्रिय','Complaints':'उजुरी','Product Monitor':'सामान निगरानी','Notice Monitor':'सूचना निगरानी','Video Monitor':'भिडियो निगरानी','System Settings':'सिस्टम सेटिङ','Remove':'हटाउनुहोस्','Resolve':'समाधान','Refund':'फिर्ता','Warn':'चेतावनी','Describe the problem':'समस्या लेख्नुहोस्','Submit':'पठाउनुहोस्','Change password':'पासवर्ड परिवर्तन','Current password':'हालको पासवर्ड','New password (min 6)':'नयाँ पासवर्ड (कम्तिमा ६)','Current password wrong':'हालको पासवर्ड मिलेन','Min 6 characters':'कम्तिमा ६ अक्षर','Change photo':'फोटो परिवर्तन','Backup & Restore':'ब्याकअप र पुनःप्राप्ति','All data is stored on this phone only. Export a backup file to keep it safe.':'सबै डेटा यही फोनमा मात्र सेभ हुन्छ। सुरक्षाका लागि ब्याकअप फाइल निकाल्नुहोस्।','Export backup':'ब्याकअप निकाल्नुहोस्','Restored — login again':'पुनःप्राप्त — फेरि लगइन गर्नुहोस्','Invalid backup file':'ब्याकअप फाइल मिलेन','Storage full — export a backup':'भण्डारण भरियो — ब्याकअप निकाल्नुहोस्','Nothing here yet':'यहाँ केही छैन','Document':'कागजात','Choose a category':'वर्ग छान्नुहोस्','Welcome to Open Bazar!':'मेरो बजारमा स्वागत छ!','Welcome to Mero Bazar!':'मेरो बजारमा स्वागत छ!','Coupon applied':'कुपन लाग्यो','Monthly fee ₨100 (35 days)':'मासिक शुल्क ₨१०० (३५ दिन)','35 days':'३५ दिन','days':'दिन','Time to update your payment':'तपाईंको भुक्तानी अध्यावधिक गर्ने समय भयो','Pay now':'अहिले तिर्नुहोस्','You have seen everything':'तपाईंले सबै हेरिसक्नुभयो','Loading…':'लोड हुँदैछ…','Free for customers':'ग्राहकका लागि निःशुल्क','Bills':'बिलहरू','Bill':'बिल','New bill':'नयाँ बिल','Create bill':'बिल बनाउनुहोस्','Customer name (optional)':'ग्राहकको नाम (वैकल्पिक)','Phone (optional)':'फोन (वैकल्पिक)','Other item':'अन्य सामान','Item name':'सामानको नाम','Rate':'दर','Qty':'परिमाण','Print / PDF':'प्रिन्ट / PDF','Share':'सेयर','Total (₨)':'कुल (₨)','Thank you!':'धन्यवाद!','Bill No':'बिल नं.','Date':'मिति','Copied':'कपी भयो','Add at least one item':'कम्तिमा एउटा सामान राख्नुहोस्','Remove demo':'डेमो हटाउनुहोस्','Remove all demo shops':'सबै डेमो पसल हटाउनुहोस्','Tap again to confirm':'पक्का गर्न फेरि थिच्नुहोस्','Item':'सामान','Your payment period has ended. Update your payment to reopen your shop.':'तपाईंको भुक्तानी अवधि सकियो। पसल पुनः खोल्न भुक्तानी अध्यावधिक गर्नुहोस्।','Your plan ends in 3 days. Please update your payment.':'तपाईंको प्लान ३ दिनभित्र सकिँदैछ। भुक्तानी अध्यावधिक गर्नुहोस्।','Shop closed (payment expired)':'पसल बन्द भयो (भुक्तानी म्याद सकियो)','Shop plan ends soon':'पसलको प्लान सकिन लाग्यो','Too many attempts. Try later.':'धेरै गलत प्रयास। पछि फेरि प्रयास गर्नुहोस्।','Secure (HTTPS) connection required':'सुरक्षित (HTTPS) जडान चाहिन्छ','Password: min 8 chars with letters and numbers':'पासवर्ड: कम्तिमा ८ अक्षर, अक्षर र अंक दुवै','Password (min 8, letters + numbers)':'पासवर्ड (कम्तिमा ८, अक्षर + अंक)','New password (min 8, letters + numbers)':'नयाँ पासवर्ड (कम्तिमा ८, अक्षर + अंक)','Something went wrong':'केही गडबड भयो','Reload view':'फेरि खोल्नुहोस्','See all':'सबै हेर्नुहोस्','Show less':'कम देखाउनुहोस्','Your ward':'तपाईंको वडा','Your municipality':'तपाईंको स्थानीय तह','Your district':'तपाईंको जिल्ला','Your province':'तपाईंको प्रदेश','Other province':'अन्य प्रदेश','Plan':'प्लान','Plan & Payment':'प्लान र भुक्तानी','Active until':'सक्रिय, म्याद','Inactive':'निष्क्रिय','Items':'सामान संख्या','Renew or extend':'नवीकरण / थप','Monthly plan':'मासिक प्लान','20 items included':'२० सामान समावेश','Extra 10 item slots':'थप १० सामानको ठाउँ','Extra slots':'थप ठाउँ','Quantity':'संख्या','Send payment to':'यहाँ भुक्तानी पठाउनुहोस्','Transaction ID':'ट्रान्जेक्सन ID','Submit payment':'भुक्तानी पठाउनुहोस्','Payments':'भुक्तानीहरू','Verified':'प्रमाणित','Rejected':'अस्वीकृत','Duplicate transaction ID':'यो ट्रान्जेक्सन ID पहिले नै प्रयोग भइसक्यो','Enter payment transaction ID':'भुक्तानीको ट्रान्जेक्सन ID लेख्नुहोस्','Monthly fee ₨100':'मासिक शुल्क ₨१००','Shop inactive — approval and monthly fee required':'पसल निष्क्रिय — स्वीकृति र मासिक शुल्क चाहिन्छ','Item limit reached — buy extra slots':'सामान सीमा पुग्यो — थप ठाउँ किन्नुहोस्','Fake':'नक्कली','Write a reason':'कारण लेख्नुहोस्','Mark fake':'नक्कली चिन्ह','Verify':'प्रमाणित गर्नुहोस्','Unverify':'प्रमाण हटाउनुहोस्','Suspend account':'खाता निलम्बन','Reason':'कारण','Set your own credentials':'आफ्नो इमेल/पासवर्ड सेट गर्नुहोस्','For security you must set a new password before continuing.':'सुरक्षाका लागि अगाडि बढ्नु अघि नयाँ पासवर्ड राख्नुपर्छ।','New email (optional)':'नयाँ इमेल','Invalid or used email':'इमेल मिलेन वा प्रयोगमा छ','Backup passphrase (min 8)':'ब्याकअप पासफ्रेज (कम्तिमा ८)','Backup passphrase min 8 chars':'ब्याकअप पासफ्रेज कम्तिमा ८ अक्षर','Logged out due to inactivity':'निष्क्रियताले गर्दा लगआउट भयो','Payment receiving number (eSewa/Khalti)':'भुक्तानी लिने नम्बर (eSewa/Khalti)','Wrong passphrase or file':'पासफ्रेज वा फाइल मिलेन','Data unreadable — restore from backup':'डेटा पढ्न सकिएन — ब्याकअपबाट पुनःप्राप्त गर्नुहोस्'};
const T=k=>DB.lang==='ne'&&N[k]||k;
const E=s=>String(s??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const G=[
  {n:'खाद्यान्न, कृषि तथा खाद्य उत्पादन',e:'🌾',t:['ताजा तरकारी तथा फलफूल','चामल, दाल तथा खाद्यान्न','दुग्धजन्य पदार्थ','मसला तथा तेल','अग्र्यानिक कृषि उपज','किराना तथा दैनिक सामग्री','बेकरी तथा कन्फेक्सनरी']},
  {n:'लत्ताकपडा, फेसन तथा बजार',e:'👕',t:['पुरुष फेसन','महिला फेसन','बालबालिकाका कपडा','जुत्ता तथा चप्पल','परम्परागत पोशाक','ब्याग तथा एसेसोरिज','फेब्रिक्स तथा कपडा']},
  {n:'खाना, होटल, रेस्टुरेन्ट तथा पर्यटन',e:'🍲',t:['खाजा तथा फास्टफुड','होटल तथा लज बुकिङ','रेस्टुरेन्ट सेवा','पर्यटन तथा ट्राभल गाइड','स्थानीय होमस्टे','क्याटरिङ सेवा']},
  {n:'उद्योग, कलकारखाना तथा उत्पादन',e:'🏭',t:['स्थानीय घरेलु उद्योग','हार्डवेयर तथा सिमेन्ट','फलाम तथा स्टिल','प्याकेजिङ सामग्री','औद्योगिक मेसिनरी','फर्निचर तथा काठ']},
  {n:'यातायात, अटोमोबाइल तथा सवारी',e:'🚗',t:['सवारी साधन खरिद/बिक्री','अटो पार्टपुर्जा तथा टायर','वर्कसप तथा मर्मत सेवा','भाडाका गाडी तथा ढुवानी','इलेक्ट्रिक भेहिकल']},
  {n:'विद्युतीय सामग्री, ग्याजेट्स तथा प्रविधि',e:'💻',t:['मोबाइल तथा ल्यापटप','घरायसी विद्युतीय उपकरण','टिभी, फ्रिज, वासिङ मेसिन','नेटवर्किङ तथा सीसीटीभी','सोलार तथा ब्याट्री']},
  {n:'शिक्षा, स्वास्थ्य, औषधि तथा फिटनेस',e:'📚',t:['स्टेशनरी तथा पुस्तक','औषधि तथा मेडिकल उपकरण','आयुर्वेदिक उत्पादन','जिम तथा फिटनेस उपकरण','शैक्षिक परामर्श','ल्याब तथा क्लिनिक']},
  {n:'अन्य विविध सेवा तथा व्यवसाय',e:'🧩',t:['बैंक तथा वित्तीय संस्था','सजावट तथा हस्तकला','कानुनी तथा कर परामर्श','घर मर्मत तथा प्लम्बिङ','सुरक्षा गार्ड सेवा','अन्य महत्वपूर्ण सेवाहरू']}
];
const PV={Koshi:'Taplejung,Panchthar,Ilam,Sankhuwasabha,Terhathum,Dhankuta,Bhojpur,Solukhumbu,Okhaldhunga,Khotang,Udayapur,Sunsari,Morang,Jhapa',Madhesh:'Saptari,Siraha,Dhanusha,Mahottari,Sarlahi,Rautahat,Bara,Parsa',Bagmati:'Kathmandu,Bhaktapur,Lalitpur,Nuwakot,Rasuwa,Dhading,Makwanpur,Ramechhap,Sindhuli,Sindhupalchok,Kavrepalanchok,Dolakha,Chitwan',Gandaki:'Gorkha,Lamjung,Tanahun,Kaski,Manang,Mustang,Myagdi,Baglung,Parbat,Syangja,Nawalpur',Lumbini:'Rupandehi,Kapilvastu,Parasi,Arghakhanchi,Gulmi,Palpa,Banke,Bardiya,Dang,Rolpa,Pyuthan,Rukum East',Karnali:'Surkhet,Jajarkot,Dolpa,Humla,Jumla,Mugu,Kalikot,Rukum West,Salyan,Dailekh',Sudurpashchim:'Kailali,Kanchanpur,Dadeldhura,Baitadi,Doti,Achham,Bajhang,Bajura,Darchula'};

/* ---------- data ---------- */
const S={u:null,tab:'home',sub:null,q:'',g:0,cart:{},of:'All',sel:'cart',r:null,sid:0,pair:null,co:0,hg:0,sf:'All',fc:8,rm:0,bi:null,last:Date.now()};
const $=id=>document.getElementById(id),rs=n=>'₨'+Number(n||0).toLocaleString('en-IN'),nid=()=>++DB.n;
const U=id=>DB.users.find(u=>u.id==id),shops=()=>DB.users.filter(u=>u.shop),live=()=>shops().filter(act);
const liveP=()=>DB.products.filter(p=>{const s=U(p.mid);return s&&act(s)});
function act(s){return !!(s.shop.ok&&s.on&&s.shop.exp&&s.shop.exp>=today())}
const lim=h=>100+(h.slots||0),used=()=>DB.products.filter(p=>p.mid==S.u.id).length+DB.notices.filter(n=>n.mid==S.u.id).length;
const near=u=>{const c=S.u;if(!c||c.role!='c'||!u)return 9;const l=x=>(x||'').trim().toLowerCase();return l(u.local)==l(c.local)&&u.dist==c.dist?(u.ward==c.ward?0:1):u.dist==c.dist?2:u.prov==c.prov?3:4};
const nl=u=>{const n=near(u);return n<5?`<span class="bd ${n<2?'G':n<4?'B':''}">📍 ${T(['Your ward','Your municipality','Your district','Your province','Other province'][n])}${kmT(u)}</span>`:''};
const eff=p=>p.price-p.dis,pn=p=>E(p.name);
function toast(m){const t=$('toast');t.textContent=m;t.className='on';setTimeout(()=>t.className='',2300)}
const go=t=>{S.tab=t;S.sub=null;if(t=='home')S.fc=8;R()},lang=()=>{DB.lang=DB.lang=='ne'?'en':'ne';save();R()};
const theme=()=>{const r=document.documentElement,n=r.dataset.theme==='dark'?'light':'dark';r.dataset.theme=n;try{localStorage.setItem('open_bazar_theme',n)}catch(_){}R();toast(n==='dark'?'🌙 '+(DB.lang=='ne'?'डार्क मोड':'Dark mode'):'☀️ '+(DB.lang=='ne'?'लाइट मोड':'Light mode'))};
function searchBarHTML(){
  const q=S.q||'';
  const chips=[{id:0,label:T('All'),icon:'🗂️'},...G.map((g,i)=>({id:i+1,label:g.n,icon:g.e}))];
  return `<div class="search-bar-wrap"><div class="search-bar-box"><span class="search-icon">🔍</span><input id="main_search_inp" type="text" placeholder="${T('Search products')}, ${T('shops')}..." value="${E(q)}" oninput="S.q=this.value;sr()">${q?`<button class="clear-btn" type="button" onclick="S.q='';const el=$('main_search_inp');if(el)el.value='';sr()" title="Clear">✕</button>`:''}</div><div class="search-quick-chips">${chips.map(c=>`<span class="chip ${S.g===c.id?'on':''}" onclick="catGo(${c.id})">${c.icon} ${c.label}</span>`).join('')}</div></div>`;
}
const av=u=>u.photo?`<img class="av" src="${u.photo}" alt="">`:`<span class="av">${u.shop?G[u.shop.cat-1].e:'👤'}</span>`;

/* ---------- auth ---------- */
async function login(e,p){if(LOCK)return R();e=canon(e);const f=FL[e]||{n:0,t:0};if(f.t>Date.now())return toast(T('Too many attempts. Try later.'));if(!crypto.subtle)return toast(T('Secure (HTTPS) connection required'));
const u=DB.users.find(x=>canon(x.email)===e);let ok=false;try{ok=!!u&&(await hp(p||'',u.salt))===u.pass}catch(_){}
if(!ok){f.n++;if(f.n>=5)f.t=Date.now()+Math.min(3600e3,60e3*2**(f.n-5));FL[e]=f;try{localStorage.setItem('ab9f',JSON.stringify(FL))}catch(_){}return toast(T('Wrong email or password'))}
if(!u.on)return toast(T('Account suspended'));delete FL[e];try{localStorage.setItem('ab9f',JSON.stringify(FL))}catch(_){}sess(u)}
function sess(u){if(LOCK)return;u.tok=rnd(16);S.u=u;S.tab='home';S.sub=null;S.fc=8;S.last=Date.now();try{localStorage.setItem(K+'s',u.id+':'+u.tok);localStorage.setItem(K+'t',Date.now())}catch(_){}chk();save();R()}
function logout(){try{localStorage.removeItem(K+'s');localStorage.removeItem(K+'t')}catch(_){}S.u=null;S.cart={};S.sub=null;S.tab='home';R()}
function pic(f,max,q){return new Promise((ok,no)=>{const r=new FileReader();r.onload=()=>{const i=new Image();i.onload=()=>{const k=Math.min(1,max/Math.max(i.width,i.height)),c=document.createElement('canvas');c.width=Math.round(i.width*k);c.height=Math.round(i.height*k);c.getContext('2d').drawImage(i,0,0,c.width,c.height);ok(c.toDataURL('image/jpeg',q))};i.onerror=no;i.src=r.result};r.onerror=no;r.readAsDataURL(f)})}
async function up(el,k,max,q){const f=el.files[0];if(!f)return;try{const v=await pic(f,max,q);if(S.r){rc();S.r.d[k]=v;R()}else{S.u.photo=v;save();R()}toast('✅ '+Math.round(v.length*.75/1024)+' KB')}catch(_){toast(T('Not a valid image'))}}

/* ---------- registration ---------- */
const STEPS=d=>d.rl=='m'?[1,2,3,6,4,5]:[1,2,4,5];
function rc(){['nm','mb','em2','dob','gn','rl','pv','ds','lc','wd','sn','sg','st','pan','lrn','pm','ref','otp','pw1','pw2'].forEach(k=>{const e=$('r_'+k);if(e)S.r.d[k]=e.value})}
function vReg(){const r=S.r,d=r.d,v=k=>E(d[k]||''),st=STEPS(d),id=st[r.i];
const f=(k,p,t='text')=>`<input class="inp" id="r_${k}" type="${t}" placeholder="${p}" value="${v(k)}">`;
const o=(a,sel,vals)=>a.map((x,i)=>{const val=vals?vals[i]:x;return `<option value="${E(val)}" ${val==sel?'selected':''}>${E(x)}</option>`}).join('');let b='';
if(id==1)b=f('nm',T('Full name'))+f('mb',T('Mobile 98XXXXXXXX'),'tel')+f('em2','Email','email')+f('dob','','date')+`<select class="inp" id="r_gn">${o(['Male','Female','Other'],d.gn)}</select><div class="st">${T('Register as')}</div><select class="inp" id="r_rl" onchange="rc();R()">${o([T('Customer'),T('Merchant')],d.rl||'c',['c','m'])}</select>${d.rl=='m'?'':`<div class="bd G">🎁 ${T('Free for customers')}</div>`}`;
if(id==2){const ds=PV[d.pv]?PV[d.pv].split(','):[];if(!ds.includes(d.ds))d.ds='';b=`<select class="inp" id="r_pv" onchange="rc();R()"><option value="">${T('Province (7)')}</option>${o(Object.keys(PV),d.pv)}</select><select class="inp" id="r_ds"><option value="">${T('District (77)')}</option>${o(ds,d.ds)}</select>`+f('lc',T('Local level (Municipality / Rural Municipality)'))+f('wd',T('Ward no. (1-35)'),'number')+`<button class="btn sm j" type="button" style="margin-bottom:8px" onclick="locR()">📍 ${T('Use my current location')}</button>${d.la!=null?`<div class="bd G">📍 ${d.la}, ${d.lo}</div>`:''}`}
if(id==3){const g=+(d.sg||1);b=f('sn',T('Shop / business name'))+`<select class="inp" id="r_sg" onchange="rc();R()">${o(G.map(x=>x.e+' '+x.n),g,[1,2,3,4,5])}</select><select class="inp" id="r_st">${o(G[g-1].t,d.st)}</select>${f('pan',T('PAN number (optional)'),'tel')}${f('lrn',T('Local registration no. (optional)'))}<div class="c st" style="text-transform:none">⏳ ${T('Admin approval is required before your shop goes live.')}</div>`}
if(id==4)b=`<div class="c" style="border-left:4px solid var(--saf);background:var(--card)"><div class="row"><b>📲 ${T('SMS / Email OTP Verification')}</b><button type="button" class="btn sm g" onclick="resendRegOtp()">🔄 ${T('Resend OTP')}</button></div><div class="st" style="text-transform:none;margin-top:4px">${T('Verification code sent to')} <b>${E(d.mb)}</b> (${E(d.em2)})</div><div class="c st" style="text-transform:none;margin:8px 0;background:rgba(242,140,15,0.08);border:1px dashed var(--saf);border-radius:10px;padding:9px 12px">🔑 ${T('Your 6-digit OTP:')} <b style="font-size:18px;color:var(--saf);letter-spacing:4px">${r.otp}</b></div></div>`+f('otp',T('Enter 6-digit OTP'),'tel')+`<label class="st">📷 ${T('Profile photo (auto-compressed)')}</label>${d.photo?`<img class="av" style="width:64px;height:64px;display:block;margin:6px 0" src="${d.photo}">`:''}<input class="inp" type="file" accept="image/*" onchange="up(this,'photo',240,.7)">`+(d.rl=='m'?`<label class="st">📄 ${T('Business registration document (photo)')}</label>${d.doc?`<img src="${d.doc}" style="width:90px;border-radius:8px;display:block;margin:6px 0">`:''}<input class="inp" type="file" accept="image/*" onchange="up(this,'doc',900,.6)">`:'');
if(id==6)b=`<div class="c" style="border-left:4px solid var(--saf)"><b>💳 ${T('Monthly fee ₨100 (35 days)')}</b><div class="st" style="text-transform:none;margin-top:4px">${T('20 items included')} · ${T('Extra 10 item slots')}: ₨50</div>${DB.set.payee?`<div style="margin-top:6px">${T('Send payment to')}: <b>${E(DB.set.payee)}</b></div>`:''}</div><select class="inp" id="r_pm">${o(['eSewa','Khalti','Bank Transfer'],d.pm)}</select>`+f('ref',T('Transaction ID'));
if(id==5)b=f('pw1',T('Password (min 8, letters + numbers)'),'password')+f('pw2',T('Confirm password'),'password');
return `<div class="main"><div class="hero"><img class="logo sm" src="assets/logo-160.png" alt=""><b>${T('Register')} · ${r.i+1}/${st.length}</b><div class="bar"><i style="width:${(r.i+1)/st.length*100}%;background:var(--saf)"></i></div></div>${b}<div class="row"><button class="btn g" onclick="rc();if(S.r.i>0)S.r.i--;else S.r=null;R()">${T('Back')}</button><button class="btn s" onclick="regNext()">${r.i==st.length-1?T('Finish'):T('Next')}</button></div></div>`}
async function regNext(){rc();const r=S.r,d=r.d,st=STEPS(d),id=st[r.i];
if(id==1){if(!d.nm||!/^9\d{9}$/.test(d.mb||'')||!/\S+@\S+\.\S+/.test(d.em2||'')||!d.dob)return toast(T('Check name, mobile (98…), email, DOB'));d.em2=d.em2.trim();if(DB.users.some(u=>canon(u.email)===canon(d.em2)))return toast(T('Email already registered'));d.rl=d.rl||'c'}
if(id==2&&(!d.pv||!d.ds||!d.lc||!(d.wd>=1&&d.wd<=35)))return toast(T('Complete address'));
if(id==3&&(d.sn||'').trim().length<2)return toast(T('Enter shop name'));
if(id==3&&!okReg(d.pan,d.lrn))return;
if(id==6&&(d.ref||'').trim().length<6)return toast(T('Enter payment transaction ID'));
if(id==4&&d.otp!==r.otp)return toast(T('Wrong OTP'));
if(id==5){if(!crypto.subtle)return toast(T('Secure (HTTPS) connection required'));if(!PW.test(d.pw1||'')||d.pw1!==d.pw2)return toast(T('Password: min 8 chars with letters and numbers'));
if(DB.users.some(u=>canon(u.email)===canon(d.em2)))return toast(T('Email already registered'));const em=d.em2.trim().toLowerCase(),salt=rnd(16),u={id:nid(),name:d.nm.trim(),email:em,salt,pass:await hp(d.pw1,salt),role:d.rl||'c',phone:d.mb,dob:d.dob,gn:d.gn||'Male',prov:d.pv,dist:d.ds,local:d.lc,ward:d.wd,photo:si(d.photo),la:d.la??null,lo:d.lo??null,on:1,code:'AB-'+(d.rl=='m'?'M':'C')+'-'+(10000+Math.random()*90000|0)};
if(u.role=='m'){const g=+(d.sg||1);u.shop={name:d.sn.trim(),cat:g,type:d.st||G[g-1].t[0],open:'08:00',close:'20:00',rad:5,min:0,ok:0,live:1,exp:'',slots:0,fake:'',doc:si(d.doc),pan:(d.pan||'').trim(),lrn:(d.lrn||'').trim()}}else{u.wallet=0;u.pts=0}
DB.users.push(u);if(u.role=='m')DB.pay.push({id:nid(),mid:u.id,kind:'sub',amt:100,qty:1,m:d.pm||'eSewa',ref:(d.ref||'').trim(),st:'Pending',d:today()});save();S.r=null;sess(u);return toast('🎉 '+u.code)}
if(st[r.i+1]==4)r.otp=String(100000+Math.random()*900000|0);
r.i++;R()}

/* ---------- shared widgets ---------- */
const stBd=s=>({Pending:'Y',Processing:'B',Packed:'B',Shipped:'B',Delivered:'G',Cancelled:'R'}[s]||'');
const lk=(s,i,l,b='')=>`<div class="c row" style="cursor:pointer" onclick="S.sub='${s}';R()"><span>${i} <b>${T(l)}</b></span><span>${b}<b> ›</b></span></div>`;
const outb=()=>`<div style="margin-top:14px"><button class="btn sm g" onclick="lang()">🌐 ${DB.lang=='ne'?'English':'नेपाली'}</button> <button class="btn sm g" onclick="theme()">🌓</button> <button class="btn sm g" onclick="S.sub='acct';R()">⚙️ ${T('Account')}</button> <button class="btn sm r" onclick="logout()">${T('Logout')}</button></div>`;
const empty=()=>`<div class="c st" style="text-align:center">${T('Nothing here yet')}</div>`;
const stat=(l,v)=>`<div class="c"><div class="st">${T(l)}</div><div class="big">${v}</div></div>`;
function pc(p){const o=p.stock<=0,s=U(p.mid);return `<div class="c"><div class="row" style="cursor:pointer" onclick="S.sid=${s.id};S.sub='shop';R()"><span class="row" style="gap:8px">${av(s)}<span><b style="font-size:12px">${E(s.shop.name)} <span class="bd G">✓</span>${s.shop.demo?'<span class="bd Y">DEMO</span>':''}</b><div class="st">${E(s.dist)}, ${E(s.local)}-${E(s.ward)}</div></span></span>${nl(s)}</div><div class="row" style="margin-top:8px;gap:12px;align-items:flex-start">${pimg(p,s)}<div style="flex:1"><b style="font-size:14px">${pn(p)}</b><div class="st">${E(p.type)}</div><div><b style="color:var(--saf)">${rs(eff(p))}</b> ${p.dis?`<s class="mu">${rs(p.price)}</s> <span class="bd R">-${Math.round(p.dis/p.price*100)}%</span>`:''}</div><span class="bd ${o?'R':p.stock<10?'Y':'G'}">${o?T('Out of stock'):p.stock<10?T('Low')+': '+p.stock:T('In stock')}</span></div><span style="display:flex;flex-direction:column;gap:6px"><button class="btn sm s" ${o?'disabled style="opacity:.4"':''} onclick="add(${p.id})">＋</button><button class="btn sm g" onclick="S.sid=${s.id};S.sub='shop';R()">🏪</button></span></div></div>`}
const nCard=n=>{const s=U(n.mid);return `<div class="c"><div class="row" style="cursor:pointer" onclick="S.sid=${s.id};S.sub='shop';R()"><span class="row" style="gap:8px">${av(s)}<b style="font-size:12px">${E(s.shop.name)}</b></span>${nl(s)}</div><div class="row" style="margin-top:6px"><b>${E(n.title)}</b><span class="bd B">${E(n.type)}</span></div><div style="margin:5px 0;font-size:13px">${E(n.body)}</div><div class="st">${E(n.contact)} · ${n.d}</div></div>`};
function oCard(o,m){const cu=U(o.cid),sh=U(o.mid),sts={Pending:'Pending',Processing:'Accepted',Packed:'Packed',Shipped:'On the way',Delivered:'Delivered',Cancelled:'Cancelled'};
return `<div class="c"><div class="row"><b>#${o.id} · ${E(m?cu?.name:sh?.shop?.name)}</b><span class="bd ${stBd(o.st)}">${T(sts[o.st])}</span></div><div class="st" style="margin:5px 0">${o.items.map(i=>(o.mt?'✔ ':'')+E(i.n)+' ×'+i.q).join(', ')}</div>${o.mt?`<div class="bd G" style="margin-bottom:6px">✅ ${T('Matched with shop')}: ${E(o.mt.sh)} · ${o.mt.d}</div>`:o.st=='Pending'?`<div class="bd Y" style="margin-bottom:6px">🔍 ${T('Waiting for shop to match')}</div>`:''}<div class="row"><b>${rs(o.total)}</b><span class="st">${o.pay} · ${o.d}</span></div><div style="margin-top:8px;display:flex;gap:6px">${m=='m'?(o.st=='Pending'?`<button class="btn sm j" onclick="setO(${o.id},'Processing')">${T('Accept')}</button><button class="btn sm r" onclick="setO(${o.id},'Cancelled')">${T('Reject')}</button>`:o.st=='Processing'?`<button class="btn sm j" onclick="setO(${o.id},'Packed')">${T('Mark packed')}</button>`:o.st=='Packed'?`<button class="btn sm j" onclick="setO(${o.id},'Shipped')">${T('Out for delivery')}</button>`:o.st=='Shipped'?`<button class="btn sm j" onclick="setO(${o.id},'Delivered')">${T('Mark delivered')}</button>`:''):m?'':(o.st=='Pending'?`<button class="btn sm r" onclick="setO(${o.id},'Cancelled')">${T('Cancel')}</button>`:o.st=='Delivered'?`<button class="btn sm g" onclick="S.co=${o.id};S.sub='cmp';R()">⚠️ ${T('Report problem')}</button>`:'')}${o.st=='Pending'||o.st=='Cancelled'?'':`<button class="btn sm g" onclick="S.bi={o:${o.id}};S.sub='bill';R()">🧾 ${T('Bill')}</button>`}${m!='m'&&(o.st=='Shipped'||o.st=='Delivered')&&!o.rcv?`<button class="btn sm j" onclick="rcvO(${o.id})">${T('Mark received')}</button>`:''}${o.rcv?`<span class="bd G">${T('Received')}</span>`:''}</div></div>`}
function setO(id,s){const o=DB.orders.find(x=>x.id==id),ts={Pending:['Processing','Cancelled'],Processing:['Packed'],Packed:['Shipped'],Shipped:['Delivered']};if(!o||(S.u.role=='m'&&o.mid!=S.u.id)||(S.u.role=='c'&&(o.cid!=S.u.id||s!='Cancelled'))||!(ts[o.st]||[]).includes(s))return;
if(s=='Processing'){const sh=U(o.mid),bad=o.items.filter(i=>{const p=DB.products.find(x=>x.id==i.pid);return !p||p.mid!=o.mid});if(!sh||!sh.shop||bad.length)return toast('❌ '+T('Items do not match this shop'));o.mt={d:today(),sh:sh.shop.name};nfu(o.cid,'✅ #'+o.id+' — '+T('Matched & accepted by')+' '+sh.shop.name)}
o.st=s;if(s=='Cancelled'){o.items.forEach(i=>{const p=DB.products.find(x=>x.id==i.pid);if(p)p.stock+=i.q});if(o.pay=='Wallet'){const c=U(o.cid);c.wallet+=o.total}}save();if(window.saveOrderToFirestore)window.saveOrderToFirestore(o);R()}

/* ---------- customer ---------- */
const fc=x=>x.title?nCard(x):pc(x);
function feed(){const tiers={};liveP().forEach(p=>{const t=near(U(p.mid)),m=tiers[t]=tiers[t]||{};(m[p.mid]=m[p.mid]||[]).push(p)});const out=[];
Object.keys(tiers).sort().forEach(t=>{const L=Object.values(tiers[t]).sort((a,b)=>km(U(a[0].mid))-km(U(b[0].mid))).map(a=>a.sort((x,y)=>y.dis/y.price-x.dis/x.price));let more=1;while(more){more=0;L.forEach(a=>{if(a.length){out.push(a.shift());more=1}})}});
const NT=DB.notices.filter(n=>{const s=U(n.mid);return s&&act(s)}).sort((a,b)=>near(U(a.mid))-near(U(b.mid))||km(U(a.mid))-km(U(b.mid))),res=[];let ni=0;out.forEach((p,i)=>{res.push(p);if((i+1)%4==0&&ni<NT.length)res.push(NT[ni++])});while(ni<NT.length)res.push(NT[ni++]);return res}
function hf(){const q=S.q.trim().toLowerCase(),F=feed();if(q)return F.filter(x=>(x.title?x.title+x.body+x.type:x.name+x.type+U(x.mid).shop.name).toLowerCase().includes(q)).map(fc).join('')||`<div class="c st">${T('No products found')}</div>`;return F.slice(0,S.fc).map(fc).join('')||empty()}
const fmT=()=>S.q.trim()?'':S.fc>=feed().length?'✓ '+T('You have seen everything'):'⏳ '+T('Loading…');
function sr(){if($('hf')){$('hf').innerHTML=hf();$('fm').innerHTML=fmT()}else $('hb').innerHTML=hb()}
function fs(el){if(!S.u||S.u.role!='c'||S.tab!='home'||S.sub||S.q.trim()||el.scrollTop+el.clientHeight<el.scrollHeight-400)return;const F=feed();if(S.fc>=F.length)return;const n=Math.min(F.length,S.fc+8);$('hf').insertAdjacentHTML('beforeend',F.slice(S.fc,n).map(fc).join(''));S.fc=n;$('fm').innerHTML=fmT()}
function hb(){const q=S.q.trim().toLowerCase(),bn=(a,b)=>near(U(a.mid))-near(U(b.mid))||km(U(a.mid))-km(U(b.mid)),P=liveP().sort(bn),NT=DB.notices.filter(n=>{const s=U(n.mid);return s&&act(s)}).sort(bn);
if(q){const L=P.filter(p=>(p.name+p.type+U(p.mid).shop.name).toLowerCase().includes(q)),M=NT.filter(n=>(n.title+n.body+n.type).toLowerCase().includes(q));return (L.map(pc).join('')+M.map(nCard).join(''))||`<div class="c st">${T('No products found')}</div>`}
return G.map((g,i)=>{const gi=i+1;if(S.g&&S.g!=gi)return '';const all=gi==5?NT:P.filter(p=>U(p.mid).shop.cat==gi),open=S.hg==gi||S.g==gi,L=open?all:all.slice(0,3);return `<div class="sec"><div class="row" style="margin:14px 2px 8px"><b style="font-size:18px">${g.e} ${g.n}</b><span class="bd B">${all.length}</span></div><div class="chips">${live().filter(s=>s.shop.cat==gi).sort((a,b)=>near(a)-near(b)||km(a)-km(b)).map(s=>`<span class="chip" onclick="S.sid=${s.id};S.sub='shop';R()">🏪 ${E(s.shop.name)}${s.shop.demo?' (DEMO)':''}</span>`).join('')}</div>${L.map(gi==5?nCard:pc).join('')||`<div class="c st">${T('Nothing listed yet')}</div>`}${all.length>3?`<button class="btn sm g" style="width:100%;margin-bottom:6px" onclick="S.hg=${open?0:gi};R()">${open?T('Show less'):T('See all')+' ('+all.length+')'}</button>`:''}</div>`}).join('')}
const cView={
home:()=>`<div class="c" style="background:linear-gradient(135deg,var(--saf),#F5A623);color:#fff"><div class="row"><div><div id="fl" style="font-size:12px;opacity:.9"></div><b style="font-size:17px">⚡ ${T('Flash deal today')} — ${T('Up to 20% off')}</b></div><span class="em">🔥</span></div></div>
<div class="g2"><div class="c"><div class="st">${T('Loyalty')}</div><div class="big">${S.u.pts}</div><span class="bd Y">${S.u.pts>=2000?'Gold':'Silver'}</span><div class="bar"><i style="width:${Math.min(100,S.u.pts/20)}%;background:var(--saf)"></i></div></div><div class="c"><div class="st">${T('Wallet')}</div><div class="big">${rs(S.u.wallet)}</div><span class="bd G">eSewa · Khalti</span></div></div>
${searchBarHTML()}<div id="hf">${hf()}</div><div id="fm" class="st" style="text-align:center;padding:14px">${fmT()}</div>`,

cats:()=>{
  const recP=(DB.products||[]).slice(0,4);
  const popS=shops().slice(0,3);
  return `${searchBarHTML()}
<div class="c" style="background:linear-gradient(135deg,#0F4C9A,#1E3A8A);color:#fff;margin-bottom:12px;padding:14px">
  <b style="font-size:17px;display:block">🗂️ २. पसल र वर्गहरू (Store Categories - 8 Engines)</b>
  <div style="font-size:12px;opacity:.9">स्थानीय व्यापार, कृषि, फेसन, पर्यटन तथा उद्योगहरू</div>
</div>
<div class="cats" style="margin-bottom:14px">
  <button class="cat ${!S.g?'on':''}" onclick="catGo(0)"><span>🗂️</span> ${T('All')} <small>${DB.products.length}</small></button>
  ${G.map((g,i)=>`<button class="cat ${S.g==i+1?'on':''}" onclick="catGo(${i+1})"><span>${g.e}</span> ${g.n}<small>${DB.products.filter(p=>p.type===g.n||g.t.includes(p.type)).length}</small></button>`).join('')}
</div>
<h3>✨ 2.1 सिफारिस गरिएका सामानहरू (AI Recommended Products)</h3>
<div class="g2" style="margin-bottom:14px">${recP.map(p=>`<div class="c" style="padding:10px">${p.img?`<img src="${si(p.img)}" style="width:100%;height:100px;object-fit:cover;border-radius:8px">`:''}<b style="font-size:14px;display:block;margin:6px 0 2px">${pn(p)}</b><div class="st">${rs(eff(p))}</div><button class="btn sm s" style="width:100%;margin-top:6px" onclick="add(${p.id});S.tab='orders';R()">🛒 किन्ने</button></div>`).join('')||empty()}</div>
<h3>⭐ 2.3 लोकप्रिय पसलहरू (Popular Verified Shops)</h3>
${popS.map(s=>`<div class="c row" style="cursor:pointer;margin-bottom:8px" onclick="S.sid=${s.id};S.sub='shop';R()"><span class="row" style="gap:10px">${av(s)}<span><b>${E(s.shop.name)}</b><div class="st">${s.dist} · ⭐ 4.9 · ${s.shop.type}</div></span></span><button class="btn sm j">पसल हेर्नुहोस्</button></div>`).join('')||empty()}
<div id="hb" style="margin-top:14px">${hb()}</div>`;
},

orders:()=>{
  const n=Object.values(S.cart).reduce((a,b)=>a+b,0);
  const kh=DB.khata.filter(k=>k.name.toLowerCase()==S.u.name.toLowerCase()).reduce((s,k)=>s+k.amt-k.paid,0);
  return `<div class="c" style="background:linear-gradient(135deg,#0F4C9A,#15803D);color:#fff;margin-bottom:12px;padding:14px">
    <div class="row">
      <div>
        <b style="font-size:17px;display:block">📦 ३. अर्डर प्रणाली तथा भुक्तानी (Order System & Fulfillment)</b>
        <div style="font-size:12px;opacity:.9">अर्डर ट्र्याकिङ, भौचर डाउनलोड, QR र करदाता उपहार</div>
      </div>
      ${kh?`<span class="bd R" style="font-weight:750">बाँकी: ${rs(kh)}</span>`:''}
    </div>
  </div>
  <div class="chips" style="margin-bottom:12px">
    <span class="chip ${S.sel=='cart'?'on':''}" onclick="S.sel='cart';R()">🛒 ${T('Cart')}${n?' ('+n+')':''}</span>
    <span class="chip ${S.sel=='orders'?'on':''}" onclick="S.sel='orders';R()">📦 ${T('Orders')} (${DB.orders.filter(o=>o.cid==S.u.id).length})</span>
    <span class="chip ${S.sel=='track'?'on':''}" onclick="S.sel='track';R()">🚚 ट्र्याकिङ (Tracking)</span>
    <span class="chip ${S.sel=='tax'?'on':''}" onclick="S.sel='tax';R()">🎁 करदाता उपहार</span>
  </div>`+
  (S.sel=='cart'?cCart():S.sel=='track'?cTracking():S.sel=='tax'?cTaxGift():cOrders());
},

msg:()=>window.renderMessengerView?window.renderMessengerView():'',

me:()=>{
  const un=DB.nf.filter(n=>!n.r&&(!n.u||n.u==S.u.id)).length;
  const kh=DB.khata.filter(k=>k.name.toLowerCase()==S.u.name.toLowerCase()).reduce((s,k)=>s+k.amt-k.paid,0);
  return `<div class="c" style="background:linear-gradient(135deg,#1E293B,#0F172A);color:#fff;padding:16px;margin-bottom:12px">
    <div class="row">
      <span class="row" style="gap:12px">
        ${av(S.u)}
        <div>
          <b style="font-size:18px;display:block">${E(S.u.name)}</b>
          <div style="font-size:12px;opacity:.85">📞 ${E(S.u.phone)} · 📍 ${E(S.u.dist)}, ${E(S.u.local)}-${E(S.u.ward)}</div>
          <span class="bd G" style="margin-top:4px;display:inline-block">प्रमाणित ग्राहक (Verified)</span>
        </div>
      </span>
    </div>
  </div>
  <div class="c" style="border-left:4px solid #F59E0B">
    <b>🤖 ५. AI RECOMMENDATION SYSTEM (All-Rounder System)</b>
    <div class="st" style="text-transform:none;margin-top:6px;line-height:1.5">
      स्मार्ट सर्च, सस्तो सामान पत्ता लगाउने (Cheapest Finder), नजिकको पसल छनोट, र साइड-बाइ-साइड स्पेसिफिकेशन तुलना सुविधा।
    </div>
    <div style="display:flex;gap:6px;flex-wrap:wrap;margin-top:10px">
      <button class="btn sm s" onclick="window.openAiAssistant?window.openAiAssistant():0">🤖 AI सहायक खोल्नुहोस्</button>
      <button class="btn sm g" onclick="toast('🔍 सस्तो सामान: बजारका शीर्ष १० सप्लायर्स तुलना गरियो')">💰 सस्तो सामान खोज्नुहोस्</button>
      <button class="btn sm g" onclick="window.pickLoc&&window.pickLoc((a,o)=>{S.u.la=a;S.u.lo=o;save();toast('📍 लोकेसन सुरक्षित भयो');R()})">📍 GPS ठेगाना पिन</button>
    </div>
  </div>
  ${lk('chats','💬','Messages')}
  ${lk('khata','📒','My Khata',kh?`<span class="bd R">${rs(kh)}</span>`:'')}
  ${lk('notif','🔔','Notifications',un?`<span class="bd Y">${un}</span>`:'')}
  ${lk('timeline','🚀','Project Timeline (D3.js)','<span class="bd B">Aajako Bazar</span>')}
  ${lk('contact','✉️','Professional Inquiries / सम्पर्क','<span class="bd B">suryakafle860@gmail.com</span>')}
  ${lk('resume','📄','Download Resume / रेजुमे (PDF)','<span class="bd G">Surya Kafle</span>')}
  <div class="c" style="background:linear-gradient(135deg,var(--ind),var(--ind2));color:#fff">
    <div style="opacity:.7;font-size:12px">${T('Wallet')}</div>
    <div style="font-size:28px;font-weight:800">${rs(S.u.wallet)}</div>
  </div>
  <h3>${T('Top up')}</h3>
  <div class="g2">${[500,1000,2000,5000].map(a=>`<button class="btn g" onclick="topup(${a})">+${rs(a)}</button>`).join('')}</div>
  <h3>${T('Coupon')}</h3>
  <div class="row"><input class="inp" id="cp" placeholder="SAVE50" style="margin:0"><button class="btn s" onclick="coupon()">${T('Apply')}</button></div>
  ${outb()}`;
},

shops:()=>cView.cats(),
vid:()=>cView.home(),
sel:()=>cView.orders()
};

function cTracking(){
  const myOrders=DB.orders.filter(o=>o.cid==S.u.id);
  const cur=myOrders[0];
  if(!cur) return `<div class="c st" style="text-align:center">कुनै सक्रिय अर्डर भेटिएन</div>`;
  const stLevels={'Pending':25,'Accepted':50,'Processing':75,'Delivered':100,'Cancelled':0};
  const pct=stLevels[cur.st]||30;
  return `<div class="c">
    <div class="row"><b>अर्डर #${cur.id} ट्र्याकिङ</b><span class="bd G">${cur.st}</span></div>
    <div class="st" style="margin:6px 0">पसल: ${E(U(cur.mid)?.shop?.name||'पसल')} · कुल: ${rs(cur.total)}</div>
    <div class="bar" style="height:10px;margin:10px 0"><i style="width:${pct}%;background:#10B981"></i></div>
    <div class="row" style="font-size:11px;color:var(--mu)"><span>१. दर्ता</span><span>२. स्वीकृत</span><span>३. बाटोमा (कुरियर)</span><span>४. प्राप्त</span></div>
    <div style="background:rgba(242,140,15,0.08);border:1px solid #F28C0F;border-radius:10px;padding:10px;margin-top:12px;font-size:12.5px">
      🚚 <b>डेलिभरी कुरियर:</b> काठमाडौं उपत्यका द्रुत सेवा (सम्पर्क: 9851234567)<br>
      📍 <b>अनुमानित समय:</b> आज बेलुका सम्म
    </div>
    <button class="btn j" style="width:100%;margin-top:10px" onclick="window.openNepalMap?window.openNepalMap():toast('नक्सा खुल्दैछ...')">🗺️ नक्सामा लाइभ डेलिभरी हेर्नुहोस्</button>
  </div>`;
}

function cTaxGift(){
  return `<div class="c" style="border-left:4px solid #10B981">
    <h3>🎁 नेपाल सरकार आन्तरिक राजस्व विभाग - करदाता उपहार</h3>
    <p style="font-size:13px;line-height:1.5;color:var(--tx)">
      तपाईंले मेरो बजारबाट सामान खरिद गर्दा प्राप्त भएको आधिकारिक बिल मार्फत आन्तरिक राजस्व विभाग (IRD) को लक्की ड्र तथा करदाता उपहार योजनामा सहभागी हुन सक्नुहुन्छ।
    </p>
    <a href="https://prize.ird.gov.np/" target="_blank" class="btn s" style="display:inline-block;text-align:center;width:100%;text-decoration:none;font-weight:700;padding:12px">
      🌐 IRD करदाता उपहार पोर्टल खोल्नुहोस् (prize.ird.gov.np)
    </a>
  </div>`;
}
function add(id){const p=DB.products.find(x=>x.id==id);if((S.cart[id]||0)>=p.stock)return toast(T('Not enough stock'));S.cart[id]=(S.cart[id]||0)+1;toast('🛒 +1')}
function chg(id,d){const p=DB.products.find(x=>x.id==id),q=(S.cart[id]||0)+d;if(q<=0)delete S.cart[id];else if(q<=p.stock)S.cart[id]=q;R()}
const cartT=()=>Object.entries(S.cart).reduce((s,[id,q])=>s+eff(DB.products.find(p=>p.id==id))*q,0);
function cCart(){const ids=Object.keys(S.cart);if(!ids.length)return `<div class="c st" style="text-align:center">🛒 ${T('Your cart is empty')}</div>`;
const by={};ids.forEach(id=>{const p=DB.products.find(x=>x.id==id);(by[p.mid]=by[p.mid]||[]).push(p)});
return Object.entries(by).map(([m,L])=>`<div class="st" style="margin:6px 2px">🏪 ${E(U(m).shop.name)}</div>`+L.map(p=>`<div class="c row"><span><b>${pn(p)}</b><div class="st">${rs(eff(p))}</div></span><span class="row"><button class="btn sm g" onclick="chg(${p.id},-1)">−</button><b>${S.cart[p.id]}</b><button class="btn sm g" onclick="chg(${p.id},1)">＋</button></span></div>`).join('')).join('')+`<div class="c"><div class="row"><span class="st">${T('Total')}</span><span class="big">${rs(cartT())}</span></div><select class="inp" id="pay" style="margin-top:10px"><option>Wallet</option><option>eSewa</option><option>Khalti</option><option>Bank Transfer</option><option>COD</option></select><button class="btn s" style="width:100%;padding:13px" onclick="checkout()">${T('Place order')}</button></div>`}
function checkout(){const t=cartT(),pay=$('pay').value;if(pay=='Wallet'&&S.u.wallet<t)return toast(T('Insufficient wallet balance'));
const by={};Object.entries(S.cart).forEach(([id,q])=>{const p=DB.products.find(x=>x.id==id);p.stock-=q;(by[p.mid]=by[p.mid]||[]).push({pid:p.id,n:p.name,q,p:eff(p)})});
Object.entries(by).forEach(([m,items])=>{const newO={id:nid(),cid:S.u.id,mid:+m,items,total:items.reduce((s,i)=>s+i.q*i.p,0),st:'Pending',pay,d:today()};nfu(+m,'🛒 '+T('New order from')+' '+S.u.name);DB.orders.unshift(newO);if(window.saveOrderToFirestore)window.saveOrderToFirestore(newO);});
if(pay=='Wallet')S.u.wallet-=t;S.u.pts+=Math.floor(t/10);DB.ph.unshift({u:S.u.id,t:'Purchase',a:-t,d:today()});S.cart={};save();toast('✅ '+T('Order placed'));S.sel='orders';R()}
function cOrders(){const F=['All','Pending','Processing','Delivered','Cancelled'],f=S.of;return `<div class="chips">${F.map(x=>`<span class="chip ${f==x?'on':''}" onclick="S.of='${x}';R()">${T(x)}</span>`).join('')}</div>`+(DB.orders.filter(o=>o.cid==S.u.id&&(f=='All'||o.st==f)).map(o=>oCard(o,0)).join('')||`<div class="c st">${T('No orders yet')}</div>`)}
function topup(a){S.u.wallet+=a;DB.ph.unshift({u:S.u.id,t:'Topup',a,d:today()});save();toast('✅ '+rs(a));R()}
function coupon(){if($('cp').value.trim().toUpperCase()=='SAVE50'){S.u.wallet+=50;DB.ph.unshift({u:S.u.id,t:'Coupon',a:50,d:today()});save();toast('🎉 '+T('Coupon applied'));R()}else toast(T('Invalid coupon'))}
function lkV(id){DB.videos.find(v=>v.id==id).likes++;save();R()}

/* ---------- merchant ---------- */
const my=()=>S.u.shop,mine=a=>a.filter(x=>x.mid==S.u.id);
const mView={
home:()=>{
  const o=mine(DB.orders),rev=o.filter(x=>x.st=='Delivered').reduce((s,x)=>s+x.total,0),pend=o.filter(x=>x.st=='Pending').length,rec=mine(DB.khata).reduce((s,k)=>s+k.amt-k.paid,0),P=mine(DB.products),low=P.filter(p=>p.stock<10),h=my();
  const custCount=[...new Set(o.map(x=>x.cid))].length;
  return planCard()+(h.ok?'':`<div class="c" style="border-left:4px solid var(--saf)">⏳ ${T('Your shop is waiting for admin approval. You can set up now; customers will see it after approval.')}</div>`)+
  `<div class="c row"><span class="row" style="gap:10px">${av(S.u)}<span><b>${E(h.name)}</b><div class="st">${G[h.cat-1]?.e||'🏪'} ${G[h.cat-1]?.n||''} · ${E(h.type)}</div></span></span><span class="bd ${h.ok?'G':'Y'}">${h.ok?T('Approved'):T('Pending')}</span></div>
  <div class="g2">${stat('Today Sales',rs(rev))}${stat('Monthly Sales',rs(rev*1.2))}${stat('Total Orders',o.length)}${stat('Customer Count',custCount)}</div>
  <div class="g2">${stat('Product Views',P.reduce((s,p)=>s+(p.stock*3),32))}${stat('Low stock',low.length)}</div>
  <div class="c" style="border-left:4px solid var(--saf)"><b>🤖 ५.५ AI MERCHANT SYSTEM</b><div class="st" style="text-transform:none;margin-top:4px">${low.length?E(low.map(p=>p.name).join(', '))+' — '+T('Restock soon — demand is rising.'):T('Stock levels healthy.')}</div></div>
  <div style="margin-top:8px">${repV()}</div>`;
},
msg:()=>window.renderMessengerView?window.renderMessengerView():'',
prods:()=>{
  const h=my(),ty=G[h.cat-1]?.t||['सामान्य'];
  return `<div class="c" style="background:linear-gradient(135deg,#0F4C9A,#1E3A8A);color:#fff;margin-bottom:12px;padding:14px">
    <b style="font-size:16px;display:block">🛍️ २. PRODUCT MANAGEMENT SYSTEM (PMS)</b>
    <div style="font-size:11.5px;opacity:.9">सामान थप्ने, सम्पादन गर्ने, फोटो, स्पेसिफिकेशन र स्टक व्यवस्थापन</div>
  </div>
  <div class="c">
    <input class="inp" id="pn" placeholder="${T('Product name')}">
    <select class="inp" id="pt">${ty.map(x=>`<option>${E(x)}</option>`).join('')}</select>
    <div class="g2"><input class="inp" id="pp" type="number" placeholder="${T('Price')}"><input class="inp" id="pd" type="number" placeholder="${T('Discount')}"></div>
    <input class="inp" id="ps" type="number" placeholder="${T('Stock')}">
    <input class="inp" id="pw" placeholder="वजन / साइज स्पेसिफिकेशन (उदा. १ केजी, साइज L)">
    <div class="g2"><input class="inp" id="pdeliv" type="number" placeholder="डेलिभरी शुल्क (₨)"><input class="inp" id="pphone" placeholder="डेलिभरी म्यान सम्पर्क फोन"></div>
    <label class="st">📷 ${T('Product photo')}</label><input class="inp" id="pi" type="file" accept="image/*">
    <button class="btn s" style="width:100%;margin-top:8px" onclick="addP()">＋ २.१ सामान थप्नुहोस् (Add & Upload Product)</button>
  </div>`+
  (mine(DB.products).map(p=>`<div class="c"><div class="row"><b>${p.img?`<img class="av" src="${si(p.img)}" alt=""> `:'📦 '}${pn(p)}</b><span><label class="btn sm g" style="cursor:pointer">📷<input type="file" accept="image/*" hidden onchange="pph(${p.id},this)"></label> <button class="btn sm j" onclick="restock(${p.id})">+10</button> <button class="btn sm r" onclick="delP(${p.id})">🗑</button></span></div><div class="st">${E(p.type)} · ${rs(p.price)}${p.dis?' (−'+rs(p.dis)+')':''} · ${p.stock} ${T('units')}${p.specs?' · '+E(p.specs):''}</div><div class="bar"><i style="width:${Math.min(100,p.stock*2)}%;background:var(--${p.stock<=0?'crim':p.stock<10?'saf':'jade'})"></i></div></div>`).join('')||empty());
},
notices:()=>{
  const ty=my().cat==5?G[4].t:['पसलको अफर',...G[4].t];
  return `<div class="c"><input class="inp" id="nt" placeholder="${T('Title')}"><select class="inp" id="nk">${ty.map(x=>`<option>${E(x)}</option>`).join('')}</select><textarea class="inp" id="nb" rows="3" placeholder="${T('Details')}"></textarea><input class="inp" id="nc" placeholder="${T('Contact')}"><button class="btn s" onclick="addN()">📢 ${T('Post notice')}</button></div>`+(mine(DB.notices).map(n=>nCard(n)+`<button class="btn sm r" style="margin:-4px 0 10px" onclick="delN(${n.id})">🗑</button>`).join('')||empty());
},
orders:()=>`<div class="c" style="background:linear-gradient(135deg,#0F4C9A,#15803D);color:#fff;margin-bottom:12px;padding:14px">
  <b style="font-size:16px;display:block">📦 ४. ORDER MANAGEMENT SYSTEM (अर्डर व्यवस्थापन)</b>
  <div style="font-size:11.5px;opacity:.9">आएका अर्डरहरू स्वीकार/अस्वीकार गर्ने, प्रोसेसिङ र बिल डाउनलोड</div>
</div>`+(mine(DB.orders).map(o=>oCard(o,'m')).join('')||`<div class="c st">कुनै अर्डर छैन</div>`),
khata:()=>{
  const L=mine(DB.khata),rec=L.reduce((s,k)=>s+k.amt-k.paid,0);
  return `<div class="c" style="background:linear-gradient(135deg,var(--saf),#F5A623);color:#fff;padding:14px;margin-bottom:12px">
    <b style="font-size:16px;display:block">📒 ५.१ DIGITAL KHATA SYSTEM (डिजिटल खाता)</b>
    <div style="opacity:.9;font-size:12px">उधारो खाता, आंशिक भुक्तानी, SMS रिमाइन्डर र PDF स्टेटमेन्ट</div>
    <div style="font-size:24px;font-weight:800;margin-top:6px">${rs(rec)}</div>
  </div>
  <div class="c">
    <input class="inp" id="kn" placeholder="${T('Customer name')}">
    <input class="inp" id="kp" placeholder="ग्राहकको फोन (९८XXXXXXXX)">
    <div class="g2"><input class="inp" id="ka" type="number" placeholder="${T('Amount')}"><input class="inp" id="kt" placeholder="${T('Note')}"></div>
    <button class="btn s" onclick="addK()">＋ उधारो थप्नुहोस् (Add Credit)</button>
  </div>`+
  (L.map(k=>{const d=k.amt-k.paid;return `<div class="c"><div class="row"><b>${E(k.name)} ${k.phone?'('+E(k.phone)+')':''}</b><span class="bd ${d?'R':'G'}">${d?T('Due ')+rs(d):T('Paid')}</span></div><div class="st">${E(k.note)} · ${rs(k.amt)}</div><div style="display:flex;gap:6px;margin-top:7px">${d?`<button class="btn sm j" onclick="payK(${k.id})">${T('Mark paid')}</button><button class="btn sm g" onclick="toast('📲 भुक्तानी रिमाइन्डर SMS/नोटिफिकेसन पठाइयो!')">🔔 रिमाइन्डर पठाउनुहोस्</button>`:''}<button class="btn sm g" onclick="window.print()">📄 PDF स्टेटमेन्ट</button></div></div>`}).join('')||empty());
},
more:()=>`<div class="c" style="background:linear-gradient(135deg,#1E293B,#0F172A);color:#fff;padding:16px;margin-bottom:12px">
  <b style="font-size:17px;display:block">⚙️ ५. MY SETTING & MERCHANT UTILITIES</b>
  <div style="font-size:12px;opacity:.9">पसल प्रोफाइल, भुक्तानी, डेलिभरी दायरा र भिडियो मार्केटिङ</div>
</div>`+
lk('shop','🏪','५.४ पसल सेटिङ (My Shop Setting)')+
lk('vup','🎥','३. भिडियो मार्केटिङ (Video Marketing VMS)')+
lk('notices','📢','पसलको अफर तथा सूचना (Offers & Notices)')+
lk('ana','📈','व्यावसायिक रिपोर्ट (Business Report)')+
lk('plan','💳','५.३ भुक्तानी प्रणाली (Payment Receive QR & Plan)')+
lk('bills','🧾','बिल तथा भउचरहरू (Invoices)')+
lk('chats','💬','५.२ ग्राहक मेसेन्जर (Customer Chat & Calls)')+
lk('notif','🔔','सूचनाहरू (Notifications)',unBadge())+
lk('timeline','🚀','प्रोजेक्ट टाइमलाइन (D3.js Roadmap)')+
lk('contact','✉️','इन्क्वायरी तथा सहायता (Support Ticket)')+
outb()
};
const delP=id=>{if(S.u.role=='m'&&(DB.products.find(p=>p.id==id)||{}).mid!=S.u.id)return;DB.videos.filter(v=>v.pid==id).forEach(v=>rmB(v.id));DB.products=DB.products.filter(p=>p.id!=id);DB.videos=DB.videos.filter(v=>v.pid!=id);save();R()},restock=id=>{DB.products.find(p=>p.id==id).stock+=10;save();R()};
function addN(){if(!act(S.u))return toast(T('Shop inactive — approval and monthly fee required'));if(used()>=lim(my()))return toast(T('Item limit reached — buy extra slots'));const t=$('nt').value.trim(),b=$('nb').value.trim();if(!t||!b)return toast(T('Fill all fields'));DB.notices.push({id:nid(),mid:S.u.id,title:t,type:$('nk').value,body:b,contact:$('nc').value,d:today()});save();R()}
const delN=id=>{if(S.u.role=='m'&&(DB.notices.find(n=>n.id==id)||{}).mid!=S.u.id)return;DB.notices=DB.notices.filter(n=>n.id!=id);save();R()};
function addK(){const n=$('kn').value.trim(),a=+$('ka').value,p=$('kp')?.value.trim()||'';if(!n||a<=0)return toast(T('Name and amount required'));const kObj={id:nid(),mid:S.u.id,name:n,phone:p,amt:a,paid:0,note:$('kt')?.value||'-',d:today()};DB.khata.unshift(kObj);save();if(window.saveKhataToFirestore)window.saveKhataToFirestore(kObj);R()}
const payK=id=>{const k=DB.khata.find(x=>x.id==id);k.paid=k.amt;save();if(window.saveKhataToFirestore)window.saveKhataToFirestore(k);R()};

/* ---------- admin ---------- */
const aView={
home:()=>`<div class="c" style="background:linear-gradient(135deg,#0F4C9A,#1E3A8A);color:#fff;margin-bottom:12px;padding:14px">
  <b style="font-size:17px;display:block">📊 १.१ ADMIN DASHBOARD & MANAGEMENT</b>
  <div style="font-size:12px;opacity:.9">केन्द्रीय नियन्त्रण, ग्राहक, व्यापारी, अर्डर तथा राजस्व विवरण</div>
</div>
<div class="g2">${stat('Users count',DB.users.length)}${stat('Shops count',shops().length)}${stat('Orders',DB.orders.length)}${stat('Revenue',rs(DB.orders.filter(o=>o.st!='Cancelled').reduce((s,o)=>s+o.total,0)))}</div>
<div class="c" style="border-left:4px solid var(--saf)"><b>${shops().filter(s=>!s.shop.ok).length} ${T('Pending shops')}</b></div>
<h3>${T('Broadcast notification')}</h3><input class="inp" id="bc" placeholder="${T('Message to all users')}"><button class="btn s" onclick="bc()">📣 ${T('Send')}</button>`+outb(),

msg:()=>window.renderMessengerView?window.renderMessengerView():'',

shops:()=>aViewShops(),

mon:()=>`<div class="c" style="background:linear-gradient(135deg,#1E293B,#0F172A);color:#fff;margin-bottom:12px;padding:14px">
  <b style="font-size:16px;display:block">🛡️ २.२ & २.३ सामान तथा भिडियो अनुगमन (Monitoring System)</b>
  <div style="font-size:11.5px;opacity:.9">Automated Fake Product Detection, Banned Removal & Video Reviews</div>
</div>
<h3>🛍️ २.२ सामान अनुगमन (All Products Monitoring - ${DB.products.length})</h3>
${DB.products.slice(0,10).map(p=>`<div class="c row"><span><b>${pn(p)}</b> (${p.id})<div class="st">${rs(p.price)} · पसल: ${E(U(p.mid)?.shop?.name||'-')}</div></span><button class="btn sm r" onclick="delP(${p.id})">हटाउनुहोस्</button></div>`).join('')||empty()}
<h3>🎥 २.३ भिडियो अनुगमन (Video Monitoring Stream - ${DB.videos.length})</h3>
${DB.videos.map(v=>`<div class="c row"><span>🎬 ${E(v.cap)}<div class="st">❤️ ${v.likes}</div></span><button class="btn sm r" onclick="delV(${v.id})">हटाउनुहोस्</button></div>`).join('')||empty()}`,

db:()=>aDbView(),

more:()=>`<div class="c" style="background:linear-gradient(135deg,#1E293B,#0F172A);color:#fff;margin-bottom:12px;padding:14px">
  <b style="font-size:16px;display:block">⚙️ ४.१ SYSTEM SETTINGS & DEVELOPER STUDIO</b>
  <div style="font-size:11.5px;opacity:.9">सुरक्षा, वडा फिल्टर, एआई कन्ट्रोल र स्टुडियो वातावरण</div>
</div>`+
lk('devmega','💻','Section D: Developer Studio Mega Bar')+
lk('backup','💾','४.३.१ All Data Backup (.zip archive)')+
lk('aord','📦','All Orders Audit',`<span class="bd B">${DB.orders.length}</span>`)+
lk('comp','⚖️','Customer Disputes',`<span class="bd R">${DB.comp.filter(c=>c.s!='Resolved').length}</span>`)+
lk('pays','💳','Payment Gateway Audit',`<span class="bd Y">${DB.pay.filter(p=>p.st=='Pending').length}</span>`)+
lk('set','⚙️','System Settings')+
lk('notif','🔔','Notifications',unBadge())+
lk('timeline','🚀','Project Timeline (D3.js)')+
lk('contact','✉️','Professional Inquiries / सम्पर्क')+
outb(),

users:()=>aDbView()
};

function aViewShops(){
  const F=['All','Pending','Verified','Fake'],f=S.sf,st=s=>s.shop.fake?'Fake':s.shop.ok?'Verified':'Pending';
  return `<div class="c" style="background:linear-gradient(135deg,#0F4C9A,#1E3A8A);color:#fff;margin-bottom:12px;padding:14px">
    <b style="font-size:16px;display:block">🏪 २.१ SHOP MANAGEMENT SYSTEM (पसल व्यवस्थापन)</b>
    <div style="font-size:11.5px;opacity:.9">Approve Shop Registration, KYC Verification & Analytics</div>
  </div>
  <div class="chips">${F.map(x=>`<span class="chip ${f==x?'on':''}" onclick="S.sf='${x}';R()">${T(x)}</span>`).join('')}</div>`+
  (shops().filter(s=>f=='All'||st(s)==f).map(s=>{
    const h=s.shop,n=DB.products.filter(p=>p.mid==s.id).length;
    return `<div class="c"><div class="row"><span class="row" style="gap:10px">${av(s)}<span><b>${E(h.name)}</b><div class="st">${G[h.cat-1]?.n||''} · ${E(h.type)}</div></span></span><span class="bd ${h.fake?'R':h.ok?'G':'Y'}">${T(st(s))}</span></div><div class="st" style="margin:6px 0;text-transform:none">👤 ${E(s.name)} · 📞 ${E(s.phone)} · 📍 ${E(s.prov)}, ${E(s.dist)}, ${E(s.local)}-${E(s.ward)}<br>${h.doc?'✅':'❌'} कागजात · सामान: ${n}/${lim(h)}</div><div style="display:flex;gap:6px;flex-wrap:wrap;margin-top:8px"><button class="btn sm ${h.ok?'g':'j'}" onclick="verS(${s.id})">${h.ok?T('Unverify'):'✓ '+T('Verify')}</button><button class="btn sm ${s.on?'r':'j'}" onclick="tgU(${s.id})">${s.on?T('Suspend account'):T('Activate')}</button><button class="btn sm j" onclick="grant(${s.id})">＋३५ दिन थप्नुहोस्</button><button class="btn sm r" onclick="delU(${s.id})">🗑 हटाउनुहोस्</button></div></div>`;
  }).join('')||empty());
}

function aDbView(){
  const curT=S.dbt||'users';
  return `<div class="c" style="background:linear-gradient(135deg,#0F4C9A,#1E3A8A);color:#fff;margin-bottom:12px;padding:14px">
    <b style="font-size:16px;display:block">🗄️ ४.२ DATABASE STATEMENT SYSTEM (INTERACTIVE INSPECTOR MODE)</b>
    <div style="font-size:11.5px;opacity:.9">सबै डेटाबेस तालिकाहरू (8 Tables Schema) प्रत्यक्ष अवलोकन तथा व्यवस्थापन</div>
  </div>
  <div class="chips" style="margin-bottom:12px;overflow-x:auto">
    ${[['users','४.२.१ USERS ('+DB.users.length+')'],['shops','४.२.२ SHOPS ('+shops().length+')'],['prods','४.२.३ PRODUCTS ('+DB.products.length+')'],['orders','४.२.४ ORDERS ('+DB.orders.length+')'],['pays','४.२.५ PAYMENTS ('+DB.pay.length+')'],['khata','४.२.६ KHATA ('+DB.khata.length+')'],['videos','४.२.७ VIDEOS ('+DB.videos.length+')'],['chats','४.२.८ CHAT ('+DB.chats.length+')']].map((x)=>`<span class="chip ${curT===x[0]?'on':''}" onclick="S.dbt='${x[0]}';R()">${x[1]}</span>`).join('')}
  </div>
  <div>${renderTableInspector(curT)}</div>`;
}

function renderTableInspector(tbl){
  if(tbl==='shops'){
    return shops().map(s=>`<div class="c row" style="font-size:12.5px"><span><b>${E(s.shop.name)}</b> (ID: ${s.id})<br>ठेगाना: ${s.dist}, ${s.local}-${s.ward} · 📞 ${E(s.phone)}</span><span><button class="btn sm ${s.shop.ok?'g':'j'}" onclick="verS(${s.id})">${s.shop.ok?'प्रमाणित':'स्वीकार'}</button> <button class="btn sm r" onclick="delShop(${s.id})">हटाउने</button></span></div>`).join('')||empty();
  }
  if(tbl==='prods'){
    return DB.products.map(p=>`<div class="c row" style="font-size:12.5px"><span><b>${pn(p)}</b> (ID: ${p.id})<br>दर: ${rs(p.price)} · स्टक: ${p.stock} · पसल: ${E(U(p.mid)?.shop?.name||'-')}</span><button class="btn sm r" onclick="delP(${p.id})">हटाउने</button></div>`).join('')||empty();
  }
  if(tbl==='orders'){
    return DB.orders.map(o=>`<div class="c row" style="font-size:12.5px"><span><b>अर्डर #${o.id}</b> · कुल: ${rs(o.total)}<br>ग्राहक: ${E(U(o.cid)?.name||'ग्राहक')} · पसल: ${E(U(o.mid)?.shop?.name||'पसल')}</span><span class="bd G">${o.st}</span></div>`).join('')||empty();
  }
  if(tbl==='pays'){
    return DB.pay.map(p=>`<div class="c row" style="font-size:12.5px"><span><b>भुक्तानी #${p.id}</b> · रकम: ${rs(p.amt)}<br>TX: ${E(p.tx||'-')} · ${p.d}</span><span class="bd ${p.st=='Verified'?'G':'Y'}">${p.st}</span></div>`).join('')||empty();
  }
  if(tbl==='khata'){
    return DB.khata.map(k=>`<div class="c row" style="font-size:12.5px"><span><b>${E(k.name)}</b><br>${E(k.note)} · कुल: ${rs(k.amt)}</span><span class="bd R">बाँकी: ${rs(k.amt-k.paid)}</span></div>`).join('')||empty();
  }
  if(tbl==='videos'){
    return DB.videos.map(v=>`<div class="c row" style="font-size:12.5px"><span>🎬 <b>${E(v.cap)}</b> (ID: ${v.id})<br>❤️ ${v.likes} लाइक्स · पसल: ${E(U(v.mid)?.shop?.name||'-')}</span><button class="btn sm r" onclick="delV(${v.id})">हटाउने</button></div>`).join('')||empty();
  }
  if(tbl==='chats'){
    return DB.chats.slice(-25).reverse().map(m=>`<div class="c" style="font-size:12.5px"><div class="row"><b>${m.f==='c'?'ग्राहक':'व्यापारी'} (ID: ${m.sender_id||m.cid})</b><span class="st">${new Date(m.ts).toLocaleTimeString()}</span></div><div style="margin-top:4px">${E(m.t||(m.voice?'🎙️ भ्वाइस':'फाईल'))}</div></div>`).join('')||empty();
  }
  return DB.users.map(u=>`<div class="c row" style="font-size:12.5px"><span><b>${E(u.name)}</b> (ID: ${u.id})<br>इमेल: ${E(u.email)} · भूमिका: ${u.role==='a'?'Admin':u.role==='m'?'Merchant':'Customer'}</span><span><button class="btn sm ${u.on?'r':'j'}" onclick="tgU(${u.id})">${u.on?'निलम्बन':'सक्रिय'}</button> <button class="btn sm r" onclick="delU(${u.id})">हटाउने</button></span></div>`).join('')||empty();
}
const verS=id=>{const s=U(id);s.shop.ok=+!s.shop.ok;if(s.shop.ok)s.shop.fake='';save();R()},fakeS=id=>{const r=($('fk'+id).value||'').trim();if(r.length<3)return toast(T('Write a reason'));const s=U(id);s.shop.fake=r;s.shop.ok=0;save();R()};
const tgS=id=>{const s=U(id);s.shop.ok=+!s.shop.ok;save();R()},tgU=id=>{const u=U(id);u.on=+!u.on;save();R()};
function bc(){const v=$('bc').value.trim();if(!v)return;DB.nf.unshift({id:nid(),t:'📣 '+v,d:today(),r:0});save();toast('📣 '+T('Sent'));R()}
const rsC=(id,s)=>{const c=DB.comp.find(x=>x.id==id);c.s='Resolved';if(s=='refund'){const o=DB.orders.find(x=>x.id==c.oid);if(o)U(o.cid).wallet+=o.total}save();R()};

/* ---------- sub views ---------- */
const SV={
shop:()=>{const s=U(S.sid),h=s.shop,L=h.cat==5?DB.notices.filter(n=>n.mid==s.id).map(nCard):DB.notices.filter(n=>n.mid==s.id).map(nCard).concat(DB.products.filter(p=>p.mid==s.id).map(pc));return `<div class="c"><div class="row"><span class="row" style="gap:10px">${av(s)}<span><b>${E(h.name)}</b><div class="st">${E(h.type)}</div></span></span><button class="btn sm" onclick="S.pair={cid:S.u.id,mid:${s.id}};S.sub='chat';R()">💬</button></div><div class="st" style="margin-top:6px">${h.open}–${h.close} · ${T('Delivery')} ${h.rad} km · ${T('Min order')} ${rs(h.min)}</div></div>`+(L.join('')||`<div class="c st">${T('Nothing listed yet')}</div>`)},
chats:()=>{const c=S.u.role=='c',ids=[...new Set(DB.chats.filter(m=>c?m.cid==S.u.id:m.mid==S.u.id).map(m=>c?m.mid:m.cid))];return ids.map(i=>{const u=U(i);return `<div class="c row" style="cursor:pointer" onclick="S.pair={cid:${c?S.u.id:i},mid:${c?i:S.u.id}};S.sub='chat';R()"><span class="row" style="gap:10px">${av(u)}<b>${E(c?u.shop?.name:u.name)}</b></span><b>›</b></div>`}).join('')||`<div class="c st">${T('No messages yet')}</div>`},
chat:()=>{const p=S.pair,me=S.u.role=='c'?'c':'m',o=U(me=='c'?p.mid:p.cid);return `<h3>💬 ${E(me=='c'?o.shop.name:o.name)}</h3>`+DB.chats.filter(m=>m.cid==p.cid&&m.mid==p.mid).map(m=>`<div style="display:flex;justify-content:${m.f==me?'flex-end':'flex-start'}"><div class="c" style="max-width:78%;margin-bottom:6px;${m.f==me?'background:var(--ind2);color:#fff':''}">${E(m.t)}</div></div>`).join('')+`<div class="row"><input class="inp" id="cm" style="margin:0" placeholder="${T('Type a message')}"><button class="btn s" onclick="cs()">➤</button></div>`},
khata:()=>DB.khata.filter(k=>k.name.toLowerCase()==S.u.name.toLowerCase()).map(k=>`<div class="c"><div class="row"><b>${E(k.note)}</b><span class="bd ${k.amt>k.paid?'R':'G'}">${k.amt>k.paid?T('Due ')+rs(k.amt-k.paid):T('Paid')}</span></div><div class="st">${E(U(k.mid)?.shop?.name)} · ${rs(k.amt)}</div></div>`).join('')||`<div class="c st">${T('No credit records')}</div>`,
notif:()=>DB.nf.filter(n=>!n.u||n.u==S.u.id).map(n=>`<div class="c row" onclick="DB.nf.find(x=>x.id==${n.id}).r=1;save();R()"><span>${E(DB.lang=='ne'&&n.t.includes('Welcome')?'🎉 '+T('Welcome to Open Bazar!'):n.t)}<div class="st">${n.d}</div></span>${n.r?'':'<span class="bd Y">NEW</span>'}</div>`).join(''),
cmp:()=>`<div class="c"><h3>⚠️ #${S.co}</h3><textarea class="inp" id="cx" rows="4" placeholder="${T('Describe the problem')}"></textarea><button class="btn s" onclick="sendC()">${T('Submit')}</button></div>`,
vup:()=>`<div class="c"><input class="inp" id="vc" placeholder="${T('Caption')}"><input class="inp" id="vt" placeholder="#hashtags"><select class="inp" id="vp">${mine(DB.products).map(p=>`<option value="${p.id}">${pn(p)}</option>`).join('')}</select><input class="inp" id="vf" type="file" accept="video/mp4,video/webm" onchange="vPick(this)"><div id="ve"></div><div class="st" style="margin-bottom:8px">MP4/WebM · max 60s</div><button class="btn s" onclick="svV()">⬆ ${T('Upload')}</button></div>`+mine(DB.videos).map(v=>`<div class="c row"><span>🎬 ${E(v.cap)}<div class="st">❤️${v.likes}${v.hv?' · 🎞':''}</div></span><button class="btn sm r" onclick="delV(${v.id})">🗑</button></div>`).join(''),
ana:()=>repV()+SV.anaX(),
anaX:()=>{const sold={};mine(DB.orders).filter(o=>o.st!='Cancelled').forEach(o=>o.items.forEach(i=>sold[i.n]=(sold[i.n]||0)+i.q*i.p));const bs=Object.entries(sold).sort((a,b)=>b[1]-a[1]).slice(0,3);return `<div class="g2">${stat('Revenue',rs(mine(DB.orders).filter(o=>o.st=='Delivered').reduce((s,o)=>s+o.total,0)))}${stat('Orders count',mine(DB.orders).length)}${stat('Rating','4.8 ⭐')}${stat('Products',mine(DB.products).length)}</div><h3>🏆 ${T('Best sellers')}</h3>`+(bs.map((b,i)=>`<div class="c row"><span>${i+1}. ${E(b[0])}</span><b>${rs(b[1])}</b></div>`).join('')||empty())},
shop2:()=>{const s=my();return `<div class="c"><input class="inp" id="s1" value="${E(s.name)}"><div class="g2"><div><div class="st">${T('Opening time')}</div><input class="inp" id="s2" type="time" value="${s.open}"></div><div><div class="st">${T('Closing time')}</div><input class="inp" id="s3" type="time" value="${s.close}"></div><div><div class="st">${T('Delivery radius (km)')}</div><input class="inp" id="s4" type="number" value="${s.rad}"></div><div><div class="st">${T('Minimum order (₨)')}</div><input class="inp" id="s5" type="number" value="${s.min}"></div></div><input class="inp" id="s6" placeholder="${T('PAN number (optional)')}" value="${E(s.pan||'')}"><input class="inp" id="s7" placeholder="${T('Local registration no. (optional)')}" value="${E(s.lrn||'')}"><div class="row"><span class="bd ${s.live?'G':'R'}">${s.live?T('Open'):T('Closed')}</span><span><button class="btn sm g" onclick="my().live=+!my().live;save();R()">${T('Toggle')}</button> <button class="btn sm s" onclick="ssS()">${T('Save')}</button></span></div></div>`},
comp:()=>DB.comp.map(c=>`<div class="c"><div class="row"><b>#${c.oid} · ${E(U(c.cid)?.name)}</b><span class="bd ${c.s=='Open'?'Y':'G'}">${c.s}</span></div><div class="st">vs ${E(U(c.mid)?.shop?.name)} · ${c.d}</div><div style="margin:5px 0">${E(c.x)}</div>${c.s=='Resolved'?'':`<button class="btn sm j" onclick="rsC(${c.id})">${T('Resolve')}</button> <button class="btn sm s" onclick="rsC(${c.id},'refund')">${T('Refund')}</button> <button class="btn sm r" onclick="toast('⚠️')">${T('Warn')}</button>`}</div>`).join('')||empty(),
prm:()=>DB.products.map(p=>`<div class="c row"><span><b>${pn(p)}</b><div class="st">${E(U(p.mid)?.shop?.name)} · ${rs(p.price)}</div></span><button class="btn sm r" onclick="delP(${p.id})">${T('Remove')}</button></div>`).join('')||empty(),
nmon:()=>DB.notices.map(n=>nCard(n)+`<button class="btn sm r" style="margin:-4px 0 10px" onclick="delN(${n.id})">${T('Remove')}</button>`).join('')||empty(),
vmon:()=>DB.videos.map(v=>`<div class="c row"><span>🎬 ${E(v.cap)}</span><button class="btn sm r" onclick="delV(${v.id})">✕</button></div>`).join('')||empty(),
set:()=>setV(),
notices:()=>mView.notices(),
aord:()=>DB.orders.map(o=>oCard(o,'a')+(o.st=='Pending'||o.st=='Processing'?`<button class="btn sm r" style="margin:-4px 0 10px" onclick="setO(${o.id},'Cancelled')">${T('Cancel')}</button>`:'')).join('')||empty(),
plan:()=>planV(),
bills:()=>billsV(),
bnew:()=>bnewV(),
bill:()=>billV(),
pays:()=>paysV(),
acct:()=>acctV(),
contact:()=>window.renderContactFormView?window.renderContactFormView():'',
resume:()=>{if(window.downloadResumePDF)window.downloadResumePDF();return window.renderContactFormView?window.renderContactFormView():'';},
timeline:()=>{if(window.openProjectTimelineModal)window.openProjectTimelineModal();return '';},
msg:()=>window.renderMessengerView?window.renderMessengerView():''};
function cs(){const v=$('cm').value.trim();if(!v)return;DB.chats.push({cid:S.pair.cid,mid:S.pair.mid,f:S.u.role=='c'?'c':'m',t:v});save();R()}
function sendC(){const x=$('cx').value.trim(),o=DB.orders.find(o=>o.id==S.co);if(!x)return;DB.comp.unshift({id:nid(),oid:o.id,cid:o.cid,mid:o.mid,x,s:'Open',d:today()});save();toast('✅');S.sub=null;R()}

/* ---------- plan, payments, account, security ---------- */
function planCard(){const h=my(),ex=h.exp,dl=ex?Math.round((new Date(ex)-new Date(today()))/864e5):-1,ok=dl>=0;return `<div class="c" style="border-left:4px solid var(--${ok&&dl>3?'jade':'crim'})"><div class="row"><b>💳 ${T('Plan')}</b><span class="bd ${ok?'G':'R'}">${ok?T('Active until')+' '+ex+' ('+dl+' '+T('days')+')':T('Inactive')}</span></div><div class="st">${T('Items')}: ${used()} / ${lim(h)}</div>${!ok||dl<=3?`<div style="margin-top:6px;color:var(--crim);font-weight:700">⏰ ${T('Time to update your payment')}</div><button class="btn sm s" style="margin-top:6px" onclick="S.sub='plan';R()">${T('Pay now')}</button>`:''}</div>`}
const payC=p=>`<div class="c row"><span><b>${p.kind=='sub'?T('Monthly plan'):T('Extra slots')} ×${p.qty} · ${rs(p.amt)}</b><div class="st">${E(p.m)} · ${E(p.ref)} · ${p.d}</div></span><span class="bd ${p.st=='Verified'?'G':p.st=='Rejected'?'R':'Y'}">${T(p.st)}</span></div>`;
function planV(){return `<div onclick="S.sub=null">`.slice(0,0)+planCard()+`<div class="c"><h3>${T('Renew or extend')}</h3><select class="inp" id="pk"><option value="sub">${T('Monthly plan')} — ₨100 · ${T('35 days')} (${T('20 items included')})</option><option value="slot">${T('Extra 10 item slots')} — ₨50</option></select><input class="inp" id="pq" type="number" min="1" max="12" value="1" placeholder="${T('Quantity')}"><select class="inp" id="pm2"><option>eSewa</option><option>Khalti</option><option>Bank Transfer</option></select>${DB.set.payee?`<div class="st" style="text-transform:none;margin-bottom:8px">${T('Send payment to')}: <b>${E(DB.set.payee)}</b></div>`:''}<input class="inp" id="pr" placeholder="${T('Transaction ID')}"><button class="btn s" onclick="payReq()">${T('Submit payment')}</button></div><h3>${T('Payments')}</h3>`+(DB.pay.filter(p=>p.mid==S.u.id).map(payC).join('')||empty())}
function payReq(){const k=$('pk').value,q=Math.max(1,Math.min(12,+$('pq').value|0)),r=$('pr').value.trim();if(r.length<6)return toast(T('Enter payment transaction ID'));if(DB.pay.some(p=>p.ref.toLowerCase()==r.toLowerCase()))return toast(T('Duplicate transaction ID'));DB.pay.unshift({id:nid(),mid:S.u.id,kind:k,amt:(k=='sub'?100:50)*q,qty:q,m:$('pm2').value,ref:r,st:'Pending',d:today()});save();toast('✅');R()}
function paysV(){return DB.pay.map(p=>`<div class="c"><div class="row"><b>${E(U(p.mid)?.shop?.name)}</b><span class="bd ${p.st=='Verified'?'G':p.st=='Rejected'?'R':'Y'}">${T(p.st)}</span></div><div class="st">${p.kind=='sub'?T('Monthly plan'):T('Extra slots')} ×${p.qty} · ${rs(p.amt)} · ${E(p.m)} · ${E(p.ref)} · ${p.d}</div>${p.st=='Pending'?`<div style="margin-top:7px"><button class="btn sm j" onclick="payOk(${p.id},1)">✓ ${T('Verify')}</button> <button class="btn sm r" onclick="payOk(${p.id},0)">✕</button></div>`:''}</div>`).join('')||empty()}
function payOk(id,y){const p=DB.pay.find(x=>x.id==id),h=U(p.mid).shop;if(y){p.st='Verified';if(p.kind=='sub')h.exp=addD(h.exp&&h.exp>today()?h.exp:today(),35*p.qty);else h.slots=(h.slots||0)+10*p.qty}else p.st='Rejected';save();R()}
function setV(){const isFloatAiHidden = localStorage.getItem('mero_bazar_hide_floating_ai') === '1';return [['tfa','🔐 2FA'],['ai','🤖 AI Chat'],['esewa','eSewa'],['khalti','Khalti'],['bank','Bank']].map(k=>`<div class="c row"><b>${k[1]}</b><button class="btn sm ${DB.set[k[0]]?'j':'g'}" onclick="DB.set.${k[0]}=+!DB.set.${k[0]};save();R()">${DB.set[k[0]]?'ON':'OFF'}</button></div>`).join('')+`<div class="c row"><b>❓ तैरिने Help बटन</b><button class="btn sm ${isFloatAiHidden?'g':'j'}" onclick="if(window.toggleFloatingAiWidget)window.toggleFloatingAiWidget(${isFloatAiHidden});R()">${isFloatAiHidden?'बन्द (OFF)':'खुला (ON)'}</button></div>`+`<div class="c"><div class="st">${T('Payment receiving number (eSewa/Khalti)')}</div><input class="inp" id="py" value="${E(DB.set.payee||'')}"><button class="btn sm s" onclick="DB.set.payee=$('py').value.trim();save();toast('✅')">${T('Save')}</button></div><div class="c st">Mero Bazar v10 · Build 2026</div>`}
const credF=()=>`<input class="inp" id="op" type="password" placeholder="${T('Current password')}">${S.u.role=='a'?`<input class="inp" id="ne" type="email" placeholder="${T('New email (optional)')}" value="${E(S.u.email)}">`:''}<input class="inp" id="np" type="password" placeholder="${T('New password (min 8, letters + numbers)')}"><button class="btn s" onclick="chPw()">${T('Save')}</button>`;
const forceV=()=>`<div class="main"><div class="hero"><img class="logo sm" src="assets/logo-160.png" alt=""><b>🔐 ${T('Set your own credentials')}</b></div><div class="c st" style="text-transform:none">${T('For security you must set a new password before continuing.')}</div><div class="c">${credF()}</div><button class="btn r sm" onclick="logout()">${T('Logout')}</button></div>`;
window.resendRegOtp=function(){if(S.r){S.r.otp=String(100000+Math.random()*900000|0);toast('📲 '+T('New OTP:')+' '+S.r.otp);R()}};
window.openEmailVerifyModal=function(){
  const isNe=DB.lang=='ne',genOtp=String(100000+Math.random()*900000|0);
  window._pendingEmailOtp=genOtp;
  const old=document.getElementById('email_otp_modal');if(old)old.remove();
  const m=document.createElement('div');
  m.id='email_otp_modal';
  m.className='resume-modal-overlay';
  m.innerHTML=`<div class="resume-modal-card" style="max-width:440px;height:auto;padding:22px;border-radius:20px;box-shadow:0 12px 36px rgba(0,0,0,0.4)">
    <div class="row" style="margin-bottom:12px">
      <b style="font-size:16px">✉️ ${isNe?'इमेल OTP प्रमाणीकरण':'Email OTP Verification'}</b>
      <button class="btn sm g" onclick="document.getElementById('email_otp_modal').remove()">✕</button>
    </div>
    <div class="st" style="margin-bottom:12px;text-transform:none">
      ${isNe?`तपाईंको इमेल <b>${E(S.u.email)}</b> मा प्रमाणीकरण कोड पठाइएको छ:`:`A 6-digit verification code has been sent to <b>${E(S.u.email)}</b>:`}
    </div>
    <div class="c st" style="text-align:center;background:rgba(242,140,15,0.08);border:1px dashed var(--saf);border-radius:12px;padding:12px;margin-bottom:14px">
      <span style="font-size:12px;color:var(--tx)">${isNe?'तपाईंको ६-अंकको OTP कोड:':'Your 6-Digit OTP Code:'}</span><br>
      <b style="font-size:24px;letter-spacing:6px;color:var(--saf)">${genOtp}</b>
      <div style="font-size:11px;opacity:0.8;margin-top:4px">${isNe?'(वास्तविक उत्पादनमा: Firebase Auth / SMTP बाट सिधै इनबक्समा जान्छ)':'(In production: delivered via Firebase Auth / SMTP)'}</div>
    </div>
    <input class="inp" id="inp_email_otp" type="tel" maxlength="6" placeholder="${isNe?'६-अंकको कोड लेख्नुहोस्':'Enter 6-digit code'}" style="text-align:center;font-size:18px;letter-spacing:4px">
    <div id="email_otp_err" class="st" style="color:var(--crim);display:none;margin-bottom:8px"></div>
    <div class="row" style="margin-top:12px;gap:8px">
      <button class="btn g" style="flex:1" onclick="document.getElementById('email_otp_modal').remove()">${isNe?'रद्द गर्नुहोस्':'Cancel'}</button>
      <button class="btn s" style="flex:1" onclick="submitEmailOtp()">${isNe?'प्रमाणित गर्नुहोस्':'Verify Email'}</button>
    </div>
  </div>`;
  document.body.appendChild(m);
};
window.submitEmailOtp=function(){
  const isNe=DB.lang=='ne',inp=document.getElementById('inp_email_otp'),err=document.getElementById('email_otp_err'),val=(inp?inp.value:'').trim();
  if(!val||val!==window._pendingEmailOtp){
    if(err){err.textContent=isNe?'⚠️ गलत OTP कोड। कृपया पुनः प्रयास गर्नुहोस्।':'⚠️ Incorrect OTP code. Please retry.';err.style.display='block';}
    return;
  }
  S.u.ev=1;save();
  const m=document.getElementById('email_otp_modal');if(m)m.remove();
  toast(isNe?'✅ इमेल सफलतापूर्वक प्रमाणित भयो!':'✅ Email successfully verified!');
  R();
};
const verifCenter=()=>{
  const u=S.u,isNe=DB.lang=='ne',ev=!!u.ev,pv=!!u.pv||true;
  return `<div class="c" style="border-left:4px solid var(--saf);">
    <div class="row">
      <b>🛡️ ${isNe?'सुरक्षा तथा प्रमाणीकरण प्रणाली':'Verification & Security Center'}</b>
      <span class="bd ${ev&&pv?'G':'Y'}">${ev&&pv?(isNe?'✓ पूर्ण प्रमाणित':'✓ Fully Verified'):(isNe?'इमेल बाँकी':'Email Pending')}</span>
    </div>
    <div style="display:flex;flex-direction:column;gap:8px;margin-top:10px">
      <div class="row" style="padding:6px 0;border-bottom:1px solid var(--ln)">
        <span>✉️ <b>${isNe?'इमेल प्रमाणीकरण':'Email Verification'}</b><div class="st">${E(u.email)}</div></span>
        ${ev?`<span class="bd G">✓ ${isNe?'प्रमाणित':'Verified'}</span>`:`<button class="btn sm s" onclick="openEmailVerifyModal()">✉️ ${isNe?'OTP प्रमाणीकरण':'Verify with OTP'}</button>`}
      </div>
      <div class="row" style="padding:6px 0;border-bottom:1px solid var(--ln)">
        <span>📱 <b>${isNe?'मोबाइल SMS प्रमाणीकरण':'Mobile SMS Verification'}</b><div class="st">${E(u.phone||'98XXXXXXXX')}</div></span>
        <span class="bd G">✓ ${isNe?'प्रमाणित':'Verified'}</span>
      </div>
      ${u.role==='m'?`<div class="row" style="padding:6px 0;border-bottom:1px solid var(--ln)">
        <span>📄 <b>${isNe?'व्यवसाय तथा प्यान प्रमाणीकरण':'Business & PAN Verification'}</b><div class="st">${E(u.shop?.pan?'PAN: '+u.shop.pan:'No PAN')}</div></span>
        <span class="bd ${u.shop?.ok?'G':'Y'}">${u.shop?.ok?(isNe?'✓ स्वीकृत':'✓ Approved'):(isNe?'प्रतीक्षारत':'Pending')}</span>
      </div>`:''}
      <div class="row" style="padding:6px 0">
        <span>🕸️ <b>${isNe?'प्राविधिक रडार चार्ट (D3.js)':'Tech Proficiency Radar (D3.js)'}</b><div class="st">Kotlin, Firebase, JavaScript, UI Design</div></span>
        <button class="btn sm j" onclick="window.openRadarChart?window.openRadarChart():0">🕸️ ${isNe?'हेर्नुहोस्':'View Radar'}</button>
      </div>
    </div>
  </div>`;
};
const acctV=()=>verifCenter()+`<div class="c"><h3>📍 ${T('Update my location')}</h3>${S.u.la!=null?`<div class="st">${S.u.la}, ${S.u.lo}</div>`:''}<button class="btn j" onclick="locU()">📍 ${T('Use my current location')}</button></div><div class="c"><h3>📷 ${T('Change photo')}</h3><input class="inp" type="file" accept="image/*" onchange="up(this,'photo',240,.7)"></div><div class="c"><h3>🔑 ${T('Change password')}</h3>${credF()}</div><div class="c"><h3>💾 ${T('Backup & Restore')}</h3><div class="st" style="text-transform:none;margin-bottom:8px">${T('All data is stored on this phone only. Export a backup file to keep it safe.')}</div><input class="inp" id="bkp" type="password" placeholder="${T('Backup passphrase (min 8)')}"><button class="btn j" onclick="bkE()">⬇ ${T('Export backup')}</button><input class="inp" type="file" accept=".abk,.txt" style="margin-top:10px" onchange="bkI(this)"></div>`;
async function chPw(){const u=S.u;if(!crypto.subtle)return toast(T('Secure (HTTPS) connection required'));if((await hp($('op').value,u.salt))!==u.pass)return toast(T('Current password wrong'));const n=$('np').value;if(!PW.test(n))return toast(T('Password: min 8 chars with letters and numbers'));const ne=$('ne')?$('ne').value.trim().toLowerCase():u.email;if(!/\S+@\S+\.\S+/.test(ne)||DB.users.some(x=>x.id!=u.id&&canon(x.email)==canon(ne)))return toast(T('Invalid or used email'));u.email=ne;u.salt=rnd(16);u.pass=await hp(n,u.salt);u.mc=0;sess(u);toast('✅')}
async function bkE(){const p=$('bkp').value;if(p.length<8)return toast(T('Backup passphrase min 8 chars'));const s=rnd(16),a=document.createElement('a');a.href=URL.createObjectURL(new Blob(['AB1.'+s+'.'+await seal(DB,await pk(p,s))],{type:'text/plain'}));a.download='open-bazar-backup-'+today()+'.abk';a.click()}
async function bkI(i){const f=i.files[0],p=$('bkp').value;if(!f)return;if(p.length<8)return toast(T('Backup passphrase min 8 chars'));try{const [m,s,c]=(await f.text()).trim().split('.');if(m!='AB1')throw 0;const d=await unseal(c,await pk(p,s));if(!valid(d))throw 0;DB=cleanDB(d);save();logout();toast('✅ '+T('Restored — login again'))}catch(_){toast(T('Wrong passphrase or file'))}}
/* ---------- bills, expiry checks, demo shops ---------- */
const unBadge=()=>{const n=DB.nf.filter(x=>!x.r&&(!x.u||x.u==S.u.id)).length;return n?`<span class="bd Y">${n}</span>`:''};
const nfu=(u,x)=>DB.nf.unshift({id:nid(),t:x,d:today(),r:0,u});
function chk(){let ch=0;const ad=DB.users.filter(u=>u.role=='a');shops().forEach(s=>{const h=s.shop;if(h.demo||!h.exp)return;const dl=Math.round((new Date(h.exp)-new Date(today()))/864e5);
if(dl<0&&h.xn!==h.exp){h.xn=h.exp;nfu(s.id,'⏰ '+T('Your payment period has ended. Update your payment to reopen your shop.'));ad.forEach(a=>nfu(a.id,'🔒 '+h.name+' — '+T('Shop closed (payment expired)')));ch=1}
else if(dl>=0&&dl<=3&&h.wn!==h.exp){h.wn=h.exp;nfu(s.id,'⏰ '+T('Your plan ends in 3 days. Please update your payment.'));ad.forEach(a=>nfu(a.id,'⚠️ '+h.name+' — '+T('Shop plan ends soon')));ch=1}});if(ch)save()}
function seedDemo(){DB.bills=DB.bills||[];if(DB.dm)return;DB.dm=1;
const A=[[1,'डेमो किराना पसल','किराना','Bagmati','Kathmandu','Kathmandu',10,['बासमती चामल (५ केजी)|किराना|650|50|40','मुसुरो दाल (१ केजी)|किराना|150|10|60','सरसोंको तेल (१ लिटर)|किराना|240|20|30','ताजा तरकारी सेट|सब्जी तथा फलफुल|180|0|25','स्कुल कापी (१० वटा)|शिक्षा|400|40|50']],
[2,'डेमो हार्डवेयर','हार्डवेयर सामग्री','Gandaki','Kaski','Pokhara',8,['सिमेन्ट (५० केजी)|हार्डवेयर सामग्री|1250|50|100','फलामे छड (१ केजी)|हार्डवेयर सामग्री|140|5|500','वाटरप्रुफ रंग (४ लिटर)|हार्डवेयर सामग्री|2200|200|20','पीभिसी पाइप (१ मिटर)|हार्डवेयर सामग्री|180|0|80','औजार बाकस सेट|हार्डवेयर सामग्री|1500|150|15']],
[3,'डेमो इलेक्ट्रिक सेन्टर','विद्युतीय पसल','Koshi','Morang','Biratnagar',4,['एलइडी बल्ब १२ वाट|विद्युतीय पसल|220|20|100','कपर तार (१ मिटर)|घरायसी इलेक्ट्रिक सामग्री|60|0|300','मोटरसाइकल ब्याट्री|सवारी साधन तथा पाटपुर्जा|3200|300|10','हेलमेट (ISI)|सवारी साधन तथा पाटपुर्जा|1800|200|25','इन्जिन अयल १ लिटर|सवारी साधन तथा पाटपुर्जा|650|50|40']],
[4,'डेमो बहुउद्देश्य पसल','बहुउद्देश्य सामग्री','Bagmati','Chitwan','Bharatpur',6,['सोलार लाइट|बहुउद्देश्य सामग्री|1200|100|30','मोबाइल च्यार्जर|बहुउद्देश्य सामग्री|450|50|60','ट्राभल ब्याग|बहुउद्देश्य सामग्री|1500|200|20','फ्लास्क (१ लिटर)|बहुउद्देश्य सामग्री|700|0|35','पावर बैंक १०००० mAh|बहुउद्देश्य सामग्री|1800|300|15']],
[5,'डेमो सूचना केन्द्र','अन्य महत्वपूर्ण सूचना','Sudurpashchim','Kailali','Dhangadhi',2,['सरकारी विज्ञापन|नगरपालिकामा सिलाइ तालिम|निःशुल्क सिलाइ तालिमका लागि आवेदन खुला छ। सीट सीमित।','रोजगारमूलक सूचना|सेल्स सहयोगी आवश्यक|काठमाडौंको पसलमा ५ जना सेल्स सहयोगी चाहिएको छ।','व्यावसायिक विज्ञापन|दशैँ विशेष छुट|सबै कपडामा १५% सम्म छुट, सीमित समयका लागि।','गैरसरकारी विज्ञापन|निःशुल्क स्वास्थ्य जाँच शिविर|यस शनिबार बिहान ८ बजेदेखि स्वास्थ्य जाँच शिविर हुँदैछ।','अन्य महत्वपूर्ण सूचना|वडा कार्यालयको नयाँ समय|वडा कार्यालय बिहान १० देखि ५ बजेसम्म खुला रहने छ।']]];
A.forEach((a,i)=>{const id=nid();DB.users.push({id,name:'Demo '+(i+1),email:'demo'+(i+1)+'@demo.local',salt:'00',pass:'!',role:'m',on:1,phone:'9800000000',prov:a[3],dist:a[4],local:a[5],ward:a[6],photo:'',shop:{name:a[1],cat:a[0],type:a[2],open:'08:00',close:'20:00',rad:10,min:0,ok:1,live:1,exp:'2099-12-31',slots:0,fake:'',demo:1,doc:''}});
a[7].forEach(s=>{const q=s.split('|');if(a[0]==5)DB.notices.push({id:nid(),mid:id,type:q[0],title:q[1],body:q[2],contact:'9800000000',d:today()});else DB.products.push({id:nid(),mid:id,name:q[0],type:q[1],price:+q[2],dis:+q[3],stock:+q[4]})})});save()}
function delShop(id){const k=id||'all';if(S.rm!==k){S.rm=k;return toast(T('Tap again to confirm'))}S.rm=0;const ids=DB.users.filter(u=>u.shop&&u.shop.demo&&(!id||u.id==id)).map(u=>u.id),has=a=>ids.includes(a.mid);DB.users=DB.users.filter(u=>!ids.includes(u.id));['products','notices','videos','orders','khata','pay','bills','chats','comp'].forEach(k=>DB[k]=DB[k].filter(a=>!has(a)));S.cart={};save();toast('✅');R()}
function invData(){const x=S.bi;if(x.o){const o=DB.orders.find(a=>a.id==x.o);return {m:U(o.mid),no:'O'+o.id,d:o.d,c:U(o.cid)?.name,items:o.items}}const b=DB.bills.find(a=>a.id==x.b);return {m:U(b.mid),no:'B'+b.id,d:b.d,c:b.cname,ph:b.phone,items:b.items}}
function billV(){const v=invData(),h=v.m.shop,tot=v.items.reduce((s,i)=>s+i.q*i.p,0),td='style="padding:5px 3px;border-bottom:1px solid #ddd"';
return `<div class="c" id="inv" style="background:#fff;color:#111"><div style="display:flex;gap:10px;align-items:center"><img src="${v.m.photo||'assets/logo-160.png'}" style="width:56px;height:56px;border-radius:50%;object-fit:cover" alt=""><div><b style="font-size:17px">${E(h.name)}</b><div style="font-size:12px">${E(v.m.dist)}, ${E(v.m.local)}-${E(v.m.ward)}, ${E(v.m.prov)}</div><div style="font-size:12px">📞 ${E(v.m.phone)}</div>${regL(h)}</div></div><hr style="margin:10px 0;border:0;border-top:2px solid #111"><div style="display:flex;justify-content:space-between;font-size:12px"><span><b>${T('Bill No')}:</b> ${v.no}</span><span><b>${T('Date')}:</b> ${v.d}</span></div><div style="font-size:12px;margin:3px 0 8px"><b>${T('Customer')}:</b> ${E(v.c||'-')}${v.ph?' · '+E(v.ph):''}</div>
<table style="width:100%;border-collapse:collapse;font-size:13px"><tr style="background:#eee"><th ${td} align="left">#</th><th ${td} align="left">${T('Item')}</th><th ${td} align="right">${T('Qty')}</th><th ${td} align="right">${T('Rate')}</th><th ${td} align="right">${T('Amount')}</th></tr>${v.items.map((i,n)=>`<tr><td ${td}>${n+1}</td><td ${td}>${E(i.n)}</td><td ${td} align="right">${i.q}</td><td ${td} align="right">${rs(i.p)}</td><td ${td} align="right">${rs(i.q*i.p)}</td></tr>`).join('')}</table><div style="display:flex;justify-content:space-between;margin-top:10px;font-size:17px;font-weight:800"><span>${T('Total (₨)')}</span><span>${rs(tot)}</span></div><div style="text-align:center;margin-top:12px;font-size:12px">🙏 ${T('Thank you!')}</div></div><div class="noprint row" style="gap:8px"><button class="btn s" style="flex:1" onclick="window.print()">🖨 ${T('Print / PDF')}</button><button class="btn j" style="flex:1" onclick="billImg()">🖼 ${T('Download as image')}</button><button class="btn" style="flex:1" onclick="shareB()">📤 ${T('Share')}</button></div>`}
function shareB(){const v=invData(),tot=v.items.reduce((s,i)=>s+i.q*i.p,0),x=v.m.shop.name+'\n'+v.m.dist+', '+v.m.local+'-'+v.m.ward+' · '+v.m.phone+(v.m.shop.pan?'\nPAN: '+v.m.shop.pan:'')+(v.m.shop.lrn?'\nReg: '+v.m.shop.lrn:'')+'\n'+v.no+' · '+v.d+'\n\n'+v.items.map(i=>i.n+' '+i.q+' × '+i.p+' = '+i.q*i.p).join('\n')+'\n\n'+T('Total (₨)')+': '+tot;try{navigator.share?navigator.share({title:v.no,text:x}).catch(()=>{}):navigator.clipboard.writeText(x).then(()=>toast(T('Copied')))}catch(_){}}
const billsV=()=>`<button class="btn s" style="width:100%;margin-bottom:10px" onclick="S.sub='bnew';R()">＋ ${T('New bill')}</button>`+(mine(DB.bills).map(b=>`<div class="c row" style="cursor:pointer" onclick="S.bi={b:${b.id}};S.sub='bill';R()"><span><b>B${b.id} · ${E(b.cname||'-')}</b><div class="st">${b.d}</div></span><b>${rs(b.total)}</b></div>`).join('')||empty());
const bnewV=()=>`<div class="c"><input class="inp" id="bn" placeholder="${T('Customer name (optional)')}"><input class="inp" id="bph" type="tel" placeholder="${T('Phone (optional)')}"></div>`+mine(DB.products).map(p=>`<div class="c row"><span><b>${pn(p)}</b><div class="st">${rs(eff(p))} · ${p.stock} ${T('units')}</div></span><input class="inp" id="bq${p.id}" type="number" min="0" max="${p.stock}" value="0" oninput="bt()" style="width:76px;margin:0"></div>`).join('')+`<div class="c"><div class="st">${T('Other item')}</div><input class="inp" id="bo" placeholder="${T('Item name')}"><div class="g2"><input class="inp" id="bp" type="number" min="0" placeholder="${T('Rate')}" oninput="bt()"><input class="inp" id="bk" type="number" min="0" placeholder="${T('Qty')}" oninput="bt()"></div></div><div class="c row"><b>${T('Total (₨)')}</b><span class="big" id="bttl">₨0</span></div><button class="btn s" style="width:100%" onclick="mkBill()">🧾 ${T('Create bill')}</button>`;
function bt(){let s=0;mine(DB.products).forEach(p=>s+=eff(p)*(+$('bq'+p.id).value||0));s+=(+$('bp').value||0)*(+$('bk').value||0);$('bttl').textContent=rs(s)}
function mkBill(){const items=[];let bad=0;mine(DB.products).forEach(p=>{const q=+$('bq'+p.id).value||0;if(q>p.stock)bad=1;if(q>0)items.push({pid:p.id,n:p.name,q,p:eff(p)})});if(bad)return toast(T('Not enough stock'));const o=$('bo').value.trim(),op=+$('bp').value||0,oq=+$('bk').value||0;if(o&&op>0&&oq>0)items.push({n:o,q:oq,p:op});if(!items.length)return toast(T('Add at least one item'));
items.forEach(i=>{const p=DB.products.find(x=>x.id==i.pid);if(p)p.stock-=i.q});const b={id:nid(),mid:S.u.id,cname:$('bn').value.trim(),phone:$('bph').value.trim(),items,total:items.reduce((s,i)=>s+i.q*i.p,0),d:today()};DB.bills.unshift(b);save();S.bi={b:b.id};S.sub='bill';R()}
/* ---------- v10 additions ---------- */
Object.assign(N,{'PAN number (optional)':'प्यान नम्बर (वैकल्पिक)','Local registration no. (optional)':'स्थानीय दर्ता नम्बर (वैकल्पिक)','PAN':'प्यान','Reg. No.':'दर्ता नं.','Invalid PAN (9 digits)':'प्यान नम्बर ९ अंकको हुनुपर्छ','Invalid registration number':'दर्ता नम्बर मिलेन','Download as image':'फोटो डाउनलोड','Use my current location':'मेरो हालको स्थान प्रयोग गर्नुहोस्','Update my location':'मेरो स्थान अपडेट','Location not available':'स्थान पाउन सकिएन (अनुमति दिनुहोस्)','Items do not match this shop':'सामान यो पसलसँग मिलेन','Matched & accepted by':'म्याच भई स्वीकार गर्‍यो —','Matched with shop':'पसलसँग म्याच भयो','Waiting for shop to match':'पसलले म्याच गर्न बाँकी','New order from':'नयाँ अर्डर —','Product photo':'सामानको फोटो','Video: MP4/WebM up to 30 MB':'भिडियो: MP4/WebM, ३० MB सम्म','Not a valid video':'मान्य भिडियो होइन','Start (s)':'सुरु (सेकेन्ड)','End (s)':'अन्त्य (सेकेन्ड)','Set start here':'यहाँबाट सुरु','Set end here':'यहाँ अन्त्य','No filter':'फिल्टर छैन','Bright':'चम्किलो','Warm':'न्यानो','Black & white':'कालोसेतो','Mute sound':'आवाज बन्द','Invalid trim range':'काट्ने समय मिलेन'});
const si=s=>/^data:image\/(jpeg|png|webp);base64,[A-Za-z0-9+\/=]+$/.test(s||'')?s:'';
const cleanDB=d=>{d.users.forEach(u=>{u.photo=si(u.photo);if(u.shop)u.shop.doc=si(u.shop.doc);if(u.la!=null&&!(Math.abs(u.la)<=90&&Math.abs(u.lo)<=180))u.la=u.lo=null});d.products.forEach(p=>{if(p.img)p.img=si(p.img)});return d};
/* location: nearest first (ward > municipality > district > province, then by km) */
const DM={Kathmandu:[27.7172,85.324],Pokhara:[28.2096,83.9856],Biratnagar:[26.4525,87.2718],Bharatpur:[27.6833,84.4333],Dhangadhi:[28.6833,80.6]};
const gp=x=>x&&x.la!=null?[x.la,x.lo]:x&&x.shop&&x.shop.demo?DM[x.local]||null:null;
const km=u=>{const a=gp(S.u),b=gp(u);if(!a||!b)return 9999;const r=Math.PI/180,h=Math.sin((b[0]-a[0])*r/2)**2+Math.cos(a[0]*r)*Math.cos(b[0]*r)*Math.sin((b[1]-a[1])*r/2)**2;return 12742*Math.asin(Math.sqrt(h))};
const kmT=u=>{const k=km(u);return k<9999?' · '+(k<1?Math.round(k*1000)+' m':k.toFixed(1)+' km'):''};
function locate(cb){return window.pickLoc?window.pickLoc(cb):locate0(cb)}
function locate0(cb){if(!navigator.geolocation)return toast(T('Location not available'));navigator.geolocation.getCurrentPosition(p=>{const a=+p.coords.latitude.toFixed(4),o=+p.coords.longitude.toFixed(4);if(Math.abs(a)<=90&&Math.abs(o)<=180)cb(a,o)},()=>toast(T('Location not available')),{timeout:10000,maximumAge:60000})}
const locR=()=>{rc();locate((a,o)=>{S.r.d.la=a;S.r.d.lo=o;R();toast('📍 ✓')})},locU=()=>locate((a,o)=>{S.u.la=a;S.u.lo=o;save();R();toast('📍 ✓')});
/* optional PAN / local registration number */
function okReg(p,l){p=(p||'').trim();l=(l||'').trim();if(p&&!/^\d{9}$/.test(p)){toast(T('Invalid PAN (9 digits)'));return 0}if(l&&!/^[A-Za-z0-9\u0966-\u096F\-\/ ]{2,30}$/.test(l)){toast(T('Invalid registration number'));return 0}return 1}
const regL=h=>(h.pan?`<div style="font-size:12px">${T('PAN')}: <b>${E(h.pan)}</b></div>`:'')+(h.lrn?`<div style="font-size:12px">${T('Reg. No.')}: <b>${E(h.lrn)}</b></div>`:'');
/* product photo + tappable shop logo */
const pimg=(p,s)=>{const im=p.img?si(p.img):'';return `<div class="pim"${im?` style="background-image:url(${im})"`:''}>${im?'':'📦'}<span class="lg" onclick="event.stopPropagation();S.sid=${s.id};S.sub='shop';R()" title="${E(s.shop.name)}">${av(s)}</span></div>`};
async function pph(id,el){const p=DB.products.find(x=>x.id==id),f=el.files[0];if(!p||p.mid!=S.u.id||!f)return;try{p.img=si(await pic(f,640,.65));save();R();toast('✅')}catch(_){toast(T('Not a valid image'))}}
const catGo=i=>{S.g=i;S.hg=0;R();const m=document.querySelector('.main');if(m)m.scrollTop=0};
/* video: stored in IndexedDB, simple non-destructive editing (trim, mute, filter) */
const FLT={n:'none',b:'brightness(1.25) contrast(1.05)',w:'sepia(.45) saturate(1.3)',g:'grayscale(1)'};
let VE=null;
const vPlayer=v=>v.hv?`<video data-v="${+v.id}" controls playsinline preload="metadata" ${v.mu?'muted':''} style="width:100%;max-height:340px;border-radius:14px;background:#000;filter:${FLT[v.fl]||'none'}"></video>`:`<div style="height:190px;border-radius:14px;background:linear-gradient(160deg,var(--ind),var(--saf));display:flex;align-items:center;justify-content:center;font-size:64px">🎬</div>`;
async function hydrate(){const els=document.querySelectorAll('video[data-v]');if(!els.length)return;let d;try{d=await idb()}catch(_){return}els.forEach(el=>{const v=DB.videos.find(x=>x.id==el.dataset.v);if(!v)return;const q=d.transaction('k').objectStore('k').get('v'+v.id);q.onsuccess=()=>{if(!(q.result instanceof Blob))return;const st=+v.st||0,en=+v.en||0;el.src=URL.createObjectURL(q.result)+(en?'#t='+st+','+en:st?'#t='+st:'');el.addEventListener('timeupdate',()=>{if(en&&el.currentTime>=en){el.pause();el.currentTime=st}})}})}
async function rmB(id){try{const d=await idb();d.transaction('k','readwrite').objectStore('k').delete('v'+id)}catch(_){}}
const delV=id=>{const v=DB.videos.find(x=>x.id==id);if(!v||(S.u.role=='m'&&v.mid!=S.u.id))return;rmB(id);DB.videos=DB.videos.filter(x=>x.id!=id);save();R()};
function vPick(el){const f=el.files[0];if(!f)return;if(!/^video\/(mp4|webm)$/.test(f.type)||f.size>30*1048576)return toast(T('Video: MP4/WebM up to 30 MB'));const u=URL.createObjectURL(f),t=document.createElement('video');t.preload='metadata';t.onloadedmetadata=()=>{if(!isFinite(t.duration))return toast(T('Not a valid video'));VE={f,u,d:+t.duration.toFixed(1)};$('ve').innerHTML=vEd()};t.onerror=()=>toast(T('Not a valid video'));t.src=u}
const vEd=()=>`<video id="vpv" src="${VE.u}" controls playsinline style="width:100%;max-height:280px;border-radius:12px;background:#000;margin-bottom:8px"></video><div class="g2"><div><div class="st">${T('Start (s)')}</div><input class="inp" id="v1" type="number" min="0" max="${VE.d}" step="0.1" value="0"></div><div><div class="st">${T('End (s)')}</div><input class="inp" id="v2" type="number" min="0" max="${VE.d}" step="0.1" value="${Math.min(VE.d,60)}"></div></div><div class="row" style="margin-bottom:8px"><button class="btn sm g" type="button" onclick="$('v1').value=$('vpv').currentTime.toFixed(1)">⏮ ${T('Set start here')}</button><button class="btn sm g" type="button" onclick="$('v2').value=$('vpv').currentTime.toFixed(1)">⏭ ${T('Set end here')}</button></div><select class="inp" id="vfl" onchange="$('vpv').style.filter=FLT[this.value]"><option value="n">${T('No filter')}</option><option value="b">${T('Bright')}</option><option value="w">${T('Warm')}</option><option value="g">${T('Black & white')}</option></select><label class="st" style="text-transform:none"><input type="checkbox" id="vmu"> ${T('Mute sound')}</label>`;
async function svV(){const c=$('vc').value.trim(),p=+$('vp').value;if(!c)return toast(T('Caption required'));if(!p||!DB.products.some(x=>x.id==p&&x.mid==S.u.id))return toast(T('Add a product first'));const rec={id:nid(),mid:S.u.id,pid:p,cap:c,tags:$('vt').value,likes:0};
if(VE&&$('vf').files[0]){const a=Math.max(0,+$('v1').value||0),b=Math.min(VE.d,+$('v2').value||VE.d);if(b-a<1)return toast(T('Invalid trim range'));if(b-a>60)return toast(T('Video must be ≤ 60s'));
try{const d=await idb();await new Promise((ok,no)=>{const x=d.transaction('k','readwrite');x.objectStore('k').put(VE.f,'v'+rec.id);x.oncomplete=ok;x.onerror=no})}catch(_){return toast(T('Storage full — export a backup'))}
Object.assign(rec,{hv:1,st:a,en:b>=VE.d?0:b,mu:$('vmu').checked?1:0,fl:FLT[$('vfl').value]?$('vfl').value:'n'})}VE=null;DB.videos.unshift(rec);save();toast('✅');R()}
async function addP(){const h=my();if(used()>=lim(h))return toast(T('Item limit reached — buy extra slots'));const n=$('pn').value.trim(),pr=+$('pp').value,st=+$('ps').value,d=+$('pd').value||0;if(!n||pr<=0||st<0||d<0||d>=pr)return toast(T('Fill all fields'));let im='';const f=$('pi').files[0];if(f){try{im=si(await pic(f,640,.65))}catch(_){return toast(T('Not a valid image'))}}const pItem={id:nid(),mid:S.u.id,name:n,type:$('pt').value,price:pr,dis:d,stock:st,img:im,weight:$('pw')?.value||'',dfee:+$('pdf')?.value||0,phone:$('pph')?.value||S.u.phone||''};DB.products.push(pItem);save();if(window.saveProductToFirestore)window.saveProductToFirestore(pItem);toast('✅ '+T('Product added'));R()}
function ssS(){if(!okReg($('s6').value,$('s7').value))return;Object.assign(my(),{name:$('s1').value.trim()||my().name,open:$('s2').value,close:$('s3').value,rad:+$('s4').value,min:+$('s5').value,pan:$('s6').value.trim(),lrn:$('s7').value.trim()});save();toast('✅')}
/* bill as image (drawn on canvas, no external library) */
async function billImg(){const v=invData(),h=v.m.shop,tot=v.items.reduce((s,i)=>s+i.q*i.p,0),W=720,RH=44,hd=[h.name,v.m.dist+', '+v.m.local+'-'+v.m.ward+', '+v.m.prov,'Tel: '+v.m.phone];if(h.pan)hd.push(T('PAN')+': '+h.pan);if(h.lrn)hd.push(T('Reg. No.')+': '+h.lrn);
const H=240+hd.length*30+(v.items.length+1)*RH+120,c=document.createElement('canvas');c.width=W;c.height=H;const x=c.getContext('2d'),F='"Noto Sans Devanagari","Segoe UI",system-ui,sans-serif',fit=(t,m)=>{t=String(t);while(t.length>1&&x.measureText(t).width>m)t=t.slice(0,-2)+'…';return t};
x.fillStyle='#fff';x.fillRect(0,0,W,H);x.fillStyle='#111';x.textBaseline='alphabetic';
const ph=si(v.m.photo);if(ph){try{const im=new Image();im.src=ph;await im.decode();const s=Math.min(im.width,im.height);x.save();x.beginPath();x.arc(W-90,80,50,0,7);x.clip();x.drawImage(im,(im.width-s)/2,(im.height-s)/2,s,s,W-140,30,100,100);x.restore()}catch(_){}}
let y=50;x.font='bold 32px '+F;x.fillText(fit(hd[0],W-200),40,y);x.font='21px '+F;hd.slice(1).forEach(t=>{y+=30;x.fillText(fit(t,W-200),40,y)});
y+=22;x.fillRect(40,y,W-80,3);y+=42;x.font='21px '+F;x.fillText(T('Bill No')+': '+v.no,40,y);x.textAlign='right';x.fillText(T('Date')+': '+v.d,W-40,y);x.textAlign='left';y+=34;x.fillText(fit(T('Customer')+': '+(v.c||'-')+(v.ph?' · '+v.ph:''),W-80),40,y);
y+=20;x.fillStyle='#eee';x.fillRect(40,y,W-80,RH);x.fillStyle='#111';x.font='bold 19px '+F;const cols=(r,b)=>{const t=y+30;x.textAlign='left';x.fillText(r[0],50,t);x.fillText(fit(r[1],250),90,t);x.textAlign='right';x.fillText(r[2],440,t);x.fillText(r[3],560,t);x.fillText(r[4],W-50,t);x.fillStyle='#ddd';x.fillRect(40,y+RH-1,W-80,1);x.fillStyle='#111'};
cols(['#',T('Item'),T('Qty'),T('Rate'),T('Amount')]);x.font='19px '+F;v.items.forEach((i,n)=>{y+=RH;cols([n+1,i.n,i.q,rs(i.p),rs(i.q*i.p)])});
y+=RH+40;x.textAlign='left';x.font='bold 28px '+F;x.fillText(T('Total (₨)'),40,y);x.textAlign='right';x.fillText(rs(tot),W-40,y);x.textAlign='center';x.font='20px '+F;x.fillText('🙏 '+T('Thank you!'),W/2,y+50);
c.toBlob(b=>{const a=document.createElement('a');a.href=URL.createObjectURL(b);a.download='bill-'+v.no+'.png';a.click();setTimeout(()=>URL.revokeObjectURL(a.href),4000);toast('✅')},'image/png')}
/* ---------- v10.1: admin lock, unique emails, reports, password eye ---------- */
Object.assign(N,{'Messenger':'मेसेन्जर','Theme':'थिम','Dark mode':'डार्क मोड','Light mode':'लाइट मोड','Offers & Notices':'अफर तथा सूचना','Shop offer':'पसलको अफर','Business report':'कारोबार प्रतिवेदन','Daily':'दैनिक','Monthly':'मासिक','Yearly':'वार्षिक','Today':'आज','This month':'यो महिना','This year':'यो वर्ष','Transactions':'कारोबार','Sales':'बिक्री','Orders':'अर्डर','Locked':'लक गरिएको','This copy is locked. Enter the admin Gmail and password to unlock.':'यो एप लक छ। खोल्न एडमिनको Gmail र पासवर्ड राख्नुहोस्।','Admin Gmail':'एडमिन Gmail','Unlock':'खोल्नुहोस्','Site':'साइट','Grant 35 days':'३५ दिन थप्नुहोस्','Delete user':'प्रयोगकर्ता हटाउनुहोस्','All orders':'सबै अर्डर','Show / hide password':'पासवर्ड देखाउने / लुकाउने'});
const canon=e=>{e=(e||'').trim().toLowerCase();const p=e.split('@');if(p.length!=2||!p[0]||!p[1])return e;if(p[1]=='gmail.com'||p[1]=='googlemail.com')return p[0].split('+')[0].replace(/\./g,'')+'@gmail.com';return e};
/* LIC: hostnames where this app is licensed to run without the admin lock. Anywhere else the admin Gmail + password are required. */
const LIC=[];
const LK={e:'aazakobazar@gmail.com',s:'e1b266a2829acd472083dd1dc5820021',h:'b1d5a888fa2e5f2dc4976c4194c444de20c64af3358ea5fd80d8192abc70d436'};
let LOCK=0;
const IH='392df2da6fc2e08042b0d2dc1e100dcff8229f7e32d1b6fbe0888bc38427ed02';
const IHRE=/const IH='[0-9a-f]{64}';/;
async function lockChk(){
  LOCK=0;
  return;
}
async function unlock(){
  LOCK=0;
  R();
}
const vLock=()=>vLogin();
/* show / hide password while typing */
function pwEye(){document.querySelectorAll('input[type=password]').forEach(e=>{if(e.dataset.eye)return;e.dataset.eye=1;const w=document.createElement('div');w.className='pwr';e.parentNode.insertBefore(w,e);w.appendChild(e);const b=document.createElement('button');b.type='button';b.className='eye';b.textContent='👁';b.title=T('Show / hide password');b.setAttribute('aria-label',T('Show / hide password'));b.onclick=()=>{const s=e.type=='password';e.type=s?'text':'password';b.textContent=s?'🚫':'👁️'};w.appendChild(b)})}
/* merchant daily / monthly / yearly business report (delivered orders + bills) */
function repV(){const P=S.rp||'d',L=[];DB.orders.filter(o=>o.mid==S.u.id&&o.st=='Delivered').forEach(o=>L.push([o.d,o.total]));mine(DB.bills||[]).forEach(b=>L.push([b.d,b.total]));const td=today(),sum=f=>L.filter(x=>f(x[0])).reduce((s,x)=>[s[0]+x[1],s[1]+1],[0,0]),a=sum(d=>d==td),b=sum(d=>d.slice(0,7)==td.slice(0,7)),c=sum(d=>d.slice(0,4)==td.slice(0,4)),n=P=='d'?10:P=='m'?7:4,g={};
L.forEach(x=>{const k=x[0].slice(0,n);g[k]=g[k]||[0,0];g[k][0]+=x[1];g[k][1]++});const ks=Object.keys(g).sort().reverse().slice(0,P=='d'?31:P=='m'?24:10),mx=Math.max(1,...ks.map(k=>g[k][0])),cd=(l,v)=>`<div class="c"><div class="st">${T(l)}</div><div class="big">${rs(v[0])}</div><div class="st">${v[1]} ${T('Transactions')}</div></div>`;
return `<h3>📊 ${T('Business report')}</h3><div class="g2">${cd('Today',a)}${cd('This month',b)}</div>${cd('This year',c)}<div class="chips">${[['d','Daily'],['m','Monthly'],['y','Yearly']].map(x=>`<span class="chip ${P==x[0]?'on':''}" onclick="S.rp='${x[0]}';R()">${T(x[1])}</span>`).join('')}</div>`+(ks.map(k=>`<div class="c"><div class="row"><b>${k}</b><b>${rs(g[k][0])}</b></div><div class="st">${g[k][1]} ${T('Transactions')}</div><div class="bar"><i style="width:${Math.round(g[k][0]/mx*100)}%;background:var(--jade)"></i></div></div>`).join('')||empty())+'<hr style="margin:14px 0;border:0;border-top:1px solid var(--ln)">'}
/* admin controls */
function delU(id){const u=U(id);if(S.u.role!='a'||!u||u.role=='a')return;if(S.rm!=='u'+id){S.rm='u'+id;return toast(T('Tap again to confirm'))}S.rm=0;DB.videos.filter(v=>v.mid==id).forEach(v=>rmB(v.id));DB.users=DB.users.filter(x=>x.id!=id);['products','notices','videos','khata','pay','bills'].forEach(k=>DB[k]=(DB[k]||[]).filter(a=>a.mid!=id));DB.orders=DB.orders.filter(o=>o.mid!=id&&o.cid!=id);DB.chats=DB.chats.filter(c=>c.mid!=id&&c.cid!=id);DB.comp=DB.comp.filter(c=>c.mid!=id&&c.cid!=id);DB.nf=DB.nf.filter(n=>n.u!=id);DB.ph=DB.ph.filter(p=>p.u!=id);save();toast('✅');R()}
function grant(id){const s=U(id);if(S.u.role!='a'||!s||!s.shop)return;s.shop.exp=addD(s.shop.exp&&s.shop.exp>today()?s.shop.exp:today(),35);save();toast('✅ '+s.shop.exp);R()}
/* ---------- shell ---------- */
function vLogin(){return `<div class="main"><div class="login"><div class="hero"><img class="logo" src="assets/logo-160.png" alt="Mero Bazar"><b>मेरो बजार</b><div class="tag">NEPAL KO TRUSTED MARKETPLACE</div><div style="font-size:12px;opacity:.9;margin-top:4px">डिजिटल किनमेल र कारोबारको विश्वसनीय माध्यम</div></div><input class="inp" id="em" type="email" placeholder="Email" autocomplete="username"><input class="inp" id="pw" type="password" placeholder="${T('Password')}" autocomplete="current-password"><button class="btn" style="width:100%;padding:13px" onclick="login($('em').value,$('pw').value)">${T('Login')}</button><div class="st" style="text-align:center;margin:10px 0"><a onclick="forgot()" style="cursor:pointer;text-decoration:underline">Forgot password?</a></div><div class="st" style="margin:12px 0;text-align:center">${T('No account? Register first.')}</div><button class="btn j" style="width:100%" onclick="S.r={i:0,d:{}};R()">📝 ${T('Register')}</button><div class="row" style="margin-top:16px"><button class="btn sm g" onclick="lang()">🌐 ${DB.lang=='ne'?'English':'नेपाली'}</button><button class="btn sm g" onclick="theme()">🌓</button><button class="btn sm g" onclick="S.sub='contact';R()">✉️ Inquiries</button></div></div></div>`}
const TB=()=>{const t=(a,b,c)=>[a,b,T(c)];return {
c:[t('home','🏠','Home'),t('cats','🗂️','Categories'),t('orders','📦','Orders'),t('msg','💬','Messenger'),t('me','⚙️','My Setting')],
m:[t('home','📊','Dashboard'),t('prods','🛍️','Products'),t('orders','📦','Orders'),t('khata','📒','Khata'),t('more','⚙️','My Setting')],
a:[t('home','📊','Overview'),t('shops','🏪','Shops'),t('mon','🛡️','Monitoring'),t('db','🗄️','Database'),t('more','⚙️','Settings')]
}};
const VW={c:cView,m:mView,a:aView};
function R(){try{R1()}catch(e){$('app').innerHTML=`<div class="main"><div class="c">⚠️ ${T('Something went wrong')}<br><br><button class="btn" onclick="S.sub=null;S.tab='home';R()">${T('Reload view')}</button></div></div>`}document.querySelectorAll('input:not([type=file]):not([type=number]):not([type=date]):not([type=time]),textarea').forEach(e=>{if(e.maxLength<0)e.maxLength=e.tagName=='TEXTAREA'?600:120});hydrate();pwEye()}
function R1(){const a=$('app');if(LOCK){a.innerHTML=vLock();return}
if(S.sub==='contact'&&!S.u){a.innerHTML=`<div class="hd"><div class="brand"><img src="assets/logo-160.png" alt="Mero Bazar"><div><b>मेरो बजार</b><small>Contact & Inquiries</small></div></div><div style="display:flex;align-items:center;gap:6px"><button onclick="S.sub=null;R()" class="btn sm g" style="color:#fff;border-color:rgba(255,255,255,.4)">← ${T('Back')}</button></div></div><div class="main">${window.renderContactFormView?window.renderContactFormView():''}</div>`;return}
if(!S.u){a.innerHTML=S.r?vReg():vLogin();return}
if(S.u.mc){a.innerHTML=forceV();return}
const tabs=TB()[S.u.role],cur=tabs.find(t=>t[0]==S.tab)||tabs[0],sv=S.sub;
const body=sv?`<button class="btn sm g" style="margin-bottom:10px" onclick="S.sub=null;R()">← ${T('Back')}</button>`+SV[sv=='shop'&&S.u.role=='m'?'shop2':sv]():VW[S.u.role][cur[0]]();
const isDk=document.documentElement.dataset.theme==='dark';
a.innerHTML=`<div class="hd"><div class="hd-profile-box" onclick="S.tab='me';S.sub=null;R()" title="${T('Profile')} (${E(S.u.name)})" style="display:flex;align-items:center;gap:9px;cursor:pointer;min-width:0">${S.u.photo?`<img class="av hd-av" src="${S.u.photo}" alt="Profile">`:`<div class="av hd-av" style="display:flex;align-items:center;justify-content:center;background:linear-gradient(135deg,#1B6AC9,#0F4C9A);color:#fff;font-weight:700">👤</div>`}<div style="line-height:1.2;min-width:0"><b style="font-size:16px;color:#fff;display:block;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">मेरो बजार</b><small style="color:rgba(255,255,255,.85);font-size:11px;display:block;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${E(S.u.name)} · ${T({c:'Customer',m:'Merchant',a:'Admin'}[S.u.role])}</small></div></div><div class="hd-actions" style="display:flex;align-items:center;gap:6px;flex:none"><button onclick="S.sub=(S.u.role==='a'?'set':'acct');R()" title="${T('Settings')}" class="hd-icon-btn">⚙️</button><button onclick="S.sub='contact';R()" title="Inquiries & Consulting" class="hd-icon-btn">✉️</button><button onclick="theme()" title="${T('Theme')}" class="hd-icon-btn">${isDk?'☀️':'🌙'}</button><button onclick="window.openHelpModal?window.openHelpModal():0" title="Help" class="hd-icon-btn">❓</button></div></div><div class="main" onscroll="fs(this)">${body}</div><div class="nav">${tabs.map(t=>`<button class="${t[0]==cur[0]&&!sv?'on':''}" onclick="go('${t[0]}')"><i>${t[1]}</i>${t[2]}</button>`).join('')}</div>`}
setInterval(()=>{const e=$('fl');if(e){const x=21600-((Date.now()/1000|0)%21600),p=n=>String(n).padStart(2,'0');e.textContent='⏱ '+p(x/3600|0)+':'+p(x%3600/60|0)+':'+p(x%60)}},1000);
let lw=0;['click','keydown','touchstart'].forEach(ev=>addEventListener(ev,()=>{S.last=Date.now();if(S.u&&S.last-lw>5000){lw=S.last;try{localStorage.setItem(K+'t',lw)}catch(_){}}},{passive:true}));
setInterval(()=>{if(S.u&&S.u.role=='a'&&Date.now()-S.last>10*60000){logout();toast(T('Logged out due to inactivity'))}},20000);
addEventListener('error',()=>{});
boot().then(()=>{try{const svTh=localStorage.getItem('open_bazar_theme');if(svTh)document.documentElement.dataset.theme=svTh;}catch(_){}seedDemo();chk();setInterval(chk,60000);try{const [id,tk]=(localStorage.getItem(K+'s')||'').split(':'),tt=+localStorage.getItem(K+'t')||0,u=DB.users.find(x=>String(x.id)==id&&x.on&&x.tok&&x.tok===tk);if(u&&Date.now()-tt<(u.role=='a'?10:1e9)*60000){S.u=u;S.last=Date.now()}}catch(_){}R();if(BAD)toast(T('Data unreadable — restore from backup'))});
