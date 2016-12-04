'use strict';

angular.module('swingifts')
    .controller('WishListInEventCtrl', function ($scope, $http, $stateParams, $state, personService) {
        var eventId = $stateParams.eventId,
            personId = $stateParams.personId;

        function remapPersonsName(wishItem) {
            if (!wishItem.createdByPerson && wishItem.createdByPersonId) {
                personService.getCurrentPersonWithIdPromise(wishItem.createdByPersonId).then(function (person) {
                    wishItem.createdByPerson = person;
                });
            }
            if (!wishItem.person && wishItem.personId) {
                personService.getCurrentPersonWithIdPromise(wishItem.personId).then(function (person) {
                    wishItem.person = person;
                });
            }
            var reservations = wishItem.reservations;
            if (reservations) {
                for (var i = 0; i < reservations.length; i++) {
                    var reservation = reservations[i];
                    if (!reservation.person && reservation.personId) {
                        personService.getCurrentPersonWithIdPromise(reservation.personId).then(function (person) {
                            reservation.person = person;
                        });
                    }
                }
            }
        }

        function deleteWishItem(wishItem) {
            var wishItems = $scope.wishList.wishItems,
                wishItemIndexToDelete = wishItems.indexOf(wishItem);
            wishItems.splice(wishItemIndexToDelete, 1);
        }

        $scope.activate = function (wishItem) {
            var activeWishItem = $scope.activeWishItem;
            if (wishItem.id && (!activeWishItem || wishItem.id !== activeWishItem.id)) {
                if (activeWishItem) {
                    activeWishItem.active = false;
                }
                $state.go('event.personWishList.wishItem', {wishItemId: wishItem.id, wishItem: wishItem});
                $scope.activeWishItem = wishItem;
            }
        };

        $scope.saveEditedWishItem = function (wishItem) {
            delete wishItem.editing;

            if (!wishItem.id) {
                $http.post('api/wishItems', wishItem).then(function (resp) {
                    wishItem.id = parseInt(resp.data);
                }, function () {
                    // error: put back the wishItem in edit mode
                    wishItem.editing = true;
                });
            } else {
                var wishItemToPut = angular.copy(wishItem);
                delete wishItemToPut.active;

                $http.put('api/wishItems/' + wishItem.id, wishItemToPut).then(function (resp) {
                    // do nothing if OK
                }, function () {
                    // error: put back the wishItem in edit mode
                    wishItem.editing = true;
                });
            }
        };

        $scope.cancelEditedWishItem = function (wishItem) {
            delete wishItem.editing; // in order to active "new wishItem" button
            if (!wishItem.id) {
                // cancel new wishItem = remove him/her from the list
                deleteWishItem(wishItem);
            } else {
                // reload this wishItem from DB
                $http.get('api/wishItems/' + wishItem.id).then(function (resp) {
                    angular.copy(resp.data, wishItem);
                    remapPersonsName(wishItem);
                });
            }
        };

        $scope.addNewWishItem = function () {
            var newWishItem = {
                wishListId: $scope.wishList.id,
                editing: true
            };
            $scope.newWishItem = newWishItem;
            $scope.wishList.wishItems.push(newWishItem);
        };


        $scope.$on('wishItem.deleting', function (event, wishItem) {
            wishItem.deleting = true;
        });
        $scope.$on('wishItem.deletionCanceled', function (event, wishItem) {
            delete wishItem.deleting;
        });
        $scope.$on('wishItem.deleted', function (event, wishItem) {
            deleteWishItem(wishItem);
        });


        $http.get('api/wishLists?eventId=' + eventId + '&personId=' + personId).then(function (resp) {
            var wishList = $scope.wishList = resp.data;
            for (var i = 0; i < wishList.wishItems.length; i++) {
                remapPersonsName(wishList.wishItems[i]);
            }
        });
    })
;