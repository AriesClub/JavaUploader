<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>500 Error</title>
<%
    final Throwable servletException = (Throwable) request.getAttribute("javax.servlet.error.exception");
    //final Class exceptionType = (Class) request.getAttribute("javax.servlet.error.exception_type");
    final String exceptionMessage = (String) request.getAttribute("javax.servlet.error.message");
    final String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
    final Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    final String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
%>
</head>
<body>
<%=exceptionMessage %>
</body>
</html>