$(document).ready(function () {
    var socket = new WebSocket("ws://" + location.host + "/websocket");


    $("#send").bind('click', function () {
        var outgoingMessage = $("#message").val();
        if (outgoingMessage != "") {
            socket.send(outgoingMessage);
            $("#message").val("");
            $("#textArea").append("Ты:    " + outgoingMessage + '\n');
        }
    });

    $("#message").keypress(function (e) {
        if (e.which == 13) {
            var outgoingMessage = $("#message").val();
            if (outgoingMessage != "") {
                //socket.send(outgoingMessage);
                $("#message").val("");
                $("#textArea").append("Ты:    " + outgoingMessage + '\n');
            }
        }
    });

    socket.onclose = function (event) {
        if (event.wasClean) {
            alert('Соедиение закрыто');
        } else {
            alert('Сервер закрыл соединение');
        }
    };

    socket.onerror = function (error) {
        alert('Ошибка ' + error.message);
    };

    socket.onmessage = function (event) {
        var incomingMessage = event.data;
        var answer = JSON.parse(incomingMessage);
        var name = answer.name;
        var isAgent = answer.isAgent;
        var message = answer.message;
        var serverinfo = answer.serverinfo;
         document.getElementById('textArea').value+=" "+name+" "+isAgent+" "+message+" "+serverinfo;
        if (serverinfo == 'MESSAGE') {
            if (isAgent == true)
                document.getElementById('textArea').value += "Агент";
            else document.getElementById('textArea').value += name + "Клиент";
            document.getElementById('textArea').value += name + ":    ";
            document.getElementById('textArea').value += message + '\n';
        }
    };
});