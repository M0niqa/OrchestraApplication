document.addEventListener("DOMContentLoaded", function () {
    const userDataEl = document.getElementById("userData");
    if (!userDataEl) return;

    const userId = userDataEl.dataset.userId;
    const socket = new SockJS('/websocket');
    const stompClient = Stomp.over(socket);

    const chatBadge = document.getElementById('chatBadge');

    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/messages/' + userId, function () {
            if (chatBadge) {
                chatBadge.classList.remove('d-none');
            }
        });
    });
});