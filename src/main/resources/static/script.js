var stompClient = null;
var allMovies = 'allMovies';
var topMovies = 'topMovies';
var latestMovies = 'latestMovies';
var searchedMovies = 'searchMovies';
var topMoviesUrl = '/movies/top5';
var latestMoviesUrl = '/movies/latest5';
var allMoviesUrl = '/movies/all';
var searchMoviesUrl = '/movies/search?term=';

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
        stompClient.subscribe('/topic/rating', function(topRatedMoviesMessage) {
            getDataFromUrlToTable(topMoviesUrl, topMovies);
            console.log(topRatedMoviesMessage);
        });
        stompClient.subscribe('/topic/latestMovies', function(latestMoviesMessage) {
            getDataFromUrlToTable(latestMoviesUrl, latestMovies);
            console.log(latestMoviesMessage);
        });
        stompClient.subscribe('/topic/all-movies', function(allMoviesMessage) {
            getDataFromUrlToTable(allMoviesUrl, allMovies);
            console.log(allMoviesMessage);
        });
        stompClient.subscribe('/topic/recalculate', function(recalculateMessage) {
            getDataFromUrlToTable(allMoviesUrl, allMovies);
            getDataFromUrlToTable(topMoviesUrl, topMovies);
            getDataFromUrlToTable(latestMoviesUrl, latestMovies);
            console.log(recalculateMessage);
        });
    });
}

function searchMovies() {
    var term = document.getElementById("searchText").value;
    getDataFromUrlToTable(searchMoviesUrl+term, searchedMovies);
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