<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<style>
/*Custom Modal Style*/
div.modal-content.limit-modal-height {
	max-height: 90vh;
}

div.limit-modal-content-height {
	max-height: 30vh;
	overflow-y: auto;
}
/*Custom Input Switch Style*/
.custom-control-input.custom-on-off-switch ~.custom-control-label::before
	{
	color: #FFFFFF;
	border-color: #8C1E14;
	background-color: #E74A3B;
}

.custom-switch .custom-control-label.custom-on-off-switch-label::after {
	background-color: #FFFFFF;
}

.custom-control-input:checked ~.custom-control-label::before {
	color: #FFFFFF;
	border-color: #167B2D;
	background-color: #28A745;
}

.custom-switch .custom-control-input:checked ~.custom-control-label.custom-on-off-switch-label::after
	{
	background-color: #FFFFFF;
}
</style>
</head>
<body>
	<div class="mt-2">
		<div class="d-flex flex-row justify-content-end mb-2">
			<button class="btn btn-sm btn-primary"
				ng-click="showRoleModal('create')" ng-if="'${actionAccessData}'[10-1]=='1'">
				<i class="far fa-plus-square"></i>&nbsp;
				<spring:message code='admin.role_management.create' />
			</button>
		</div>
		<table id="roleListTable"
			class="table table-striped table-bordered dataTable"
			style="min-width: 100%; width: auto;"></table>
	</div>

	<div class="modal fade" id="roleModal" tabindex="-1" role="dialog"
		aria-labelledby="pageModal" aria-hidden="true" data-backdrop="static">
		<div class="modal-dialog modal-lg modal-dialog-centered"
			role="document">
			<div class="modal-content limit-modal-height">
				<div class="modal-header">
					<h5 class="modal-title" id="dialogModalTitle">
						<span ng-if="roleModalType=='create'"><spring:message
								code='admin.role_management.create_role' /></span> <span
							ng-if="roleModalType=='update'"><spring:message
								code='admin.role_management.update_role' /></span>
					</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<form name="inputForm" class="was-validated" novalidate>
						<div class="input-group mb-3">
							<div class="input-group-prepend">
								<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
										code='admin.role_management.role_name' /><span
									class="text-danger">*</span></span>
							</div>
							<input id="roleModal_role_name" type="text" class="form-control"
								ng-model="roleName"
								placeholder="<spring:message
									code='gt.input_field_required' />"
								required>
						</div>
						<div class="input-group">
							<div class="input-group-prepend">
								<label class="input-group-text" for="roleModal_role_status"><spring:message
										code='admin.role_management.role_status' /><span
									class="text-danger">*</span></label>
							</div>
							<select class="custom-select" id="roleModal_role_status"
								ng-model="roleStatusID"
								ng-options="roleStatus.id as roleStatus.status_name for roleStatus in roleStatusList"
								required>
								<option class="d-none" value="">--
									<spring:message code='gt.input_field.please_select' />--
								</option>
							</select>
						</div>
						<div class="mb-3">
							<span class="text-danger">*<spring:message
									code='gt.input_field_required' /></span>
						</div>
					</form>
					<nav class="nav nav-pills nav-justified mb-3">
						<a class="nav-item nav-link" href=""
							ng-class="{active: roleAccessType=='page'}"
							ng-click="roleAccessType='page'"><spring:message
								code='admin.role_management.page_access' /></a> <a
							class="nav-item nav-link" href=""
							ng-class="{active: roleAccessType=='api'}"
							ng-click="roleAccessType='api'"><spring:message
								code='admin.role_management.api_access' /></a> <a
							class="nav-item nav-link" href=""
							ng-class="{active: roleAccessType=='menu'}"
							ng-click="roleAccessType='menu'"><spring:message
								code='admin.role_management.menu_access' /></a> <a
							class="nav-item nav-link" href=""
							ng-class="{active: roleAccessType=='action'}"
							ng-click="roleAccessType='action'"><spring:message
								code='admin.role_management.action_access' /></a>
					</nav>
					<div class="limit-modal-content-height"
						ng-if="roleAccessType=='page'">
						<table class="table">
							<thead class="thead-dark">
								<tr>
									<th scope="col"><spring:message
											code='admin.role_management.access_name' /></th>
									<th scope="col"><spring:message
											code='admin.role_management.access' /></th>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="pageAccess in roleAccessData.page_access"
									ng-hide="pageAccess.is_hidden">
									<td>{{pageAccess.page_desc}}</td>
									<td><div class="custom-control custom-switch">
											<input type="checkbox"
												class="custom-control-input custom-on-off-switch"
												ng-attr-id="{{'page-access-switch-' + $index}}"
												ng-model="pageAccess.flag"> <label
												class="custom-control-label custom-on-off-switch-label"
												for="{{'page-access-switch-' + $index}}"></label>
										</div></td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="limit-modal-content-height"
						ng-if="roleAccessType=='api'">
						<table class="table">
							<thead class="thead-dark">
								<tr>
									<th scope="col"><spring:message
											code='admin.role_management.access_name' /></th>
									<th scope="col"><spring:message
											code='admin.role_management.access' /></th>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="apiAccess in roleAccessData.api_access"
									ng-hide="apiAccess.is_hidden">
									<td>{{apiAccess.api_desc}}</td>
									<td><div class="custom-control custom-switch">
											<input type="checkbox"
												class="custom-control-input custom-on-off-switch"
												ng-attr-id="{{'api-access-switch-' + $index}}"
												ng-model="apiAccess.flag"> <label
												class="custom-control-label custom-on-off-switch-label"
												for="{{'api-access-switch-' + $index}}"></label>
										</div></td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="limit-modal-content-height"
						ng-if="roleAccessType=='menu'">
						<ul class="list-group" ng-model="roleAccessData.menu_access">
							<li class="list-group-item"
								ng-repeat="mainMenu in roleAccessData.menu_access"
								ng-hide="mainMenu.is_hidden">
								<div class="d-flex flex-row">
									<div
										class="flex-fill d-flex flex-column justify-content-center">
										<span><ng-bind-html class="mr-2"
												ng-bind-html="mainMenu.icon_html | custom_trusted_html">
											</ng-bind-html>{{mainMenu.menu_name}}</span>
									</div>
									<div class="d-flex flex-row">
										<div class="custom-control custom-switch">
											<input type="checkbox"
												class="custom-control-input custom-on-off-switch"
												id="main-menu-enabled-{{$index}}" ng-model="mainMenu.flag">
											<label
												class="custom-control-label custom-on-off-switch-label"
												for="main-menu-enabled-{{$index}}"></label>
										</div>
									</div>
								</div>
								<ul ng-if="mainMenu.sub_menu.length > 0" class="list-group mt-2"
									ng-model="mainMenu.sub_menu">
									<li class="list-group-item"
										ng-repeat="subMenu in mainMenu.sub_menu"
										ng-hide="subMenu.is_hidden">
										<div class="d-flex flex-row">
											<div
												class="flex-fill d-flex flex-column justify-content-center">
												<span><ng-bind-html class="mr-2"
														ng-bind-html="subMenu.icon_html | custom_trusted_html">
													</ng-bind-html>{{subMenu.menu_name}}</span>
											</div>
											<div class="d-flex flex-row">
												<div class="custom-control custom-switch">
													<input type="checkbox"
														class="custom-control-input custom-on-off-switch"
														id="sub-menu-enabled-{{$index}}" ng-model="subMenu.flag">
													<label
														class="custom-control-label custom-on-off-switch-label"
														for="sub-menu-enabled-{{$index}}"></label>
												</div>
											</div>
										</div>
									</li>
								</ul>
							</li>
						</ul>
					</div>
					<div class="limit-modal-content-height"
						ng-if="roleAccessType=='action'">
						<table class="table">
							<thead class="thead-dark">
								<tr>
									<th scope="col"><spring:message
											code='admin.role_management.access_name' /></th>
									<th scope="col"><spring:message
											code='admin.role_management.access' /></th>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="actionAccess in roleAccessData.action_access"
									ng-hide="actionAccess.is_hidden">
									<td>{{actionAccess.action_name}}</td>
									<td><div class="custom-control custom-switch">
											<input type="checkbox"
												class="custom-control-input custom-on-off-switch"
												ng-attr-id="{{'action-access-switch-' + $index}}"
												ng-model="actionAccess.flag"> <label
												class="custom-control-label custom-on-off-switch-label"
												for="{{'action-access-switch-' + $index}}"></label>
										</div></td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				<div class="modal-footer">
					<button class="btn btn-primary" ng-click="confirmRoleAction()">
						<i class='fas fa-check'></i>&nbsp; <span
							ng-if="roleModalType=='create'"><spring:message
								code='admin.role_management.create' /></span> <span
							ng-if="roleModalType=='update'"><spring:message
								code='admin.role_management.update' /></span>
					</button>
					<button type="button" class="btn btn-secondary"
						data-dismiss="modal">
						<i class='fas fa-times'></i>&nbsp;
						<spring:message code='gt.button.cancel' />
					</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
