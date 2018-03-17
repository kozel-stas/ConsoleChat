<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Cache-control" content="NO-CACHE">
    <meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
    <link href="/css/ChatPage.css" rel="stylesheet" type="text/css"/>
    <title>ChatRoom</title>
    <script type="text/javascript" src="/js/jquery-3.3.1.js"></script>
</head>
<body>
<header>
    <a class="reg" href="/signOut">Sign Out</a>
</header>
<div class="pain">
    <div class="main">
        <div class="divTextArea">
            <textarea id="textArea" readonly></textarea>
        </div>
        <div class="divInput">
            <input type="text" id="message" placeholder="Type your message here...">
        </div>
        <div class="buttonSend">
            <button id="send"> Отправить</button>
            <%if("Client".equals(request.getSession().getAttribute("typeUser"))){%>
            <button id="leave"> Покинуть</button>
            <%}%>
        </div>
    </div>
</div>
<footer>
    TouchSoft
</footer>
<script type="text/javascript" src="../js/ChatPage.js"></script>
</body>
</html>