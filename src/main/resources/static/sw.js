// /sw.js (오리진 루트에 위치)
self.addEventListener('push', event => {
    try {
        const data = event.data ? event.data.json() : {};
        const title = data.title || '알림';
        const body  = data.body  || '';
        const url   = data.url   || '/';

        event.waitUntil(
            self.registration.showNotification(title, {
                body,
                data: { url },
                // 아래 아이콘/배지는 선택
                // icon: '/icon-192.png',
                // badge: '/badge-72.png'
            })
        );
        console.log('Push received:', data); // 디버깅용
    } catch (e) {
        // payload 파싱 실패 대비: 최소한이라도 띄워보자
        event.waitUntil(
            self.registration.showNotification('알림', {
                body: '메시지를 불러오지 못했습니다.'
            })
        );
    }
});

self.addEventListener('notificationclick', event => {
    event.notification.close();

    // url에서 eventId 추출
    const url = event.notification.data && event.notification.data.url;
    let eventId = null;
    if (url) {
        const u = new URL(url, self.location.origin);
        eventId = u.searchParams.get("eventId");
    }

    if (!eventId) {
        console.error("eventId not found in notification data");
        return;
    }

    // 서버로 복용 완료 요청
    event.waitUntil(
        fetch('/api/meds/alert/complete', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ eventId: Number(eventId) })
        })
            .then(res => {
                console.log("복용 완료 응답:", res.status);
                return clients.openWindow('/push-test.html'); // 필요 시 화면 열기
            })
            .catch(err => {
                console.error("복용 완료 호출 실패:", err);
            })
    );
});

// sw.js
self.addEventListener('install', () => self.skipWaiting());
self.addEventListener('activate', (event) => event.waitUntil(self.clients.claim()));

self.addEventListener('message', (event) => {
    if (event.data && event.data.type === 'notify') {
        const { title, body } = event.data;
        event.waitUntil(
            self.registration.showNotification(title || 'SW notify', {
                body: body || 'SW가 알림을 띄웠습니다',
                requireInteraction: true
            })
        );
    }
});