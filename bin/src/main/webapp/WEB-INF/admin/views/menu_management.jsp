<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<style>
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
	<div class="mt-2" style="overflow-y: auto;">
		<div class="d-flex flex-row justify-content-end mb-2"
			style="min-width: fit-content;">
			<div
				class="d-flex flex-column justify-content-center custom-control custom-switch mr-2">
				<input type="checkbox"
					class="custom-control-input custom-on-off-switch"
					id="sortable-switch" ng-model="sortableOptions.disabled"
					ng-true-value="false" ng-false-value="true"> <label
					class="custom-control-label custom-on-off-switch-label"
					for="sortable-switch"><span
					ng-if="sortableOptions.disabled"><spring:message
							code='admin.menu_management.unsortable' /></span><span
					ng-if="!sortableOptions.disabled"><spring:message
							code='admin.menu_management.sortable' /></span></label>
			</div>
			<button class="btn btn-sm btn-primary text-nowrap mr-2"
				ng-click="showMenuModal('create_main')"
				ng-if="'${actionAccessData}'[5-1]=='1'">
				<i class="far fa-plus-square"></i>&nbsp;
				<spring:message code='admin.menu_management.create_main_menu' />
			</button>
			<button class="btn btn-sm btn-primary text-nowrap"
				ng-click="confirmUpdateMenu()" ng-disabled="!isChangesUnsaved"
				ng-if="'${actionAccessData}'[9-1]=='1'">
				<i class="fas fa-upload"></i>&nbsp;
				<spring:message code='admin.menu_management.update' />
			</button>
		</div>
		<div class="d-flex flex-row justify-content-center"
			ng-show="mainMenuList.length == 0">
			<h4>
				<i class="far fa-frown-open"></i>&nbsp;
				<spring:message code='gt.error.general.no_data_available' />
			</h4>
		</div>
		<ul class="list-group" ui-sortable="sortableOptions"
			ng-model="mainMenuList" ng-show="mainMenuList.length > 0"
			style="min-width: fit-content;">
			<li class="list-group-item" ng-repeat="mainMenu in mainMenuList">
				<div class="d-flex flex-row">
					<div class="flex-fill d-flex flex-column justify-content-center">
						<span><ng-bind-html class="mr-2"
								ng-bind-html="mainMenu.icon_html | custom_trusted_html">
							</ng-bind-html>{{mainMenu.menu_name}}</span>
					</div>
					<div class="d-flex flex-row">
						<div
							class="d-flex flex-column justify-content-center custom-control custom-switch">
							<input type="checkbox"
								class="custom-control-input custom-on-off-switch"
								id="main-menu-enabled-{{$index}}" ng-model="mainMenu.is_enabled"
								ng-change="setRequireSave()"> <label
								class="custom-control-label custom-on-off-switch-label"
								for="main-menu-enabled-{{$index}}"><span
								ng-if="mainMenu.is_enabled"><spring:message
										code='admin.menu_management.enabled' /></span><span
								ng-if="!mainMenu.is_enabled"><spring:message
										code='admin.menu_management.disabled' /></span></label>
						</div>
						<button class="btn btn-sm btn-primary text-nowrap ml-2"
							ng-click="showMenuModal('update', mainMenu)" ng-if="'${actionAccessData}'[7-1]=='1'">
							<i class="far fa-edit"></i>&nbsp;
							<spring:message code='admin.menu_management.edit' />
						</button>
						<button class="btn btn-sm btn-primary text-nowrap ml-2"
							ng-click="showMenuModal('create_sub', mainMenu)" ng-if="'${actionAccessData}'[6-1]=='1'">
							<i class="far fa-plus-square"></i>&nbsp;
							<spring:message code='admin.menu_management.create_sub_menu' />
						</button>
					</div>
				</div>
				<ul ng-if="mainMenu.sub_menu.length > 0" class="list-group mt-2"
					ui-sortable="sortableOptions" ng-model="mainMenu.sub_menu">
					<li class="list-group-item"
						ng-repeat="subMenu in mainMenu.sub_menu">
						<div class="d-flex flex-row">
							<div class="flex-fill d-flex flex-column justify-content-center">
								<span><ng-bind-html class="mr-2"
										ng-bind-html="subMenu.icon_html | custom_trusted_html">
									</ng-bind-html>{{subMenu.menu_name}}</span>
							</div>
							<div class="d-flex flex-row">
								<div
									class="d-flex flex-column justify-content-center custom-control custom-switch">
									<input type="checkbox"
										class="custom-control-input custom-on-off-switch"
										id="sub-menu-enabled-{{$parent.$index}}-{{$index}}"
										ng-model="subMenu.is_enabled" ng-change="setRequireSave()">
									<label class="custom-control-label custom-on-off-switch-label"
										for="sub-menu-enabled-{{$parent.$index}}-{{$index}}"><span
										ng-if="subMenu.is_enabled"><spring:message
												code='admin.menu_management.enabled' /></span><span
										ng-if="!subMenu.is_enabled"><spring:message
												code='admin.menu_management.disabled' /></span></label>
								</div>
								<button class="btn btn-sm btn-primary text-nowrap ml-2"
									ng-click="showMenuModal('update', subMenu)" ng-if="'${actionAccessData}'[8-1]=='1'">
									<i class="far fa-edit"></i>&nbsp;
									<spring:message code='admin.menu_management.edit' />
								</button>
							</div>
						</div>
					</li>
				</ul>
			</li>
		</ul>
	</div>

	<div class="modal fade" id="menuModal" tabindex="-1" role="dialog"
		aria-labelledby="menuModal" aria-hidden="true" data-backdrop="static">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="dialogModalTitle">
						<span
							ng-if="menuModalType=='create_main'||menuModalType=='create_sub'"><spring:message
								code='admin.menu_management.create_menu' /></span> <span
							ng-if="menuModalType=='update'"><spring:message
								code='admin.menu_management.update_menu' /></span>
					</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="row m-0 p-0">
						<div class="col-12 p-0 m-0">
							<form name="inputForm" class="was-validated" novalidate>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
												code='admin.menu_management.menu_name' /><span
											class="text-danger">*</span></span>
									</div>
									<input id="menuModal_menu_name" type="text"
										class="form-control" ng-model="editMenuData.menu_name"
										placeholder="<spring:message
											code='gt.input_field_required' />"
										required>
								</div>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
												code='admin.menu_management.locale_name' /><span
											class="text-danger">*</span></span>
									</div>
									<input id="menuModal_locale_name" type="text"
										class="form-control" ng-model="editMenuData.locale_name"
										placeholder="<spring:message
											code='gt.input_field_required' />"
										required>
								</div>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
												code='admin.menu_management.nav_name' /><span
											class="text-danger">*</span></span>
									</div>
									<input id="menuModal_nav_name" type="text" class="form-control"
										ng-model="editMenuData.nav_name"
										placeholder="<spring:message
											code='gt.input_field_required' />"
										required>
								</div>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
												code='admin.menu_management.icon_html' /></span>
									</div>
									<input id="menuModal_icon_html" type="text"
										class="form-control" ng-model="editMenuData.icon_html"
										placeholder="<spring:message
											code='gt.input_field_optional' />">
								</div>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
												code='admin.menu_management.redirect_link' /></span>
									</div>
									<input id="menuModal_redirect_link" type="text"
										class="form-control" ng-model="editMenuData.redirect_link"
										placeholder="<spring:message
											code='gt.input_field_optional' />">
								</div>
								<div>
									<span class="text-danger">*<spring:message
											code='gt.input_field_required' /></span>
								</div>
							</form>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button class="btn btn-primary text-nowrap"
						ng-click="confirmMenuAction()">
						<i class='fas fa-check'></i>&nbsp; <span
							ng-if="menuModalType=='create_main'||menuModalType=='create_sub'"><spring:message
								code='admin.menu_management.create' /></span> <span
							ng-if="menuModalType=='update'"><spring:message
								code='admin.menu_management.update' /></span>
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
