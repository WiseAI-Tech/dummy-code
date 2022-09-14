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
				ng-click="showPageModal('create')" ng-if="'${actionAccessData}'[1-1]=='1'">
				<i class="far fa-plus-square"></i>&nbsp;
				<spring:message code='admin.page_management.create' />
			</button>
		</div>
		<table id="pageListTable"
			class="table table-striped table-bordered dataTable"
			style="min-width: 100%; width: auto;"></table>
	</div>

	<div class="modal fade" id="pageModal" tabindex="-1" role="dialog"
		aria-labelledby="pageModal" aria-hidden="true" data-backdrop="static">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="dialogModalTitle">
						<span ng-if="pageModalType=='create'"><spring:message
								code='admin.page_management.create_page' /></span> <span
							ng-if="pageModalType=='update'"><spring:message
								code='admin.page_management.update_page' /></span>
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
												code='admin.page_management.page_name' /><span
											class="text-danger">*</span></span>
									</div>
									<input id="pageModal_page_name" type="text"
										class="form-control" ng-model="pageName"
										placeholder="<spring:message
											code='gt.input_field_required' />"
										required>
								</div>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<span class="input-group-text" id="inputGroup-sizing-default"><spring:message
												code='admin.page_management.page_description' /><span
											class="text-danger">*</span></span>
									</div>
									<input id="pageModal_page_desc" type="text"
										class="form-control" ng-model="pageDesc"
										placeholder="<spring:message
											code='gt.input_field_required' />"
										required>
								</div>
								<div class="input-group">
									<div class="input-group-prepend">
										<label class="input-group-text" for="pageModal_page_status"><spring:message
												code='admin.page_management.page_status' /><span
											class="text-danger">*</span></label>
									</div>
									<select class="custom-select" id="pageModal_page_status"
										ng-model="pageStatusID"
										ng-options="pageStatus.id as pageStatus.status_name for pageStatus in pageStatusList"
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
					<button class="btn btn-primary" ng-click="confirmPageAction()">
						<i class='fas fa-check'></i>&nbsp; <span
							ng-if="pageModalType=='create'"><spring:message
								code='admin.page_management.create' /></span> <span
							ng-if="pageModalType=='update'"><spring:message
								code='admin.page_management.update' /></span>
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
