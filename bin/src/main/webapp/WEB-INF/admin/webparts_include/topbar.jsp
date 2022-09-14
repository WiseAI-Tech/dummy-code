<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<nav
	class="navbar navbar-expand navbar-light bg-white topbar static-top shadow">

	<!-- Sidebar Toggle (Topbar) -->
	<button id="sidebarToggleTop"
		class="btn btn-link d-md-none rounded-circle">
		<i class="fa fa-bars"></i>
	</button>

	<button id="backButton" class="btn btn-link p-0 m-0 mr-2"
		ng-show="getPageBreadCrumb().length > 0"
		ng-click="backToPreviousPageBreadCrumb()">
		<i class="far fa-caret-square-left fa-2x"></i>
	</button>

	<span><b id="headerTitle"></b></span>


	<div class="dropdown ml-auto">
		<button class="btn btn-sm btn-secondary dropdown-toggle" type="button"
			id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true"
			aria-expanded="false">
			<spring:message code="lang.current.ln" />
		</button>
		<div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
			<a class="dropdown-item" style="cursor: pointer;"
				onclick="changeLocale('<spring:message
				code='lang.english.sn' />')"><spring:message
					code="lang.english.ln" /></a> <a class="dropdown-item"
				style="cursor: pointer;"
				onclick="changeLocale('<spring:message
				code='lang.chinese.sn' />')"><spring:message
					code="lang.chinese.ln" /></a>
		</div>
	</div>

	<!-- Topbar Navbar -->
	<ul class="navbar-nav">
		<li class="nav-item dropdown no-arrow"><a
			class="nav-link dropdown-toggle" href="" id="userDropdown"
			role="button" data-toggle="dropdown" aria-haspopup="true"
			aria-expanded="false"> <i class="fas fa-user"></i> <span
				class="ml-2 d-none d-lg-inline text-gray-600 small"><c:out
						value="${displayName}" /></span>
		</a>
			<div
				class="dropdown-menu dropdown-menu-right shadow animated--grow-in"
				aria-labelledby="userDropdown">
				<a class="dropdown-item disabled" href="">Hi, <c:out
						value="${displayName}" /></a> <a class="dropdown-item"
					href="#!/my_profile"> <i
					class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i> <spring:message
						code="gt.profile_menu.profile" />
				</a>
				<!--<a class="dropdown-item" href="#!/my_settings"> <i
					class="fas fa-cogs fa-sm fa-fw mr-2 text-gray-400"></i> <spring:message
						code="gt.profile_menu.settings" />
				</a>-->
				<div class="dropdown-divider"></div>
				<a class="dropdown-item" href="" onclick="confirmLogout()"> <i
					class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i> <spring:message
						code="gt.profile_menu.logout" />
				</a>
			</div></li>
	</ul>
</nav>