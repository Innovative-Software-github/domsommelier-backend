rm -rf ~/projects/domsommelier-backend
git clone git@github.com:Innovative-Software-github/domsommelier-backend.git ~/projects/domsommelier-backend
cd ~/projects/domsommelier-backend

docker rm -f $(sudo docker ps -a -q)
docker rmi -f $(sudo docker images -q)
docker-compose up --build -d
