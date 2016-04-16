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

        $scope.reportTables= [
            {id: '1', name: 'All the issues', columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"]},
            {id: '2', name: 'Epic Summary', columns:["Key", "Title", "time original estimate", "time estimate"]},
            {id: '3', name: 'Story Summay', columns:["Key", "Title", "time original estimate", "time estimate"]},
            {id: '4', name: 'Bugs summary', columns:["Key", "Title", "time original estimate", "time estimate"]},
            {id: '5', name: 'Tasks Summary', columns:["Key", "Title", "time original estimate", "time estimate"]},
            {id: '6', name: 'Issues by owner', columns:["Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"]}
        ];

        $scope.addSection = function(){
            $scope.sections.push({});
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


    }
})();
