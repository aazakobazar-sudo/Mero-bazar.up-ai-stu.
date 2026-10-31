/**
 * Mero Bazar D3.js Metrics & Analytics Dashboard Engine
 * Visualizes key market performance metrics:
 * - User Growth Trends (Active Shoppers, Verified Merchants & Farmers)
 * - Transaction Volume Trends (Daily & Monthly GMV in रु / NPR)
 * - Order Velocity & Fulfillment Rates
 * - Regional Distribution across Nepal's 7 Provinces
 * - Payment Gateway Breakdown (eSewa, Khalti, Fonepay QR, Digital Khata)
 */
(function(window) {
  'use strict';

  let currentMetric = 'gmv';
  let currentRange = '30d';
  let isNepali = false;

  // Format NPR Currency
  function formatNpr(val, nep) {
    const num = Math.round(Number(val) || 0);
    if (nep) {
      if (num >= 10000000) return 'रु. ' + (num / 10000000).toFixed(2) + ' करोड';
      if (num >= 100000) return 'रु. ' + (num / 100000).toFixed(2) + ' लाख';
      if (num >= 1000) return 'रु. ' + (num / 1000).toFixed(1) + ' हजार';
      return 'रु. ' + num.toLocaleString('en-IN');
    }
    if (num >= 10000000) return 'NPR ' + (num / 10000000).toFixed(2) + ' Cr';
    if (num >= 100000) return 'NPR ' + (num / 100000).toFixed(2) + ' Lakh';
    if (num >= 1000) return 'NPR ' + (num / 1000).toFixed(1) + 'k';
    return 'NPR ' + num.toLocaleString('en-IN');
  }

  // Devanagari numerals converter
  function toDevanagariNum(num) {
    const str = String(num);
    const nepDigits = ['०', '१', '२', '३', '४', '५', '६', '७', '८', '९'];
    return str.replace(/[0-9]/g, w => nepDigits[Number(w)]);
  }

  // Generate Realistic High-Fidelity Time-Series Data
  function generateTimeSeriesData(range) {
    const pointsCount = range === '7d' ? 7 : (range === '30d' ? 30 : (range === '90d' ? 18 : 24));
    const now = new Date();
    const data = [];

    // Base figures for Mero Bazar
    let baseUsers = 112000;
    let baseGmv = 3100000;
    let baseOrders = 12400;

    for (let i = pointsCount - 1; i >= 0; i--) {
      const d = new Date(now);
      if (range === '7d' || range === '30d') {
        d.setDate(d.getDate() - i);
      } else if (range === '90d') {
        d.setDate(d.getDate() - (i * 5));
      } else {
        d.setDate(d.getDate() - (i * 15));
      }

      const progress = (pointsCount - i) / pointsCount;
      // Growth curve with weekly seasonality & weekend/festive peaks
      const dayOfWeek = d.getDay();
      const weekendBoost = (dayOfWeek === 5 || dayOfWeek === 6) ? 1.22 : 0.96;
      const festiveSpike = (i === Math.floor(pointsCount * 0.4) || i === Math.floor(pointsCount * 0.8)) ? 1.35 : 1.0;

      const gmvVal = Math.round((baseGmv * (0.65 + progress * 0.75) * weekendBoost * festiveSpike) / (range === '7d' ? 5 : (range === '30d' ? 24 : 8)));
      const usersVal = Math.round(baseUsers + (progress * 38250) + (Math.sin(i) * 350));
      const ordersVal = Math.round((baseOrders * (0.7 + progress * 0.6) * weekendBoost * festiveSpike) / (range === '7d' ? 6 : (range === '30d' ? 22 : 6)));
      const aovVal = Math.round(gmvVal / Math.max(ordersVal, 1));

      const month = d.toLocaleString('en-US', { month: 'short' });
      const day = d.getDate();
      const label = `${month} ${day}`;
      const labelNp = `${d.getMonth() + 1} महिना ${toDevanagariNum(day)}`;

      data.push({
        date: label,
        dateNp: labelNp,
        rawDate: d.toISOString().slice(0, 10),
        gmv: gmvVal,
        users: usersVal,
        orders: ordersVal,
        aov: aovVal
      });
    }

    return data;
  }

  // Provincial Metrics for Nepal's 7 Provinces
  const PROVINCE_METRICS = [
    {
      id: 'bagmati',
      name: 'Bagmati Province',
      nameNp: 'बागमती प्रदेश (काठमाडौँ उपत्यका/चितवन)',
      share: 38.5,
      gmv: 'रू १.८८ करोड',
      gmvVal: 18800000,
      users: '५८,०००+ प्रयोगकर्ता',
      color: '#0F4C9A'
    },
    {
      id: 'koshi',
      name: 'Koshi Province',
      nameNp: 'कोशी प्रदेश (विराटनगर/धरान/इलाम)',
      share: 18.2,
      gmv: 'रू ८८.५ लाख',
      gmvVal: 8850000,
      users: '२८,५००+ प्रयोगकर्ता',
      color: '#F28C0F'
    },
    {
      id: 'lumbini',
      name: 'Lumbini Province',
      nameNp: 'लुम्बिनी प्रदेश (बुटवल/भैरहवा/दाङ)',
      share: 14.8,
      gmv: 'रू ७२.० लाख',
      gmvVal: 7200000,
      users: '२२,४००+ प्रयोगकर्ता',
      color: '#10B981'
    },
    {
      id: 'gandaki',
      name: 'Gandaki Province',
      nameNp: 'गण्डकी प्रदेश (पोखरा/बागलुङ)',
      share: 13.5,
      gmv: 'रू ६५.८ लाख',
      gmvVal: 6580000,
      users: '१९,२००+ प्रयोगकर्ता',
      color: '#06B6D4'
    },
    {
      id: 'madhesh',
      name: 'Madhesh Province',
      nameNp: 'मधेश प्रदेश (जनकपुर/वीरगञ्ज)',
      share: 8.6,
      gmv: 'रू ४१.९ लाख',
      gmvVal: 4190000,
      users: '१३,०००+ प्रयोगकर्ता',
      color: '#8B5CF6'
    },
    {
      id: 'sudurpashchim',
      name: 'Sudurpashchim Province',
      nameNp: 'सुदूरपश्चिम प्रदेश (धनगढी/महेन्द्रनगर)',
      share: 4.2,
      gmv: 'रू २०.४ लाख',
      gmvVal: 2040000,
      users: '६,४००+ प्रयोगकर्ता',
      color: '#F43F5E'
    },
    {
      id: 'karnali',
      name: 'Karnali Province',
      nameNp: 'कर्णाली प्रदेश (सुर्खेत/जुम्ला स्याउ)',
      share: 2.2,
      gmv: 'रू १०.७ लाख',
      gmvVal: 1070000,
      users: '३,५००+ प्रयोगकर्ता',
      color: '#EAB308'
    }
  ];

  // Category Distribution Data
  const CATEGORY_DISTRIBUTION = [
    { nameEn: 'Krishi & Farm Fresh', nameNp: 'कृषि तथा ताजा तरकारी', share: 34, value: 'रू १.६५ करोड', color: '#10B981' },
    { nameEn: 'Groceries & Khadhanna', nameNp: 'किराना तथा खाद्यान्न', share: 26, value: 'रू १.२६ करोड', color: '#F28C0F' },
    { nameEn: 'Electronics & Mobiles', nameNp: 'इलेक्ट्रोनिक्स र ग्याजेट', share: 18, value: 'रू ८७.५ लाख', color: '#0F4C9A' },
    { nameEn: 'Local Handcrafts & Fashion', nameNp: 'फेसन तथा स्थानीय घरेलु', share: 14, value: 'रू ६८.० लाख', color: '#8B5CF6' },
    { nameEn: 'Health, Beauty & Others', nameNp: 'स्वास्थ्य, सौन्दर्य र विविध', share: 8, value: 'रू ३८.९ लाख', color: '#06B6D4' }
  ];

  // Payment Gateway Split Data
  const PAYMENT_GATEWAY_DATA = [
    { nameEn: 'eSewa Digital Wallet', nameNp: 'ई-सेवा डिजिटल वालेट', share: 44, color: '#60BB46' },
    { nameEn: 'Fonepay QR & Bank Transfer', nameNp: 'फोनपे क्युआर तथा मोबाइल बैंकिङ', share: 28, color: '#ED1C24' },
    { nameEn: 'Khalti Mobile Wallet', nameNp: 'खल्ती वालेट', share: 18, color: '#5C2D91' },
    { nameEn: 'Digital Khata / Cash on Delivery', nameNp: 'डिजिटल खाता / क्यास अन डेलिभरी', share: 10, color: '#0F4C9A' }
  ];

  /* ---------------- D3 Chart Rendering ---------------- */

  function renderMainTrendChart(containerId, metric, range, isNep) {
    const container = document.getElementById(containerId);
    if (!container || typeof d3 === 'undefined') return;
    container.innerHTML = '';

    const width = container.clientWidth || 360;
    const height = Math.min(Math.max(width * 0.58, 250), 320);
    const margin = { top: 24, right: 18, bottom: 42, left: metric === 'gmv' ? 64 : 52 };
    const innerWidth = width - margin.left - margin.right;
    const innerHeight = height - margin.top - margin.bottom;

    const rawData = generateTimeSeriesData(range);

    const svg = d3.select(container)
      .append('svg')
      .attr('width', width)
      .attr('height', height)
      .attr('viewBox', `0 0 ${width} ${height}`)
      .attr('style', 'max-width:100%;height:auto;overflow:visible;');

    const g = svg.append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`);

    // Color definitions based on active metric
    let primaryColor = '#F28C0F'; // saffron for GMV
    let gradientStart = '#F28C0F';
    let gradientEnd = '#F28C0F';
    let metricLabel = isNep ? 'कारोबार (GMV)' : 'Transaction Volume';

    if (metric === 'users') {
      primaryColor = '#10B981'; // emerald for Users
      gradientStart = '#10B981';
      metricLabel = isNep ? 'सक्रिय प्रयोगकर्ता' : 'Active Users';
    } else if (metric === 'orders') {
      primaryColor = '#06B6D4'; // cyan for Orders
      gradientStart = '#06B6D4';
      metricLabel = isNep ? 'अर्डर संख्या' : 'Orders Placed';
    } else if (metric === 'aov') {
      primaryColor = '#8B5CF6'; // purple for AOV
      gradientStart = '#8B5CF6';
      metricLabel = isNep ? 'औसत अर्डर रकम (AOV)' : 'Avg Order Value';
    }

    // Gradients
    const defs = svg.append('defs');
    const areaGradId = 'mbAreaGradient_' + metric;
    const areaGradient = defs.append('linearGradient')
      .attr('id', areaGradId)
      .attr('x1', '0%').attr('y1', '0%')
      .attr('x2', '0%').attr('y2', '100%');

    areaGradient.append('stop')
      .attr('offset', '0%')
      .attr('stop-color', gradientStart)
      .attr('stop-opacity', 0.42);

    areaGradient.append('stop')
      .attr('offset', '100%')
      .attr('stop-color', gradientStart)
      .attr('stop-opacity', 0.01);

    // Scales
    const xScale = d3.scalePoint()
      .domain(rawData.map(d => isNep ? d.dateNp : d.date))
      .range([0, innerWidth])
      .padding(0.1);

    const valAccessor = d => d[metric];
    const maxVal = d3.max(rawData, valAccessor) || 100;
    const minVal = d3.min(rawData, valAccessor) || 0;
    const yDomainMin = metric === 'users' ? Math.max(0, minVal * 0.95) : 0;

    const yScale = d3.scaleLinear()
      .domain([yDomainMin, maxVal * 1.15])
      .range([innerHeight, 0])
      .nice();

    // Subtle Gridlines
    g.append('g')
      .attr('class', 'grid')
      .call(d3.axisLeft(yScale)
        .ticks(4)
        .tickSize(-innerWidth)
        .tickFormat('')
      )
      .selectAll('line')
      .attr('stroke', '#1E2D4A')
      .attr('stroke-dasharray', '3,3')
      .attr('opacity', 0.75);

    // X Axis
    const tickInterval = Math.max(1, Math.floor(rawData.length / 5));
    const xAxisTicks = rawData.filter((_, i) => i % tickInterval === 0 || i === rawData.length - 1)
      .map(d => isNep ? d.dateNp : d.date);

    const xAxis = d3.axisBottom(xScale).tickValues(xAxisTicks);

    g.append('g')
      .attr('transform', `translate(0,${innerHeight})`)
      .call(xAxis)
      .selectAll('text')
      .attr('fill', '#94A3B8')
      .attr('font-size', '10.5px')
      .attr('font-weight', '600')
      .attr('dy', '1em');

    // Y Axis
    const yAxis = d3.axisLeft(yScale).ticks(4).tickFormat(d => {
      if (metric === 'gmv' || metric === 'aov') {
        if (d >= 10000000) return (d / 10000000).toFixed(1) + (isNep ? 'क' : 'Cr');
        if (d >= 100000) return (d / 100000).toFixed(1) + (isNep ? 'ला' : 'L');
        if (d >= 1000) return (d / 1000).toFixed(0) + 'k';
        return d;
      }
      if (d >= 1000) return (d / 1000).toFixed(1) + 'k';
      return d;
    });

    g.append('g')
      .call(yAxis)
      .selectAll('text')
      .attr('fill', '#94A3B8')
      .attr('font-size', '10.5px')
      .attr('font-weight', '600');

    // Remove harsh domain borders
    g.selectAll('.domain').attr('stroke', '#1E2D4A');

    // Area Path
    const area = d3.area()
      .x(d => xScale(isNep ? d.dateNp : d.date))
      .y0(innerHeight)
      .y1(d => yScale(valAccessor(d)))
      .curve(d3.curveMonotoneX);

    g.append('path')
      .datum(rawData)
      .attr('fill', `url(#${areaGradId})`)
      .attr('d', area);

    // Line Path
    const line = d3.line()
      .x(d => xScale(isNep ? d.dateNp : d.date))
      .y(d => yScale(valAccessor(d)))
      .curve(d3.curveMonotoneX);

    const path = g.append('path')
      .datum(rawData)
      .attr('fill', 'none')
      .attr('stroke', primaryColor)
      .attr('stroke-width', 2.8)
      .attr('stroke-linecap', 'round')
      .attr('d', line);

    // Entrance Animation
    const totalLength = path.node().getTotalLength();
    path
      .attr('stroke-dasharray', totalLength + ' ' + totalLength)
      .attr('stroke-dashoffset', totalLength)
      .transition()
      .duration(850)
      .ease(d3.easeCubicOut)
      .attr('stroke-dashoffset', 0);

    // Interactive Floating Tooltip
    const tooltip = document.getElementById('d3DashboardTooltip');

    // Dots & Hover Interactivity
    g.selectAll('.dot')
      .data(rawData)
      .enter()
      .append('circle')
      .attr('class', 'dot')
      .attr('cx', d => xScale(isNep ? d.dateNp : d.date))
      .attr('cy', d => yScale(valAccessor(d)))
      .attr('r', 4.5)
      .attr('fill', '#121E34')
      .attr('stroke', primaryColor)
      .attr('stroke-width', 2.2)
      .style('cursor', 'pointer')
      .on('mouseenter touchstart', function(event, d) {
        d3.select(this)
          .transition()
          .duration(150)
          .attr('r', 7.5)
          .attr('fill', primaryColor);

        if (tooltip) {
          let valFormatted = '';
          if (metric === 'gmv' || metric === 'aov') {
            valFormatted = formatNpr(valAccessor(d), isNep);
          } else if (metric === 'users') {
            valFormatted = Number(d.users).toLocaleString('en-IN') + (isNep ? ' प्रयोगकर्ता' : ' Users');
          } else {
            valFormatted = Number(d.orders).toLocaleString('en-IN') + (isNep ? ' अर्डर' : ' Orders');
          }

          tooltip.style.display = 'block';
          tooltip.innerHTML = `
            <div style="font-size:11px;color:#94A3B8;margin-bottom:2px;">${isNep ? d.dateNp : d.date} (${d.rawDate})</div>
            <div style="font-size:14px;font-weight:800;color:${primaryColor};">${valFormatted}</div>
            <div style="font-size:10.5px;color:#CBD5E1;margin-top:3px;">${metricLabel} · <b>+२४.५% वार्षिक वृद्धि</b></div>
          `;

          const cxPos = xScale(isNep ? d.dateNp : d.date) + margin.left;
          const leftBound = Math.min(Math.max(cxPos - 60, 12), width - 140);
          tooltip.style.left = leftBound + 'px';
          tooltip.style.top = Math.max(yScale(valAccessor(d)) - 10, 8) + 'px';
        }
      })
      .on('mouseleave touchend', function() {
        d3.select(this)
          .transition()
          .duration(150)
          .attr('r', 4.5)
          .attr('fill', '#121E34');
        if (tooltip) tooltip.style.display = 'none';
      });
  }

  // Province Bar Chart
  function renderProvinceChart(containerId, isNep) {
    const container = document.getElementById(containerId);
    if (!container || typeof d3 === 'undefined') return;
    container.innerHTML = '';

    const width = container.clientWidth || 360;
    const barHeight = 24;
    const gap = 16;
    const height = PROVINCE_METRICS.length * (barHeight + gap) + 30;

    const svg = d3.select(container)
      .append('svg')
      .attr('width', width)
      .attr('height', height)
      .attr('viewBox', `0 0 ${width} ${height}`);

    const g = svg.append('g').attr('transform', 'translate(10, 15)');
    const maxShare = 45;
    const maxBarW = width - 125;

    PROVINCE_METRICS.forEach((p, i) => {
      const y = i * (barHeight + gap);
      const barW = Math.max((p.share / maxShare) * maxBarW, 20);

      // Label
      g.append('text')
        .attr('x', 0)
        .attr('y', y - 4)
        .attr('fill', '#F1F5F9')
        .attr('font-size', '12px')
        .attr('font-weight', '700')
        .text(isNep ? p.nameNp : p.name);

      // Track background
      g.append('rect')
        .attr('x', 0)
        .attr('y', y)
        .attr('width', maxBarW)
        .attr('height', barHeight)
        .attr('rx', 6)
        .attr('fill', '#1E2D4A')
        .attr('opacity', 0.6);

      // Filled Animated Bar
      g.append('rect')
        .attr('x', 0)
        .attr('y', y)
        .attr('width', 0)
        .attr('height', barHeight)
        .attr('rx', 6)
        .attr('fill', p.color)
        .transition()
        .duration(700)
        .delay(i * 90)
        .attr('width', barW);

      // Value & Share
      g.append('text')
        .attr('x', maxBarW + 10)
        .attr('y', y + 16)
        .attr('fill', '#94A3B8')
        .attr('font-size', '11.5px')
        .attr('font-weight', '750')
        .text(`${p.share}% (${p.gmv})`);
    });
  }

  // Payment Gateway Donut Chart
  function renderPaymentDonut(containerId, isNep) {
    const container = document.getElementById(containerId);
    if (!container || typeof d3 === 'undefined') return;
    container.innerHTML = '';

    const width = container.clientWidth || 360;
    const height = 220;
    const radius = Math.min(width, height) / 2 - 12;

    const svg = d3.select(container)
      .append('svg')
      .attr('width', width)
      .attr('height', height)
      .append('g')
      .attr('transform', `translate(${width / 2},${height / 2})`);

    const pie = d3.pie().value(d => d.share).sort(null);
    const arc = d3.arc().innerRadius(radius * 0.58).outerRadius(radius);
    const hoverArc = d3.arc().innerRadius(radius * 0.55).outerRadius(radius + 5);

    const arcs = svg.selectAll('.arc')
      .data(pie(PAYMENT_GATEWAY_DATA))
      .enter()
      .append('g')
      .attr('class', 'arc');

    arcs.append('path')
      .attr('d', arc)
      .attr('fill', d => d.data.color)
      .attr('stroke', '#0B1220')
      .attr('stroke-width', 2.5)
      .style('cursor', 'pointer')
      .on('mouseenter touchstart', function(event, d) {
        d3.select(this).transition().duration(150).attr('d', hoverArc);
        const centerEl = document.getElementById('donutCenterSummary');
        if (centerEl) {
          centerEl.innerHTML = `
            <div style="font-size:14px;font-weight:800;color:${d.data.color};">${d.data.share}%</div>
            <div style="font-size:11px;color:#CBD5E1;">${isNep ? d.data.nameNp : d.data.nameEn}</div>
          `;
        }
      })
      .on('mouseleave touchend', function() {
        d3.select(this).transition().duration(150).attr('d', arc);
      });

    // Center Label
    svg.append('text')
      .attr('text-anchor', 'middle')
      .attr('dy', '-0.2em')
      .attr('font-size', '13px')
      .attr('font-weight', '800')
      .attr('fill', '#FFFFFF')
      .text(isNep ? 'भुक्तानी' : 'Payments');

    svg.append('text')
      .attr('text-anchor', 'middle')
      .attr('dy', '1.2em')
      .attr('font-size', '10px')
      .attr('fill', '#94A3B8')
      .text(isNep ? 'eSewa / QR' : 'Nepal Split');
  }

  /* ---------------- Public API & Controller ---------------- */

  window.MeroBazarD3Dashboard = {
    init: function(opts) {
      opts = opts || {};
      if (typeof opts.isNepali === 'boolean') isNepali = opts.isNepali;
      if (opts.metric) currentMetric = opts.metric;
      if (opts.range) currentRange = opts.range;

      this.refreshAll();
    },

    setMetric: function(metric) {
      currentMetric = metric;
      renderMainTrendChart('mainTrendSvgContainer', currentMetric, currentRange, isNepali);
      this.updateActivePills();
    },

    setRange: function(range) {
      currentRange = range;
      renderMainTrendChart('mainTrendSvgContainer', currentMetric, currentRange, isNepali);
      this.updateActivePills();
    },

    toggleLanguage: function() {
      isNepali = !isNepali;
      this.refreshAll();
      this.updateLanguageTexts();
      return isNepali;
    },

    refreshAll: function() {
      renderMainTrendChart('mainTrendSvgContainer', currentMetric, currentRange, isNepali);
      renderProvinceChart('provinceBarSvgContainer', isNepali);
      renderPaymentDonut('paymentDonutSvgContainer', isNepali);
      this.updateActivePills();
    },

    updateActivePills: function() {
      // Metric buttons
      document.querySelectorAll('.metric-pill').forEach(btn => {
        const m = btn.getAttribute('data-metric');
        if (m === currentMetric) {
          btn.classList.add('active');
        } else {
          btn.classList.remove('active');
        }
      });

      // Range buttons
      document.querySelectorAll('.range-pill').forEach(btn => {
        const r = btn.getAttribute('data-range');
        if (r === currentRange) {
          btn.classList.add('active');
        } else {
          btn.classList.remove('active');
        }
      });
    },

    updateLanguageTexts: function() {
      document.querySelectorAll('[data-np]').forEach(el => {
        const text = isNepali ? el.getAttribute('data-np') : el.getAttribute('data-en');
        if (text) el.textContent = text;
      });
    },

    // Export Data CSV
    exportCsv: function() {
      const data = generateTimeSeriesData(currentRange);
      let csvContent = "Date,RawDate,GMV_NPR,Users,Orders,AvgOrderValue_NPR\n";
      data.forEach(row => {
        csvContent += `"${row.date}","${row.rawDate}",${row.gmv},${row.users},${row.orders},${row.aov}\n`;
      });

      const filename = `mero_bazar_metrics_${currentMetric}_${currentRange}.csv`;
      if (window.obDownload) {
        window.obDownload(csvContent, filename, 'text/csv');
      } else {
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
      }
    },

    // Export PNG
    exportPng: function() {
      const svg = document.querySelector('#mainTrendSvgContainer svg');
      if (!svg) return;

      const svgData = new XMLSerializer().serializeToString(svg);
      const canvas = document.createElement('canvas');
      const svgSize = svg.getBoundingClientRect();
      canvas.width = svgSize.width * 2;
      canvas.height = svgSize.height * 2;
      const ctx = canvas.getContext('2d');
      ctx.fillStyle = '#0B1220';
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      const img = new Image();
      img.onload = function() {
        ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
        const dataUrl = canvas.toDataURL('image/png');
        const filename = `mero_bazar_d3_chart_${Date.now()}.png`;
        if (window.obDownload) {
          window.obDownload(dataUrl, filename, 'image/png');
        } else {
          const a = document.createElement('a');
          a.href = dataUrl;
          a.download = filename;
          a.click();
        }
      };
      img.src = 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svgData)));
    }
  };

})(window);
