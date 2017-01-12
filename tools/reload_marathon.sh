#!/usr/bin/env bash

echo "Restarting the dcos-marathon service in the Master container..."

# Issue restart command via docker exec in the Master container
docker exec -it dcos-docker-master1 sudo systemctl restart dcos-marathon

echo "--> Restart has finished!"
echo "Status of the dcos-marathon service:"

# Show service status
docker exec -it dcos-docker-master1 sudo systemctl status dcos-marathon
