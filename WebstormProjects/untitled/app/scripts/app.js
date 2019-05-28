'use strict';

// declare modules
angular.module('Authentication', []);
angular.module('Home', []);

angular.module('feed', ['ngCookies']);
angular.module('thread', ['ngCookies']);
angular.module('profile', ['ngCookies']);
angular.module('find', ['ngCookies']);


angular.module('BasicHttpAuthExample', [
    'Authentication',
    'Home',
    'ngRoute',
    'feed',
    'thread',
    'profile',
    'find',
    // 'dataService',
    'ngCookies'
])

.config(['$routeProvider', function ($routeProvider) {

    $routeProvider
        .when('/login', {
            controller: 'LoginController',
            templateUrl: 'modules/authentication/views/login.html'
        })

        .when('/', {
            // controller: 'HomeController',
            // templateUrl: 'modules/home/views/home.html'
            controller: 'Feed',
            templateUrl: 'feed.html'

        })

        .when('/thread/:postID' , {
            controller: 'Thread',
            templateUrl: 'thread.html'
        })

        .when('/profile/:profileID' , {
            controller: 'Profile',
            templateUrl: 'profile.html'
        })

        .when('/find' , {
            controller: 'Find',
            templateUrl: 'find.html'
        })

        .otherwise({ redirectTo: '/login' });
}])

.run(['$rootScope', '$location', '$cookieStore', '$http',
    function ($rootScope, $location, $cookieStore, $http) {
        // keep user logged in after page refresh
        $rootScope.globals = $cookieStore.get('globals') || {};
        if ($rootScope.globals.currentUser) {
            $http.defaults.headers.common['Authorization'] = 'Bearer ' + $rootScope.globals.currentUser.token; // jshint ignore:line
            // $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata; // jshint ignore:line
        }

        $rootScope.$on('$locationChangeStart', function (event, next, current) {
            // redirect to login page if not logged in
            if ($location.path() !== '/login' && !$rootScope.globals.currentUser) {
                $location.path('/login');
            }
        });
    }]);
