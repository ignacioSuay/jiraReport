(function() {
    'use strict';

    angular
        .module('jiraReportApp')
        .controller('MailController', MailController);

    MailController.$inject = ['$scope', '$http'];

    function MailController ($scope, $http) {
        $scope.emailSent= false;
        $scope.email = {name:"", email:"",phone:"", message:""};

        $(function() {
            $("body").on("input propertychange", ".floating-label-form-group", function(e) {
                $(this).toggleClass("floating-label-form-group-with-value", !! $(e.target).val());
            }).on("focus", ".floating-label-form-group", function() {
                $(this).addClass("floating-label-form-group-with-focus");
            }).on("blur", ".floating-label-form-group", function() {
                $(this).removeClass("floating-label-form-group-with-focus");
            });
        });

        $scope.sendMeEmail = function(){
            alert("ueee");
            $http.post("http://ec2-52-18-48-89.eu-west-1.compute.amazonaws.com:8080/email-spring/api/sendMeEmail", $scope.email).success(function(data){
                $scope.emailSent= true;
            });
        }
    }
})();
