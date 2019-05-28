angular.module('demo', [])

.service('dataService', function() {
        var nextToken;
        var newReplies = [];

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
            }
        };
    })
    .controller('Thread', function($scope, $http, dataService) {

        var config = {
            headers: {
                'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
            }
        };

        $http.get('http://siph.io/cloud/thread/1', config).
        then(function(response) {
            //$scope.thread = response.data;


            $scope.post = response.data.postThread.post;
            console.log(response.data.id);
            //nextToken = response.data.id;

            dataService.setNextToken(response.data.id)



            $scope.replies = [];
            angular.forEach(response.data.postThread.replies, function(value, tkey) {
                if (value.vote == 0) {
                    value.upvote = "text-muted";
                    value.downvote = "text-muted";
                    value.voteTotal = "text-muted";
                } else if (value.vote == 1) {
                    value.upvote = "text-green";
                    value.downvote = "text-green";
                    value.voteTotal = "text-muted";
                } else if (value.vote == -1) {
                    value.upvote = "text-muted";
                    value.downvote = "text-red";
                    value.voteTotal = "text-red";
                }
                $scope.replies.push(value);
                // console.log(value.color);
                // dataService.setNewReplies($scope.replies)
                console.log(value.replyID);
            });
        });
    })

.controller('CurrentUser', function($scope, $http) {
    var token = 'cserpdm4i74o3catumpafdfbut';
    var config = {
        headers: {
            'Authorization': 'Bearer '.concat(token)
        }
    };

    $http.get('http://siph.io/cloud/user', config).then(function(response) {
        $scope.user = response.data;
        //console.log($scope.user);
    });
})

.controller('More', ['$scope', '$http', 'dataService', function($scope, $http, dataService) {



    $scope.load = function() {
        console.log(dataService.getNextToken());
        console.log(dataService.getNewReplies());

        var nextToken = dataService.getNextToken();
        //console.log($scope.replies);

        console.log("button pressed")
        var config = {
            headers: {
                'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
            }
        };

        $http.get('http://siph.io/cloud/thread/1?id='.concat(nextToken), config).then(function(response) {
            //$scope.thread = response.data;


            // $scope.post = response.data.postThread.post;

            $scope.replies = [];
            $scope.replies = dataService.getNewReplies();
            angular.forEach(response.data.postThread.replies, function(value, tkey) {
                    if (value.vote == 0) {
                        value.upvote = "text-muted";
                        value.downvote = "text-muted";
                        value.voteTotal = "text-muted";
                    } else if (value.vote == 1) {
                        value.upvote = "text-green";
                        value.downvote = "text-green";
                        value.voteTotal = "text-muted";
                    } else if (value.vote == -1) {
                        value.upvote = "text-muted";
                        value.downvote = "text-red";
                        value.voteTotal = "text-red";
                    }


                    $scope.replies.push(value);
                    dataService.setNewReplies($scope.replies)
                    console.log(value.replyID);
                    // console.log(value.downvote);
                    // console.log(value.voteTotal);
                },
                function(failure) { console.log("failed :(", failure); });

        });
    }

    $scope.upvote = function(reply) {

        console.log("upvote button");
        console.log(reply);

        //var nextToken = dataService.getNextToken();
        //console.log($scope.replies);

        console.log("button pressed");


        replies = dataService.getNewReplies();

        console.log(replies);
        var prevVote;
        var nextVote;

        function changeDesc(arr, value) {
            for (var i in arr) {
                if (arr[i].replyID == value) {

                    prevVote=arr[i].vote;
                    console.log("prevVote ".concat(prevVote));

                    if(prevVote==0) {
                        arr[i].upvote = "text-green";
                        arr[i].downvote = "text-muted";
                        arr[i].voteTotal = "text-green";

                        arr[i].voteCount = arr[i].voteCount + 1;
                        arr[i].vote = 1;
                        nextVote=1;
                    }else if(prevVote==-1){
                        arr[i].upvote = "text-green";
                        arr[i].downvote = "text-muted";
                        arr[i].voteTotal = "text-green";
                        arr[i].voteCount = arr[i].voteCount + 2;
                        arr[i].vote = 1;
                        nextVote=1;
                    }else if(prevVote==1) {
                        arr[i].upvote = "text-muted";
                        arr[i].downvote = "text-muted";
                        arr[i].voteTotal = "text-muted";
                        arr[i].voteCount = arr[i].voteCount - 1;
                        arr[i].vote = 0;
                        nextVote = 0;
                    }else{
                        console.log("vote error");
                    }
                    console.log(arr[i]);

                    console.log("nextVote ".concat(nextVote));

                    break;
                }
            }
        }
        changeDesc (replies, reply);


        $scope.replies=replies;


        var config = {
            headers: {
                'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
            }
        };

        $http.get('http://siph.io/cloud/vote?reply='.concat(reply).concat('&vote=').concat(nextVote), config).then(function(response) {

        });

        
    }

    $scope.downvote = function(reply) {

        console.log("downvote button");
        console.log(reply);

        //var nextToken = dataService.getNextToken();
        //console.log($scope.replies);

        console.log("button pressed");


        replies = dataService.getNewReplies();

        console.log(replies);
        var prevVote;
        var nextVote;

        function changeDesc(arr, value) {
            for (var i in arr) {
                if (arr[i].replyID == value) {

                    prevVote=arr[i].vote;
                    console.log("prevVote ".concat(prevVote));

                    if(prevVote==0) {
                        arr[i].upvote = "text-muted";
                        arr[i].downvote = "text-red";
                        arr[i].voteTotal = "text-red";

                        arr[i].voteCount = arr[i].voteCount - 1;
                        arr[i].vote = -1;
                        nextVote=-1;
                    }else if(prevVote==-1){
                        arr[i].upvote = "text-muted";
                        arr[i].downvote = "text-muted";
                        arr[i].voteTotal = "text-muted";
                        arr[i].voteCount = arr[i].voteCount + 1;
                        arr[i].vote = 0;
                        nextVote=0;
                    }else if(prevVote==1) {
                        arr[i].upvote = "text-muted";
                        arr[i].downvote = "text-red";
                        arr[i].voteTotal = "text-red";
                        arr[i].voteCount = arr[i].voteCount - 2;
                        arr[i].vote = -1;
                        nextVote = -1;
                    }else{
                        console.log("vote error");
                    }
                    console.log(arr[i]);

                    console.log("nextVote ".concat(nextVote));

                    break;
                }
            }
        }
        changeDesc (replies, reply);


        $scope.replies=replies;


        var config = {
            headers: {
                'Authorization': 'Bearer cserpdm4i74o3catumpafdfbut'
            }
        };

        $http.get('http://siph.io/cloud/vote?reply='.concat(reply).concat('&vote=').concat(nextVote), config).then(function(response) {

        });


    }

}]);
