<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script>
	app.controller('my_profile_CTRL', function ($scope, $location, $http, $timeout) {
		/* Set Header Title */
		changeTitle("<spring:message code='admin.nav.my_profile' />");

		$scope.currentPassword = null;
		$scope.newPassword = null;
		$scope.confirmNewPassword = null;

		$scope.confirmChangePassword = function () {
			if (!$scope.currentPassword) {
				$scope.showInvalidInput("<spring:message code='gt.input_field.entry_required' />", "current_password");
			} else if (!$scope.newPassword) {
				$scope.showInvalidInput("<spring:message code='gt.input_field.entry_required' />", "new_password");
			} else if (!$scope.confirmNewPassword) {
				$scope.showInvalidInput("<spring:message code='gt.input_field.entry_required' />", "confirm_new_password");
			} else if ($scope.newPassword != $scope.confirmNewPassword) {
				$scope.showInvalidInput("<spring:message code='admin.my_profile.new_and_confirm_does_not_match' />", "new_password");
				$scope.newPassword = null;
				$scope.confirmNewPassword = null;
			} else {
				var dialogOption = {};
				dialogOption.isCloseButton = false;
				dialogOption.isCloseOnBackground = false;
				dialogOption.title = "<spring:message code='admin.my_profile.confirm_change_password' />";
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
						$scope.changePassword();
					});
				};
				showDialog(dialogOption);
			}
		}

		$scope.changePassword = function () {
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/my_profile/change_password',
				data: {
					currentPassword: $scope.currentPassword,
					newPassword: $scope.newPassword
				}
			}).then(function successCallback(response) {
				if (response.status == 200) {
					hideLoading();

					if (response.data.response_code == "00") {
						$scope.showChangePasswordStatus(true, response.data.response_message);
					} else {
						$scope.showChangePasswordStatus(false, response.data.response_code + " - " + response.data.response_message);
					}
				} else {
					$scope.showChangePasswordStatus(false, response.data);
				}
			}, function errorCallback(error) {
				hideLoading();
				if (error.status == 401) {
					showSessionExpired();
				} else {
					if (error.data && error.data.length > 0) {
						$scope.showChangePasswordStatus(false, "(" + error.status + ") " + error.data);
					} else {
						$scope.showChangePasswordStatus(false, "(" + error.status + ") <spring:message code='gt.error.general.load_error' />");
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

		$scope.showChangePasswordStatus = function (isSuccess, messageStr) {
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

				$timeout(function() {
					if (isSuccess) {
						$scope.currentPassword = null;
						$scope.newPassword = null;
						$scope.confirmNewPassword = null;

						showLoading("<spring:message code='gt.loading.loading' />...");

						setTimeout(function(){ 
							location.href = "${pageContext.request.contextPath}/admin/logout";
						}, 500);
					}
				});
			};
			showDialog(dialogOption);
		}

		angular.element(document).ready(function () {
	    });
	});
</script>
