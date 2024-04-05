#!/bin/bash

docker rm -f backend
docker image rm sscwebapphw/backend
docker pull sscwebapphw/backend
docker-compose up --force-recreate -d