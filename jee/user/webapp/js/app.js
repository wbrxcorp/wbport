//https://groups.google.com/forum/#!topic/angular/FO_PsiwNINU
angular.module("WbPort", ["ngResource","ngSanitize","ui.bootstrap"])
.config(["$locationProvider", function($locationProvider) {
    $locationProvider.html5Mode(true);
}])
.controller("BasicInfo", ["$scope", "$resource", "$modal", function($scope, $resource, $modal) {
    var info = $resource("./api/info");
    var password = $resource("./api/password");
    $scope.info = info.get();
    $scope.changePassword = function() {
        $modal.open({
            templateUrl:"password.html",scope:$scope
        }).result.then(function (result) {
            var modalInstance = $modal.open({
                templateUrl:"progress.html",
                backdrop:"static",keyboard:false
            });
            password.save(result, function(result) {
                modalInstance.close();
                if (result.success) {
                    $scope.message = "password changed.";
                    $scope.info = info.get(); // info refresh
                } else {
                    $scope.message = result.info;
                }
            }, function() {
                modalInstance.close();
                $scope.message = "comm failed";
            });
        });
    }
}])
.controller("Server", ["$scope", "$resource", function($scope, $resource) {
    var server = $resource("./api/server/:fqdn")
    var domain = $resource("./api/domain")
    $scope.servers = server.query();
    $scope.domains = domain.query();

    $scope.addServer = function() {
        var matches = [];
        angular.forEach($scope.domains, function(value) {
            if ($scope.fqdn == value || $scope.fqdn.slice(-value.length - 1) == '.' + value) {
                this.push(value);
            }
        }, matches);
        if ($scope.fqdn.slice(-11) == ".wbport.com") {
            matches.push("wbport.com");
        }

        if (matches.length == 0) {
            $scope.error = "FQDNは wbport.comもしくは承認された独自ドメインに属している必要があります。"
            return;
        }
        server.save({}, {fqdn:$scope.fqdn}, function(result) {
            if (result.success) {
                $scope.error = null;
                $scope.fqdn = null;
                $scope.servers = server.query();
            } else {
                $scope.error = result.info;
            }
        });
    }
    $scope.delServer = function(fqdn) {
        server.delete({fqdn:fqdn},{}, function(result) {
            if (result.success) {
                $scope.servers = server.query();
            }
        });
    }
}])
.run(["$rootScope", "$resource", "$window", function($scope, $resource, $window) {
    var logout = $resource("./api/logout");
    $scope.logout = function() {
        logout.save({}, {}, function(result) {
            $window.location.reload();
        });
    }
    $scope.account = $resource("./api/info").get();
}])
