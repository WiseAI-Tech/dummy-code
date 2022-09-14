<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script>
	app.controller('menu_management_CTRL', function ($scope, $location, $http, $compile, $timeout, $window) {
		/* Set Header Title */
		changeTitle("<spring:message code='admin.nav.access_management.menu_management' />");

		$scope.sortableOptions = {
			disabled: true,
			update: function(e, ui) {
				$scope.isChangesUnsaved = true;
			}
		};

		$scope.isChangesUnsaved = false;

		$scope.setRequireSave = function() {
			$scope.isChangesUnsaved = true;
		}
		
		$scope.mainMenuList = [];

		$scope.initData = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/menu_management/get_menu_list'
			}).then(function successCallback(response) {
				if (response.status == 200) {
					$scope.mainMenuList = response.data;
					
					hideLoading();
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

		$scope.menuModalType = "";
		$scope.oriMenuList = [];
		$scope.oriMenuData = {};
		$scope.editMenuData = {};

		$scope.showMenuModal = function (modalType, param1) {
			$scope.editMenuData = {};
			
			$scope.menuModalType = modalType;
			if ($scope.menuModalType == "create_main") {
				$scope.oriMenuList = $scope.mainMenuList;
				$scope.editMenuData.is_enabled = true;
			} else if ($scope.menuModalType == "create_sub") {
				if (!param1.sub_menu) {
					param1.sub_menu = [];
				}
				$scope.oriMenuList = param1.sub_menu;
				$scope.editMenuData.is_enabled = true;
			} else if ($scope.menuModalType == "update") {
				$scope.oriMenuData = param1;
				$scope.editMenuData = angular.copy(param1);
			}
			
			$("#menuModal").modal("show");
		}

		$scope.confirmMenuAction = function () {
			if ($scope.inputForm.$error.required) {
				$scope.showInvalidInput("<spring:message code='gt.input_field.entry_required' />", $scope.inputForm.$error.required[0].$$attr.id);
				return;
			}
			if ($scope.inputForm.$error.email) {
				$scope.showInvalidInput("<spring:message code='gt.input_field.email_invalid' />", $scope.inputForm.$error.email[0].$$attr.id);
				return;
			}
			
			if ($scope.menuModalType == "create_main") {
				$scope.oriMenuList.push(angular.copy($scope.editMenuData));
			} else if ($scope.menuModalType == "create_sub") {
				$scope.oriMenuList.push(angular.copy($scope.editMenuData));
			} else if ($scope.menuModalType == "update") {
				$scope.oriMenuData.menu_name = $scope.editMenuData.menu_name;
				$scope.oriMenuData.locale_name = $scope.editMenuData.locale_name;
				$scope.oriMenuData.nav_name = $scope.editMenuData.nav_name;
				$scope.oriMenuData.icon_html = $scope.editMenuData.icon_html;
				$scope.oriMenuData.redirect_link = $scope.editMenuData.redirect_link;
			}

			$scope.isChangesUnsaved = true;

			$scope.editMenuData = {};

			$("#menuModal").modal("hide");
		}

		$scope.confirmUpdateMenu = function () {
			var dialogOption = {};
			dialogOption.isCloseButton = false;
			dialogOption.isCloseOnBackground = false;
			dialogOption.title = "<spring:message code='admin.menu_management.confirm_update_menu' />";
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
				$scope.updateMenu();
			};
			showDialog(dialogOption);
		}

		$scope.updateMenu = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/menu_management/update_menu',
				data: {
					menuData: $scope.mainMenuList
				}
			}).then(function successCallback(response) {
				if (response.status == 200) {
					hideLoading();

					if (response.data.response_code == "00") {
						$scope.showUpdateStatus(true, response.data.response_message);

						$scope.isChangesUnsaved = false;
					} else {
						$scope.showUpdateStatus(false, response.data.response_code + " - " + response.data.response_message);
					}
				} else {
					$scope.showUpdateStatus(false, response.data);
				}
			}, function errorCallback(error) {
				hideLoading();
				if (error.status == 401) {
					showSessionExpired();
				} else {
					if (error.data && error.data.length > 0) {
						$scope.showUpdateStatus(false, "(" + error.status + ") " + error.data);
					} else {
						$scope.showUpdateStatus(false, "(" + error.status + ") <spring:message code='gt.error.general.load_error' />");
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

		$scope.showUpdateStatus = function (isSuccess, messageStr) {
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
					$("#menuModal").modal("hide");

					$scope.initData();
				}
			};
			showDialog(dialogOption);
		}

	    angular.element(document).ready(function () {
		    $window.onbeforeunload = function (event) {
			    if ($scope.isChangesUnsaved) {
			    	return "";
				}
			}

		    $scope.setManualHandleRouteChange(true);

		    $scope.$on("$routeChangeStart", function(event, next, current) {
		    	if ($scope.isChangesUnsaved) {
		    		event.preventDefault();
		    		$scope.confirmLeaveWithoutSaving(next.$$route.originalPath);
				} else {
					$scope.doRouteHandling();
				}
		    });

		    $scope.initData();
	    });

	    $scope.confirmLeaveWithoutSaving = function (proceedRoute) {
	    	var dialogOption = {};
			dialogOption.isCloseButton = false;
			dialogOption.isCloseOnBackground = false;
			dialogOption.title = "<spring:message code='gt.confirm_leave_without_saving_data' />";
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

				$timeout(function() {
					$scope.isChangesUnsaved = false;
					$scope.setManualHandleRouteChange(false);
					$location.path(proceedRoute);
				});
			};
			showDialog(dialogOption);
		}

	    $scope.$on('$destroy', function() {
	        $window.onbeforeunload = function () {}
	    });
	});
</script>
