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
    .directive('isComboInput', function () {
        return {
            restrict: 'E',
            scope:{
                listElements:"=",
                clickFn: "&",
                searchField: "=?",
                searchText: "=?"
            },
            templateUrl: 'app/directives/combo.input.template.html',
            link: function(scope){
                scope.initData = function(){
                    if(typeof scope.searchField === "undefined"){
                        scope.searchField = scope.listElements[0];
                    }
                };
                scope.initData();


                scope.clickList = function(elem){
                    scope.searchField =  elem;
                    if(elem.type === 'drop'){
                        scope.searchText = elem.items[0];
                    }else{
                        scope.searchText = "";
                    }
                };

                scope.clickDropDownList = function(elem){
                    scope.searchText =  elem;
                };

                scope.clickEnter = function(){
                    scope.params = {searchField: scope.searchField,
                        searchText: scope.searchText};
                    scope.clickFn(scope.params);

                };
            }
        }
    });
