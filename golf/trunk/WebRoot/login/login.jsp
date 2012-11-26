<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>loginFrom</title>
</head>
<body>
<form action="<%=request.getContextPath()%>/version/login" method="post">
	userCode:<input type="text" name="personId"><br>
	passWord:<input type="password" name="personName"><br>
	<button type="submit" accesskey="">登录</button>
</form>
</body>
</html>