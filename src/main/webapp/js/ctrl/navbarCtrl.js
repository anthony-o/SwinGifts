'use strict';

angular.module('swingifts')
    .controller('NavbarCtrl', function ($scope, securityService) {
        $scope.logout = function() {
            securityService.logout();
        }
    })
;