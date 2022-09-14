<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
</head>
<body>
	<div class="mt-2">
		<div class="d-flex flex-row justify-content-end mb-2">
			<button class="btn btn-sm btn-primary"
				ng-click="showUserModal('create')"
				ng-if="'${actionAccessData}'[12-1]=='1'">
				<i class="far fa-plus-square"></i>&nbsp;
				<spring:message code='admin.user_management.create' />
			</button>
		</div>
		<table id="userListTable"
			class="table table-striped table-bordered dataTable"
			style="min-width: 100%; width: auto;"></table>
	</div>

	<div class="modal fade" id="userModal" tabindex="-1" role="dialog"
		aria-labelledby="userModal" aria-hidden="true" data-backdrop="static">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="dialogModalTitle">
						<span ng-if="userModalType=='create'"><spring:message
								code='admin.user_management.create_user' /></span> <span
							ng-if="userModalType=='update'"><spring:message
								code='admin.user_management.update_user' /></span>
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
												code='admin.user_management.login_id' /><span
											class="text-danger">*</span></span>
									</div>
									<input id="userModal_login_id" type="text" class="form-control"
										ng-model="loginID"
										placeholder="<spring:message
											code='gt.input_field_required' />"
										required ng-disabled="userModalType=='update'">
								</div>
								<div class="input-group mb-3" ng-show="userModalType=='create'">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
												code='admin.user_management.login_password' /><span
											class="text-danger">*</span></span>
									</div>
									<input id="userModal_login_password" type="password"
										class="form-control" ng-model="loginPassword"
										ng-disabled="userModalType!='create'"
										ng-required="userModalType=='create'"
										placeholder="<spring:message
											code='gt.input_field_required' />"
										required>
								</div>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
												code='admin.user_management.full_name' /><span
											class="text-danger">*</span></span>
									</div>
									<input id="userModal_full_name" type="text"
										class="form-control" ng-model="fullName"
										placeholder="<spring:message
											code='gt.input_field_required' />"
										required>
								</div>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
												code='admin.user_management.display_name' /><span
											class="text-danger">*</span></span>
									</div>
									<input id="userModal_display_name" type="text"
										class="form-control" ng-model="displayName"
										placeholder="<spring:message
											code='gt.input_field_required' />"
										required>
								</div>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
												code='admin.user_management.email' /><span
											class="text-danger">*</span></span>
									</div>
									<input id="userModal_email" type="email" class="form-control"
										ng-model="email"
										placeholder="<spring:message
											code='gt.input_field_required' />"
										required>
								</div>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<label class="input-group-text" for="userModal_user_role"><spring:message
												code='admin.user_management.user_role' /><span
											class="text-danger">*</span></label>
									</div>
									<select class="custom-select" id="userModal_user_role"
										ng-model="userRoleID"
										ng-options="userRole.id as userRole.role_name for userRole in userRoleList"
										required>
										<option class="d-none" value="">--
											<spring:message code='gt.input_field.please_select' />--
										</option>
									</select>
								</div>
								<div class="input-group mb-3"
									ng-show="userSuperGroupIDList | contains:userRoleID">
									<div class="input-group-prepend">
										<label class="input-group-text"
											for="userModal_user_super_group_id"><spring:message
												code='admin.user_management.user_super_group' /><span
											class="text-danger">*</span></label>
									</div>
									<select class="custom-select"
										id="userModal_user_super_group_id"
										ng-disabled="!(userSuperGroupIDList | contains:userRoleID)"
										ng-required="userSuperGroupIDList | contains:userRoleID"
										ng-model="userSuperGroupID"
										ng-options="userSuperGroup.id as userSuperGroup.super_group_name for userSuperGroup in userSuperGroupList"
										required>
										<option class="d-none" value="">--{{userSuperGroupList.length
											== 0?"
											<spring:message code='gt.input_field.no_available_selection' />":"
											<spring:message code='gt.input_field.please_select' />"}}--
										</option>
									</select>
								</div>
								<div class="input-group mb-3"
									ng-show="userStoreIDList | contains:userRoleID">
									<div class="input-group-prepend">
										<label class="input-group-text" for="userModal_user_store_id"><spring:message
												code='admin.user_management.user_store' /><span
											class="text-danger">*</span></label>
									</div>
									<select class="custom-select" id="userModal_user_store_id"
										ng-disabled="!(userStoreIDList | contains:userRoleID)"
										ng-required="userStoreIDList | contains:userRoleID"
										ng-model="userStoreID"
										ng-options="userStore.id as userStore.store_name for userStore in userStoreList"
										required>
										<option class="d-none" value="">--{{userStoreList.length
											== 0?"
											<spring:message code='gt.input_field.no_available_selection' />":"
											<spring:message code='gt.input_field.please_select' />"}}--
										</option>
									</select>
								</div>
								<div class="input-group">
									<div class="input-group-prepend">
										<label class="input-group-text" for="userModal_user_status"><spring:message
												code='admin.user_management.user_status' /><span
											class="text-danger">*</span></label>
									</div>
									<select class="custom-select" id="userModal_user_status"
										ng-model="userStatusID"
										ng-options="userStatus.id as userStatus.status_name for userStatus in userStatusList"
										required>
										<option class="d-none" value="">--
											<spring:message code='gt.input_field.please_select' />--
										</option>
									</select>
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
					<button class="btn btn-primary" ng-click="confirmUserAction()">
						<i class='fas fa-check'></i>&nbsp; <span
							ng-if="userModalType=='create'"><spring:message
								code='admin.user_management.create' /></span> <span
							ng-if="userModalType=='update'"><spring:message
								code='admin.user_management.update' /></span>
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

	<div class="modal fade" id="resetPasswordModal" tabindex="-1"
		role="dialog" aria-labelledby="resetPasswordModal" aria-hidden="true"
		data-backdrop="static">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="dialogModalTitle">
						<span><spring:message
								code='admin.user_management.reset_password' /></span>
					</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="row m-0 p-0">
						<div class="col-12 p-0 m-0">
							<form name="resetPasswordForm" class="was-validated" novalidate>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
												code='admin.user_management.login_password' /><span
											class="text-danger">*</span></span>
									</div>
									<input id="resetPasswordModal_login_password" type="password"
										class="form-control" ng-model="resetPasswordPassword"
										placeholder="<spring:message
											code='gt.input_field_required' />"
										required>
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
					<button class="btn btn-primary" ng-click="confirmResetPassword()">
						<i class='fas fa-check'></i>&nbsp; <span><spring:message
								code='admin.user_management.reset_password' /></span>
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
