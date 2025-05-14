document.addEventListener("DOMContentLoaded", function () {
    const chatDataDiv = document.getElementById('chatData');
    if (!chatDataDiv) return;

    const receiverId = chatDataDiv.dataset.receiverId;
    const senderId = chatDataDiv.dataset.senderId;

    let stompClient = null;

    function connect() {
        const socket = new SockJS('/websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, () => {
            stompClient.subscribe('/topic/messages/' + senderId, (message) => {
                const msg = JSON.parse(message.body);
                if (String(msg.senderId) === String(receiverId) || String(msg.receiverId) === String(receiverId)) {
                    showMessage(msg);
                } else {
                    const badge = document.querySelector(`[data-user-id="${msg.senderId}"] .unread-badge`);
                    if (badge) badge.classList.remove('d-none');
                }
            });
        });
    }

    window.sendMessage = function () {
        const messageContent = document.getElementById("message").value;
        if (!receiverId || !messageContent) return;

        const message = { senderId, receiverId, messageContent };
        stompClient.send("/app/chat", {}, JSON.stringify(message));
        document.getElementById("message").value = "";
    };

    window.changeReceiver = function (newReceiverId) {
        if (newReceiverId) {
            window.location.href = '/chat/' + newReceiverId;
        }
    };

    function showMessage(message) {
        const messagesDiv = document.getElementById("messages");
        const messageElement = document.createElement("div");
        messageElement.className = "message " + (String(message.senderId) === String(senderId) ? "sender-message" : "receiver-message");
        const timestamp = new Date(message.timestamp).toLocaleString();

        messageElement.innerHTML = `
        <div>
            <div class="text-muted small mt-1 ms-1 timestamp-small">${timestamp}</div>
            <span class="badge ${String(message.senderId) === String(senderId) ? 'bg-primary' : 'bg-secondary'}">
                ${message.messageContent}
            </span>
        </div>
    `;
        messagesDiv.appendChild(messageElement);
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
    }

    connect();

    if (receiverId) {
        const badge = document.querySelector(`[data-user-id="${receiverId}"] .unread-badge`);
        if (badge) badge.classList.add('d-none');
    }

    const messagesDiv = document.getElementById("messages");
    if (messagesDiv) {
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
    }
});