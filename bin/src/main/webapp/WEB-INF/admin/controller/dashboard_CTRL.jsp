<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script>
	app.controller('dashboard_CTRL', function ($scope, $location, $http, $filter) {
		/* Set Header Title */
		changeTitle("<spring:message code='admin.nav.dashboard' />");

		$scope.initData = function () {
			//Do Nothing At The Moment
			/*showLoading("<spring:message code='gt.loading.loading' />...");
			
			$http({
				method: 'POST',
				url: '${pageContext.request.contextPath}/admin_api/get_store_and_super_group_init_data'
			}).then(function successCallback(response) {
				if (response.status == 200) {
					
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
			});*/
		}

		angular.element(document).ready(function () {
		    $scope.initData();
	    });
	});
</script>
