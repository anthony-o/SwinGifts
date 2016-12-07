'use strict';

angular.module('swingifts')
    .controller('HomeCtrl', function ($scope, $http) {
        
        $scope.addNewEvent = function() {
            var newEvent = {
                editing: true
            };
            $scope.newEvent = newEvent;
            $scope.events.push(newEvent);  
        };
        
        $scope.saveEditedEvent = function(event) {
            delete event.editing;

            if (!event.id) {
                $http.post('api/events', event).then(function (resp) {
                    angular.copy(resp.data, event); // copy id + key
                }, function () {
                    // error: put back the event in edit mode
                    event.editing = true;
                });
            } else {
                $http.put('api/events/' + event.id, event).then(function (resp) {
                    // do nothing if OK
                }, function () {
                    // error: put back the event in edit mode
                    event.editing = true;
                });
            }
        };

        $scope.cancelEditedEvent = function (event) {
            delete event.editing; // in order to active "new event" button
            if (!event.id) {
                // cancel new event = remove him/her from the list
                var events = $scope.events,
                    editingEventIndex = events.indexOf(event);
                events.splice(editingEventIndex, 1);
            } else {
                // reload this event from DB
                $http.get('api/events/' + event.id).then(function (resp) {
                    angular.copy(resp.data, event);
                });
            }
        };
        
        $http.get('api/events').then(function (resp) {
            $scope.events = resp.data;
        });
        $scope.events = [];
    })
;