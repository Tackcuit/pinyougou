app.controller("indexController", function ($scope, loginService) {

    $scope.showLoginName = function () {
        loginService.showName().success(function (response) {
            $scope.loginName = response.name;
        });
    }

});