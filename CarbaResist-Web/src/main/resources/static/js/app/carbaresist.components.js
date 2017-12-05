/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular
        .module("CarbaResist")
        .directive("jobSubmissionForm", function () {
            return  {
                restrict: "E",
                scope: {
                    submit: '&onSubmit'
                },
                templateUrl: "directives/job-submission-form.html",
                controller: JobSubmissionFormController,
                controllerAs: 'vm'
            };
        })
        .directive("jobList", function () {
            return  {
                restrict: "E",
                templateUrl: "directives/job-list.html",
                scope: {
                    "jobs": "="
                },
                controller: JobListController,
                controllerAs: 'vm'
            };
        })
        .controller("JobSubmissionFormController", JobSubmissionFormController)
        .controller("JobListController", JobListController);

JobSubmissionFormController.$inject = ['$scope', 'JobService'];

function JobSubmissionFormController($scope, JobService) {
    var vm = this;

    vm.jobInfo = {
        jobName: "",
        email: "",
        substitutionMatrix: "",
        genomeIds: [],
        resistanceGeneIds: []
    };

    vm.addGenome = function () {
        vm.jobInfo.genomeIds.push("");
    };

    vm.addResistanceGene = function () {
        vm.jobInfo.resistanceGeneIds.push("");
    };

    vm.submit = function () {

        JobService.submit(vm.jobInfo)
                .then(function (response) {
                    $scope.submit(response.data);

                    vm.jobInfo = {
                        jobName: "",
                        email: "",
                        substitutionMatrix: "",
                        genomeIds: [],
                        resistanceGeneIds: []
                    };
                });
    };
}

JobListController.$inject = ['JobService', '$scope'];

function JobListController(JobService, $scope) {
    var vm = this;
    
    $scope.$watch("jobs", function(newJobs, oldJobs, scope) {
        vm.jobs = newJobs;
    });
    
    
}