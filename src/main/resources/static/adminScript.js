var stompClient = null;

window.onload = function() {
    console.log('window onload function');
    connect();
};

window.onclose = function () {
    disconnect();
};

function refreshPage() {
    location.reload();
}

function connect() {
    var socket = new SockJS('/movies');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/admin', function(adminMessage) {
            refreshPage();
            console.log(adminMessage);
        });
    });
}

function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
};