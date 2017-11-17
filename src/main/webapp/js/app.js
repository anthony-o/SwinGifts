'use strict';

angular.module('swingifts', ['ui.router', 'ngCookies', 'angular-loading-bar'])
    .factory('securityService', function ($q, $cookies, $state, $http, $window, $rootScope) {
        var statesToReloadAfterLogin = [],
            securityService = {
                setAuthenticatedUser: function (authenticatedUser, rememberMe) {
                    $rootScope.authenticatedUser = securityService.authenticatedUser = authenticatedUser;
                    if (rememberMe) {
                        $window.localStorage.setItem('authenticatedUser', angular.toJson(authenticatedUser));
                        $window.localStorage.setItem('SWINGIFTS_TOKEN', $cookies.get('SWINGIFTS_TOKEN'));
                    }
                },

                login: function (user, rememberMe, create, eventId, eventKey) {
                    return $q(function (resolve, reject) {
                        function onSuccess(resp) {
                            if (!user.id && (eventId || create)) { // corresponds to "post"s cases
                                if (eventId) {
                                    // the user was created on an event, so the server must have returned an id and a wishListId
                                    user.id = parseInt(resp.data.id);
                                } else {
                                    user.id = parseInt(resp.data);
                                }
                            } else {
                                angular.copy(resp.data, user);
                            }
                            securityService.setAuthenticatedUser(user, rememberMe);

                            // now we resolve all the save states
                            var state;
                            while (state = statesToReloadAfterLogin.pop()) {
                                $state.transitionTo(state.state, state.stateParams, { // reload the state thanks to http://stackoverflow.com/a/23609343/535203
                                    reload: true, inherit: false, notify: true
                                });
                            }
                        }

                        if (user.id) {
                            $http.put('api/persons/' + user.id + '?authenticate=true', user).then(onSuccess, reject);
                        } else {
                            if (eventId) {
                                // this is an authentication from a given event, without user id, that means that it's a new user creation
                                $http.post('api/persons?authenticate=true&eventId=' + eventId + '&eventKey=' + eventKey, user).then(onSuccess, reject);
                            } else {
                                // no event and no id, it is either a user creation or a user authentication
                                if (create) {
                                    $http.post('api/persons?authenticate=true', user).then(onSuccess, reject);
                                } else {
                                    $http.put('api/persons?authenticate=true', user).then(onSuccess, reject);
                                }
                            }
                        }
                    });
                },

                logout: function () {

                    function reallyLogout() {
                        // delete all stored parameters for this user
                        delete $rootScope.authenticatedUser;
                        delete securityService.authenticatedUser;
                        $window.localStorage.removeItem('authenticatedUser');
                        $window.localStorage.removeItem('SWINGIFTS_TOKEN');
                        $cookies.remove('SWINGIFTS_TOKEN');

                        $rootScope.$broadcast('logout');

                        $state.go('home', {}, {reload: true});
                    }

                    var token = $cookies.get('SWINGIFTS_TOKEN');

                    if (token) {
                        var tokenParts = token.split('~'),
                            personId = tokenParts[0],
                            sessionToken = tokenParts[1];
                        $http.delete('api/sessions/?personId=' + personId + '&token=' + sessionToken).then(reallyLogout, reallyLogout);
                    } else {
                        reallyLogout();
                    }
                },

                getAuthenticatedUserPromise: function (state, stateParams) {
                    return $q(function (resolve, reject) {
                        var authenticatedUser = securityService.authenticatedUser;

                        if (!authenticatedUser) {
                            securityService.tryLoginUsingCookieOrLocalStoragePromise().then(resolve, function () {
                                // fails to identify using cookie or local storage, will ask the user to login
                                statesToReloadAfterLogin.push({
                                    state: state,
                                    stateParams: stateParams
                                });
                                reject();
                                $state.go('login');
                            });
                        } else {
                            resolve(authenticatedUser);
                        }
                    });
                },

                tryLoginUsingCookieOrLocalStoragePromise: function () {
                    return $q(function (resolve, reject) {
                        var cookieToken = $cookies.get('SWINGIFTS_TOKEN'),
                            localToken = $window.localStorage.getItem('SWINGIFTS_TOKEN');
                        if (cookieToken) {
                            // Cookie token must have priority to local stored one
                            securityService.tryLoginUsingTokenPromise(cookieToken).then(resolve, reject);
                        } else if (localToken) {
                            securityService.tryLoginUsingTokenPromise(localToken).then(resolve, reject);
                        } else {
                            reject();
                        }
                    });
                },

                tryLoginUsingTokenPromise: function (token) {
                    return $q(function (resolve, reject) {
                        var tokenParts = token.split('~'),
                            personId = tokenParts[0],
                            sessionToken = tokenParts[1];
                        $http.get('api/persons/' + personId + '?sessionToken=' + sessionToken).then(function (resp) {
                            var authenticatedUser = resp.data;
                            if (authenticatedUser) {
                                if (!$cookies.get('SWINGIFTS_TOKEN')) {
                                    // restore cookie
                                    $cookies.put('SWINGIFTS_TOKEN', token);
                                }
                                securityService.setAuthenticatedUser(authenticatedUser);
                                resolve(securityService.authenticatedUser);
                            } else {
                                // session not found
                                reject();
                            }
                        }, reject);
                    })
                }
            };

        return securityService;
    })
    .factory('personService', function ($rootScope, $q) {
        var currentPersonsByIdResolveFns = [],
            personService = {
                setCurrentPersons: function (persons) {
                    $rootScope.currentPersons = persons;
                    var currentPersonsById = $rootScope.currentPersonsById = persons.reduce(function (personsById, person) {
                        personsById[person.id] = person;
                        return personsById;
                    }, {});
                    var resolve;
                    while (resolve = currentPersonsByIdResolveFns.shift()) {
                        resolve(currentPersonsById);
                    }
                },
                getCurrentPersonsByIdPromise: function () {
                    return $q(function (resolve, reject) {
                        var currentPersonsById = $rootScope.currentPersonsById;
                        if (currentPersonsById) {
                            resolve(currentPersonsById);
                        } else {
                            currentPersonsByIdResolveFns.push(resolve);
                        }
                    });
                },
                getCurrentPersonWithIdPromise: function (id) {
                    return $q(function (resolve, reject) {
                        personService.getCurrentPersonsByIdPromise().then(function (currentPersonsById) {
                            resolve(currentPersonsById[id]);
                        }, reject);
                    });
                },
                addNewPersonToCurrentPerson: function(person) {
                    $rootScope.currentPersons.push(person);
                    $rootScope.currentPersonsById[person.id] = person;
                },
                removePersonFromCurrentPerson: function(person) {
                    $rootScope.currentPersons.splice($rootScope.currentPersons.indexOf(person), 1);
                    delete $rootScope.currentPersonsById[person.id];
                }
            }
            ;

        $rootScope.$on('logout', function () {
            currentPersonsByIdResolveFns = [];
            delete $rootScope.currentPersons;
            delete $rootScope.currentPersonsById;
        });

        return personService;
    })

    .factory('eventService', function ($rootScope) {

        $rootScope.$on('logout', function () {
            delete $rootScope.currentEvent;
        });

        return {
            setCurrentEvent: function (event) {
                $rootScope.currentEvent = event;
            }
        }
    })

    .factory('wishListService', function ($rootScope, personService) {
        var wishListService = {
                setMyWishListForCurrentEvent: function (wishList) {
                    $rootScope.myWishListForCurrentEvent = wishList;
                    var circleGiftTargetPersonId = wishList.circleGiftTargetPersonId;
                    if (circleGiftTargetPersonId && (!wishList.circleGiftTargetPerson || wishList.circleGiftTargetPerson.id != circleGiftTargetPersonId)) {
                        personService.getCurrentPersonWithIdPromise(circleGiftTargetPersonId).then(function (person) {
                            wishList.circleGiftTargetPerson = person;
                        });
                    }
                },
                setCurrentWishLists: function (wishLists) {
                    $rootScope.currentWishLists = wishLists;
                    $rootScope.currentWishListsByPersonId = wishLists.reduce(function (wishListsByPersonId, wishList) {
                        wishListsByPersonId[wishList.person.id] = wishList;
                        return wishListsByPersonId;
                    }, {});
                    personService.setCurrentPersons(wishLists.map(function (wishList) {
                        return wishList.person;
                    }));
                },
                addNewWishListToCurrentWishLists: function (wishList) {
                    // only add if not currently already in the list
                    if ($rootScope.currentWishLists.indexOf(wishList) === -1) {
                        $rootScope.currentWishLists.push(wishList);
                    }
                    $rootScope.currentWishListsByPersonId[wishList.person.id] = wishList;
                    personService.addNewPersonToCurrentPerson(wishList.person);
                },
                removeWishListFromCurrentWishLists: function (wishList) {
                    $rootScope.currentWishLists.splice($rootScope.currentWishLists.indexOf(wishList), 1);
                    delete $rootScope.currentWishListsByPersonId[wishList.person.id];
                    personService.removePersonFromCurrentPerson(wishList.person);
                }
            }
            ;

        $rootScope.$on('logout', function () {
            delete $rootScope.myWishListForCurrentEvent;
            delete $rootScope.currentWishLists;
            delete $rootScope.currentWishListsByPersonId;
        });

        return wishListService;
    })
;

