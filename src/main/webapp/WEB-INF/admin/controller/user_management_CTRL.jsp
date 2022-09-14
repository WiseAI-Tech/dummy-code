<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script>
	app.controller('user_management_CTRL', function ($scope, $location, $http, $compile, $timeout) {
		/* Set Header Title */
		changeTitle("<spring:message code='admin.nav.user_management' />");

		$scope.userRoleList = [];
		$scope.userStatusList = [];
		$scope.userListDT = null;
		$scope.userListData = [];

		$scope.initData = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/user_management/get_user_init_data'
			}).then(function successCallback(response) {
				if (response.status == 200) {
					$scope.userRoleList = response.data.user_role_list;
					$scope.userStatusList = response.data.user_status_list;
					
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
			
			$scope.userListDT = $('#userListTable').DataTable({
				language: $scope.dataTableLocale,
		        processing: true,
		        serverSide: true,
		        ajax: {
			        url: "${pageContext.request.contextPath}/admin_api/user_management/get_user_list",
			        type: "POST",
			        dataSrc: function (json) {
                        $scope.userListData = json.data;
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
					    return meta.row + 1 + $scope.userListDT.page.info().start;
					}},
			    	{ title: "<spring:message code='admin.user_management.user_id' />", data: 'id' },
			    	{ title: "<spring:message code='admin.user_management.login_id' />", data: 'login_id' },
			    	{ title: "<spring:message code='admin.user_management.full_name' />", data: 'full_name' },
			    	{ title: "<spring:message code='admin.user_management.display_name' />", data: 'display_name' },
			    	{ title: "<spring:message code='admin.user_management.email' />", data: 'email' },
			    	{ title: "<spring:message code='admin.user_management.last_login' />", data: 'last_login_time', render: function (data, type, row) {
				    	if (row.last_login_time == null) {
					    	return "N/A";
					   	} else {
						   	return row.last_login_time;
						}
				    }},
			    	{ title: "<spring:message code='admin.user_management.failed_login_attempt' />", data: 'failed_login_attempt' },
			    	{ title: "<spring:message code='admin.user_management.user_role' />", data: 'role_name' },
			    	{ title: "<spring:message code='admin.user_management.user_status' />", data: 'status_name' },
				    { title: "<spring:message code='admin.user_management.action' />", data: null, sortable: false, searchable: false, visible: ('${actionAccessData}'[13-1]=='1' || '${actionAccessData}'[14-1]=='1'), render: function (data, type, row, meta) {
					    var actionStr = "";
					    actionStr += "<button class='btn btn-sm btn-primary text-nowrap mr-1 mb-1' ng-click='showUserModal(\"update\","+meta.row+")' ng-show='\"${actionAccessData}\"[13-1]==\"1\"'><i class='far fa-edit'></i>&nbsp;<spring:message code='admin.user_management.update' /></button>";
						actionStr += "<button class='btn btn-sm btn-primary text-nowrap mr-1 mb-1' ng-click='showResetPasswordModal("+row.id+")' ng-show='\"${actionAccessData}\"[14-1]==\"1\"'><i class='fas fa-unlock-alt'></i>&nbsp;<spring:message code='admin.user_management.reset_password' /></button>";
				    	return actionStr;
				    }}
			    ],
			    order: [[1, "asc"]],
				fnCreatedRow: function (nRow, aData, iDataIndex) {
				    $compile(nRow)($scope);
				},
				drawCallback: function(settings, json) {
					$timeout(function() {
					    $scope.userListDT.columns.adjust();
					}, 250);
			    },
			    initComplete: function (settings, json) {
			    	if ("${actionAccessData}"[15-1] == "1") {
				    	initializeExportSetting(settings, "Admin User");
					}
			    	
				    $('#userListTable_wrapper input[type=search]').unbind();
				    $('#userListTable_wrapper input[type=search]').on('search', function () {
				    	$scope.userListDT.search(this.value).draw();
				    });
				}
		    });

			hideLoading();
		}

		$scope.userModalType = "";
		$scope.userID = null;
		$scope.loginID = null;
		$scope.loginPassword = null;
		$scope.fullName = null;
		$scope.displayName = null;
		$scope.email = null;
		$scope.userRoleID = null;
		$scope.userStatusID = null;

		$scope.showUserModal = function (modalType, userDataPOS) {
			$scope.userModalType = modalType;
			
			$scope.userID = null;
            $scope.loginID = null;
			$scope.loginPassword = null;
            $scope.fullName = null;
            $scope.displayName = null;
            $scope.email = null;
            $scope.userRoleID = null;
            $scope.userStatusID = null;
            
            if (modalType == 'update') {
                $scope.tempUserData = JSON.parse(JSON.stringify($scope.userListData[userDataPOS]));
                $scope.userID = $scope.tempUserData.id;
                $scope.loginID = $scope.tempUserData.login_id;
                $scope.loginPassword = null;
                $scope.fullName = $scope.tempUserData.full_name;
                $scope.displayName = $scope.tempUserData.display_name;
                $scope.email = $scope.tempUserData.email;
                $scope.userRoleID = $scope.tempUserData.role_id;
                $scope.userStatusID = $scope.tempUserData.status_id;
            }
			
			$("#userModal").modal("show");
		}

		$scope.confirmUserAction = function () {
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
			if ($scope.userModalType == 'create') {
				dialogOption.title = "<spring:message code='admin.user_management.confirm_create_user' />";
			} else if ($scope.userModalType == 'update') {
				dialogOption.title = "<spring:message code='admin.user_management.confirm_update_user' />";
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
				if ($scope.userModalType == 'create') {
					$scope.createUser();
				} else if ($scope.userModalType == 'update') {
					$scope.updateUser();
				}
			};
			showDialog(dialogOption);
		}

		$scope.createUser = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/user_management/create_user',
				data: {
					loginID: $scope.loginID,
					loginPassword: $scope.loginPassword,
					fullName: $scope.fullName,
					displayName: $scope.displayName,
					email: $scope.email,
					userRoleID: $scope.userRoleID,
					userStatusID: $scope.userStatusID
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

		$scope.updateUser = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/user_management/update_user',
				data: {
					userID: $scope.userID,
					fullName: $scope.fullName,
					displayName: $scope.displayName,
					email: $scope.email,
					userRoleID: $scope.userRoleID,
					userStatusID: $scope.userStatusID
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
					$("#userModal").modal("hide");
					$scope.userListDT.ajax.reload(null, false);
				}
			};
			showDialog(dialogOption);
		}

		$scope.resetPasswordUserID = null;
		$scope.resetPasswordPassword = null;
		
		$scope.showResetPasswordModal = function (userID) {
			$scope.resetPasswordUserID = userID;
			$scope.resetPasswordPassword = null;
			
			$("#resetPasswordModal").modal("show");
		}

		$scope.confirmResetPassword = function () {
			if ($scope.resetPasswordForm.$error.required) {
				$scope.showInvalidResetPasswordInput("<spring:message code='gt.input_field.entry_required' />", $scope.resetPasswordForm.$error.required[0].$$attr.id);
				return;
			}
			
			var dialogOption = {};
			dialogOption.isCloseButton = false;
			dialogOption.isCloseOnBackground = false;
			dialogOption.title = "<spring:message code='admin.user_management.confirm_reset_password' />";
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
				
				$scope.resetPassword();
			};
			showDialog(dialogOption);
		}

		$scope.resetPassword = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/user_management/reset_password',
				data: {
					userID: $scope.resetPasswordUserID,
					loginPassword: $scope.resetPasswordPassword
				}
			}).then(function successCallback(response) {
				if (response.status == 200) {
					hideLoading();

					if (response.data.response_code == "00") {
						$scope.showResetPasswordStatus(true, response.data.response_message);
					} else {
						$scope.showResetPasswordStatus(false, response.data.response_code + " - " + response.data.response_message);
					}
				} else {
					$scope.showResetPasswordStatus(false, response.data);
				}
			}, function errorCallback(error) {
				hideLoading();
				if (error.status == 401) {
					showSessionExpired();
				} else {
					if (error.data && error.data.length > 0) {
						$scope.showResetPasswordStatus(false, "(" + error.status + ") " + error.data);
					} else {
						$scope.showResetPasswordStatus(false, "(" + error.status + ") <spring:message code='gt.error.general.load_error' />");
					}
				}
			});
		}

		$scope.showInvalidResetPasswordInput = function (invalidMessage, focusElement) {
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

		$scope.showResetPasswordStatus = function (isSuccess, messageStr) {
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
					$("#resetPasswordModal").modal("hide");
					$scope.userListDT.ajax.reload(null, false);
				}
			};
			showDialog(dialogOption);
		}

	    angular.element(document).ready(function () {
		    $scope.initData();
	    });
	});
</script>
