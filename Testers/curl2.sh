#!/bin/bash

for i in `seq 1 1000`;
do

 curl -s -X GET \
 -H "Authorization: Bearer p1psohvv58s6f0hmu0k5ssv89f" \
 -i http://localhost/cloud/feed?id=de7v7ga4b9rjqr6c
 
 	echo
	echo $i
	sleep 0.5
          
done  