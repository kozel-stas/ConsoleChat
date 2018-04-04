<%@ page import="model.SupportClasses.AnswerCode" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
    <link href="/css/RegLog.css" rel="stylesheet" type="text/css"/>
    <title>Регистрация агента</title>
</head>
<body>
<header>
</header>
<div class="pain">
    <div class="main">
        <div class="register">
            <h1>Sign up</h1>
            <%
                if (request.getAttribute("answerCode") != null) {
                    AnswerCode answerCode = (AnswerCode) request.getAttribute("answerCode");
                    switch (answerCode) {
                        case NAME_ALREADY_USED:
            %>
            <p>Выбранное имя уже занято</p>
            <%
                            break;
                    }
                }%>
            <form method="post" action="/regLogAgent">
                <input class="inputLine" type="text" required placeholder="Type your login here..." name="login"><br>
                <input type="hidden" name="typeOperation" value="register">
                <input class="button" type="submit" value="Sign up"><br>
            </form>
        </div>
        <div class="login">
            <h1>Sign in</h1>
            <%
                if (request.getAttribute("answerCode") != null) {
                    AnswerCode answerCode = (AnswerCode) request.getAttribute("answerCode");
                    switch (answerCode) {
                        case AGENT_ONLINE_YET:
            %>
            <p>Агент с таким именем уже в сети</p>
            <%
                    break;
                case DONT_HAVE_REGISTER_AGENT:
            %>
            <p>Нет такого зарегистрированного агента</p>
            <%
                            break;
                    }
                }%>
            <form method="post" action="/regLogAgent">
                <input class="inputLine" type="text" required placeholder="Type your login here..." name="login"><br>
                <input type="hidden" name="typeOperation" value="login">
                <input class="button" type="submit" value="Sign in"><br>
            </form>
        </div>
    </div>
</div>
<footer>
    TouchSoft
</footer>
</body>
</html>
