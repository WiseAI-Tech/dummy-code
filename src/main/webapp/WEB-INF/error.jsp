<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=2.0, shrink-to-fit=no, user-scalable=yes">
<meta name="Description" content="Error">
<title>4 Fingers - Error</title>

<link rel="shortcut icon" type="image/ico" href="favicon.ico" />
<link rel="apple-touch-icon" href="favicon.png" />

<!-- CSS -->
<!-- Bootstrap -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/plugins/bootstrap-4.3.1/css/bootstrap.min.css">
<!-- Theme - SB Admin 2 -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/plugins/sb-admin-2/css/sb-admin-2.min.css">
<!-- Font Awesome -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/plugins/fontawesome-5.10.0/css/all.min.css">
</head>
<body>
	<div class="container p-0">
		<div class="text-center">
			<div class="error mx-auto" data-text="${requestScope.status}">${requestScope.status}</div>
			<p class="lead text-gray-800 mb-5">${requestScope.error}</p>
			<p class="text-gray-500 mb-0">
				<spring:message code='gt.error.general' />
			</p>
			<a href="/">&larr; <spring:message code='gt.button.back_to_main' /></a>
		</div>
	</div>
</body>
</html>