<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Golf</title>
<script type="text/javascript">
var ctx = "<%=request.getContextPath()%>";
</script>
</head>
<body>
Test Form Post!
<form action="<%=request.getContextPath()%>/version/addPerson" method="post">
	personId:<input type="text" name="personId"><br>
	personName:<input type="text" name="personName"><br>
	age:<input type="text" name="age"><br>
	<button type="submit" accesskey="">提交</button>
</form>
</body>
</html>