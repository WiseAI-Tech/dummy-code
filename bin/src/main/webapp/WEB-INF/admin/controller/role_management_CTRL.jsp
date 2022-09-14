<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script>
	app.controller('role_management_CTRL', function ($scope, $location, $http, $compile, $timeout) {
		/* Set Header Title */
		changeTitle("<spring:message code='admin.nav.access_management.role_management' />");

		$scope.roleListDT = null;

		$scope.roleStatusList = [];
		$scope.defaultAccessData = {};
		$scope.roleListData = [];

		$scope.initData = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/role_access/get_role_init_data'
			}).then(function successCallback(response) {
				if (response.status == 200) {
					$scope.roleStatusList = response.data.role_status_list;
					$scope.defaultAccessData = response.data.default_access_data;
					
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
			$scope.rowIndex = 0;
			
			$scope.roleListDT = $('#roleListTable').DataTable({
				language: $scope.dataTableLocale,
		        processing: true,
		        serverSide: true,
		        ajax: {
			        url: "${pageContext.request.contextPath}/admin_api/role_access/get_role_list",
			        type: "POST",
			        dataSrc: function (json) {
			        	$scope.rowIndex = 0;
				        $scope.roleListData = json.data;

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
			    	{ title: "#", data: null, sortable: false, searchable: false, visible: false, render: function(data, type, row, meta) {
					    return meta.row + 1 + $scope.roleListDT.page.info().start;
					}},
			    	{ title: "<spring:message code='admin.role_management.role_id' />", data: 'id' },
			    	{ title: "<spring:message code='admin.role_management.role_name' />", data: 'role_name' },
			    	{ title: "<spring:message code='admin.role_management.role_status' />", data: 'status_name' },
			    	{ title: "<spring:message code='admin.role_management.action' />", data: null, sortable: false, searchable: false, visible: '${actionAccessData}'[11-1]=='1', render: function (data, type, row) {
				    	return "<button class='btn btn-sm btn-primary text-nowrap' ng-click='showRoleModal(\"update\"," + data.id + ",\"" + data.role_name + "\"," + data.role_status + "," + $scope.rowIndex++ + ")' ng-show='\"${actionAccessData}\"[11-1]==\"1\"'><i class='far fa-edit'></i>&nbsp;<spring:message code='admin.role_management.update' /></button>";
				    }}
			    ],
			    order: [[1, "asc"]],
				fnCreatedRow: function (nRow, aData, iDataIndex) {
				    $compile(nRow)($scope);
				},
				drawCallback: function(settings, json) {
					$timeout(function() {
					    $scope.roleListDT.columns.adjust();
					}, 250);
			    },
			    initComplete: function (settings, json) {
				    $('#roleListTable_wrapper input[type=search]').unbind();
				    $('#roleListTable_wrapper input[type=search]').on('search', function () {
				    	$scope.roleListDT.search(this.value).draw();
				    });
				}
		    });
		}

		$scope.roleModalType = "";
		$scope.roleID = null;
		$scope.roleName = null;
		$scope.roleAccessData = null;
		$scope.roleAccessType = "page";
		$scope.roleStatusID = null;

		$scope.showRoleModal = function (modalType, roleID, roleName, roleStatusID, roleIndex) {
			$scope.roleModalType = modalType;
			$scope.roleID = roleID;
			$scope.roleName = roleName;
			$scope.roleStatusID = roleStatusID;
			if (modalType != 'update') {
				$scope.roleAccessData = angular.copy($scope.defaultAccessData);
			} else {
				$scope.roleAccessData = angular.copy($scope.roleListData[roleIndex]);
			}

			$scope.roleAccessType = "page";
				
			$("#roleModal").modal("show");
		}

		$scope.confirmRoleAction = function () {
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
			if ($scope.roleModalType == 'create') {
				dialogOption.title = "<spring:message code='admin.role_management.confirm_create_role' />";
			} else if ($scope.roleModalType == 'update') {
				dialogOption.title = "<spring:message code='admin.role_management.confirm_update_role' />";
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
				if ($scope.roleModalType == 'create') {
					$scope.createRole();
				} else if ($scope.roleModalType == 'update') {
					$scope.updateRole();
				}
			};
			showDialog(dialogOption);
		}

		$scope.createRole = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/role_access/create_role',
				data: {
					roleName: $scope.roleName,
					rolePageAccess: $scope.roleAccessData.page_access,
					roleAPIAccess: $scope.roleAccessData.api_access,
					roleMenuAccess: $scope.roleAccessData.menu_access,
					roleActionAccess: $scope.roleAccessData.action_access,
					roleStatusID: $scope.roleStatusID
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

		$scope.updateRole = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/role_access/update_role',
				data: {
					roleID: $scope.roleID,
					roleName: $scope.roleName,
					rolePageAccess: $scope.roleAccessData.page_access,
					roleAPIAccess: $scope.roleAccessData.api_access,
					roleMenuAccess: $scope.roleAccessData.menu_access,
					roleActionAccess: $scope.roleAccessData.action_access,
					roleStatusID: $scope.roleStatusID
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
					$("#roleModal").modal("hide");
					$scope.roleListDT.ajax.reload(null, false);
				}
			};
			showDialog(dialogOption);
		}

	    angular.element(document).ready(function () {
		    $scope.initData();
	    });
	});
</script>
