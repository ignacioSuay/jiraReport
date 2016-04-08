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

        $scope.actions = [
            {id: 't1', name: 'Action 1'},
            {id: 't2', name: 'Action 2'},
            {id: 't3', name: 'Action 3'}
        ];
        $scope.selectedAction = $scope.actions[0];
        $scope.setAction = function(action) {
            $scope.selectedAction = action;
            $scope.submit();
        };
        $scope.submit = function() {
            console.log($scope.selectedAction.id);
        };
    }
})();
