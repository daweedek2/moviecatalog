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

$(document).ready(function () {
    toggleFields();
    $("#configType").change(function () {
        toggleFields();
    });

});

function toggleFields() {
    var configType = $("#configType option:selected").text();
    console.log(configType);
    if (configType === "visible_movies")
        $("#visible_movies").show();
    else
        $("#visible_movies").hide();
}

function updateRuntimeConfig() {
    var configName = $("#configType option:selected").text();
    switch (configName) {
        case "visible_movies":
            updateVisibleMoviesConfig(configName);
            break;
        default:
            break;
    }
}

function updateVisibleMoviesConfig(configName) {
    var configNameJson = '"configName":"' + configName + '"';
    var limit = $("#limit").val();
    var optionsJson = '"options": {"limit":"' + limit + '"}';
    var finalJson = '{' + configNameJson + ',' + optionsJson + '}';
    sendPostToUpdateRuntimeConfig(finalJson);
}

function sendPostToUpdateRuntimeConfig(data) {
    $.ajax({
        contentType: 'application/json',
        data: data,
        dataType: 'json',
        success: function(data){
            console.log("Update request sent successfully");
        },
        error: function(){
            console.error("Update request failed.");
        },
        processData: false,
        type: 'POST',
        url: '/runtimeConfig/update'
    });

}

function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
};