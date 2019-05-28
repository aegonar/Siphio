'use strict';

angular.module('profile')

.service('dataService', function() {
    var nextToken;
    var newReplies = [];
    var user;

    return {
        getNextToken: function() {
            return nextToken;
        },
        setNextToken: function(value) {
            nextToken = value;
        },
        getNewReplies: function() {
            return newReplies;
        },
        setNewReplies: function(value) {
            newReplies = value;
        },
        getUser: function() {
            return user;
        },
        setUser: function(value) {
            user = value;
        }
    };
})

.controller('Profile', ['$scope', '$location', '$http', '$route', '$routeParams', '$cookies', '$cookieStore', '$rootScope', 'dataService',
    function($scope, $location, $http, $route, $routeParams, $cookies, $cookieStore, $rootScope, dataService) {

        console.log("route ".concat($routeParams.profileID));
        $scope.target = $routeParams.profileID;
        console.log($scope.target)

        $rootScope.globals = $cookieStore.get('globals') || {};
        // if ($rootScope.globals.currentUser) {
        //     $http.defaults.headers.common['Authorization'] = 'Bearer ' + $rootScope.globals.currentUser.token; // jshint ignore:line
        //     // $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata; // jshint ignore:line
        // }
        //
        console.log("token: ".concat($rootScope.globals.currentUser.token))
        var token = $rootScope.globals.currentUser.token;

        // var config = {
        //     headers: {
        //         'Authorization': 'Bearer '.concat(token)
        //     }
        // };

        // $http.get('http://localhost/cloud/user', config).then(function (response) {
        $http.get('http://localhost/cloud/user').then(function(response) {
            $scope.user = response.data;
            dataService.setUser($scope.user);
        });

        // $http.get('http://localhost/cloud/feed/', config).then(function (response) {
        $http.get('http://siph.io/cloud/profile/'.concat($scope.target)).then(function(response) {

            //var post = response.data.postThread.post;
            console.log("loading profile")

            $scope.posts = [];

            $scope.id = (response.data.id);

            var profile = (response.data.profile.user);

            if (profile.follow == 1) {
                profile.follow_color = "text-blue";
            } else if (profile.follow == 0) {
                profile.follow_color = "text-dark-gray";
            } else {
                console.log("follow error");
            }

            $scope.profile = profile;


            console.log(response.data.profile.user);
            // dataService.setNextToken(response.data.id)

            angular.forEach(response.data.profile.posts, function(post, tkey) {

                console.log(post.postID);
                var updatedPost = post;

                if (updatedPost.favorite == 1) {
                    updatedPost.fav_color = "text-red";
                } else if (updatedPost.favorite == 0) {
                    updatedPost.fav_color = "text-dark-gray";
                } else {
                    console.log("fav error");
                }

                $scope.posts.push(updatedPost);
            });

        });



        $scope.load = function() {

            var nextToken = $scope.id;
            console.log("token ".concat(nextToken));

            // console.log("button pressed")
            // var config = {
            //     headers: {
            //         'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
            //     }
            // };
            //
            // $http.get('http://localhost/cloud/feed'.concat('?id=').concat(nextToken), config).then(function (response) {
            $http.get('http://localhost/cloud/profile/'.concat($scope.target).concat('?id=').concat(nextToken)).then(function(response) {

                //var post = response.data.postThread.post;
                console.log("loading next posts")

                // $scope.threads = [];
                // dataService.setNextToken(response.data.id)

                var posts = $scope.posts;

                angular.forEach(response.data.profile.posts, function(post, tkey) {

                    console.log(post.postID);
                    var updatedPost = post;

                    if (updatedPost.favorite == 1) {
                        updatedPost.fav_color = "text-red";
                    } else if (updatedPost.favorite == 0) {
                        updatedPost.fav_color = "text-dark-gray";
                    } else {
                        console.log("fav error");
                    }

                    // thread.post = updatedPost;
                    //
                    // var updatedReplies = [];
                    // // $scope.replies = dataService.getNewReplies();
                    // angular.forEach(thread.replies, function(reply, tkey) {
                    //     if (reply.vote == 0) {
                    //         reply.upvote_color = "text-dark-gray";
                    //         reply.downvote_color = "text-dark-gray";
                    //         reply.voteTotal_color = "text-dark-gray";
                    //     } else if (reply.vote == 1) {
                    //         reply.upvote_color = "text-green";
                    //         reply.downvote_color = "text-dark-gray";
                    //         reply.voteTotal_color = "text-green";
                    //     } else if (reply.vote == -1) {
                    //         reply.upvote_color = "text-dark-gray";
                    //         reply.downvote_color = "text-red";
                    //         reply.voteTotal_color = "text-red";
                    //     }
                    //
                    //     updatedReplies.push(reply);
                    //     // dataService.setNewReplies($scope.replies);
                    //     // console.log(reply.replyID);
                    //     // console.log(value.downvote_color);
                    //     // console.log(value.voteTotal_color);
                    //     console.log(reply);
                    //
                    // });
                    // console.log("");
                    // thread.replies = updatedReplies
                    $scope.posts.push(updatedPost);
                });
                $scope.posts = posts;

            });
        }

        $scope.favorite = function(postID) {

            console.log("favorite button");
            console.log(postID);

            var posts = $scope.posts;

            var nextFav;

            function fav(posts, postID) {
                for (var i in posts) {
                    if (posts[i].postID == postID) {

                        // var post = posts[i];
                        var prevFav = posts[i].favorite;

                        console.log("prevFav ".concat(prevFav));

                        if (prevFav == 0) {
                            posts[i].fav_color = "text-red";
                            posts[i].favoriteCount = posts[i].favoriteCount + 1;
                            posts[i].favorite = 1;
                            nextFav = 1;
                        } else if (prevFav == 1) {
                            posts[i].fav_color = "text-dark-gray";
                            posts[i].favoriteCount = posts[i].favoriteCount - 1;
                            posts[i].favorite = 0;
                            nextFav = 0;
                        } else {
                            console.log("fav error");
                        }

                        console.log(posts[i]);

                        console.log("nextVote ".concat(nextFav));

                        break;
                    }
                }
            }

            fav(posts, postID);

            console.log("nextFav ".concat(nextFav));

            $scope.posts = posts;


            // var config = {
            //     headers: {
            //         'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
            //     }
            // };
            // $http.post('http://localhost/cloud/favorite?postID='.concat(postID).concat('&favorite=').concat(nextFav), {}, config).then(function (response) {

            $http.post('http://localhost/cloud/favorite?postID='.concat(postID).concat('&favorite=').concat(nextFav), {}).then(function(response) {

            });
        }

        $scope.follow = function(profileID) {

            console.log("follow button");
            console.log(profileID);

            var profile = $scope.profile;

            var prevFollow = profile.follow;
            var nextFollow;

            if (prevFollow == 1) {
                profile.follow_color = "text-dark-gray";
                profile.follower=profile.follower-1;
                profile.follow=0;
                nextFollow = 0;
            } else if (prevFollow == 0) {
                profile.follow_color = "text-blue";
                profile.follower=profile.follower+1;
                profile.follow=1;
                nextFollow = 1;
            } else {
                console.log("follow error");
            }
            console.log("nextFollow ".concat(nextFollow));
            $scope.profile = profile;


            $http.post('http://localhost/cloud/follow?profileID='.concat(profileID).concat('&follow=').concat(nextFollow), {}).then(function(response) {

            });


        }

        $scope.delete = function() {

            return 0;
        }
    }
])
