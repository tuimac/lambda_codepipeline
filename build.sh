#!/bin/bash

PJTDIR=src

function createDockerNetwork(){
    # Create original local network
    if [[ -z $(docker network ls | grep serverless) ]]; then
        docker network create \
            --driver=bridge \
            --subnet=172.20.0.0/24 \
            --gateway=172.20.0.1 \
            serverless
    fi
}

function createDynamodbContainer(){
    # Create Local DynamoDB container
    if [[ -z $(docker ps -a | grep dynamodb) ]]; then
        docker run -itd \
            --name dynamodb \
            -p 8000:8000 \
            --network serverless \
            --ip 172.20.0.2 \
            amazon/dynamodb-local
    fi
}

function buildProject(){
    # Build code by maven docker
    if [[ -z $(docker ps -a | grep maven) ]]; then
        docker run -it \
            --name maven \
            -v $(pwd)/${PJTDIR}/src:/usr/src/mymaven \
            -w /usr/src/mymaven \
            maven:3.6.3-amazoncorretto-11 \
            mvn clean package
    else
        docker start maven
        docker exec -i maven mvn clean package
    fi
}

function executeLambdaContainer(){
    # Execute Local Lambda container
    docker run --rm \
        -v $(pwd)/${PJTDIR}:/var/task:ro,delegated \
        -v /tmp:/tmp \
        --network serverless \
        --add-host dynamodb:172.20.0.2 \
        lambci/lambda:java11 \
        com.aws.lambda.Handler
}

function uploadToLambda(){
    sudo chown $(id -u):$(id -g) -R ${PJTDIR}/target
    cp ${PJTDIR}/target/lambda.jar ${PJTDIR}/target/lambda.zip
    echo "Start to send JAR to Lambda..."
    aws lambda update-function-code \
        --function-name test \
        --zip-file fileb://${PWD}/${PJTDIR}/target/lambda.zip
    if [ $? -eq 0 ];then
        echo "Sent JAR to Lambda was sucessed!"
    else
        echo "Sent JAR to Lambda was failed!"
    fi
}

function cleanupEnvironment(){
    docker stop dynamodb
    docker rm dynamodb
}

function main(){
    createDockerNetwork
    createDynamodbContainer
    buildProject
    executeLambdaContainer
    uploadToLambda
    cleanupEnvironment
}

main
