#!/bin/bash

# Pull latest images
docker pull nginxproxy/nginx-proxy:latest
docker pull nginxproxy/acme-companion:latest

# Remove running nginx-proxy and acme-companion containers
docker rm -f letsencrypt > /dev/null 2>&1 || true
docker rm -f nginx-proxy > /dev/null 2>&1 || true

export DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

# Start nginx-proxy and acme-companion
docker-compose up -d