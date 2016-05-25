(function() {
    'use strict';

    angular
        .module('jiraReportApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService'];

    function HomeController ($scope) {
        var vm = this;
        $scope.sections=[{}];

        $scope.reportTables = [
            {id: '1', name: 'Epic Summary', columns:["Epic Key", "name", "priority", "status", "resolution", "created", "updated", "assignee", "reporter"], groupBy:["time original estimate", "time estimate", "time spent", "number of issues"]},
            {id: '2', name: 'Story Summay', columns:["Story Key", "name", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "time spent"], groupBy:["time original estimate", "time estimate", "time spent", "number of issues"]},
            {id: '3', name: 'Issues by owner', columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "time spent", "Sprint"]},
            {id: '4', name: 'Issues by Epic', columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"]},
            {id: '5', name: 'Issues by Story', columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"]},
            {id: '6', name: 'All the issues', columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"]}
        ];

        $scope.defaultSections = function(){
            $scope.sections = [
                {action:$scope.reportTables[0], columns:{"name":true, "status":true}, groupBy:{"time original estimate":true, "number of issues":true}},
                {action:$scope.reportTables[1], columns:{"Key":true, "name":true, "status":true}, groupBy:{"time original estimate":true, "number of issues":true}},
                {action:$scope.reportTables[2], columns:{"Key":true, "title":true, "type":true, "priority":true, "status":true, "resolution":true, "created":true, "updated":true, "Sprint":true, "time estimate":true}},
                {action:$scope.reportTables[3], columns:{"Key":true, "title":true, "type":true, "priority":true, "status":true, "resolution":true, "created":true, "updated":true, "Sprint":true, "time estimate":true}},
            ];
        };
        $scope.defaultSections();

        $scope.addSection = function(){
            $scope.sections.push({action:$scope.reportTables[0]});
        };

        $scope.removeSection = function(index){
            $scope.sections.splice(index,1);
        };



        $scope.sendData = function(){
            var reportDTO = {title: $scope.title, author:$scope.authors};
            reportDTO.sections = [];
            $scope.sections.forEach(function(sec){
               reportDTO.sections.push({name: sec.action.name, columns: sec.columns});
            });
            alert(JSON.stringify(reportDTO));
        };

        $scope.clear = function(){
            $scope.sections = [];
        }

    }
})();
