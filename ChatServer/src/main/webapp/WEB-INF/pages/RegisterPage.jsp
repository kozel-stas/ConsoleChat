<%@ page import="model.AnswerCode" %>
<%@ page import="java.util.EnumMap" %><%--
  Created by IntelliJ IDEA.
  User: a4tec
  Date: 10.03.2018
  Time: 10:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Регистрация</title>
</head>
<body>
<div class="form">
    <%=request.getAttribute("answerCode")%>
    <h1>Вход в систему</h1><br>
    <form method="post" action="/registerFilter">
        <input type="text" required placeholder="login" name="login"><br>
        <p><input type="radio" name="typeUser" value="Agent"> Агент </p>
        <p><input type="radio" name="typeUser" value="Client"> Клиент </p>
        <input class="button" type="submit" value="Войти">
    </form>

</div>
</body>
</html>
