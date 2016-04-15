(function() {
    'use strict';

    angular
        .module('jiraReportApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService'];

    function HomeController ($scope, Principal, LoginService) {
        var vm = this;

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
            {id: '1', name: 'All the issues'},
            {id: '2', name: 'Epic Summary'},
            {id: '3', name: 'Story Summay'},
            {id: '4', name: 'Bugs summary'},
            {id: '5', name: 'Tasks Summary'},
            {id: '6', name: 'Issues by owner'}
        ];
    }
})();
