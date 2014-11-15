angular.module("WbPortAdmin", ["ngResource","ui.bootstrap"])
.run(["$rootScope", function($scope) {
    $scope.hoge = "foo";
}])