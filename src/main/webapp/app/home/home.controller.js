(function() {
    'use strict';

    angular
        .module('jiraReportApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', '$http'];

    function HomeController ($scope, $http) {
        var vm = this;
        $scope.sections=[{}];

        //"Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"
        var columns = [{id:"", text: "Key"}, {id:"", text: "title"},{id:"", text: "type"},{id:"", text: "priority"},{id:"", text: "status"},{id:"", text: "resolution"},
            {id:"", text: "created"},{id:"", text: "updated"},{id:"", text: "assignee"},{id:"", text: "reporter"},{id:"", text: "Sprint"},
            {id:"", text: "time original estimate"},{id:"", text: "time estimate"},{id:"", text: "Epic Key"},{id:"", text: "Story Key"}];

        $scope.reportTables = [
            {id: '1', name: 'Epic Summary', nameId: "EPIC_SUMMARY", columns:["Epic Key", "name", "priority", "status", "resolution", "created", "updated", "assignee", "reporter"], groupBy:["time original estimate", "time estimate", "time spent", "number of issues"]},
            {id: '2', name: 'Story Summay', nameId: "STORY_SUMMARY", columns:["Story Key", "name", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "time spent"], groupBy:["time original estimate", "time estimate", "time spent", "number of issues"]},
            {id: '3', name: 'Issues by owner', nameId: "ISSUES_OWNER", columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "time spent", "Sprint"]},
            {id: '4', name: 'Issues by Epic', nameId: "ISSUES_EPIC", columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"]},
            {id: '5', name: 'Issues by Story', nameId: "ISSUES_STORY", columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"]},
            {id: '6', name: 'All the issues', nameId: "ALL_ISSUES", columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"]}
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
            var reportDTO = {title: $scope.title, authors:$scope.authors};
            reportDTO.sections = [{name:"EPIC_SUMMARY", columns:["TITLE"]}];
            //$scope.sections.forEach(function(sec){
            //   reportDTO.sections.push({name: sec.action.nameId, columns: sec.columns});
            //});


            console.log(reportDTO);
            //console.log(JSON.stringify(reportDTO));
            $http.post("/api/repot", reportDTO).success(function(){
                alert("uee");
            });
        };

        $scope.clear = function(){
            $scope.sections = [];
        }

    }
})();
