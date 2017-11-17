'use strict';

angular.module('swingifts')
    .controller('LoginCtrl', function ($scope, $http, $location, $rootScope, personService, securityService) {

        $scope.activate = function (person) {
            $scope.activePerson = person;
        };

        $scope.declareNewPerson = function () {
            $scope.activePerson = {
                creating: true
            }
        };

        $scope.cancelNewPerson = function () {
            $scope.activePerson = null;
        };

        $scope.login = function () {
            var user = angular.copy($scope.user),
                activePerson = $scope.activePerson,
                rememberMe = user.rememberMe,
                create = activePerson ? activePerson.creating : false;

            delete user.passwordRepeat;
            delete user.rememberMe;
            if (activePerson) {
                if (!activePerson.creating) {
                    user.name = activePerson.name;
                }
                user.id = activePerson.id;
            }

            securityService.login(user, rememberMe, create, $scope.eventId, $scope.eventKey).then(function(resp) {
                $scope.loginError = null;
            }, function(resp) {
                // error while loging
                if (resp.status == '400') {
                    $scope.loginError = $scope.event ? 'Mot de passe incorrect' : 'Login ou mot de passe incorrect';
                } else {
                    $scope.loginError = resp.statusText + " - " + resp.status;
                }
            });
        };

        var eventMatched = /\/e\/([0-9]+)\/([a-zA-Z0-9\-_]+)/.exec($location.path());
        if (eventMatched) {
            var eventId = $scope.eventId = eventMatched[1],
                eventKey = $scope.eventKey = eventMatched[2];
            $http.get('api/persons?eventId=' + eventId + '&eventKey=' + eventKey).then(function (resp) {
                personService.setCurrentPersons($scope.persons = resp.data);
            });
            $http.get('api/events/' + eventId + '?key=' + eventKey).then(function (resp) {
                $scope.event = resp.data;
            });
        }

        $scope.user = {};

    })
;