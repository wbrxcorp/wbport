angular.module("WbPort", ["ngResource","ngSanitize","ui.bootstrap"])
.controller("Login", ["$scope", "$resource", "$window", function($scope, $resource, $window) {
    var login = $resource("./login");
    $scope.login = function() {
        login.save({}, {email:$scope.email,password:$scope.password}, function(result) {
            if (result.success) {
                $window.location.reload();
            } else {
                $scope.error = result.info;
            }
        });
    }
}])
.controller("BasicInfo", ["$scope", "$resource", function($scope, $resource) {
    var info = $resource("./info");
    $scope.info = info.get();
}])
.controller("Server", ["$scope", "$resource", function($scope, $resource) {
    var server = $resource("./server/:fqdn")
    var domain = $resource("./domain")
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
.controller("Signup", ["$scope", "$resource","$window", function($scope, $resource,$window) {
    var signup = $resource("./signup");
    $scope.signup = function() {
        signup.save({}, {email:$scope.email,password:$scope.password}, function(result) {
            if (result.success) {
                $window.location.href = "./";
            } else {
                $scope.error = result.info;
            }
        });
    }
}])
.run(["$rootScope", "$resource", "$window", function($scope, $resource, $window) {
    var logout = $resource("./logout");
    $scope.logout = function() {
        logout.save({}, {}, function(result) {
            $window.location.reload();
        });
    }
}])
