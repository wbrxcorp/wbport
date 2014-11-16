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
                    $scope.message = "パスワードが変更されました";
                    $scope.info = info.get(); // info refresh
                    $scope.refreshAccount();
                } else {
                    $scope.message = result.info;
                }
            }, function() {
                modalInstance.close();
                $scope.message = "通信エラー";
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
.factory("messageBox", ["$rootScope","$modal", function($rootScope, $modal) {
    return function(message, options, callback, callbackDismiss) {
       var $scope = $rootScope.$new();
       $scope.message = message;
       $scope.options = options;
       $modal.open(
           {
               templateUrl:"messagebox.html",
               scope: $scope
           }
       ).result.then(function (result) {
           if (typeof callback === "undefined") return;
           callback(result);
       }, function() {
           if (typeof callbackDismiss === "undefined") return;
           callbackDismiss(result);
       });
    }
}])
.run(["$rootScope", "$resource", "$window", "$modal", "messageBox", function($scope, $resource, $window, $modal, messageBox) {
    var logout = $resource("./api/logout");
    var quit = $resource("./api/quit");
    $scope.logout = function() {
        logout.save({}, {}, function(result) {
            $window.location.reload();
        });
    }
    $scope.refreshAccount = function() {
        $scope.account = $resource("./api/info").get();
    }

    $scope.refreshAccount();

    $scope.quit = function() {
        $modal.open({
            templateUrl:"quit.html",scope:$scope
        }).result.then(function (password) {
            var modalInstance = $modal.open({
                templateUrl:"progress.html",
                backdrop:"static",keyboard:false
            });
            quit.save({password:password}, function(result) {
                modalInstance.close();
                if (result.success) {
                    $window.location.reload();
                } else {
                    messageBox("退会に失敗しました:" + result.info, {danger:true});
                }
            }, function() {
                modalInstance.close();
                messageBox("通信エラー", {danger:true});
            });
        });
    }
}])
