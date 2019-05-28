'use strict';

angular.module('thread')
// angular.module('minimal', ['ngCookies'])

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

    .controller('Thread', ['$scope', '$location', '$http', '$route', '$routeParams', '$cookies', '$cookieStore', '$rootScope', 'dataService',
                    function($scope, $location, $http, $route, $routeParams, $cookies, $cookieStore, $rootScope, dataService) {
    // .controller('Thread', ['$scope', '$location', '$http', '$cookies', '$cookieStore', '$rootScope', 'dataService',
    //     function ($scope, $location, $http, $cookies, $cookieStore, $rootScope, dataService) {

            $rootScope.globals = $cookieStore.get('globals') || {};

        console.log("route ".concat($routeParams.postID));

            console.log("token: ".concat($rootScope.globals.currentUser.token))
                        var token = $rootScope.globals.currentUser.token;

        // $scope.target = $location.search()['postID'];
        $scope.target=$routeParams.postID;
        console.log($scope.target);


        // var config = {
        //     headers: {
        //         'Authorization': 'Bearer '.concat(token)
        //     }
        // };
        //
        // $http.get('http://siph.io/cloud/user', config).then(function(response) {
        //     $scope.user = response.data;
        //     dataService.setUser($scope.user);
        // });


        $http.get('http://siph.io/cloud/user').then(function(response) {
            $scope.user = response.data;
            dataService.setUser($scope.user);
        });

        // $http.get('http://siph.io/cloud/thread/'.concat($scope.target), config).then(function(response) {
        $http.get('http://siph.io/cloud/thread/'.concat($scope.target)).then(function(response) {

            var post = response.data.postThread.post;

            if (post.favorite == 1) {
                post.fav_color = "text-red";
            } else if (post.favorite == 0) {
                post.fav_color = "text-dark-gray";
            } else {
                console.log("fav error");
            }

            $scope.post = post;

            $scope.id = (response.data.id);

            // dataService.setNextToken(response.data.id);
            console.log("token ".concat($scope.id));

            $scope.replies = [];
            // $scope.replies = dataService.getNewReplies();
            angular.forEach(response.data.postThread.replies, function(value, tkey) {
                    if (value.vote == 0) {
                        value.upvote_color = "text-dark-gray";
                        value.downvote_color = "text-dark-gray";
                        value.voteTotal_color = "text-dark-gray";
                    } else if (value.vote == 1) {
                        value.upvote_color = "text-green";
                        value.downvote_color = "text-dark-gray";
                        value.voteTotal_color = "text-green";
                    } else if (value.vote == -1) {
                        value.upvote_color = "text-dark-gray";
                        value.downvote_color = "text-red";
                        value.voteTotal_color = "text-red";
                    }

                    $scope.replies.push(value);
                    dataService.setNewReplies($scope.replies)
                    console.log(value.replyID);
                    // console.log(value.downvote_color);
                    // console.log(value.voteTotal_color);
                },
                function(failure) { console.log("failed :(", failure); });

        });

        $scope.upvote = function(reply) {

            console.log("upvote button");
            console.log(reply);

            //var nextToken = dataService.getNextToken();
            //console.log($scope.replies);

            console.log("button pressed");


            // var replies = dataService.getNewReplies();
            var replies = $scope.replies;

            console.log(replies);
            var prevVote;
            var nextVote;

            function changeDesc(arr, value) {
                for (var i in arr) {
                    if (arr[i].replyID == value) {

                        prevVote = arr[i].vote;
                        console.log("prevVote ".concat(prevVote));

                        if (prevVote == 0) {
                            arr[i].upvote_color = "text-green";
                            arr[i].downvote_color = "text-dark-gray";
                            arr[i].voteTotal_color = "text-green";
                            arr[i].voteCount = arr[i].voteCount + 1;
                            arr[i].vote = 1;
                            nextVote = 1;
                        } else if (prevVote == -1) {
                            arr[i].upvote_color = "text-green";
                            arr[i].downvote_color = "text-dark-gray";
                            arr[i].voteTotal_color = "text-green";
                            arr[i].voteCount = arr[i].voteCount + 2;
                            arr[i].vote = 1;
                            nextVote = 1;
                        } else if (prevVote == 1) {
                            arr[i].upvote_color = "text-dark-gray";
                            arr[i].downvote_color = "text-dark-gray";
                            arr[i].voteTotal_color = "text-dark-gray";
                            arr[i].voteCount = arr[i].voteCount - 1;
                            arr[i].vote = 0;
                            nextVote = 0;
                        } else {
                            console.log("vote error");
                        }
                        console.log(arr[i]);

                        console.log("nextVote ".concat(nextVote));

                        break;
                    }
                }
            }
            changeDesc(replies, reply);


            $scope.replies = replies;


            // var config = {
            //     headers: {
            //         'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
            //     }
            // };
            // $http.get('http://siph.io/cloud/vote?reply='.concat(reply).concat('&vote=').concat(nextVote), config).then(function(response) {

            $http.get('http://siph.io/cloud/vote?reply='.concat(reply).concat('&vote=').concat(nextVote)).then(function(response) {

            });


        }

        $scope.downvote = function(reply) {

            console.log("downvote button");
            console.log(reply);

            //var nextToken = dataService.getNextToken();
            //console.log($scope.replies);

            console.log("button pressed");


            var replies = dataService.getNewReplies();
            var replies = dataService.getNewReplies();

            console.log(replies);
            var prevVote;
            var nextVote;

            function changeDesc(arr, value) {
                for (var i in arr) {
                    if (arr[i].replyID == value) {

                        prevVote = arr[i].vote;
                        console.log("prevVote ".concat(prevVote));

                        if (prevVote == 0) {
                            arr[i].upvote_color = "text-dark-gray";
                            arr[i].downvote_color = "text-red";
                            arr[i].voteTotal_color = "text-red";

                            arr[i].voteCount = arr[i].voteCount - 1;
                            arr[i].vote = -1;
                            nextVote = -1;
                        } else if (prevVote == -1) {
                            arr[i].upvote_color = "text-dark-gray";
                            arr[i].downvote_color = "text-dark-gray";
                            arr[i].voteTotal_color = "text-dark-gray";
                            arr[i].voteCount = arr[i].voteCount + 1;
                            arr[i].vote = 0;
                            nextVote = 0;
                        } else if (prevVote == 1) {
                            arr[i].upvote_color = "text-dark-gray";
                            arr[i].downvote_color = "text-red";
                            arr[i].voteTotal_color = "text-red";
                            arr[i].voteCount = arr[i].voteCount - 2;
                            arr[i].vote = -1;
                            nextVote = -1;
                        } else {
                            console.log("vote error");
                        }
                        console.log(arr[i]);

                        console.log("nextVote ".concat(nextVote));

                        break;
                    }
                }
            }
            changeDesc(replies, reply);


            $scope.replies = replies;


            // var config = {
            //     headers: {
            //         'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
            //     }
            // };
            //
            // $http.get('http://siph.io/cloud/vote?reply='.concat(reply).concat('&vote=').concat(nextVote), config).then(function(response) {
            $http.get('http://siph.io/cloud/vote?reply='.concat(reply).concat('&vote=').concat(nextVote)).then(function(response) {

            });


        }

        $scope.reply = function(formData, postID) {

            $scope.formData = {};
            // $scope.postID = {};

            console.log(formData.message);
            console.log($scope.post.postID);

            if (formData.message == null)
                return false;

            //var nextToken = dataService.getNextToken();
            //console.log($scope.replies);

            console.log("button pressed");


            // var replies = dataService.getNewReplies();
            var replies = $scope.replies;

            console.log(replies);

            var user = $scope.user;
            console.log(user);



            // var config = {
            //     headers: {
            //         'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
            //     }
            // };

            // var reply = {
            //     postID: this.postID,
            //     replyMessage: formData.message
            // }st

            // console.log(this.post)st
            // $http.post('http://siph.io/cloud/reply', { postID: postID, replyMessage: formData.message }, config).then(function(response) {

            $http.post('http://siph.io/cloud/reply', { postID: postID, replyMessage: formData.message }).then(function(response) {

                console.log("replyID ".concat(response.data.replyID));

                var newReply = {
                    replyID: response.data.replyID,
                    postID: $scope.post.postID,
                    userID: user.userID,
                    replyMessage: formData.message,
                    dateTime: "Just now",
                    voteCount: 1,,
                    vote: 1,
                    userName: user.userName,
                    name: user.name,
                    lastname: user.lastname,
                    upvote_color: "text-green",
                    downvote_color: "text-dark-gray",
                    voteTotal_color: "text-green"
                }

                console.log(newReply);

                replies.unshift(newReply);

                $scope.replies = replies;

                console.log($scope.post);
                var updatePost = $scope.post
                console.log(updatePost.replyCount);
                updatePost.replyCount = updatePost.replyCount + 1;
                console.log(updatePost.replyCount);
                console.log(updatePost)

                $scope.post = updatePost;
            });


        }

        $scope.load = function() {
            // console.log(dataService.getNextToken());
            // console.log(dataService.getNewReplies());

            var nextToken = $scope.id;
            console.log("token ".concat(nextToken));
            var postID = $scope.post.postID;

            // console.log("button pressed")
            // var config = {
            //     headers: {
            //         'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
            //     }
            // };
            // $http.get('http://siph.io/cloud/thread/'.concat(postID).concat('?id=').concat(nextToken), config).then(function(response) {

            $http.get('http://siph.io/cloud/thread/'.concat(postID).concat('?id=').concat(nextToken)).then(function(response) {
                //$scope.thread = response.data;


                // $scope.post = response.data.postThread.post;

                // $scope.replies = [];
                // $scope.replies = dataService.getNewReplies();
                //
                // $scope.replies = [];
                var replies = $scope.replies;
                // $scope.replies = $scope.replies;
                angular.forEach(response.data.postThread.replies, function(value, tkey) {
                        if (value.vote == 0) {
                            value.upvote_color = "text-dark-gray";
                            value.downvote_color = "text-dark-gray";
                            value.voteTotal_color = "text-dark-gray";
                        } else if (value.vote == 1) {
                            value.upvote_color = "text-green";
                            value.downvote_color = "text-dark-gray";
                            value.voteTotal_color = "text-green";
                        } else if (value.vote == -1) {
                            value.upvote_color = "text-dark-gray";
                            value.downvote_color = "text-red";
                            value.voteTotal_color = "text-red";
                        }


                        replies.push(value);
                        // dataService.setNewReplies($scope.replies)
                        console.log(value.replyID);
                        // console.log(value.downvote_color);
                        // console.log(value.voteTotal_color);
                    }, function(failure) { console.log("failed :(", failure); });
                    $scope.replies = replies;
                 });

            ;
        }

        $scope.favorite = function(postID) {

            console.log("favorite button");
            console.log(postID);

            console.log($scope.post);

            var updatePost = $scope.post

            var prevFav = updatePost.favorite;
            var nextFav;

            if (prevFav == 0) {
                updatePost.fav_color = "text-red";
                updatePost.favoriteCount = updatePost.favoriteCount + 1;
                updatePost.favorite = 1;
                nextFav = 1;
            } else if (prevFav == 1) {
                updatePost.fav_color = "text-dark-gray";
                updatePost.favoriteCount = updatePost.favoriteCount - 1;
                updatePost.favorite = 0;
                nextFav = 0;
            } else {
                console.log("fav error");
            }

            console.log("nextFav ".concat(nextFav));

            console.log(updatePost)
            $scope.post = updatePost;

            //$scope.replies=replies;


            // var config = {
            //     headers: {
            //         'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
            //     }
            // };
            //
            // $http.post('http://siph.io/cloud/favorite?postID='.concat(postID).concat('&favorite=').concat(nextFav), {}, config).then(function(response) {

            $http.post('http://siph.io/cloud/favorite?postID='.concat(postID).concat('&favorite=').concat(nextFav), {}).then(function(response) {

            });


        }
    }])

    .controller('CurrentUser', function($scope, $http) {
        // var token = 'cserpdm4i74o3catumpafdfbut';
        // var config = {
        //     headers: {
        //         'Authorization': 'Bearer '.concat(token)
        //     }
        // };
        // $http.get('http://siph.io/cloud/user', config).then(function(response) {

        $http.get('http://siph.io/cloud/user').then(function(response) {
            $scope.user = response.data;
            dataService.setUser($scope.user);
            //console.log($scope.user);
        });
    })

    // .controller('More', ['$scope', '$http', 'dataService', function($scope, $http, dataService) {
    //
    //
    //
    //     $scope.load = function() {
    //         console.log(dataService.getNextToken());
    //         console.log(dataService.getNewReplies());
    //
    //         var nextToken = dataService.getNextToken();
    //         //console.log($scope.replies);
    //
    //         console.log("button pressed")
    //         var config = {
    //             headers: {
    //                 'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
    //             }
    //         };
    //
    //         $http.get('http://siph.io/cloud/thread/1?id='.concat(nextToken), config).then(function(response) {
    //             //$scope.thread = response.data;
    //
    //
    //             // $scope.post = response.data.postThread.post;
    //
    //             $scope.replies = [];
    //             $scope.replies = dataService.getNewReplies();
    //             angular.forEach(response.data.postThread.replies, function(value, tkey) {
    //                     if (value.vote == 0) {
    //                         value.upvote_color = "text-dark-gray";
    //                         value.downvote_color = "text-dark-gray";
    //                         value.voteTotal_color = "text-dark-gray";
    //                     } else if (value.vote == 1) {
    //                         value.upvote_color = "text-green";
    //                         value.downvote_color = "text-dark-gray";
    //                         value.voteTotal_color = "text-green";
    //                     } else if (value.vote == -1) {
    //                         value.upvote_color = "text-dark-gray";
    //                         value.downvote_color = "text-red";
    //                         value.voteTotal_color = "text-red";
    //                     }
    //
    //
    //                     $scope.replies.push(value);
    //                     dataService.setNewReplies($scope.replies)
    //                     console.log(value.replyID);
    //                     // console.log(value.downvote_color);
    //                     // console.log(value.voteTotal_color);
    //                 },
    //                 function(failure) { console.log("failed :(", failure); });
    //
    //         });
    //     }
    //
    //     $scope.upvote = function(reply) {
    //
    //         console.log("upvote button");
    //         console.log(reply);
    //
    //         //var nextToken = dataService.getNextToken();
    //         //console.log($scope.replies);
    //
    //         console.log("button pressed");
    //
    //
    //         replies = dataService.getNewReplies();
    //
    //         console.log(replies);
    //         var prevVote;
    //         var nextVote;
    //
    //         function changeDesc(arr, value) {
    //             for (var i in arr) {
    //                 if (arr[i].replyID == value) {
    //
    //                     prevVote = arr[i].vote;
    //                     console.log("prevVote ".concat(prevVote));
    //
    //                     if (prevVote == 0) {
    //                         arr[i].upvote_color = "text-green";
    //                         arr[i].downvote_color = "text-dark-gray";
    //                         arr[i].voteTotal_color = "text-green";
    //                         arr[i].voteCount = arr[i].voteCount + 1;
    //                         arr[i].vote = 1;
    //                         nextVote = 1;
    //                     } else if (prevVote == -1) {
    //                         arr[i].upvote_color = "text-green";
    //                         arr[i].downvote_color = "text-dark-gray";
    //                         arr[i].voteTotal_color = "text-green";
    //                         arr[i].voteCount = arr[i].voteCount + 2;
    //                         arr[i].vote = 1;
    //                         nextVote = 1;
    //                     } else if (prevVote == 1) {
    //                         arr[i].upvote_color = "text-dark-gray";
    //                         arr[i].downvote_color = "text-dark-gray";
    //                         arr[i].voteTotal_color = "text-dark-gray";
    //                         arr[i].voteCount = arr[i].voteCount - 1;
    //                         arr[i].vote = 0;
    //                         nextVote = 0;
    //                     } else {
    //                         console.log("vote error");
    //                     }
    //                     console.log(arr[i]);
    //
    //                     console.log("nextVote ".concat(nextVote));
    //
    //                     break;
    //                 }
    //             }
    //         }
    //         changeDesc(replies, reply);
    //
    //
    //         $scope.replies = replies;
    //
    //
    //         var config = {
    //             headers: {
    //                 'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
    //             }
    //         };
    //
    //         $http.get('http://siph.io/cloud/vote?reply='.concat(reply).concat('&vote=').concat(nextVote), config).then(function(response) {
    //
    //         });
    //
    //
    //     }
    //
    //     $scope.downvote = function(reply) {
    //
    //         console.log("downvote button");
    //         console.log(reply);
    //
    //         //var nextToken = dataService.getNextToken();
    //         //console.log($scope.replies);
    //
    //         console.log("button pressed");
    //
    //
    //         replies = dataService.getNewReplies();
    //
    //         console.log(replies);
    //         var prevVote;
    //         var nextVote;
    //
    //         function changeDesc(arr, value) {
    //             for (var i in arr) {
    //                 if (arr[i].replyID == value) {
    //
    //                     prevVote = arr[i].vote;
    //                     console.log("prevVote ".concat(prevVote));
    //
    //                     if (prevVote == 0) {
    //                         arr[i].upvote_color = "text-dark-gray";
    //                         arr[i].downvote_color = "text-red";
    //                         arr[i].voteTotal_color = "text-red";
    //                         arr[i].voteCount = arr[i].voteCount - 1;
    //                         arr[i].vote = -1;
    //                         nextVote = -1;
    //                     } else if (prevVote == -1) {
    //                         arr[i].upvote_color = "text-dark-gray";
    //                         arr[i].downvote_color = "text-dark-gray";
    //                         arr[i].voteTotal_color = "text-dark-gray";
    //                         arr[i].voteCount = arr[i].voteCount + 1;
    //                         arr[i].vote = 0;
    //                         nextVote = 0;
    //                     } else if (prevVote == 1) {
    //                         arr[i].upvote_color = "text-dark-gray";
    //                         arr[i].downvote_color = "text-red";
    //                         arr[i].voteTotal_color = "text-red";
    //                         arr[i].voteCount = arr[i].voteCount - 2;
    //                         arr[i].vote = -1;
    //                         nextVote = -1;
    //                     } else {
    //                         console.log("vote error");
    //                     }
    //                     console.log(arr[i]);
    //
    //                     console.log("nextVote ".concat(nextVote));
    //
    //                     break;
    //                 }
    //             }
    //         }
    //         changeDesc(replies, reply);
    //
    //
    //         $scope.replies = replies;
    //
    //
    //         var config = {
    //             headers: {
    //                 'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
    //             }
    //         };
    //
    //         $http.get('http://siph.io/cloud/vote?reply='.concat(reply).concat('&vote=').concat(nextVote), config).then(function(response) {
    //
    //         });
    //
    //
    //     }
    //
    // }]);
