<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<script>
	/* Set Header Title */
	changeTitle("<spring:message code='admin.nav.session_expired' />");
</script>
</head>
<body>
	<div class="container p-0">
		<div class="text-center">
			<p class="lead text-gray-800 mt-5">
				<i class="fas fa-dizzy fa-4x"></i>
			</p>
			<p class="lead text-gray-800 mb-5">
				<spring:message code='gt.page_session_expired.title' />
			</p>
			<p class="text-gray-500 mb-0">
				<spring:message code='gt.page_session_expired.description' />
			</p>
			<a href="${pageContext.request.contextPath}/">&larr; <spring:message
					code='gt.page_session_expired.back_to_login' /></a>
		</div>
	</div>
</body>
</html>
