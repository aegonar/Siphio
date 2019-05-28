'use strict';

angular.module('Authentication')

.controller('LoginController',
    ['$scope', '$rootScope', '$location', 'AuthenticationService',
    function ($scope, $rootScope, $location, AuthenticationService) {
        // reset login status
        AuthenticationService.ClearCredentials();

        $scope.login = function () {
            $scope.dataLoading = true;
            AuthenticationService.Login($scope.username, $scope.password, function (response) {
                if (response.success) {
                    // console.log(response);
                    // console.log(response.success);

                    // console.log("data ".concat(response.success.token));
                    // AuthenticationService.SetCredentials($scope.username, $scope.password);
                    // console.log("1");
                    // console.log("1");
                    AuthenticationService.SetCredentials(response.success.token);
                    // console.log("2");
                    $location.path('/');
                } else {
                    $scope.error = response.message;
                    // console.log("false");
                    $scope.dataLoading = false;
                }
            });
        };
    }]);