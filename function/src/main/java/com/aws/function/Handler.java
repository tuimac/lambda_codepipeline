package com.aws.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	
	@Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
		AmazonDynamoDB client;
		String table = "test";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
        	String endpoint = System.getenv("ENDPOINT");
        	if(endpoint == "") {
        		client = AmazonDynamoDBClientBuilder.standard()
        				.withRegion(Regions.AP_NORTHEAST_1)
        				.build(); 
        	}else {
        		client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
        				new AwsClientBuilder.EndpointConfiguration("http://localhost:7000", "ap-northeast-1"))
        				.build(); 
        	}
        	DynamoDB dynamoDB = new DynamoDB(client);
        	
        	String output = getItems(dynamoDB, table, "10");
            
            return response.withStatusCode(200).withBody(output);
        } catch(Exception e) {
            String output = String.format("{ \"message\": \"%s\" }", e);
            return response.withStatusCode(500).withBody(output);
        }
    }
    public static String getItems(DynamoDB client, String tableName, String key) {
        HashMap<String,AttributeValue> key_to_get = new HashMap<String,AttributeValue>();

            key_to_get.put("DATABASE_NAME", new AttributeValue(name));

            GetItemRequest request = null;
            if (projection_expression != null) {
                request = new GetItemRequest()
                    .withKey(key_to_get)
                    .withTableName(table_name)
                    .withProjectionExpression(projection_expression);
            } else {
                request = new GetItemRequest()
                    .withKey(key_to_get)
                    .withTableName(table);
            }

            try {
                Map<String,AttributeValue> returned_item =
                   client.getItem(request).getItem();
                if (returned_item != null) {
                    Set<String> keys = returned_item.keySet();
                    for (String key : keys) {
                        System.out.format("%s: %s\n",
                                key, returned_item.get(key).toString());
                    }
                } else {
                    System.out.format("No item found with the key %s!\n", name);
                }
            } catch (AmazonServiceException e) {
                System.err.println(e.getErrorMessage());
                System.exit(1);
            }
    }
}