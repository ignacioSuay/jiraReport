(function() {
    'use strict';

    angular
        .module('jiraReportApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('home', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/home/home.html',
                    controller: 'HomeController',
                    controllerAs: 'vm'
                }
            }
        })
        .state('privacy', {
            parent: 'app',
            url: '/privacy',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/footer/privacy.html'
                }
            }
        })
            .state('terms', {
                parent: 'app',
                url: '/terms',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'app/footer/terms.html'
                    }
                }
            });
    }
})();
