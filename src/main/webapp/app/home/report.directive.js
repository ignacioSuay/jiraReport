'use strict';

angular.module('jiraReportApp')
    .directive('isReport', function () {
        return {
            restrict: 'E',
            scope: false, //the directive use the same scope as the parent
            templateUrl: 'app/home/report.template.html'
        }
    });
