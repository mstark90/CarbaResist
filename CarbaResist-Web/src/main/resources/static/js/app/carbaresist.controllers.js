/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


angular
        .module("CarbaResist")
        .controller("HomeController", HomeController)
        .controller("JobViewController", JobViewController);

HomeController.$inject = ["JobService"];

function HomeController(JobService) {
    var vm = this;

    vm.jobs = [];

    JobService.findAll()
            .then(function (jobs) {
                vm.jobs = vm.jobs.concat(jobs);
            });

    vm.onJobSubmit = function (job) {
        vm.jobs.push(job);
    };
}

JobViewController.$inject = ["JobService", "$routeParams", "$scope", "$log"];

function JobViewController(JobService, $routeParams, $scope, $log) {
    var vm = this;

    vm.result = null;
    vm.progress = 0;

    $("#accordion").collapse();

    JobService.getJob($routeParams["jobId"])
            .then(function (job) {
                vm.job = job;
            });

    JobService.getResult($routeParams["jobId"])
            .then(function (result) {
                vm.result = result;

                vm.progress = vm.result.entryCount > 0 ? vm.result.entries.length / vm.result.entryCount * 100 : 0;
                vm.progress = vm.progress < 100 ? vm.progress : 100;

            });

    $scope.$watch(function () {
        return vm.result != null && angular.element(".alignment-viewer").length === vm.result.entries.length;
    }, function () {
        if(vm.result === null) {
            return;
        }
        
        for (var i = 0; i < vm.result.entries.length; i++) {
            try {
                var entry = vm.result.entries[i];
                var resultElement = "#result-" + i + " .alignment-viewer";

                var seqs = msa.io.clustal.parse(entry.alignment);

                var m = msa({
                    el: document.querySelector(resultElement),
                    seqs: seqs
                });
                m.render();
            } catch (e) {
                $log.warn(e);
            }
        }

    });
}