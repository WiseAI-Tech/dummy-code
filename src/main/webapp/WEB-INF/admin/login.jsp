<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<% String isAllowPWA = request.getParameter("isAllowPWA"); %>
<!DOCTYPE HTML>
<html lang="en">
<head>
<jsp:include page="webparts_include/import.jsp" />

<!-- Page CSS -->
<style>
body {
	background:
		url("${pageContext.request.contextPath}/admin/image/background/login_background.jpg");
	background-repeat: no-repeat;
	background-size: cover;
	background-position: center;
}

.default-text-color {
	color: rgb(255, 255, 255);
}

.default-text-title {
	text-shadow: 2px 2px black;
}

div.login-wrapper {
	max-width: 500px;
}

div.login-container {
	background-color: rgba(0, 0, 0, 0.5);
}

div.login-container hr {
	border: solid 1px rgb(255, 255, 255);
	border-top: 1px;
}

div.footer-wrapper {
	background-color: rgba(0, 0, 0, 0.5);
	font-size: 0.8em;
}
</style>

<script type="text/javascript">
	function showLoading() {
		$('#loadingModal').modal('show');
	}

	function requestNewSecurityCode() {
		$("#security_check_loading").show();
		$("#security_check_image").hide();
		
		$.ajax({
			method: "POST",
			url: "${pageContext.request.contextPath}/admin_api/refresh_security_code",
			data: { }
		}).done(function( data ) {
			setTimeout(function() {
				$("#security_check_image").attr("src", data);
				$("#security_check_loading").hide();
				$("#security_check_image").show();
			}, 500);
		});
	}

	//Block Form Repost
	if (window.history.replaceState) {
		window.history.replaceState( null, null, window.location.href );
	}

	$(document).ready(function() {
		$("#loginID").focus();
	});
</script>
</head>
<body>
	<div class="w-100 h-100 d-flex flex-column default-text-color">
		<div class="dropdown text-right p-2">
			<button class="btn btn-secondary dropdown-toggle" type="button"
				id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true"
				aria-expanded="false">
				<spring:message code="lang.current.ln" />
			</button>
			<div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
				<a class="dropdown-item"
					href="?lang=<spring:message
						code='lang.english.sn' />"><spring:message
						code="lang.english.ln" /></a> <a class="dropdown-item"
					href="?lang=<spring:message
						code='lang.chinese.sn' />"><spring:message
						code="lang.chinese.ln" /></a>
			</div>
		</div>
		<div class="d-flex flex-fill justify-content-center">
			<div class="d-flex flex-row justify-content-center">
				<div class="login-wrapper d-flex flex-column justify-content-center">
					<div class="mb-3 default-text-title text-center">
						<h2>
							<spring:message code="app.name" />
						</h2>
						<br>
						<h5>
							<spring:message code="app.description" />
						</h5>
					</div>

					<div class="login-container m-3 p-3 text-center">
						<h4 class="text-capitalize">
							<spring:message code="gt.page_login.login" />
						</h4>
						<hr>
						<div>
							<form class="m-0" method="POST"
								action="${pageContext.request.contextPath}/admin/login"
								autocomplete="off" onsubmit="showLoading()">
								<div class="form-group text-left">
									<input type="text" class="form-control" id="loginID"
										name="loginID"
										placeholder="<spring:message code="gt.page_login.login_id"/>"
										aria-label="<spring:message code="gt.page_login.login_id"/>"
										autofocus required>
								</div>
								<div class="form-group text-left">
									<input type="password" class="form-control" id="loginPassword"
										name="loginPassword"
										placeholder="<spring:message code="gt.page_login.login_password"/>"
										aria-label="<spring:message code="gt.page_login.login_password"/>"
										required>
								</div>
								<c:if
									test="${sessionScope.admin_session_security_code_image != null && sessionScope.admin_session_security_code_image != ''}">
									<div>
										<img id="security_check_image" width="200"
											src="data:image/png;base64,${sessionScope.admin_session_security_code_image}"
											alt="<spring:message code="gt.page_login.security_code"/>" />
										<div id="security_check_loading" class="spinner-border"
											role="status" style="display: none;"></div>
										<button type="button" class="btn text-success"
											onclick="requestNewSecurityCode()" tabindex="-1">
											<span><i class="fas fa-sync"></i></span>
										</button>
										<div class="form-group text-left mt-1">
											<input type="text" class="form-control" id="securityCode"
												name="securityCode"
												placeholder="<spring:message code="gt.page_login.security_code"/>"
												aria-label="<spring:message code="gt.page_login.security_code"/>"
												required>
										</div>
									</div>
								</c:if>
								<c:if
									test="${sessionScope.admin_session_login_err != null && sessionScope.admin_session_login_err != ''}">
									<div class="text-danger">
										<c:out value="${sessionScope.admin_session_login_err}"
											escapeXml="false" />
									</div>
									<c:remove var="admin_session_login_err" scope="session" />
								</c:if>
								<button type="submit" class="btn btn-dark">
									<spring:message code="gt.page_login.login" />
								</button>
							</form>
						</div>
					</div>
				</div>
			</div>

		</div>
		<div class="footer-wrapper pt-1 pb-1 text-center">
			<span><b><spring:message code="app.copyright_footer" /></b></span>
		</div>
	</div>

	<div id="loadingModal" class="modal" tabindex="-1" role="dialog"
		data-backdrop="static">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-body">
					<div class="d-flex justify-content-center">
						<div class="spinner-border text-warning m-4" role="status">
						</div>
					</div>
					<div class="d-flex justify-content-center">
						<span><b><spring:message code="gt.loading.signing_in" />...</b></span>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
<script>
$.ajax({
	method: "POST",
	url: "${pageContext.request.contextPath}/admin_api/get_pwa_status",
	data: { }
}).done(function( data ) {
	if (!data.isPWA) {
		if ((typeof(caches) !== "undefined")) {
			caches.keys().then(function(keys) {
			    for (let key of keys)
			        caches.delete(key);
			});
			navigator.serviceWorker.getRegistrations().then(function(registrations) {
				for(let registration of registrations)
					registration.unregister();
			});
		} else {
			console.log('[SW] Not Available in HTTP');
		}
	}
});

if ('serviceWorker' in navigator) {
	if (<%=isAllowPWA%>) {
		if ((typeof(caches) !== "undefined")) {
			navigator.serviceWorker.register('sw.js')
			.then(reg => console.log('[SW] Registered'))
			.catch(err => console.log('[SW] Registration Error: ', err));
		} else {
			console.log('[SW] Not Available in HTTP');
		}
	} else {
		if ((typeof(caches) !== "undefined")) {
			console.log('[SW] Disabled');
			
			caches.keys().then(function(keys) {
			    for (let key of keys)
			        caches.delete(key);
			});
			navigator.serviceWorker.getRegistrations().then(function(registrations) {
				for(let registration of registrations)
					registration.unregister();
			});
		} else {
			console.log('[SW] Not Available in HTTP');
		}
	}
}
</script>
</html>
