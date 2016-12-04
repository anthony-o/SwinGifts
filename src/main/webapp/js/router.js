'use strict';

angular.module('swingifts')
    .config(function ($stateProvider, $urlRouterProvider) {
        var authenticatedUserResolver = {
            authenticatedUser: function (securityService, $stateParams) {
                return securityService.getAuthenticatedUserPromise(this, $stateParams); // using "this" to get current state thanks to http://stackoverflow.com/a/23088644/535203
            }
        };

        $stateProvider
            .state('home', {
                url: '/home',
                views: {
                    'navbar@': {
                        templateUrl: 'tpl/navbar.html',
                        controller: 'NavbarCtrl',
                        resolve: authenticatedUserResolver
                    },
                    '@': {
                        templateUrl: 'tpl/home.html',
                        controller: 'HomeCtrl',
                        resolve: authenticatedUserResolver
                    },
                    'leftPanel@': {},
                    'centerPanel@': {},
                    'rightPanel@': {}
                }
            })
            .state('login', {
                templateUrl: 'tpl/login.html',
                controller: 'LoginCtrl'
            })
            .state('event', {
                url: '/e/:eventId/:eventKey',
                views: {
                    'navbar@': {
                        templateUrl: 'tpl/navbar.html',
                        controller: 'NavbarCtrl',
                        resolve: authenticatedUserResolver
                    },
                    '@': {
                        templateUrl: 'tpl/event.html',
                        controller: 'EventCtrl',
                        resolve: authenticatedUserResolver
                    },
                    'leftPanel@': {
                        templateUrl: 'tpl/personsInEvent.html',
                        controller: 'PersonsInEventCtrl',
                        resolve: authenticatedUserResolver
                    },
                    'centerPanel@': {},
                    'rightPanel@': {}
                }
            })
            .state('event.personWishList', {
                url: '/p/:personId',
                views: {
                    'centerPanel@': {
                        templateUrl: 'tpl/wishListInEvent.html',
                        controller: 'WishListInEventCtrl',
                        resolve: authenticatedUserResolver
                    },
                    'rightPanel@': {}
                }
            })
            .state('event.personWishList.wishItem', {
                url: '/w/:wishItemId',
                params: {wishItem: null},
                views: {
                    'rightPanel@': {
                        templateUrl: 'tpl/actionsForWishItem.html',
                        controller: 'ActionsForWishItemCtrl',
                        resolve: authenticatedUserResolver
                    }
                }
            })
        ;

        $urlRouterProvider.otherwise('/home'); // thanks to http://stackoverflow.com/a/37307995/535203
    })
;
