'use strict';

angular.module('swingifts')
    .controller('EventCtrl', function ($scope, $http, $stateParams, eventService, wishListService, securityService, $timeout) {
        var eventId = $stateParams.eventId,
            timeoutPromise;

        $scope.launchCircleGift = function () {
            $http.post('api/events/' + eventId + '/launchCircleGift', {}).then(function (resp) {
                $scope.wishList.circleGiftTargetPersonId = resp.data.circleGiftTargetPersonId;
                wishListService.setMyWishListForCurrentEvent($scope.wishList);
            });
        };

        $scope.isPersonParticipatesInCircleGiftChanged = function () {
            var wishList = $scope.wishList;
            $http.put('api/wishLists/' + wishList.id, {isPersonParticipatesInCircleGift: wishList.isPersonParticipatesInCircleGift}); // TODO user feedback
        };


        $http.get('api/events/' + eventId).then(function (resp) {
            var event = $scope.event = resp.data;
            eventService.setCurrentEvent(event);
        });

        $http.get('api/wishLists?eventId=' + eventId + '&personId=' + securityService.authenticatedUser.id).then(function (resp) {
            var wishList = $scope.wishList = resp.data;
            wishListService.setMyWishListForCurrentEvent(wishList);
            if (wishList.circleGiftTargetPersonId && !wishList.isCircleGiftTargetPersonIdRead) {
                timeoutPromise = $timeout(function () {
                    $http.post('api/wishLists/' + wishList.id + '/readCircleGiftTargetPersonId', {}).then(function () {
                        wishList.isCircleGiftTargetPersonIdRead = true;
                    })
                }, 5000);
            }
        });

        $scope.$on('$destroy', function () {
            if (timeoutPromise) {
                $timeout.cancel(timeoutPromise);
            }
        });
    })
;