$(document).ready(function () {
    var socket = new WebSocket("ws://" + location.host + "/websocket");
    var nameAgent;

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

    function sendMessage(name, msg) {
        var message = {
            name: name,
            msg: msg
        };
        socket.send(JSON.stringify(message));
    }

    socket.onmessage = function (event) {
        var incomingMessage = event.data;
        var answer = JSON.parse(incomingMessage);
        var login = answer.login;
        var role = answer.role;
        var message = answer.message;
        var command = answer.command;
        var serverInfo = answer.serverInfo;
        if (command == 'MESSAGE') {
            if (role == 'AGENT')
                document.getElementById('textArea').value += "Агент ";
            else document.getElementById('textArea').value += "Клиент ";
            document.getElementById('textArea').value += login + ":    ";
            document.getElementById('textArea').value += message + '\n';
        }
        switch (serverInfo) {
            case 'FIRST_AGENT_ANSWER_YOU':
                var answerMessage = 'Первый освободившийся агент ответит вам';
                document.getElementById('textArea').value += login + ":    ";
                document.getElementById('textArea').value += answerMessage + '\n';
                break;
            case 'AGENT_LEAVE_WAIT_NEW':
                var answerMessage = 'Агент отключился, первый освободившийся агент ответит вам';
                document.getElementById('textArea').value += login + ":    ";
                document.getElementById('textArea').value += answerMessage + '\n';
                break;
            case 'NO_AGENT_WAIT':
                var answerMessage = 'К сожалению, свободных агентов нет, мы уведовим вас когда вас подключат';
                document.getElementById('textArea').value += login + ":    ";
                document.getElementById('textArea').value += answerMessage + '\n';
                break;
            case 'AGENT_LEAVE':
                var answerMessage = 'Агент отключился';
                document.getElementById('textArea').value += login + ":    ";
                document.getElementById('textArea').value += answerMessage + '\n';
                nameAgent = "";
                break;
            case 'NEW_AGENT':
                var answerMessage = 'К вам подключился агент';
                document.getElementById('textArea').value += answerMessage + " ";
                document.getElementById('textArea').value += login + '\n';
                nameAgent = login;
                break;
            case 'LEAVE_CHAT':
                var answerMessage = 'Вы покинули беседу';
                document.getElementById('textArea').value += login + ":    ";
                document.getElementById('textArea').value += answerMessage + '\n';
                break;
            case 'DONT_HAVE_CHAT':
                var answerMessage = 'У вас нет активной беседы';
                document.getElementById('textArea').value += login + ":    ";
                document.getElementById('textArea').value += answerMessage + '\n';
                break;
        }
    };

    $("#send").bind('click', function () {
        var outgoingMessage = $("#message").val();
        if (outgoingMessage != "") {
            sendMessage(nameAgent, outgoingMessage);
            $("#message").val("");
            document.getElementById('textArea').value += "Ты:    " + outgoingMessage + '\n';
        }
    });

    $("#leave").bind('click', function () {
        socket.send('LEAVE');
    });

    $("#message").keypress(function (e) {
        if (e.which == 13) {
            var outgoingMessage = $("#message").val();
            if (outgoingMessage != "") {
                sendMessage(nameAgent, outgoingMessage);
                $("#message").val("");
                document.getElementById('textArea').value += "Ты:    " + outgoingMessage + '\n';
            }
        }
    });
});