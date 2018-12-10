#!/bin/bash
#
# Exit on first error, print all commands.
set -e

#Start from here
echo -e "\nStopping the previous network (if any)"
docker-compose -f docker-compose.yml down

# Create and Start the Docker containers for the network
echo -e "\nSetting up the Hyperledger Fabric 1.3 network"
docker-compose -f docker-compose.yml up -d
# sleep 15
echo -e "\nNetwork setup completed!!\n"