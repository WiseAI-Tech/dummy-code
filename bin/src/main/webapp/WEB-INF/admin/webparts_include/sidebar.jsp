<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<ul class="navbar-nav side-bar-bg sidebar sidebar-dark accordion"
	id="accordionSidebar" style="z-index: 2;">
	<!-- Sidebar - Brand -->
	<a
		class="sidebar-brand d-flex align-items-center justify-content-center"
		href="#">
		<div class="sidebar-brand-icon">
			<img height="30"
				src="${pageContext.request.contextPath}/admin/meta/images/main_logo.png" />
		</div>
		<div class="sidebar-brand-text mx-3">
			<spring:message code="app.name" />
		</div>
	</a>

	<!-- Main Menu Access Load -->
	<div ng-repeat="mainMenu in mainMenuData">
		<hr class="sidebar-divider my-0">

		<li ng-if="mainMenu.sub_menu.length == 0" class="nav-item mb-0"
			ng-class="{active:currentActivePage==mainMenu.nav_name}"><a
			class="nav-link" ng-href="{{mainMenu.redirect_link}}"> <ng-bind-html
					ng-bind-html="mainMenu.icon_html | custom_trusted_html">
				</ng-bind-html> <span>{{mainMenu.menu_name}}</span></a></li>

		<li ng-if="mainMenu.sub_menu.length > 0"
			class="nav-header nav-item mb-0"><a
			class="nav-link cursor-pointer collapsed"
			ng-href="{{mainMenu.redirect_link}}" data-toggle="collapse"
			data-target="#coll_{{mainMenu.nav_name}}"> <ng-bind-html
					ng-bind-html="mainMenu.icon_html | custom_trusted_html">
				</ng-bind-html> <span>{{mainMenu.menu_name}}</span>
		</a>
			<div ng-if="mainMenu.sub_menu.length > 0"
				id="coll_{{mainMenu.nav_name}}" class="collapse"
				data-parent="#accordionSidebar">
				<div class="bg-white py-2 collapse-inner rounded">
					<a ng-repeat="subMenu in mainMenu.sub_menu" class="collapse-item"
						ng-class="{active:currentActivePage==subMenu.nav_name}"
						ng-href="{{subMenu.redirect_link}}"><ng-bind-html
							ng-bind-html="subMenu.icon_html | custom_trusted_html">
						</ng-bind-html> <span>{{subMenu.menu_name}}</span></a>
				</div>
			</div></li>
	</div>

	<hr class="sidebar-divider my-0">
	<!-- Nav Item - LogOut -->
	<li class="nav-item"><a class="nav-link" href=""
		onclick="confirmLogout()"> <i class="fas fa-sign-out-alt fa-fw"></i> <span><spring:message
					code="admin.nav.logout" /></span>
	</a></li>

	<hr class="sidebar-divider d-none d-md-block">
	<!-- Sidebar Toggler (Sidebar) -->
	<div class="text-center d-none d-md-inline">
		<button class="rounded-circle border-0" id="sidebarToggle"></button>
	</div>
</ul>