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

JobViewController.$inject = ["JobService", "$routeParams"];

function JobViewController(JobService, $routeParams) {
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

                for (var i = 0; i < result.entries.length; i++) {
                    var seqs = msa.io.clustal.parse(result.entries[i].alignment);

                    var m = msa({
                        el: document.querySelector("#result-"+ i +" .alignment-viewer"),
                        seqs: seqs
                    });
                    m.render();
                }

                vm.progress = vm.result.entryCount > 0 ? vm.result.entries.length / vm.result.entryCount * 100 : 0;
                vm.progress = vm.progress < 100 ? vm.progress : 100;
            });
}