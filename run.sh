#!/bin/bash

if docker ps -a -q > /dev/null; then
  docker rm -f $(docker ps -a -q)
fi

if docker images -q > /dev/null; then
  docker rmi -f $(docker images -q)
fi

docker-compose up --build -d
