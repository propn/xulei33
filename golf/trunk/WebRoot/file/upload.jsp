<%@page import="java.io.*"%>
<%@page import="com.oreilly.servlet.MultipartRequest"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>upload.jsp</title>
</head>
<body>
<%
//获得根目录的物理路径
String path=getServletContext().getRealPath("/");
String saveDirectory =path+"uploadfile\\";
//每个文件最大5m,最多3个文件,所以...
int maxPostSize =3 * 5 * 1024 * 1024 ;
//response的编码为"gb2312",同时采用缺省的文件名冲突解决策略,实现上传
MultipartRequest multi = new MultipartRequest(request,saveDirectory,maxPostSize,"utf-8");
//用于接收文本字段
String text = multi.getParameter("text");
//把获得的文件名放在容器中
Enumeration files = multi.getFileNames();
while (files.hasMoreElements()) {
       String name = (String)files.nextElement();
       File f = multi.getFile(name);
       if(f!=null){
         String fileName = multi.getFilesystemName(name);
  		 //在这里进行相应的操作，如存入数据库等
         out.println("上传的文件:"+fileName);
         out.println("<br>");
       }
}
out.print(text);
%>
</body>
</html>