'use strict';

angular.module('swingifts')
    .controller('PersonsInEventCtrl', function ($scope, $http, $stateParams, $state, personService) {
        var eventId = $stateParams.eventId;

        $scope.activate = function (person) {
            var activePerson = $scope.activePerson;
            if (person.id && (!activePerson || person.id !== activePerson.id)) {
                if (activePerson) {
                    activePerson.active = false;
                }
                $state.go('event.personWishList', {personId: person.id});
                $scope.activePerson = person;
            }
        };

        $scope.saveEditedPerson = function (person) {
            delete person.editing;

            if (!person.id) {
                $http.post('api/persons?eventId=' + eventId, person).then(function (resp) {
                    person.id = parseInt(resp.data);
                }, function () {
                    // error: put back the person in edit mode
                    person.editing = true;
                });
            } else {
                //TODO handle update with PUT
            }
        };

        $scope.cancelEditedPerson = function (person) {
            if (!person.id) {
                // cancel new person = remove him/her from the list
                delete person.editing; // in order to active "new person" button
                var persons = $scope.persons,
                    editingPersonIndex = persons.indexOf(person);
                persons.splice(editingPersonIndex, 1);
            } else {
                //TODO handle reload this person from DB
            }
        };

        $scope.addNewPerson = function () {
            var newPerson = {
                editing: true
            };
            $scope.newPerson = newPerson;
            $scope.persons.push(newPerson);
        };


        $http.get('api/persons?eventId=' + eventId).then(function (resp) {
            personService.setCurrentPersons($scope.persons = resp.data);
        });
    })
;