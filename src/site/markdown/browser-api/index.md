## Browser APIs

Flow Viritin exposes several browser APIs as clean Java interfaces so you can
use them directly from server-side code without writing JavaScript.

| Helper | Browser API | Description |
|--------|-------------|-------------|
| [ResizeObserver](resize-observer.html) | [ResizeObserver](https://developer.mozilla.org/en-US/docs/Web/API/ResizeObserver) | Observe component size changes |
| [IntersectionObserver](intersection-observer.html) | [IntersectionObserver](https://developer.mozilla.org/en-US/docs/Web/API/IntersectionObserver) | Detect component visibility in viewport or scroll container |
| Geolocation | [Geolocation API](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API) | Access device location |
| Share | [navigator.share()](https://developer.mozilla.org/en-US/docs/Web/API/Navigator/share) | Native share dialog |
| PageVisibility | [Page Visibility API](https://developer.mozilla.org/en-US/docs/Web/API/Page_Visibility_API) | Detect tab/page visibility changes |
| WebNotification | [Notifications API](https://developer.mozilla.org/en-US/docs/Web/API/Notifications_API) | Browser-native notifications |
| Fullscreen | [Fullscreen API](https://developer.mozilla.org/en-US/docs/Web/API/Fullscreen_API) | Enter/exit fullscreen mode |
| BrowserCookie | `document.cookie` | Cookie handling that works with web sockets |
| WebStorage | `localStorage` / `sessionStorage` | Client-side key-value storage |
| ScreenWakeLock | [Screen Wake Lock API](https://developer.mozilla.org/en-US/docs/Web/API/Screen_Wake_Lock_API) | Prevent screen from dimming |
| Clipboard | [Clipboard API](https://developer.mozilla.org/en-US/docs/Web/API/Clipboard_API) | Read/write system clipboard |
