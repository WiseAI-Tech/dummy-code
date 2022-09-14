<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<style>
/*table#auditTrailListTable tr[role="row"] {
	cursor: pointer;
}*/
</style>
</head>
<body>
	<div class="mt-2">
		<div class="card mb-2">
			<div class="card-body">
				<div class="mb-2">
					<div class="row">
						<div class="col-md-6 col-sm-12">
							<div class="form-group row">
								<label class="col-sm-4 col-form-label"><spring:message
										code='admin.audit_trail.start_date' /></label>
								<div class="col-sm-8">
									<div class="input-group">
										<input type="text" class="form-control" on-open-focus="false"
											uib-datepicker-popup="yyyy-MMM-dd"
											ng-model="selectedStartDate"
											close-text="<spring:message
										code='gt.uibootstrap.datepicker.close' />"
											clear-text="<spring:message
										code='gt.uibootstrap.datepicker.clear' />"
											current-text="<spring:message
										code='gt.uibootstrap.datepicker.today' />"
											datepicker-options="startDateOptions"
											is-open="datepicker1.opened"
											ng-click="datepicker1.opened = true"
											ng-change="checkDateRestrictions()"
											datepicker-template-url="${pageContext.request.contextPath}/template/uib/datepicker"
											datepicker-popup-template-url="${pageContext.request.contextPath}/template/uib/datepicker_popup.html"
											readonly /> <span class="input-group-btn">
											<button type="button" class="btn btn-secondary"
												ng-click="datepicker1.opened = true">
												<i class="far fa-calendar-alt"></i>
											</button>
										</span>
									</div>
								</div>
							</div>
						</div>
						<div class="col-md-6 col-sm-12">
							<div class="form-group row">
								<label class="col-sm-4 col-form-label"><spring:message
										code='admin.audit_trail.end_date' /></label>
								<div class="col-sm-8">
									<div class="input-group">
										<input type="text" class="form-control" on-open-focus="false"
											uib-datepicker-popup="yyyy-MMM-dd" ng-model="selectedEndDate"
											close-text="<spring:message
										code='gt.uibootstrap.datepicker.close' />"
											clear-text="<spring:message
										code='gt.uibootstrap.datepicker.clear' />"
											current-text="<spring:message
										code='gt.uibootstrap.datepicker.today' />"
											datepicker-options="endDateOptions"
											is-open="datepicker2.opened"
											ng-click="datepicker2.opened = true"
											ng-change="checkDateRestrictions()"
											datepicker-template-url="${pageContext.request.contextPath}/template/uib/datepicker"
											datepicker-popup-template-url="${pageContext.request.contextPath}/template/uib/datepicker_popup.html"
											readonly /> <span class="input-group-btn">
											<button type="button" class="btn btn-secondary"
												ng-click="datepicker2.opened = true">
												<i class="far fa-calendar-alt"></i>
											</button>
										</span>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6 col-sm-12">
							<div class="form-group row">
								<label for="selectedOrderType" class="col-sm-4 col-form-label"><spring:message
										code='admin.audit_trail.audit_type' /></label>
								<div class="col-sm-8">
									<select class="form-control selectpicker-handler"
										id="selectedAuditType" data-live-search="true"
										ng-model="selectedAuditType"
										ng-options="auditType.id as auditType.name for auditType in auditTypeList"
										ng-change="initAuditTrailDT()">
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="card mb-2" ng-show="selectedStartDate && selectedEndDate && selectedAuditType">
			<div class="card-body">
				<div class="d-flex flex-row justify-content-end mb-2">
					<button class="btn btn-sm btn-primary"
						ng-click="refreshAuditTrailListTable()">
						<i class="fas fa-sync"></i>&nbsp;
						<spring:message code='admin.audit_trail.refresh' />
					</button>
				</div>
				<table id="auditTrailListTable"
					class="table table-striped table-bordered dataTable"
					style="min-width: 100%; width: auto;"></table>
			</div>
		</div>
	</div>
</body>
</html>
