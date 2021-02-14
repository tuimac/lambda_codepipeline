#!/bin/bash

IP='localhost'

aws dynamodb create-table --table-name test --endpoint-url http://${IP}:7000 --key-schema  AttributeName=ID,KeyType=HASH AttributeName=NAME,KeyType=RANGE --attribute-definitions AttributeName=ID,AttributeType=N AttributeName=NAME,AttributeType=S --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

sleep 2

aws dynamodb scan --table-name test --endpoint-url http://${IP}:7000