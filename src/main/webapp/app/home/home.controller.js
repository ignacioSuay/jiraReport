(function() {
    'use strict';

    angular
        .module('jiraReportApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', '$http'];

    function HomeController ($scope, $http) {
        var vm = this;
        $scope.sections=[{}];
        $scope.dynamic = 0;
        $scope.docReady = false;

        //"Key", "title", "type", "priority", "status", "resolution", "created", "updated", "assignee", "reporter", "time original estimate", "time estimate", "Sprint"
        var columnOps = [{id:"KEY", text: "Key"}, {id:"TITLE", text: "title"},{id:"TYPE", text: "type"},{id:"PRIORITY", text: "priority"},{id:"STATUS", text: "status"},{id:"RESOLUTION", text: "resolution"},
            {id:"CREATED", text: "created"},{id:"UPDATED", text: "updated"},{id:"ASSIGNEE", text: "assignee"},{id:"REPORTER", text: "reporter"},{id:"SPRINT", text: "Sprint"},
            {id:"TIME_ORIGINAL_ESTIMATE", text: "time original estimate"},{id:"TIME_ESTIMATE", text: "time estimate"},{id:"TIME_SPENT", text: "time spent"}, {id:"KEY", text: "Epic Key"},{id:"KEY", text: "Story Key"},
            {id:"NUMBER_ISSUES", text: "number of issues"}];

        $scope.reportTables = [
            {id: '1', name: 'Epic Summary', nameId: "EPIC_SUMMARY", columns:[columnOps[14],columnOps[1],columnOps[2],columnOps[3],columnOps[4],columnOps[5],columnOps[6],columnOps[7],columnOps[8],columnOps[9],columnOps[10],columnOps[11],columnOps[12],columnOps[13]], groupBy:[columnOps[11],columnOps[12],columnOps[13], columnOps[16]]},
            {id: '2', name: 'Story Summay', nameId: "STORY_SUMMARY", columns:[columnOps[15],columnOps[1],columnOps[2],columnOps[3],columnOps[4],columnOps[5],columnOps[6],columnOps[7],columnOps[8],columnOps[9],columnOps[10],columnOps[11],columnOps[12],columnOps[13]], groupBy:[columnOps[11],columnOps[12],columnOps[13], columnOps[16]]},
            {id: '3', name: 'Issues by owner', nameId: "ISSUES_OWNER", columns:[columnOps[0],columnOps[1],columnOps[2],columnOps[3],columnOps[4],columnOps[5],columnOps[6],columnOps[7],columnOps[8],columnOps[9],columnOps[10],columnOps[11],columnOps[12],columnOps[13]]},
            {id: '4', name: 'Issues by Epic', nameId: "ISSUES_EPIC", columns:[columnOps[0],columnOps[1],columnOps[2],columnOps[3],columnOps[4],columnOps[5],columnOps[6],columnOps[7],columnOps[8],columnOps[9],columnOps[10],columnOps[11],columnOps[12],columnOps[13]]},
            {id: '5', name: 'Issues by Story', nameId: "ISSUES_STORY", columns:[columnOps[0],columnOps[1],columnOps[2],columnOps[3],columnOps[4],columnOps[5],columnOps[6],columnOps[7],columnOps[8],columnOps[9],columnOps[10],columnOps[11],columnOps[12],columnOps[13]]},
            {id: '6', name: 'All the issues', nameId: "ALL_ISSUES", columns:[columnOps[0],columnOps[1],columnOps[2],columnOps[3],columnOps[4],columnOps[5],columnOps[6],columnOps[7],columnOps[8],columnOps[9],columnOps[10],columnOps[11],columnOps[12],columnOps[13]]}
        ];

        $scope.defaultSections = function(){
            $scope.sections = [
                {action:$scope.reportTables[0], columns:{"KEY":true}, groupBy:{"TIME_ORIGINAL_ESTIMATE":true, "NUMBER_ISSUES":true},trueColumns:[]},
                {action:$scope.reportTables[1], columns:{"KEY":true, "TITLE":true, "STATUS":true}, groupBy:{"TIME_ORIGINAL_ESTIMATE":true, "NUMBER_ISSUES":true}},
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
              reportDTO.sections.push({name: sec.action.nameId, columns: colsTrue, groupsBy:groupsTrue});
            });


            console.log(reportDTO);
            $http({
                method: 'POST',
                url: '/api/report',
                headers: {'Content-Type': undefined },
                transformRequest: function (data) {
                    var formData = new FormData();

                    formData.append("file", data.file);
                    formData.append('reportDTO', new Blob([angular.toJson(data.reportDTO)], {
                        type: "application/json"
                    }));

                    return formData;
                },
                data: { file: $scope.myFile, reportDTO: reportDTO }

            }).
            then(function (data) {
                    $scope.docReady = true;
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
        }

        $scope.download = function(){
            $http.get("/api/download").success(function(result){alert("ueee")});
        }
    }
})();
