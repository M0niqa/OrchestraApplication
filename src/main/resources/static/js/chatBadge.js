document.addEventListener("DOMContentLoaded", function () {
    const userDataEl = document.getElementById("userData");
    if (!userDataEl) return;

    const userId = userDataEl.dataset.userId;
    const socket = new SockJS('/websocket');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/messages/' + userId, function () {
            showChatBadge();
        });
    });

    function showChatBadge() {
        const badge = document.getElementById('chatBadge');
        if (badge) badge.classList.remove('d-none');
    }
});