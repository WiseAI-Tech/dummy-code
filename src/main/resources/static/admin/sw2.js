var cacheVersion = '1.0';
var cacheName = 'usdtech.omniu';
var dataCacheName = cacheName + "$" + cacheVersion;
var filesToCache = [
	//Pages
	'/member/home',
	'/member/views/dashboard',
	'/member/views/under_development',
	'/member/views/404_not_found',
	'/member/views/error',
	//Fonts
	'/plugins/fontawesome-5.10.0/webfonts/fa-solid-900.woff2',
	//Images
	'/member/meta/images/main_logo.png',
	'/member/image/background/login_background.jpg',
	'/member/image/background/drawer_background.jpg',
	'/member/image/directory/404.png',
	'/member/image/directory/error.png',
	'/member/image/directory/under_development.png',
	//Scripts
	'/plugins/sb-admin-2/css/sb-admin-2.min.css',
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