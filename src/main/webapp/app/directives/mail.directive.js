'use strict';

angular.module('jiraReportApp')
    .directive('suayMail', function () {
        return {
            restrict: 'E',
            scope:false, //use parecent scope
            templateUrl: 'app/directives/mail.template.html',
            link: function($scope, $http){
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
                    var emailParams = $scope.email;
                    emailParams.message = $scope.email.message + "\n" + $scope.uuid;
                    $http.post("http://ec2-52-18-48-89.eu-west-1.compute.amazonaws.com:8080/email-spring/api/sendMeEmail", emailParams).success(function(data){
                        $scope.emailSent= true;
                    });
                }
            }
        }
    });
