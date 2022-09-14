<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta
	content="width=device-width, initial-scale=1, maximum-scale=5, user-scalable=yes"
	name="viewport">
<meta name="Description" content="<spring:message code="app.name" />">
<title><spring:message code="app.name" /></title>

<link rel="shortcut icon" type="image/ico"
	href="${pageContext.request.contextPath}/admin/meta/images/main_logo.png" />
<link rel="icon"
	href="${pageContext.request.contextPath}/admin/meta/images/main_logo.png"
	type="image/x-icon" />
<link rel="apple-touch-icon"
	href="${pageContext.request.contextPath}/admin/meta/images/main_logo.png">

<!-- CSS -->
<!-- JQuery -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/plugins/jquery-ui-1.12.1/jquery-ui.min.css">
<!-- Bootstrap -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/plugins/bootstrap-4.6.2/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/plugins/bootstrap-select-1.13.9/css/bootstrap-select.min.css">
<!-- Theme - SB Admin 2 -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/plugins/sb-admin-2/css/sb-admin-2.min.css">
<!-- Font Awesome -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/plugins/fontawesome-6.2.0/css/all.min.css">
<!-- DataTable -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/plugins/dataTables-1.10.20/dataTables.bootstrap4.min.css">
<!-- Angular X-Editable -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/plugins/angular-xeditable-0.10.2/css/xeditable.min.css">
<!-- ChartJS -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/plugins/chartjs-2.9.3/Chart.min.css">
<!-- UICropper -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/plugins/ui-cropper/ui-cropper.min.css">
<!-- Default CSS -->
<style>
@font-face {
	font-family: 'Roboto', sans-serif;
}

html, body {
	height: 100%;
	width: 100%;
	margin: 0;
	padding: 0;
	top: 0;
	left: 0;
}
</style>

<!-- JS -->
<!-- JQuery -->
<script
	src="${pageContext.request.contextPath}/plugins/jquery-3.4.1/jquery.min.js"></script>
<script
	src="${pageContext.request.contextPath}/plugins/jquery-ui-1.12.1/jquery-ui.min.js"></script>
<!-- Bootstrap -->
<script
	src="${pageContext.request.contextPath}/plugins/bootstrap-4.6.2/js/bootstrap.bundle.min.js"></script>
<script
	src="${pageContext.request.contextPath}/plugins/bootstrap-select-1.13.9/js/bootstrap-select.min.js"></script>
<!-- Angular -->
<script
	src="${pageContext.request.contextPath}/plugins/angular-1.8.2/angular.min.js"></script>
<script
	src="${pageContext.request.contextPath}/plugins/angular-1.8.2/angular-route.min.js"></script>
<script
	src="${pageContext.request.contextPath}/plugins/angular-ui/ui-sortable-0.19.0/sortable.min.js"></script>
<script
	src="${pageContext.request.contextPath}/plugins/angular-filter-0.5.17/angular-filter.min.js"></script>
<script
	src="${pageContext.request.contextPath}/plugins/angular-ui/ui-bootstrap/ui-bootstrap-3.0.6.min.js"></script>
<!-- DataTable -->
<script
	src="${pageContext.request.contextPath}/plugins/dataTables-1.10.20/jquery.dataTables.min.js"></script>
<script
	src="${pageContext.request.contextPath}/plugins/dataTables-1.10.20/dataTables.bootstrap4.min.js"></script>
<!-- Angular X-Editable -->
<script
	src="${pageContext.request.contextPath}/plugins/angular-xeditable-0.10.2/js/xeditable.min.js"></script>
<!-- MomentJS -->
<script
	src="${pageContext.request.contextPath}/plugins/momentjs-2.24.0/moment.min.js"></script>
<!-- ChartJS -->
<script
	src="${pageContext.request.contextPath}/plugins/chartjs-2.9.3/Chart.min.js"></script>
<!-- Angular ChartJS -->
<script
	src="${pageContext.request.contextPath}/plugins/angular-chartjs-1.1.1/angular-chart.min.js"></script>
<!-- Blob-Util -->
<script
	src="${pageContext.request.contextPath}/plugins/blob-util/blob-util.min.js"></script>
<!-- TinyMCE -->
<script
	src="${pageContext.request.contextPath}/plugins/tinymce-5.7.1-108/tinymce.min.js"></script>
	<script
	src="${pageContext.request.contextPath}/plugins/angular-ui/tinymce/tinymce.min.js"></script>
<!-- UICropper -->
<script src="${pageContext.request.contextPath}/plugins/ui-cropper/ui-cropper.min.js"></script>
<script>
//For Bootstrap Select Picker
$.fn.selectpicker.Constructor.DEFAULTS.noneResultsText = "<spring:message code='gt.bootstrap_select.no_result' />";
$.fn.selectpicker.Constructor.DEFAULTS.noneSelectedText = "<spring:message code='gt.bootstrap_select.none_selected' />";
$.fn.selectpicker.Constructor.DEFAULTS.selectAllText = "<spring:message code='gt.bootstrap_select.select_all' />";
$.fn.selectpicker.Constructor.DEFAULTS.deselectAllText = "<spring:message code='gt.bootstrap_select.deselect_all' />";
</script>

<c:if test="${isAllowPWA}">
	<!-- For PWA -->
	<link rel="manifest"
		href="${pageContext.request.contextPath}/admin/manifest.json">
	<meta name="theme-color" content="#3B55EB" />
</c:if>
</head>