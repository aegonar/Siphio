#!/bin/bash

for i in `seq 1 1000`;
do

 curl -s -X GET \
 -H "Authorization: Bearer cserpdm4i74o3catumpafdfbut" \
 -i http://localhost/cloud/feed?id=iju7do26ucctcoll
 
 	echo
	echo $i
	sleep 0.5
          
done  