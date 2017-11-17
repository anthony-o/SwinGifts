'use strict';

angular.module('swingifts')
    .controller('EventCtrl', function ($scope, $http, $stateParams, eventService, wishListService, securityService, $timeout, $location) {
        var eventId = $stateParams.eventId,
            eventKey = $stateParams.eventKey,
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

        $scope.selectText = function($event) {
            var element = $event.target;
            // Select the text thanks to https://stackoverflow.com/a/1173319/535203
            if (document.selection) {
                var range = document.body.createTextRange();
                range.moveToElementText(element);
                range.select();
            } else if (window.getSelection) {
                var range = document.createRange();
                range.selectNode(element);
                window.getSelection().removeAllRanges();
                window.getSelection().addRange(range);
            }
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

        var protocol = $location.protocol(),
            port = $location.port(),
            defaultPort = (protocol == 'https' && port == 443) || (protocol == 'http' && port == 80);
        $scope.sharingUrl = protocol + '://' + $location.host() + (!defaultPort ? ':' + port : '') + window.location.pathname + '#/e/' + eventId + '/' + eventKey;
    })
;