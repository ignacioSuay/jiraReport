(function() {
    'use strict';

    angular
        .module('jiraReportApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService'];

    function HomeController ($scope, Principal, LoginService) {
        var vm = this;
        $scope.sections=[{}];

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }

        $scope.reportTables = [
            {id: '1', name: 'Epic Summary', columns:["Epic Key", "name", "priority", "status", "resolution", "created", "updated", "assignee", "reporter"], groupBy:["time original estimate", "time estimate", "time logged", "number of issues"]},
            {id: '2', name: 'Story Summay', columns:["Epic Key", "name", "priority", "status", "resolution", "created", "updated", "assignee", "reporter"], groupBy:["time original estimate", "time estimate", "time logged", "number of issues"]},
            {id: '3', name: 'Issues by owner', columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "time logged", "Sprint"]},
            {id: '4', name: 'Issues by Epic', columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"]},
            {id: '5', name: 'Issues by Story', columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"]},
            {id: '6', name: 'All the issues', columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"]}
        ];

        $scope.addSection = function(){
            $scope.sections.push({action:$scope.reportTables[0]});
        };

        $scope.removeSection = function(index){
            $scope.sections.splice(index,1);
        };

        $scope.defaultSections = function(){
            $scope.sections = [
                {action:$scope.reportTables[0], columns:{"name":true, "status":true}, groupBy:{"time original estimate":true, "number of issues":true}},
                {action:$scope.reportTables[1], columns:{"Key":true, "name":true, "status":true}, groupBy:{"time original estimate":true, "number of issues":true}},
                {action:$scope.reportTables[2], columns:{"Key":true, "title":true, "type":true, "priority":true, "status":true, "resolution":true, "created":true, "updated":true, "Sprint":true, "time estimate":true}},
                {action:$scope.reportTables[3], columns:{"Key":true, "title":true, "type":true, "priority":true, "status":true, "resolution":true, "created":true, "updated":true, "Sprint":true, "time estimate":true}},


            ];
        };


        $scope.sendData = function(){
            var reportDTO = {title: $scope.title, author:$scope.authors};
            reportDTO.sections = [];
            $scope.sections.forEach(function(sec){
               reportDTO.sections.push({name: sec.action.name, columns: sec.columns});
            });
            alert(JSON.stringify(reportDTO));
        };


    }
})();
