aws dynamodb create-table --table-name test --endpoint-url http://10.3.0.233:7000 --key-schema  AttributeName=ID,KeyType=HASH AttributeName=NAME,KeyType=RANGE --attribute-definitions AttributeName=ID,AttributeType=N AttributeName=NAME,AttributeType=S --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

timeout /t 2

aws dynamodb scan --table-name test --endpoint-url http://10.3.0.233:7000

timeout /t 5




--attribute-definitions AttributeName=Artist,AttributeType=S AttributeName=SongTitle,AttributeType=S \
    --key-schema AttributeName=Artist,KeyType=HASH AttributeName=SongTitle,KeyType=RANGE 