docker rm -f $(sudo docker ps -a -q)
docker rmi -f $(sudo docker images -q)
docker-compose up --build -d
