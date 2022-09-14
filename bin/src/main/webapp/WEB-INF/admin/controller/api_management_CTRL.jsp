<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script>
	app.controller('api_management_CTRL', function ($scope, $location, $http, $compile, $timeout) {
		/* Set Header Title */
		changeTitle("<spring:message code='admin.nav.access_management.api_management' />");

		$scope.apiStatusList = [];
		$scope.apiListDT = null;

		$scope.initData = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/api_access/get_api_init_data'
			}).then(function successCallback(response) {
				if (response.status == 200) {
					$scope.apiStatusList = response.data.api_status_list;
					
					hideLoading();
					
					$scope.initDT();
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

		$scope.initDT = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$scope.apiListDT = $('#apiListTable').DataTable({
				language: $scope.dataTableLocale,
		        processing: true,
		        serverSide: true,
		        ajax: {
			        url: "${pageContext.request.contextPath}/admin_api/api_access/get_api_list",
			        type: "POST",
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
			    	{ title: "#", data: null, sortable: false, searchable: false, visible: false, render: function(data, type, row, meta) {
					    return meta.row + 1 + $scope.apiListDT.page.info().start;
					}},
			    	{ title: "<spring:message code='admin.api_management.api_id' />", data: 'id' },
			    	{ title: "<spring:message code='admin.api_management.api_name' />", data: 'api_name' },
			    	{ title: "<spring:message code='admin.api_management.api_description' />", data: 'api_desc' },
			    	{ title: "<spring:message code='admin.api_management.api_status' />", data: 'status_name' },
				    { title: "<spring:message code='admin.api_management.action' />", data: null, sortable: false, searchable: false, visible: '${actionAccessData}'[4-1]=='1', render: function (data, type, row) {
				    	return "<button class='btn btn-sm btn-primary text-nowrap' ng-click='showAPIModal(\"update\","+row.id+",\""+row.api_name+"\",\""+row.api_desc+"\","+row.api_status+")' ng-show='\"${actionAccessData}\"[4-1]==\"1\"'><i class='far fa-edit'></i>&nbsp;<spring:message code='admin.api_management.update' /></button>";
				    }}
			    ],
			    order: [[1, "asc"]],
				fnCreatedRow: function (nRow, aData, iDataIndex) {
				    $compile(nRow)($scope);
				},
				drawCallback: function(settings, json) {
					$timeout(function() {
					    $scope.apiListDT.columns.adjust();
					}, 250);
			    },
			    initComplete: function (settings, json) {
				    $('#apiListTable_wrapper input[type=search]').unbind();
				    $('#apiListTable_wrapper input[type=search]').on('search', function () {
				    	$scope.apiListDT.search(this.value).draw();
				    });
				}
		    });

			hideLoading();
		}

		$scope.apiModalType = "";
		$scope.apiID = null;
		$scope.apiName = null;
		$scope.apiDesc = null;
		$scope.apiStatusID = null;

		$scope.showAPIModal = function (modalType, updateID, apiName, apiDesc, apiStatusID) {
			$scope.apiModalType = modalType;
			$scope.apiID = updateID;
			$scope.apiName = apiName;
			$scope.apiDesc = apiDesc;
			$scope.apiStatusID = apiStatusID;
			
			$("#apiModal").modal("show");
		}

		$scope.confirmAPIAction = function () {
			if ($scope.inputForm.$error.required) {
				$scope.showInvalidInput("<spring:message code='gt.input_field.entry_required' />", $scope.inputForm.$error.required[0].$$attr.id);
				return;
			}
			if ($scope.inputForm.$error.email) {
				$scope.showInvalidInput("<spring:message code='gt.input_field.email_invalid' />", $scope.inputForm.$error.email[0].$$attr.id);
				return;
			}
			
			var dialogOption = {};
			dialogOption.isCloseButton = false;
			dialogOption.isCloseOnBackground = false;
			if ($scope.apiModalType == 'create') {
				dialogOption.title = "<spring:message code='admin.api_management.confirm_create_api' />";
			} else if ($scope.apiModalType == 'update') {
				dialogOption.title = "<spring:message code='admin.api_management.confirm_update_api' />";
			}
			dialogOption.button1 = {};
			dialogOption.button1.isShow = true;
			dialogOption.button1.text = "<i class='fas fa-times'></i>&nbsp;<spring:message code='gt.button.no' />";
			dialogOption.button1.importantFlag = false;
			dialogOption.button1.fn = function() {
				hideDialog();
			};
			dialogOption.button2 = {};
			dialogOption.button2.isShow = true;
			dialogOption.button2.text = "<i class='fas fa-check'></i>&nbsp;<spring:message code='gt.button.yes' />";
			dialogOption.button2.importantFlag = true;
			dialogOption.button2.fn = function() {
				hideDialog();
				if ($scope.apiModalType == 'create') {
					$scope.createAPI();
				} else if ($scope.apiModalType == 'update') {
					$scope.updateAPI();
				}
			};
			showDialog(dialogOption);
		}

		$scope.createAPI = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/api_access/create_api',
				data: {
					apiName: $scope.apiName,
					apiDesc: $scope.apiDesc,
					apiStatusID: $scope.apiStatusID
				}
			}).then(function successCallback(response) {
				if (response.status == 200) {
					hideLoading();

					if (response.data.response_code == "00") {
						$scope.showActionStatus(true, response.data.response_message);
					} else {
						$scope.showActionStatus(false, response.data.response_code + " - " + response.data.response_message);
					}
				} else {
					$scope.showActionStatus(false, response.data);
				}
			}, function errorCallback(error) {
				hideLoading();
				if (error.status == 401) {
					showSessionExpired();
				} else {
					if (error.data && error.data.length > 0) {
						$scope.showActionStatus(false, "(" + error.status + ") " + error.data);
					} else {
						$scope.showActionStatus(false, "(" + error.status + ") <spring:message code='gt.error.general.load_error' />");
					}
				}
			});
		}

		$scope.updateAPI = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/api_access/update_api',
				data: {
					apiID: $scope.apiID,
					apiName: $scope.apiName,
					apiDesc: $scope.apiDesc,
					apiStatusID: $scope.apiStatusID
				}
			}).then(function successCallback(response) {
				if (response.status == 200) {
					hideLoading();

					if (response.data.response_code == "00") {
						$scope.showActionStatus(true, response.data.response_message);
					} else {
						$scope.showActionStatus(false, response.data.response_code + " - " + response.data.response_message);
					}
				} else {
					$scope.showActionStatus(false, response.data);
				}
			}, function errorCallback(error) {
				hideLoading();
				if (error.status == 401) {
					showSessionExpired();
				} else {
					if (error.data && error.data.length > 0) {
						$scope.showActionStatus(false, "(" + error.status + ") " + error.data);
					} else {
						$scope.showActionStatus(false, "(" + error.status + ") <spring:message code='gt.error.general.load_error' />");
					}
				}
			});
		}

		$scope.showInvalidInput = function (invalidMessage, focusElement) {
			var dialogOption = {};
			dialogOption.isCloseButton = false;
			dialogOption.isCloseOnBackground = false;
			dialogOption.title = invalidMessage;
			dialogOption.button1 = {};
			dialogOption.button1.isShow = true;
			dialogOption.button1.text = "<spring:message code='gt.button.ok' />";
			dialogOption.button1.importantFlag = false;
			dialogOption.button1.fn = function() {
				hideDialog();

				if (focusElement) {
					$("#" + focusElement).focus();
				}
			};
			showDialog(dialogOption);
		}

		$scope.showActionStatus = function (isSuccess, messageStr) {
			var dialogOption = {};
			dialogOption.isCloseButton = false;
			dialogOption.isCloseOnBackground = false;
			dialogOption.title = messageStr;
			dialogOption.button1 = {};
			dialogOption.button1.isShow = true;
			dialogOption.button1.text = "<spring:message code='gt.button.ok' />";
			dialogOption.button1.importantFlag = false;
			dialogOption.button1.fn = function() {
				hideDialog();

				if (isSuccess) {
					$("#apiModal").modal("hide");
					$scope.apiListDT.ajax.reload(null, false);
				}
			};
			showDialog(dialogOption);
		}

	    angular.element(document).ready(function () {
		    $scope.initData();
	    });
	});
</script>
