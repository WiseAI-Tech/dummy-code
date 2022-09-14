<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
</head>
<body>
	<div class="mt-2">
		<div class="card mb-2">
			<div class="card-body">
				<div class="row">
					<div class="col-12">
						<div class="input-group mb-3">
							<div class="input-group-prepend">
								<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
										code='admin.my_profile.current_password' /><span
									class="text-danger">*</span></span>
							</div>
							<input id="current_password" type="password" class="form-control"
								ng-model="currentPassword"
								placeholder="<spring:message
											code='gt.input_field_required' />"
								required>
						</div>
						<div class="input-group mb-3">
							<div class="input-group-prepend">
								<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
										code='admin.my_profile.new_password' /><span
									class="text-danger">*</span></span>
							</div>
							<input id="new_password" type="password" class="form-control"
								ng-model="newPassword"
								placeholder="<spring:message
											code='gt.input_field_required' />"
								required>
						</div>
						<div class="input-group mb-3">
							<div class="input-group-prepend">
								<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
										code='admin.my_profile.confirm_new_password' /><span
									class="text-danger">*</span></span>
							</div>
							<input id="confirm_new_password" type="password"
								class="form-control" ng-model="confirmNewPassword"
								placeholder="<spring:message
											code='gt.input_field_required' />"
								required>
						</div>
						<div class="d-flex justify-content-end">
							<button class="btn btn-primary" ng-click="confirmChangePassword()">
								<spring:message code='admin.my_profile.update' />
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
