/**
 * Open Bazar Native Android Bridge & Universal Downloader / Sharer
 * Ensures 100% reliable real downloading and real sharing across all Android devices:
 * - Real Downloads: Saves directly to device "Downloads" folder with notifications and media indexing.
 * - Real Sharing: Opens Android Native Share Sheet (WhatsApp, Facebook, Viber, Telegram, Gmail, Bluetooth).
 * - Automatic Interception: Seamlessly upgrades standard <a> download clicks and navigator.share calls.
 */
(function() {
  'use strict';

  function blobToBase64(blob) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => resolve(reader.result);
      reader.onerror = reject;
      reader.readAsDataURL(blob);
    });
  }

  function resolveMime(filename, fallback) {
    if (!filename) return fallback || 'application/octet-stream';
    const ext = filename.split('.').pop().toLowerCase();
    switch (ext) {
      case 'png': return 'image/png';
      case 'jpg':
      case 'jpeg': return 'image/jpeg';
      case 'webp': return 'image/webp';
      case 'svg': return 'image/svg+xml';
      case 'pdf': return 'application/pdf';
      case 'csv': return 'text/csv';
      case 'txt': return 'text/plain';
      case 'json': return 'application/json';
      case 'abk': return 'text/plain';
      default: return fallback || 'application/octet-stream';
    }
  }

  /**
   * Universal Real Download Handler
   */
  window.obDownload = async function(dataOrBlobOrUrl, filename, mimeType) {
    try {
      const safeName = filename || ('open_bazar_' + Date.now());
      let base64 = '';
      let resolvedMime = mimeType || resolveMime(safeName, 'application/octet-stream');

      if (typeof dataOrBlobOrUrl === 'string') {
        if (dataOrBlobOrUrl.startsWith('data:')) {
          base64 = dataOrBlobOrUrl;
          const match = dataOrBlobOrUrl.match(/^data:([^;]+);/);
          if (match && !mimeType) resolvedMime = match[1];
        } else if (dataOrBlobOrUrl.startsWith('blob:')) {
          const resp = await fetch(dataOrBlobOrUrl);
          const blob = await resp.blob();
          base64 = await blobToBase64(blob);
          if (blob.type) resolvedMime = blob.type;
        } else {
          // Plain text / CSV data string
          base64 = 'data:' + resolvedMime + ';base64,' + btoa(unescape(encodeURIComponent(dataOrBlobOrUrl)));
        }
      } else if (dataOrBlobOrUrl instanceof Blob) {
        base64 = await blobToBase64(dataOrBlobOrUrl);
        if (dataOrBlobOrUrl.type) resolvedMime = dataOrBlobOrUrl.type;
      }

      // Check for Native AndroidBridge
      if (window.AndroidBridge && typeof window.AndroidBridge.downloadFile === 'function') {
        window.AndroidBridge.downloadFile(base64, safeName, resolvedMime);
        return true;
      }

      // Web fallback for browser environment
      const a = document.createElement('a');
      a.href = base64;
      a.download = safeName;
      document.body.appendChild(a);
      a.click();
      setTimeout(() => {
        try { document.body.removeChild(a); } catch (_) {}
      }, 500);

      if (typeof window.toast === 'function') {
        window.toast('📥 डाउनलोड सुरु भयो: ' + safeName);
      }
      return true;
    } catch (err) {
      console.error('obDownload error:', err);
      if (typeof window.toast === 'function') {
        window.toast('डाउनलोडमा समस्या आयो');
      }
      return false;
    }
  };

  /**
   * Universal Real Share Handler
   */
  window.obShare = async function(options) {
    try {
      options = options || {};
      const title = options.title || 'Mero Bazar';
      const text = options.text || '';
      const safeName = options.filename || ('share_' + Date.now() + '.png');
      let base64 = '';
      let resolvedMime = options.mimeType || resolveMime(safeName, 'image/png');

      if (options.dataOrBlobOrUrl) {
        const item = options.dataOrBlobOrUrl;
        if (typeof item === 'string') {
          if (item.startsWith('data:')) {
            base64 = item;
            const match = item.match(/^data:([^;]+);/);
            if (match && !options.mimeType) resolvedMime = match[1];
          } else if (item.startsWith('blob:')) {
            const resp = await fetch(item);
            const blob = await resp.blob();
            base64 = await blobToBase64(blob);
            if (blob.type) resolvedMime = blob.type;
          }
        } else if (item instanceof Blob) {
          base64 = await blobToBase64(item);
          if (item.type) resolvedMime = item.type;
        }
      }

      // Check for Native AndroidBridge
      if (window.AndroidBridge) {
        if (base64 && typeof window.AndroidBridge.shareFile === 'function') {
          window.AndroidBridge.shareFile(base64, safeName, resolvedMime, title + (text ? ('\n' + text) : ''));
          return true;
        }
        if (text && typeof window.AndroidBridge.shareText === 'function') {
          window.AndroidBridge.shareText(text, title);
          return true;
        }
      }

      // Standard Web Share API
      if (navigator.share) {
        const sharePayload = { title: title, text: text };
        if (base64) {
          try {
            const resp = await fetch(base64);
            const blob = await resp.blob();
            const file = new File([blob], safeName, { type: resolvedMime });
            if (navigator.canShare && navigator.canShare({ files: [file] })) {
              sharePayload.files = [file];
            }
          } catch (_) {}
        }
        await navigator.share(sharePayload);
        return true;
      }

      // Fallback: Copy to clipboard
      if (text && navigator.clipboard && navigator.clipboard.writeText) {
        await navigator.clipboard.writeText(text);
        if (typeof window.toast === 'function') {
          window.toast('📋 सेयर टेक्स्ट क्लिपबोर्डमा कपी भयो');
        }
        return true;
      }

      return false;
    } catch (err) {
      console.warn('obShare notice:', err);
      return false;
    }
  };

  /**
   * Automatic interceptor for <a download="...">.click()
   * Ensures any dynamically created <a> tags (like bill images, backups, CSVs)
   * cleanly execute real Android native download instead of getting silently swallowed.
   */
  const originalAnchorClick = HTMLAnchorElement.prototype.click;
  HTMLAnchorElement.prototype.click = function() {
    const downloadAttr = this.getAttribute('download');
    const href = this.getAttribute('href') || this.href;

    if (downloadAttr !== null && href && (href.startsWith('blob:') || href.startsWith('data:'))) {
      window.obDownload(href, downloadAttr || 'download');
      return;
    }
    return originalAnchorClick.apply(this, arguments);
  };

  /**
   * Polyfill / Route navigator.share directly to AndroidBridge when available
   */
  if (window.AndroidBridge) {
    const origShare = navigator.share;
    navigator.share = async function(data) {
      if (!data) return;
      if (data.files && data.files.length > 0) {
        const file = data.files[0];
        const base64 = await blobToBase64(file);
        window.AndroidBridge.shareFile(base64, file.name, file.type, data.title || data.text || 'Open Bazar');
        return Promise.resolve();
      }
      if (data.text || data.title) {
        const combined = (data.title ? data.title + '\n\n' : '') + (data.text || '') + (data.url ? '\n' + data.url : '');
        window.AndroidBridge.shareText(combined.trim(), data.title || 'Open Bazar');
        return Promise.resolve();
      }
      if (origShare) {
        return origShare.call(navigator, data);
      }
      return Promise.resolve();
    };
  }

})();
