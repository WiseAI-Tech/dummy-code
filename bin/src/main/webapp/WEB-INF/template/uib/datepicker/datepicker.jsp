<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<style>
td.uib-year button.btn-info span, td.uib-month button.btn-info span, td.uib-day button.btn-info span {
	color: black !important;
}
</style>
<div ng-switch="datepickerMode">
  <div uib-daypicker ng-switch-when="day" tabindex="0" class="uib-daypicker" template-url="${pageContext.request.contextPath}/template/uib/datepicker/day.html"></div>
  <div uib-monthpicker ng-switch-when="month" tabindex="0" class="uib-monthpicker" template-url="${pageContext.request.contextPath}/template/uib/datepicker/month.html"></div>
  <div uib-yearpicker ng-switch-when="year" tabindex="0" class="uib-yearpicker" template-url="${pageContext.request.contextPath}/template/uib/datepicker/year.html"></div>
</div>