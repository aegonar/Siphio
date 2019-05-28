'use strict';

angular.module('find')

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

.controller('Find', ['$scope', '$location', '$http', '$cookies', '$cookieStore', '$rootScope', 'dataService', function($scope, $location, $http, $cookies, $cookieStore, $rootScope, dataService) {

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

    // $http.get('http://siph.io/cloud/user', config).then(function (response) {
    $http.get('http://siph.io/cloud/user').then(function(response) {
        $scope.user = response.data;
        dataService.setUser($scope.user);
    });

    // $http.get('http://siph.io/cloud/feed/', config).then(function (response) {
    // $http.get('http://siph.io/cloud/feed/').then(function(response) {
    //
    //     //var post = response.data.postThread.post;
    //     console.log("loading feed")
    //
    //     $scope.threads = [];
    //
    //     $scope.id = (response.data.id);
    //     // dataService.setNextToken(response.data.id)
    //
    //     angular.forEach(response.data.feed, function(thread, tkey) {
    //
    //         console.log(thread.post.postID);
    //         var updatedPost = thread.post;
    //
    //         if (updatedPost.favorite == 1) {
    //             updatedPost.fav_color = "text-red";
    //         } else if (updatedPost.favorite == 0) {
    //             updatedPost.fav_color = "text-dark-gray";
    //         } else {
    //             console.log("fav error");
    //         }
    //
    //         thread.post = updatedPost;
    //
    //         var updatedReplies = [];
    //         // $scope.replies = dataService.getNewReplies();
    //         angular.forEach(thread.replies, function(reply, tkey) {
    //             if (reply.vote == 0) {
    //                 reply.upvote_color = "text-dark-gray";
    //                 reply.downvote_color = "text-dark-gray";
    //                 reply.voteTotal_color = "text-dark-gray";
    //             } else if (reply.vote == 1) {
    //                 reply.upvote_color = "text-green";
    //                 reply.downvote_color = "text-dark-gray";
    //                 reply.voteTotal_color = "text-green";
    //             } else if (reply.vote == -1) {
    //                 reply.upvote_color = "text-dark-gray";
    //                 reply.downvote_color = "text-red";
    //                 reply.voteTotal_color = "text-red";
    //             }
    //
    //             updatedReplies.push(reply);
    //             // dataService.setNewReplies($scope.replies);
    //             // console.log(reply.replyID);
    //             // console.log(value.downvote_color);
    //             // console.log(value.voteTotal_color);
    //             console.log(reply);
    //
    //         });
    //         console.log("");
    //         thread.replies = updatedReplies
    //         $scope.threads.push(thread);
    //     });
    //
    // });

    $scope.upvote = function(postID, replyID) {

        console.log("upvote button");
        console.log(postID);
        console.log(replyID);

        var threads = $scope.threads;

        var nextVote;

        function upv(threads, postID, replyID) {
            for (var i in threads) {
                if (threads[i].post.postID == postID) {

                    var newReplies = threads[i].replies;

                    for (var j in newReplies) {

                        if (newReplies[j].replyID == replyID) {

                            var prevVote = newReplies[j].vote;
                            console.log("prevVote ".concat(prevVote));

                            if (prevVote == 0) {
                                newReplies[j].upvote_color = "text-green";
                                newReplies[j].downvote_color = "text-dark-gray";
                                newReplies[j].voteTotal_color = "text-green";
                                newReplies[j].voteCount = newReplies[j].voteCount + 1;
                                newReplies[j].vote = 1;
                                nextVote = 1;
                            } else if (prevVote == -1) {
                                newReplies[j].upvote_color = "text-green";
                                newReplies[j].downvote_color = "text-dark-gray";
                                newReplies[j].voteTotal_color = "text-green";
                                newReplies[j].voteCount = newReplies[j].voteCount + 2;
                                newReplies[j].vote = 1;
                                nextVote = 1;
                            } else if (prevVote == 1) {
                                newReplies[j].upvote_color = "text-dark-gray";
                                newReplies[j].downvote_color = "text-dark-gray";
                                newReplies[j].voteTotal_color = "text-dark-gray";
                                newReplies[j].voteCount = newReplies[j].voteCount - 1;
                                newReplies[j].vote = 0;
                                nextVote = 0;
                            } else {
                                console.log("vote error");
                            }
                            console.log(newReplies[j]);

                            console.log("nextVote ".concat(nextVote));

                            threads[i].replies = newReplies;

                            break;
                        }
                    }
                }
            }
        }

        upv(threads, postID, replyID);

        $scope.replies = threads;


        // var config = {
        //     headers: {
        //         'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
        //     }
        // };
        //
        // $http.get('http://siph.io/cloud/vote?reply='.concat(replyID).concat('&vote=').concat(nextVote), config).then(function (response) {

        $http.get('http://siph.io/cloud/vote?reply='.concat(replyID).concat('&vote=').concat(nextVote)).then(function(response) {

        });

    }

    $scope.downvote = function(postID, replyID) {

        console.log("downvote button");
        console.log(postID);
        console.log(replyID);

        var threads = $scope.threads;

        var nextVote;

        function upv(threads, postID, replyID) {
            for (var i in threads) {
                if (threads[i].post.postID == postID) {

                    var newReplies = threads[i].replies;

                    for (var j in newReplies) {

                        if (newReplies[j].replyID == replyID) {

                            var prevVote = newReplies[j].vote;
                            console.log("prevVote ".concat(prevVote));

                            if (prevVote == 0) {
                                newReplies[j].upvote_color = "text-dark-gray";
                                newReplies[j].downvote_color = "text-red";
                                newReplies[j].voteTotal_color = "text-red";

                                newReplies[j].voteCount = newReplies[j].voteCount - 1;
                                newReplies[j].vote = -1;
                                nextVote = -1;
                            } else if (prevVote == -1) {
                                newReplies[j].upvote_color = "text-dark-gray";
                                newReplies[j].downvote_color = "text-dark-gray";
                                newReplies[j].voteTotal_color = "text-dark-gray";
                                newReplies[j].voteCount = newReplies[j].voteCount + 1;
                                newReplies[j].vote = 0;
                                nextVote = 0;
                            } else if (prevVote == 1) {
                                newReplies[j].upvote_color = "text-dark-gray";
                                newReplies[j].downvote_color = "text-red";
                                newReplies[j].voteTotal_color = "text-red";
                                newReplies[j].voteCount = newReplies[j].voteCount - 2;
                                newReplies[j].vote = -1;
                                nextVote = -1;
                            } else {
                                console.log("vote error");
                            }
                            console.log(newReplies[j]);

                            console.log("nextVote ".concat(nextVote));

                            threads[i].replies = newReplies;

                            break;
                        }
                    }
                }
            }
        }

        upv(threads, postID, replyID);

        $scope.replies = threads;


        // var config = {
        //     headers: {
        //         'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
        //     }
        // };
        //
        // $http.get('http://siph.io/cloud/vote?reply='.concat(replyID).concat('&vote=').concat(nextVote), config).then(function (response) {

        $http.get('http://siph.io/cloud/vote?reply='.concat(replyID).concat('&vote=').concat(nextVote)).then(function(response) {

        });


    }

    $scope.find = function(formData) {

        console.log("find");

        $scope.formData = {};

        console.log(formData.query);
        // console.log(formData.title);

        if (formData.query == null)
            return false;

        // // if (formData.title == null)
        // //     return false;
        //
        // // console.log("button pressed");
        //
        //
        // // console.log(replies);
        //
        // var user = $scope.user;
        // console.log(user);
        //
        // var post = {
        //     postMessage: formData.title,
        //     postLink: formData.url,
        //     dateTime: "Just now",
        //     favoriteCount: 0,
        //     favorite: 0,
        //     userName: user.userName,
        //     name: user.name,
        //     lastname: user.lastname,
        //     fav_color: "text-dark-gray"
        // }
        //
        // console.log(post);
        //
        // var replies = [];
        //
        // var thread = {};
        // thread.post = post;
        // thread.replies = replies;
        //
        // console.log(thread);

        // $scope.threads.unshift(thread);

        // $scope.replies = replies;
        //
        // console.log($scope.post);
        // var updatePost = $scope.post
        // console.log(updatePost.replyCount);
        // updatePost.replyCount = updatePost.replyCount + 1;
        // console.log(updatePost.replyCount);
        // console.log(updatePost)
        //
        // $scope.post = updatePost;

        // var config = {
        //     headers: {
        //         'Authorization': 'Bearer 6q8u9a70npqq778vp1f4qqnr1l'
        //     }
        // };

        // var reply = {
        //     postID: this.postID,
        //     replyMessage: formData.message
        // }

        // console.log(this.post)

        // $http.post('http://siph.io/cloud/post', {
        //     postMessage: formData.title,
        //     postLink: formData.url
        // }, config).then(function (response) {

        $http.get('http://siph.io/cloud/find/'.concat(formData.query), {

        }).then(function(response) {

            console.log(response.data);
            var results = [];

            angular.forEach(response.data, function(userID, tkey) {
                $http.get('http://siph.io/cloud/user/'.concat(userID)).then(function(response) {
                    results.push(response.data);
                    // dataService.setUser($scope.user);
                });

             });

            console.log(results);

            angular.forEach(results, function(result, tkey) {
                console.log("follow error")
                var profile = result;

                if (profile.follow == 1) {
                    profile.follow_color = "text-blue";
                } else if (profile.follow == 0) {
                    profile.follow_color = "text-dark-gray";
                } else {
                    console.log("follow error");
                }

                // $scope.profile = profile;
                $scope.results.push(profile);
            });

            $scope.results=results;
        });
    }

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
        // $http.get('http://siph.io/cloud/feed'.concat('?id=').concat(nextToken), config).then(function (response) {
        $http.get('http://siph.io/cloud/feed'.concat('?id=').concat(nextToken)).then(function(response) {

            //var post = response.data.postThread.post;
            console.log("loading feed")

            // $scope.threads = [];
            // dataService.setNextToken(response.data.id)

            angular.forEach(response.data.feed, function(thread, tkey) {

                console.log(thread.post.postID);
                var updatedPost = thread.post;

                if (updatedPost.favorite == 1) {
                    updatedPost.fav_color = "text-red";
                } else if (updatedPost.favorite == 0) {
                    updatedPost.fav_color = "text-dark-gray";
                } else {
                    console.log("fav error");
                }

                thread.post = updatedPost;

                var updatedReplies = [];
                // $scope.replies = dataService.getNewReplies();
                angular.forEach(thread.replies, function(reply, tkey) {
                    if (reply.vote == 0) {
                        reply.upvote_color = "text-dark-gray";
                        reply.downvote_color = "text-dark-gray";
                        reply.voteTotal_color = "text-dark-gray";
                    } else if (reply.vote == 1) {
                        reply.upvote_color = "text-green";
                        reply.downvote_color = "text-dark-gray";
                        reply.voteTotal_color = "text-green";
                    } else if (reply.vote == -1) {
                        reply.upvote_color = "text-dark-gray";
                        reply.downvote_color = "text-red";
                        reply.voteTotal_color = "text-red";
                    }

                    updatedReplies.push(reply);
                    // dataService.setNewReplies($scope.replies);
                    // console.log(reply.replyID);
                    // console.log(value.downvote_color);
                    // console.log(value.voteTotal_color);
                    console.log(reply);

                });
                console.log("");
                thread.replies = updatedReplies
                $scope.threads.push(thread);
            });

        });
    }

    $scope.favorite = function(postID) {

        console.log("favorite button");
        console.log(postID);

        var threads = $scope.threads;

        var nextFav;

        function fav(threads, postID) {
            for (var i in threads) {
                if (threads[i].post.postID == postID) {

                    var post = threads[i].post;
                    var prevFav = post.favorite;

                    console.log("prevFav ".concat(prevFav));

                    if (prevFav == 0) {
                        threads[i].post.fav_color = "text-red";
                        threads[i].post.favoriteCount = threads[i].post.favoriteCount + 1;
                        threads[i].post.favorite = 1;
                        nextFav = 1;
                    } else if (prevFav == 1) {
                        threads[i].post.fav_color = "text-dark-gray";
                        threads[i].post.favoriteCount = threads[i].post.favoriteCount - 1;
                        threads[i].post.favorite = 0;
                        nextFav = 0;
                    } else {
                        console.log("fav error");
                    }

                    console.log(post);

                    console.log("nextVote ".concat(nextFav));

                    break;
                }
            }
        }

        fav(threads, postID);

        console.log("nextFav ".concat(nextFav));

        $scope.threads = threads;


        // var config = {
        //     headers: {
        //         'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
        //     }
        // };
        // $http.post('http://siph.io/cloud/favorite?postID='.concat(postID).concat('&favorite=').concat(nextFav), {}, config).then(function (response) {

        $http.post('http://siph.io/cloud/favorite?postID='.concat(postID).concat('&favorite=').concat(nextFav), {}).then(function(response) {

        });
    }

    $scope.delete = function() {

        return 0;
    }
}])
