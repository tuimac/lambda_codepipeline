#!/bin/bash

# Create original local network
docker network create \
    --driver=bridge \
    --subnet=172.20.0.0/24 \
    --gateway=172.20.0.1 \
    serverless \

# Create Local DynamoDB container
if [[ -z $(docker ps -a | grep dynamodb) ]]; then
    docker run -itd \
        --name dynamodb \
        -p 8000:8000 \
        --network serverless \
        --ip 172.20.0.2 \
        amazon/dynamodb-local
fi

# Build code by maven docker
if [[ -z $(docker ps -a | grep maven) ]]; then
    docker run -it \
        --name maven \
        -v $(pwd):/usr/src/mymaven \
        -w /usr/src/mymaven \
        maven:3.6.3-amazoncorretto-11 \
        mvn package
else
    docker start maven
    docker exec -i maven mvn package
fi

# Execute Local Lambda container
docker run --rm \
    -v $(pwd)/target:/var/task:ro,delegated \
    -v /tmp:/tmp \
    --network serverless \
    --add-host dynamodb:172.20.0.2 \
    lambci/lambda:java11 \
    handler.Handler

docker stop dynamodb
docker rm dynamodb
