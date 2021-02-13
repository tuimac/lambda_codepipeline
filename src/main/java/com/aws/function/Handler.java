package com.aws.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


	@Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
		AmazonDynamoDB client;
		
		final String table = "test";
		final String key = "ID";
		final String value = "3";
		
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
        				new AwsClientBuilder.EndpointConfiguration("http://10.3.0.233:7000", "ap-northeast-1"))
        				.build(); 
        	}
        	DynamoDB dynamoDB = new DynamoDB(client);
        	
        	String output = getItems(dynamoDB, table, key, value);
            
            return response.withStatusCode(200).withBody(output);
        } catch(Exception e) {
            String output = String.format("{ \"message\": \"%s\" }", e);
            return response.withStatusCode(500).withBody(output);
        }
    }
    public static String getItems(DynamoDB client, String tableName, String key, String value) {
        String message;
        try {
        	Table table = client.getTable(tableName);
        	Item item = table.getItem(key, value);
            if (item != null) {
            	message = String.format("{ \"message\": %s\" }", item.toJSONPretty());
            } else {
            	message = String.format("{ \"message\": \"No item found with the key %s!\" }", value);
            }
            return message;
        } catch (AmazonServiceException e) {
        	message = String.format("{ \"message\": \"%s\" }", e);
        	return message;
        }
    }
}