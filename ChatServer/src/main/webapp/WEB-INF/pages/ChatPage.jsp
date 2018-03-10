<%--
  Created by IntelliJ IDEA.
  User: a4tec
  Date: 10.03.2018
  Time: 10:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<textarea name="textArea" id="textArea" cols="30" rows="10">

</textarea>
<form name="publish">
    <input type="text" name="message">
    <input type="submit" value="Отправить">
</form>

<script>
    var socket = new WebSocket("ws://" + location.host + "/websocket");

    // отправить сообщение из формы publish
    document.forms.publish.onsubmit = function () {
        var outgoingMessage = this.message.value;
        socket.send(outgoingMessage);
        this.message.value = "";
        return false;
    };

    // обработчик входящих сообщений
    socket.onmessage = function (event) {
        var incomingMessage = event.data;
        document.getElementById('textArea').value += incomingMessage + '\n';
    };


</script>
</body>
</html>
