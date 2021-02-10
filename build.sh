#!/bin/bash

# Create DynamoDB container
docker run -itd \
    --name dynamodb \
    -p 8000:8000 \
    --network bridge \
    --ip 172.17.0.2 \
    amazon/dynamodb-local

# Build code by maven docker
docker run -it --rm \
    -v $(pwd):/usr/src/mymaven \
    -w /usr/src/mymaven \
    maven:3.6.3-amazoncorretto-11 \
    maven clean install


docker stop dynamodb
docker rm dynamodb
