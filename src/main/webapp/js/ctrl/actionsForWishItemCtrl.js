'use strict';

angular.module('swingifts')
    .controller('ActionsForWishItemCtrl', function ($scope, $http, $stateParams, securityService, $state, $rootScope) {
        var authenticatedUser = securityService.authenticatedUser,
            wishItem = $stateParams.wishItem,
            wishItemId = $stateParams.wishItemId,
            currentWishListPersonId = $stateParams.personId,
            myPersonId = authenticatedUser.id;

        function searchMyReservationInReservations(reservations) {
            if (reservations) {
                for (var i = 0; i < reservations.length; i++) {
                    if (reservations[i].personId == myPersonId) {
                        return reservations[i];
                    }
                }
            }
        }

        function removeReservationFromReservations(reservation, reservations) {
            reservations.splice(reservations.indexOf(reservation), 1);
        }

        $scope.reserve = function () {
            var reservation = {
                    personId: myPersonId,
                    wishItemId: wishItemId
                },
                reservationToPost = angular.copy(reservation);

            reservation.person = {
                name: authenticatedUser.name
            };

            var reservations = wishItem.reservations = wishItem.reservations || [];
            reservations.push(reservation);

            $scope.reserving = reservations;

            $http.post('api/reservations', reservationToPost).then(function (resp) {
                reservation.id = parseInt(resp.data);
                $scope.myReservation = reservation;
                $scope.reserving = null;
            }, function () {
                // problem while creating the reservation, let's remove it
                removeReservationFromReservations(reservation, reservations);
                $scope.reserving = null;
            });
        };

        $scope.deleteReservation = function () {
            var reservations = wishItem.reservations,
                reservation = $scope.myReservation;

            removeReservationFromReservations(reservation, reservations);
            $scope.deletingReservation = reservation;
            $scope.myReservation = null;

            $http.delete('api/reservations/' + reservation.id).then(function () {
                $scope.deletingReservation = null;
            }, function () {
                // problem while deleting the reservation, let's add it again
                reservations.push(reservation);
                $scope.deletingReservation = null;
                $scope.myReservation = reservation;
            });
        };

        $scope.edit = function() {
            $scope.wishItem.editing = true;
        };

        $scope.delete = function() {
            $state.go('event.personWishList', {personId: currentWishListPersonId});
            $rootScope.$broadcast('wishItem.deleting', wishItem);
            $http.delete('api/wishItems/' + wishItemId).then(function () {
                $rootScope.$broadcast('wishItem.deleted', wishItem);
            }, function () {
                // A problem happened, restore the wishItem
                $rootScope.$broadcast('wishItem.deletionCanceled', wishItem);
            });
        };

        $scope.wishItem = wishItem;
        // check if I have already reserved this wishitem
        var myReservation = searchMyReservationInReservations(wishItem.reservations);
        if (myReservation) {
            $scope.myReservation = myReservation;
        }
        $scope.wishItemIsMine = wishItem.personId == myPersonId;
        $scope.myPersonId = myPersonId;
    })
;