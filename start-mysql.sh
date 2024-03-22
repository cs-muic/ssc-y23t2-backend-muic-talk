#!/bin/bash

docker run -p 127.0.0.1:13306:3306 --name muictalk --env MARIADB_ROOT_PASSWORD=securedpassword --restart=always mariadb:10