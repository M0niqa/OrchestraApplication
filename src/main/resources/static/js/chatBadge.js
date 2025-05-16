document.addEventListener("DOMContentLoaded", function () {
    const userDataEl = document.getElementById("userData");
    if (!userDataEl) return;

    const socket = new SockJS('/websocket');
    const stompClient = Stomp.over(socket);

    const chatBadge = document.getElementById('chatBadge');

    stompClient.connect({}, function () {
        stompClient.subscribe('/user/queue/messages', function () {
            if (chatBadge) {
                chatBadge.classList.remove('d-none');
            }
        });
    });
});