<%@ page language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
    <h2>WebMarket - Login</h2>
    <form method="post" action="login">
        <label>Username:</label><br>
        <input type="text" name="username" required><br><br>
        <label>Password:</label><br>
        <input type="password" name="password" required><br><br>
        <button type="submit">Login</button>
    </form>

    <%
        String error = request.getParameter("error");
        if ("true".equals(error)) {
    %>
        <p style="color:red;">Invalid username or password!</p>
    <%
        }
    %>
</body>
</html>

