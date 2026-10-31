// Lightweight server for serving Mero Bazar web client & APK on port 3000
const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const ASSETS_DIR = path.join(__dirname, 'app/src/main/assets/open-bazar');
const APK_PATH = path.join(__dirname, 'app/build/outputs/apk/debug/app-debug.apk');
const ALT_APK_PATH = path.join(__dirname, '.build-outputs/app-debug.apk');

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.webp': 'image/webp',
  '.ico': 'image/x-icon',
  '.wav': 'audio/wav',
  '.mp3': 'audio/mpeg',
  '.ogg': 'audio/ogg',
  '.apk': 'application/vnd.android.package-archive'
};

const server = http.createServer((req, res) => {
  let reqUrl = decodeURI(req.url.split('?')[0]);

  // Handle direct APK download routes
  if (reqUrl === '/app-debug.apk' || reqUrl === '/download-apk' || reqUrl === '/mero-bazar.apk' || reqUrl === '/apk' || reqUrl === '/download') {
    const targetApk = fs.existsSync(APK_PATH) ? APK_PATH : (fs.existsSync(ALT_APK_PATH) ? ALT_APK_PATH : null);
    if (targetApk) {
      const stat = fs.statSync(targetApk);
      res.writeHead(200, {
        'Content-Type': 'application/vnd.android.package-archive',
        'Content-Length': stat.size,
        'Content-Disposition': 'attachment; filename="mero-bazar.apk"',
        'Cache-Control': 'no-cache'
      });
      fs.createReadStream(targetApk).pipe(res);
      return;
    }
  }

  // Normalize path
  if (reqUrl === '/' || reqUrl === '') {
    reqUrl = '/index.html';
  }

  const cleanReqUrl = reqUrl.replace(/^\/(assets|open-bazar)\//, '/');
  const normalizedUrl = reqUrl.replace(/^\/+/, '');
  const basename = path.basename(reqUrl);

  const candidatePaths = [
    path.join(ASSETS_DIR, normalizedUrl),
    path.join(ASSETS_DIR, cleanReqUrl.replace(/^\/+/, '')),
    path.join(ASSETS_DIR, 'assets', basename),
    path.join(__dirname, 'app/src/main/assets', normalizedUrl),
    path.join(__dirname, 'app/src/main/assets', cleanReqUrl.replace(/^\/+/, '')),
    path.join(__dirname, 'app/src/main/assets/assets', basename),
    path.join(__dirname, 'public', normalizedUrl),
    path.join(__dirname, 'public/assets', basename),
    path.join(__dirname, 'app/src/main/res/drawable', basename)
  ];

  let resolvedPath = null;
  for (const cand of candidatePaths) {
    try {
      if (fs.existsSync(cand) && fs.statSync(cand).isFile()) {
        resolvedPath = cand;
        break;
      }
    } catch (_) {}
  }

  if (!resolvedPath) {
    if (!path.extname(reqUrl)) {
      resolvedPath = path.join(ASSETS_DIR, 'index.html');
    } else {
      res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
      res.end('404 Not Found');
      return;
    }
  }

  const ext = path.extname(resolvedPath).toLowerCase();
  const contentType = MIME_TYPES[ext] || 'application/octet-stream';

  res.writeHead(200, {
    'Content-Type': contentType,
    'Access-Control-Allow-Origin': '*'
  });

  fs.createReadStream(resolvedPath).pipe(res);
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Mero Bazar web server running at http://0.0.0.0:${PORT}/`);
});
