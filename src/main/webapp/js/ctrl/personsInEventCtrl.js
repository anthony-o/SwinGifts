'use strict';

angular.module('swingifts')
    .controller('PersonsInEventCtrl', function ($scope, $http, $stateParams, $state, wishListService) {
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

        $scope.saveEditedPerson = function (wishList) {
            var person = wishList.person;
            delete person.editing;

            if (!person.id) {
                $http.post('api/persons?eventId=' + eventId, person).then(function (resp) {
                    person.id = resp.data.id;
                    wishList.id = resp.data.wishListId;
                    wishListService.addNewWishListToCurrentWishLists(wishList);
                }, function () {
                    // error: put back the person in edit mode
                    person.editing = true;
                });
            } else {
                //TODO handle update with PUT
            }
        };

        $scope.cancelEditedPerson = function (wishList) {
            var person = wishList.person;

            if (!person.id) {
                // cancel new person = remove him/her from the list
                delete person.editing; // in order to active "new person" button
                var wishLists = $scope.wishLists,
                    editingWishListIndex = wishLists.indexOf(wishList);
                wishLists.splice(editingWishListIndex, 1);
            } else {
                //TODO handle reload this person from DB
            }
        };

        $scope.addNewPerson = function () {
            var newPerson = {
                    editing: true
                },
                newWishList = {
                    person: newPerson
                };
            $scope.newPerson = newPerson;
            $scope.wishLists.push(newWishList);
        };

        $scope.deleteActiveWishList = function () {
            angular.element('#deleteActiveWishListModal').modal('hide');
            var wishListToDelete = null;
            angular.forEach($scope.wishLists, function(wishList) {
                if (wishList.person.id == $scope.activePerson.id) {
                    wishListToDelete = wishList;
                }
            });
            $http.delete('api/wishLists/' + wishListToDelete.id).then(function () {
                $scope.activePerson = null;
                wishListService.removeWishListFromCurrentWishLists(wishListToDelete);
                $state.go('event', {eventId: eventId});
            });
        };

        $scope.resetActivePersonPassword = function() {
            var activePersonToResetPassword = $scope.activePerson;
            $http.delete('api/persons/' + activePersonToResetPassword.id + '?resetPassword=true' + '&eventId=' +eventId).then(function (resp) {
                if (resp.data) {
                    activePersonToResetPassword.isUser = false;
                }
            });
        };


        $http.get('api/wishLists?eventId=' + eventId).then(function (resp) {
            wishListService.setCurrentWishLists($scope.wishLists = resp.data);
        });
    })
;