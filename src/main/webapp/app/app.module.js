(function() {
    'use strict';

    angular
        .module('jiraReportApp', [
            'ngStorage',
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngCacheBuster',
            'ngFileUpload',
            'ui.bootstrap',
            'ui.bootstrap.datetimepicker',
            'ui.router',
            'infinite-scroll',
            // jhipster-needle-angularjs-add-module JHipster will add new module here
            'angular-loading-bar',
            'duScroll',
            'ngFileUpload'
        ])
        .run(run);

    //Add an offset of 20 pixels for the angular-scroll
    angular.module('jiraReportApp').value('duScrollOffset', 30);

    run.$inject = ['stateHandler'];

    function run(stateHandler) {
        stateHandler.initialize();
    }
})();
