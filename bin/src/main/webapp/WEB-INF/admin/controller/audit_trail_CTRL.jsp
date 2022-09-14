<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script>
	app.controller('audit_trail_CTRL', function ($scope, $location, $http, $compile, $timeout, $filter) {
		/* Set Header Title */
		changeTitle("<spring:message code='admin.nav.audit_trail' />");

		$scope.selectedAuditType = null;
		$scope.auditTypeList = [];
		$scope.initAuditTrailData = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/audit_trail/get_audit_trail_init_data'
			}).then(function successCallback(response) {
				hideLoading();
				
				if (response.status == 200) {
					$scope.auditTypeList = response.data.audit_type_list;

					if ($scope.auditTypeList.length == 1) {
						$scope.selectedAuditType = $scope.auditTypeList[0].id;
						$scope.initAuditTrailDT();
					}
				} else {
					showLoadError(response.data);
				}
			}, function errorCallback(error) {
				hideLoading();
				if (error.status == 401) {
					showSessionExpired();
				} else {
					if (error.data && error.data.length > 0) {
						showLoadError("(" + error.status + ") " + error.data);
					} else {
						showLoadError("(" + error.status + ") <spring:message code='gt.error.general.load_error' />");
					}
				}
			});
		}

		$scope.selectedStartDate = new Date();
		$scope.selectedEndDate = new Date();
		$scope.todayDate = new Date();

		$scope.startDateOptions = {
		    maxDate: $scope.selectedEndDate
		};
		$scope.endDateOptions = {
			minDate: $scope.selectedStartDate,
		    maxDate: $scope.todayDate
		};

		$scope.checkDateRestrictions = function() {
			$scope.startDateOptions.maxDate = $scope.selectedEndDate?$scope.selectedEndDate:$scope.todayDate;
			$scope.endDateOptions.minDate = $scope.selectedStartDate;
			$scope.endDateOptions.maxDate = $scope.todayDate;

			$scope.refreshAuditTrailListTable();
		}

		$scope.auditTrailDT = null;
		$scope.omniuOrderListData = [];
		$scope.initAuditTrailDT = function () {
			if (!$scope.selectedStartDate || !$scope.selectedEndDate || !$scope.selectedAuditType) {
				return;
			}
			
			showLoading("<spring:message code='gt.loading.loading' />...");

			if ($scope.auditTrailDT != null) {
				$scope.auditTrailDT.clear();
				$scope.auditTrailDT.destroy();
			}
			
			$scope.auditTrailListData = null;
			$scope.auditTrailDT = $('#auditTrailListTable').DataTable({
				language: $scope.dataTableLocale,
		        processing: true,
		        serverSide: true,
		        ajax: {
			        url: "${pageContext.request.contextPath}/admin_api/audit_trail/get_audit_trail_list",
			        type: "POST",
			        data: {
				        startDate: $filter('date')($scope.selectedStartDate, "yyyy-MM-ddTHH:mm:ssZ"),
				        endDate: $filter('date')($scope.selectedEndDate, "yyyy-MM-ddTHH:mm:ssZ"),
				        auditTypeID: JSON.stringify($scope.selectedAuditType)
				    },
				    dataSrc: function (json) {
			        	$scope.auditTrailListData = json.data;
				        return json.data;
			        },
			        error: function (xhr, error, thrown) {
				        if (xhr.status == 401) {
				        	showSessionExpired();
					    } else {
					    	showLoadError(xhr.responseText);
						}
			        }
			    },
		        scrollX: true,
			    columns: [
			    	/*{ title: "#", data: null, sortable: false, searchable: false, render: function(data, type, row, meta) {
					    return meta.row + 1 + $scope.auditTrailDT.page.info().start;
					}},*/
			    	{ title: "<spring:message code='admin.audit_trail.user_id' />", data: 'user_id' },
			    	{ title: "<spring:message code='admin.audit_trail.user_name' />", data: 'user_name' },
			    	{ title: "<spring:message code='admin.audit_trail.action_desc' />", data: 'action_desc' },
			    	{ title: "<spring:message code='admin.audit_trail.action_date' />", data: 'action_date' }
			    ],
			    order: [[3, "desc"]],
				fnCreatedRow: function (nRow, aData, iDataIndex) {
				    $compile(nRow)($scope);
				},
				drawCallback: function(settings, json) {
					$timeout(function() {
					    $scope.auditTrailDT.columns.adjust();
					}, 250);
			    },
			    initComplete: function (settings, json) {
				    $('#auditTrailListTable_wrapper input[type=search]').unbind();
				    $('#auditTrailListTable_wrapper input[type=search]').on('search', function () {
				    	$scope.auditTrailDT.search(this.value).draw();
				    });
				}
		    });

			hideLoading();
		}

		$scope.refreshAuditTrailListTable = function() {
			$scope.initAuditTrailDT();
		}

	    angular.element(document).ready(function () {
		    $scope.initAuditTrailData();
	    });
	});
</script>
