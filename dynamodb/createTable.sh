#!/bin/bash

IP='localhost'
TABLE='test'

aws dynamodb create-table --table-name ${TABLE} --endpoint-url http://${IP}:7000 --key-schema  AttributeName=ID,KeyType=HASH AttributeName=NAME,KeyType=RANGE --attribute-definitions AttributeName=ID,AttributeType=N AttributeName=NAME,AttributeType=S --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

sleep 2

aws dynamodb put-item --table-name ${TABLE} --endpoint-url http://${IP}:7000 --item '{ "ID": { "N": "1" }, "NAME": { "S": "BOB" }, "age": { "N": "24" } }'
aws dynamodb put-item --table-name ${TABLE} --endpoint-url http://${IP}:7000 --item '{ "ID": { "N": "2" }, "NAME": { "S": "SHELLY" }, "age": { "N": "21" } }'
aws dynamodb put-item --table-name ${TABLE} --endpoint-url http://${IP}:7000 --item '{ "ID": { "N": "3" }, "NAME": { "S": "TOM" }, "age": { "N": "30" } }'

aws dynamodb scan --table-name ${TABLE} --endpoint-url http://${IP}:7000