'use strict';

angular.module('jiraReportApp')
    .directive('isEnter', function () {
        return function (scope, element, attrs) {
            element.bind("keydown keypress", function (event) {
                if(event.which === 13) {
                    scope.$apply(function (){
                        scope.$eval(attrs.wwEnter);
                    });

                    event.preventDefault();
                }
            });
        };
    })
    .directive('suayComboInput', function () {
        return {
            restrict: 'E',
            scope:{
                listElements:"=",
                clickFn: "&",
                searchField: "=?",
                searchText: "=?"
            },
            templateUrl: 'app/directives/combo.input.template.html',
            link: function($scope){
                $scope.actions = $scope.listElements;
                $scope.selectedAction = $scope.actions[0];
                $scope.setAction = function(action) {
                    $scope.selectedAction = action;
                    $scope.submit();
                };
                $scope.submit = function() {
                    console.log($scope.selectedAction.id);
                };
            }
        }
    });
