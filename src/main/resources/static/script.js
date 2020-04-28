var stompClient = null;
var searchedMovies = 'searchMovies';
var searchMoviesUrl = '/movies/search?term=';
var specMoviesUrl = '/movies/spec'
var specFieldParam = '?field=';
var specOperationParam = '&operation=';
var specValueParam = '&value=';

window.onload = function() {
    console.log('window onload function');
    connect();
};

window.onclose = function () {
    disconnect();
};

function getDataFromUrlToTable(url, table) {
    getJSON(url,
        function (err, data) {
            if (err !== null) {
                alert('Movies are not loaded: ' + err);
            } else {
                updateTable(data, table);
            }
        });
}

function connect() {
    var socket = new SockJS('/movies');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/recalculate', function(recalculateMessage) {
            this.refreshPage();
            console.log(recalculateMessage);
        });
    });
}

function refreshPage() {
    location.reload();
}

function searchMovies() {
    var term = document.getElementById("searchText").value;
    console.log('Searching movies with term: ' + term);
    getDataFromUrlToTable(searchMoviesUrl+term, searchedMovies);
}

function specSearchMovies() {
    var field = document.getElementById("specField").value;
    var operation = document.getElementById("specOperation").value;
    var value = document.getElementById("specValue").value;
    console.log('Searching movies with specification: ' + field + operation + value);
    var url = specMoviesUrl + specFieldParam + field + specOperationParam + operation + specValueParam + value;
    getDataFromUrlToTable(url, searchedMovies);
}

function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function updateTable(movies, tableId) {
    // / EXTRACT VALUE FOR HTML HEADER.
    var col = [];
    for (var i = 0; i < movies.length; i++) {
        for (var key in movies[i]) {
            if (col.indexOf(key) === -1) {
                col.push(key);
            }
        }
    }

    // CREATE DYNAMIC TABLE.
    var table = document.createElement("table");

    // CREATE HTML TABLE HEADER ROW USING THE EXTRACTED HEADERS ABOVE.
    var tr = table.insertRow(-1);                   // TABLE ROW.
    for (var l = 0; l < col.length; l++) {
        var th = document.createElement("th");      // TABLE HEADER.
        th.innerHTML = col[l];
        tr.appendChild(th);
    }

    // ADD JSON DATA TO THE TABLE AS ROWS.
    for (var k = 0; k < movies.length; k++) {
        tr = table.insertRow(-1);
        for (var j = 0; j < col.length; j++) {
            var tabCell = tr.insertCell(-1);
            tabCell.innerHTML = movies[k][col[j]];
        }
    }

    // FINALLY ADD THE NEWLY CREATED TABLE WITH JSON DATA TO A CONTAINER.
    var divContainer = document.getElementById(tableId);
    if (divContainer.childElementCount > 0) {
        divContainer.removeAttribute('table');
    }
    divContainer.innerHTML = "";
    divContainer.appendChild(table);
}

var getJSON = function(url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.responseType = 'json';
    xhr.onload = function() {
        var status = xhr.status;
        if (status === 200) {
            callback(null, xhr.response);
        } else {
            callback(status, xhr.response);
        }
    };
    xhr.send();
};