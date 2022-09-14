var cacheVersion = '1.0';
var cacheName = 'usdtech.omniu';
var dataCacheName = cacheName + "$" + cacheVersion;
var filesToCache = [
	//Pages
	'/member/member_login',
	//Fonts
	'/plugins/fontawesome-5.10.0/webfonts/fa-solid-900.woff2',
	//Images
	'/member/meta/images/main_logo.png',
	'/member/image/background/login_background.jpg',
	//Scripts
	'/plugins/bootstrap-4.3.1/css/bootstrap.min.css',
	'/plugins/fontawesome-5.10.0/css/all.min.css',
	'/plugins/jquery-3.4.1/jquery.min.js',
	'/plugins/bootstrap-4.3.1/js/bootstrap.bundle.min.js',
	'/plugins/angular-1.7.8/angular.min.js',
	'/plugins/angular-1.7.8/angular-route.min.js'
];

self.addEventListener('install', event => {
  console.log('[SW] Install');
  event.waitUntil(
	caches.open(dataCacheName).then(cache => cache.addAll(filesToCache))
  );
});

self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys => Promise.all(
      keys.map(key => {
        if (dataCacheName != key) {
          return caches.delete(key);
        }
      })
    )).then(() => {
      console.log('[SW] Ready To Listen');
    })
  );
});

self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request).then(function(response) {
      return response || fetch(event.request);
    })
  );
});

self.skipWaiting();