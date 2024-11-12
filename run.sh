#!/bin/bash

if docker ps -a -q > /dev/null; then
  docker rm -f $(docker ps -a -q)
fi

if docker images -q > /dev/null; then
  docker rmi -f $(docker images | grep "domsommelier-backend-domsommelier-app" | awk 'NR>1 {print $3}')
fi

docker-compose up --build -d
