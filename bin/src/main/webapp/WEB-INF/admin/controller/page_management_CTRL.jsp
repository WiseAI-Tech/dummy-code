<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script>
	app.controller('page_management_CTRL', function ($scope, $location, $http, $compile, $timeout) {
		/* Set Header Title */
		changeTitle("<spring:message code='admin.nav.access_management.page_management' />");

		$scope.pageStatusList = [];
		$scope.pageListDT = null;

		$scope.initData = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/page_access/get_page_init_data'
			}).then(function successCallback(response) {
				if (response.status == 200) {
					$scope.pageStatusList = response.data.page_status_list;
					
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
			
			$scope.pageListDT = $('#pageListTable').DataTable({
				language: $scope.dataTableLocale,
		        processing: true,
		        serverSide: true,
		        ajax: {
			        url: "${pageContext.request.contextPath}/admin_api/page_access/get_page_list",
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
					    return meta.row + 1 + $scope.pageListDT.page.info().start;
					}},
			    	{ title: "<spring:message code='admin.page_management.page_id' />", data: 'id' },
			    	{ title: "<spring:message code='admin.page_management.page_name' />", data: 'page_name' },
			    	{ title: "<spring:message code='admin.page_management.page_description' />", data: 'page_desc' },
			    	{ title: "<spring:message code='admin.page_management.page_status' />", data: 'status_name' },
				    { title: "<spring:message code='admin.page_management.action' />", data: null, sortable: false, searchable: false, visible: '${actionAccessData}'[2-1]=='1', render: function (data, type, row) {
				    	return "<button class='btn btn-sm btn-primary text-nowrap' ng-click='showPageModal(\"update\","+row.id+",\""+row.page_name+"\",\""+row.page_desc+"\","+row.page_status+")' ng-show='\"${actionAccessData}\"[2-1]==\"1\"'><i class='far fa-edit'></i>&nbsp;<spring:message code='admin.page_management.update' /></button>";
				    }}
			    ],
			    order: [[1, "asc"]],
				fnCreatedRow: function (nRow, aData, iDataIndex) {
				    $compile(nRow)($scope);
				},
				drawCallback: function(settings, json) {
					$timeout(function() {
					    $scope.pageListDT.columns.adjust();
					}, 250);
			    },
			    initComplete: function (settings, json) {
				    $('#pageListTable_wrapper input[type=search]').unbind();
				    $('#pageListTable_wrapper input[type=search]').on('search', function () {
				    	$scope.pageListDT.search(this.value).draw();
				    });
				}
		    });

			hideLoading();
		}

		$scope.pageModalType = "";
		$scope.pageID = null;
		$scope.pageName = null;
		$scope.pageDesc = null;
		$scope.pageStatusID = null;

		$scope.showPageModal = function (modalType, updateID, pageName, pageDesc, pageStatusID) {
			$scope.pageModalType = modalType;
			$scope.pageID = updateID;
			$scope.pageName = pageName;
			$scope.pageDesc = pageDesc;
			$scope.pageStatusID = pageStatusID;
			
			$("#pageModal").modal("show");
		}

		$scope.confirmPageAction = function () {
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
			if ($scope.pageModalType == 'create') {
				dialogOption.title = "<spring:message code='admin.page_management.confirm_create_page' />";
			} else if ($scope.pageModalType == 'update') {
				dialogOption.title = "<spring:message code='admin.page_management.confirm_update_page' />";
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
				if ($scope.pageModalType == 'create') {
					$scope.createPage();
				} else if ($scope.pageModalType == 'update') {
					$scope.updatePage();
				}
			};
			showDialog(dialogOption);
		}

		$scope.createPage = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/page_access/create_page',
				data: {
					pageName: $scope.pageName,
					pageDesc: $scope.pageDesc,
					pageStatusID: $scope.pageStatusID
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

		$scope.updatePage = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/page_access/update_page',
				data: {
					pageID: $scope.pageID,
					pageName: $scope.pageName,
					pageDesc: $scope.pageDesc,
					pageStatusID: $scope.pageStatusID
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
					$("#pageModal").modal("hide");
					$scope.pageListDT.ajax.reload(null, false);
				}
			};
			showDialog(dialogOption);
		}

	    angular.element(document).ready(function () {
		    $scope.initData();
	    });
	});
</script>
