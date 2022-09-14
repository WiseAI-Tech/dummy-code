<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<script>
	/* Set Header Title */
	changeTitle("<spring:message code='admin.nav.error' />");
</script>
</head>
<body>
	<div class="container p-0">
		<div class="text-center">
			<p class="lead text-gray-800 mb-1">
				<spring:message code='gt.page_error.title' />
			</p>
			<p class="text-gray-500 mb-0">
				<spring:message code='gt.page_error.description' />
			</p>
			<a href="#!/dashboard">&larr; <spring:message
					code='gt.page_error.back_to_dashboard' /></a>
		</div>
	</div>
</body>
</html>
