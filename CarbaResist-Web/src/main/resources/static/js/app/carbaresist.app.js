/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

angular
        .module("CarbaResist", ["ngRoute"])
        .config(config);

function config($routeProvider) {
    $routeProvider
            .when("/", {
                templateUrl: "../ang_templates/home.html",
                controller: "HomeController",
                controllerAs: "vm"
            })
            .when("/jobs/:jobId", {
                templateUrl: "../ang_templates/job.html",
                controller: "JobViewController",
                controllerAs: "vm"
            });
}