angular.module("WbPortAdmin", ["ngResource","ui.bootstrap"])
.run(["$rootScope","$resource","$modal", function($scope, $resource,$modal) {
    $scope.hoge = "foo";
    $scope.recipients = "";

    $scope.sendEmail = function() {
        var bulkMail = $resource("./api/bulk-mail");

        var recipients = [];
        angular.forEach($scope.recipients.split(/\r\n|\r|\n/), function(value) {
            var trimmed = value.trim();
            if (trimmed != "") this.push({email:trimmed});
        }, recipients);
        var email = {
            subject:$scope.subject,
            from:"support@wbport.com",
            fromName: "wbport.comサポート担当",
            body:$scope.body,
            recipients:recipients
        }

        var modalInstance = $modal.open({
            templateUrl:"progress.html",
            backdrop:"static",keyboard:false
        });
        bulkMail.save(email, function(result) {
            modalInstance.close();
        }, function() {
            modalInstance.close();
        });
    }
}])