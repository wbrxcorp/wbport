angular.module("WbPortGuest", ["ngResource","ngRoute","ui.bootstrap"])
.config(["$locationProvider","$routeProvider", function($locationProvider, $routeProvider) {
    $routeProvider
        .when("/login", {templateUrl:"login.html",controller:"Login"})
        .when("/signup", {templateUrl:"signup.html",controller:"Signup"})
        .when("/confirm/:auth_token", {templateUrl:"confirm.html",controller:"Confirm"})
        .otherwise({redirectTo:"/login"});
}])
.controller("Login", ["$scope", "$resource", "$window", function($scope, $resource, $window) {
    var login = $resource("./api/login");
    $scope.login = function() {
        login.save({}, {email:$scope.email,password:$scope.password}, function(result) {
            if (result.success) {
                $window.location = "./";
            } else {
                $scope.error = result.info ? result.info : "メールアドレス又はパスワードが正しくありません";
            }
        });
    }
}])
.controller("Signup", ["$scope", "$resource","$window","$modal", function($scope, $resource,$window,$modal) {
    var signup = $resource("./api/signup");
    $scope.signup = function() {
        var modalInstance = $modal.open({
            templateUrl:"progress.html",
            backdrop:"static",keyboard:false
        });
        signup.save({}, {email:$scope.email,password:$scope.password}, function(result) {
            if (result.success) {
                $scope.done = true;
                modalInstance.close();
            } else {
                $scope.error = result.info;
                modalInstance.close();
            }
        },function() {
            $scope.error = "通信エラー";
            modalInstance.close();
        });
    }
}])
.controller("Confirm", ["$scope", "$resource","$location","$routeParams","$window", function($scope, $resource,$location,$routeParams,$window) {
    var confirm = $resource("./api/confirm/:auth_token")
    var auth_token = $routeParams.auth_token;
    $scope.info = confirm.get({auth_token:auth_token});
    $scope.confirm = function() {
        confirm.save({auth_token:auth_token}, {}, function() {
            $window.location = "./";
        })
    }
}])
.run(["$rootScope", "$resource", "$window", function($scope, $resource, $window) {
    $scope.info = $resource("./api/info").get();
}])
