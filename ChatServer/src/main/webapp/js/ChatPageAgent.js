$(document).ready(function () {
    var socket = new WebSocket("ws://" + location.host + "/websocket");
    var arrClient = [];

    Array.prototype.remove = function (value) {
        var idx = this.indexOf(value);
        if (idx != -1) {
            return this.splice(idx, 1);
        }
        return false;
    };

    function sendMessage(name,msg) {
        var message={
            name:name,
            msg:msg
        };
        socket.send(JSON.stringify(message));
    }

    function addNewTab(name) {
        var part1 = "<li ";
        var part2 = "<div id=\"panel" + name + "\" class=\"tab-pane fade";
        if (arrClient.length === 0) {
            part1 += " class=\"active\" ";
            part2 += " active in";
        }
        part1 += "><a href=\"#panel" + name + "\" data-toggle=\"tab\" id=\"li" + name + "\">Клиент " + name + "</a></li>";
        part2 += "\">\n" +
            "                <div class=\"divTextArea\">\n" +
            "                    <textarea class='textArea' id=\"textArea" + name + "\" readonly></textarea>\n" +
            "                </div>\n" +
            "                <div class=\"divInput\">\n" +
            "                    <input type=\"text\" class='message' id=\"message" + name + "\" placeholder=\"Type your message here...\">\n" +
            "                </div>\n" +
            "                <div class=\"buttonSend\">\n" +
            "                    <button class='send' id=\"send" + name + "\"> Отправить</button>\n" +
            "                </div>\n" +
            "            </div>";
        $(".nav-tabs").append(part1);
        $(".tab-content").append(part2);
        arrClient[arrClient.length] = name;
    }

    function deleteTab(name) {
        $("#li" + name).remove();
        $("#panel" + name).remove();
        arrClient.remove(name);
    }

    socket.onmessage = function (event) {
        var incomingMessage = event.data;
        var answer = JSON.parse(incomingMessage);
        var name = answer.name;
        var isAgent = answer.isAgent;
        var message = answer.message;
        var serverinfo = answer.serverinfo;
        switch (serverinfo) {
            case 'MESSAGE':
                if (isAgent == true)
                    document.getElementById('textArea'+name).value += "Агент ";
                else document.getElementById('textArea'+name).value += "Клиент ";
                document.getElementById('textArea'+name).value += name + ":    ";
                document.getElementById('textArea'+name).value += message + '\n';
                break;
            case 'CLIENT_LEAVE':
                var answerMessage = 'Клиент отключился';
                document.getElementById('textArea'+name).value += "Server:    ";
                document.getElementById('textArea'+name).value += answerMessage + '\n';
                deleteTab(name);
                break;
            case 'NEW_CLIENT':
                addNewTab(name)
                var answerMessage = 'Вы подключены к клиенту';
                document.getElementById('textArea'+name).value += answerMessage + " ";
                document.getElementById('textArea'+name).value += name + '\n';
                break;
        }
    };


    $(".main").on('click',".send", function () {
        var curTab = $("#myTab").find("li.active a");
        var name=curTab.attr("id");
        name=name.substring(2);
        var outgoingMessage = $("#message"+name).val();
        if (outgoingMessage != "") {
            sendMessage(name,outgoingMessage);
            $("#message"+name).val("");
            document.getElementById('textArea'+name).value += "Ты:    " + outgoingMessage + '\n';
        }
    });

    $(".main").keypress(function (e) {
        if (e.which == 13) {
            var curTab = $("#myTab").find("li.active a");
            var name=curTab.attr("id");
            name=name.substring(2);
            var outgoingMessage = $("#message"+name).val();
            if (outgoingMessage != "") {
                sendMessage(name,outgoingMessage);
                $("#message"+name).val("");
                document.getElementById('textArea'+name).value += "Ты:    " + outgoingMessage + '\n';
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

});