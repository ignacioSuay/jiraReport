(function() {
    'use strict';

    angular
        .module('jiraReportApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', '$http', '$timeout', 'Upload'];

    function HomeController ($scope, $http, $timeout, Upload) {
        var vm = this;
        $scope.sections=[{}];
        $scope.dynamic = 0;
        $scope.docReady = false;
        $scope.uuid = guid();
        $scope.title = "";
        $scope.fileUpload =false;

        //"Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"
        var columnOps = [{id:"KEY", text: "Key"}, {id:"TITLE", text: "title"},{id:"TYPE", text: "type"},{id:"PRIORITY", text: "Priority"},{id:"STATUS", text: "Status"},{id:"RESOLUTION", text: "Resolution"},
            {id:"CREATED", text: "Created"},{id:"UPDATED", text: "Updated"},{id:"ASSIGNEE", text: "Assignee"},{id:"REPORTER", text: "Reporter"},{id:"SPRINT", text: "Sprint"},
            {id:"TIME_ORIGINAL_ESTIMATE", text: "Original time estimate"},{id:"TIME_ESTIMATE", text: "Time estimate"},{id:"TIME_SPENT", text: "Time spent"}, {id:"KEY", text: "Epic Key"},{id:"KEY", text: "Story Key"},
            {id:"NUMBER_ISSUES", text: "Number of issues"},{id:"SUM_TIME_ORIGINAL_ESTIMATE", text: "Original time estimate"},{id:"SUM_TIME_ESTIMATE", text: "Time estimate"},{id:"SUM_TIME_SPENT", text: "Time spent"}];

        var types = [{text:"Epics", type:"EPIC", default:false},{text:"Stories", type:"STORY", default:true},{text:"Tasks", type:"TASK", default:true},{text:"Bugs", type:"BUG", default:true}, {text:"Sub-tasks", type:"SUB_TASK", default:true}];
        var typesNoEpic = [{text:"Stories", type:"STORY"},{text:"Tasks", type:"TASK"},{text:"Bugs", type:"BUG"}, {text:"Sub-tasks", type:"SUB_TASK"}];

        $scope.reportTables = [
            {id: '1', name: 'Epic Summary', nameId: "EPIC_SUMMARY", columns:[columnOps[14],columnOps[1],columnOps[2],columnOps[3],columnOps[4],columnOps[5],columnOps[6],columnOps[7],columnOps[8],columnOps[9],columnOps[10],columnOps[11],columnOps[12],columnOps[13]], groupBy:[columnOps[17],columnOps[18],columnOps[19], columnOps[16]]},
            {id: '2', name: 'Story Summay', nameId: "STORY_SUMMARY", columns:[columnOps[15],columnOps[1],columnOps[2],columnOps[3],columnOps[4],columnOps[5],columnOps[6],columnOps[7],columnOps[8],columnOps[9],columnOps[10],columnOps[11],columnOps[12],columnOps[13]], groupBy:[columnOps[17],columnOps[18],columnOps[19], columnOps[16]]},
            {id: '3', name: 'Issues by assignee', nameId: "ISSUES_ASSIGNEE", columns:[columnOps[0],columnOps[1],columnOps[2],columnOps[3],columnOps[4],columnOps[5],columnOps[6],columnOps[7],columnOps[8],columnOps[9],columnOps[10],columnOps[11],columnOps[12],columnOps[13]], include:types},
            {id: '4', name: 'Issues by Epic', nameId: "ISSUES_EPIC", columns:[columnOps[0],columnOps[1],columnOps[2],columnOps[3],columnOps[4],columnOps[5],columnOps[6],columnOps[7],columnOps[8],columnOps[9],columnOps[10],columnOps[11],columnOps[12],columnOps[13]], include:typesNoEpic},
            {id: '5', name: 'Issues by Story', nameId: "ISSUES_STORY", columns:[columnOps[0],columnOps[1],columnOps[2],columnOps[3],columnOps[4],columnOps[5],columnOps[6],columnOps[7],columnOps[8],columnOps[9],columnOps[10],columnOps[11],columnOps[12],columnOps[13]],include:types},
            {id: '6', name: 'All the issues', nameId: "ALL_ISSUES", columns:[columnOps[0],columnOps[1],columnOps[2],columnOps[3],columnOps[4],columnOps[5],columnOps[6],columnOps[7],columnOps[8],columnOps[9],columnOps[10],columnOps[11],columnOps[12],columnOps[13]],include:types}
        ];

        $scope.defaultSections = function(){
            $scope.sections = [
                {action:$scope.reportTables[0], columns:{"KEY":true, "TITLE":true}, groupBy:{"SUM_TIME_ORIGINAL_ESTIMATE":true, "NUMBER_ISSUES":true},trueColumns:[]},
                {action:$scope.reportTables[1], columns:{"KEY":true, "TITLE":true, "STATUS":true}, groupBy:{"SUM_TIME_ORIGINAL_ESTIMATE":true, "NUMBER_ISSUES":true}},
                {action:$scope.reportTables[2], columns:{"KEY":true, "TITLE":true, "TYPE":true, "PRIORITY":true, "STATUS":true}},
                {action:$scope.reportTables[3], columns:{"KEY":true, "TITLE":true, "TYPE":true, "PRIORITY":true, "STATUS":true}}
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
            var reportDTO = {title: $scope.title, authors:$scope.authors, sections:[]};
            // reportDTO.sections = [{name:"EPIC_SUMMARY", columns:["TITLE"]}];
            $scope.sections.forEach(function(sec){
                if(sec.columns)
                    var colsTrue = getArrayOfTrueValues(sec.columns);
                if(sec.groupBy)
                    var groupsTrue = getArrayOfTrueValues(sec.groupBy);
                if(sec.include)
                    var includeTrue = getArrayOfTrueValues(sec.include);

              reportDTO.sections.push({name: sec.action.nameId, columns: colsTrue, groupsBy:groupsTrue, include: includeTrue});
            });


            console.log(reportDTO);
            $http({
                method: 'POST',
                url: '/api/report',
                headers: {'Content-Type': undefined },
                transformRequest: function (data) {
                    var formData = new FormData();

                    // formData.append("file", data.file);
                    formData.append('uuid', data.uuid);
                    formData.append('reportDTO', new Blob([angular.toJson(data.reportDTO)], {
                        type: "application/json"
                    }));

                    return formData;
                },
                data: { uuid: $scope.uuid, reportDTO: reportDTO }

            }).
            then(function (data) {
                    $scope.docReady = true;
            },function(response){
                console.log("error sending data");
            });
        };

        $scope.clear = function(){
            $scope.sections = [];
        };

        //Given an object with multiple properties to true or false
        // return an array of the properties to true
        //e.g. {a: true, b:false, c:true} -> [a,c]
        var getArrayOfTrueValues = function(object){
            var cols = Object.keys(object);
            var colsTrue = cols.filter(function(col){
                if(object[col]){return true;}
                return false;
            });
            return colsTrue;
        };


        $scope.uploadFiles = function(file, errFiles) {
            $scope.f = file;
            $scope.errFile = errFiles && errFiles[0];
            if (file) {
                $scope.file = file;
                file.upload = Upload.upload({
                    url: 'api/upload',
                    data: {file: file, uuid: $scope.uuid}
                });

                file.upload.then(function (response) {
                    $timeout(function () {
                        file.result = response.data;
                    });
                    $scope.fileUpload = true;
                }, function (response) {
                    if (response.status > 0)
                        $scope.errorMsg = response.status + ': ' + response.data;
                }, function (evt) {
                    file.progress = Math.min(100, parseInt(100.0 *
                        evt.loaded / evt.total));
                });
            }
        };

        function guid() {
            function s4() {
                return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
            }
            return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
                s4() + '-' + s4() + s4() + s4();
        };
    }
})();
