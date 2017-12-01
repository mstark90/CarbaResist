/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

angular
        .module("CarbaResist")
        .service("JobService", JobService)
        .service("ErrorService", ErrorService);

JobService.$inject = ['$http', 'ErrorService'];

function JobService($http, ErrorService) {
    var service = {
        submit: submit,
        getJob: getJob,
        getResult: getResult,
        findByEmail: findByEmail,
        findByStatus: findByStatus,
        findAll: findAll
    };
    
    
    function submit(job) {
        ErrorService.hide();
        
        return $http({
            url: "jobs",
            method: "POST",
            data: job
        }).then(function(response) {
            return response.data;
        }, function(response) {
            ErrorService.display("Could not submit the job for processing.");
        }).catch(function(e) {
            ErrorService.display("Could not submit the job for processing.");
        });
    }
    
    function getJob(jobId) {
        ErrorService.hide();
        
        return $http({
            url: "jobs/"+ jobId,
            method: "GET"
        }).then(function(response) {
            return response.data;
        }, function(response) {
            ErrorService.display("Could not get the job at the current time.");
        }).catch(function(e) {
            ErrorService.display("Could not get the job at the current time.");
        });
    }
    
    function getResult(jobId) {
        ErrorService.hide();
        
        return $http({
            url: "jobs/"+ jobId +"/result",
            method: "GET"
        }).then(function(response) {
            return response.data;
        }, function(response) {
            ErrorService.display("Could not get the job result at the current time.");
        }).catch(function(e) {
            ErrorService.display("Could not get the job result at the current time.");
        });
    }
    
    function findByEmail(email) {
        ErrorService.hide();
        
        return $http({
            url: "jobs/email/"+ email,
            method: "GET"
        }).then(function(response) {
            return response.data;
        }, function(response) {
            ErrorService.display("Could not get the jobs at the current time.");
        }).catch(function(e) {
            ErrorService.display("Could not get the jobs at the current time.");
        });
    }
    
    function findByStatus(status) {
        ErrorService.hide();
        
        return $http({
            url: "jobs/status/"+ status,
            method: "GET"
        }).then(function(response) {
            return response.data;
        }, function(response) {
            ErrorService.display("Could not get the jobs at the current time.");
        }).catch(function(e) {
            ErrorService.display("Could not get the jobs at the current time.");
        });
    }
    
    function findAll() {
        ErrorService.hide();
        
        return $http({
            url: "jobs",
            method: "GET"
        }).then(function(response) {
            return response.data;
        }, function(response) {
            ErrorService.display("Could not get the jobs at the current time.");
        }).catch(function(e) {
            ErrorService.display("Could not get the jobs at the current time.");
        });
    }
    
    return service;
}

ErrorService.$inject = ['$rootScope']

function ErrorService($rootScope) {
    var service = {
        display: display,
        hide: hide
    };
    
    function hide() {
        $rootScope.errorMessage = null;
    }
    
    function display(message) {
        $rootScope.errorMessage = message;
    };
    
    return service;
}