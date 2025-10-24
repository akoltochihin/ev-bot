package com.akoltochihin.evbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.StreamSupport;

public class MalankaClient {

    private static final String MALANKA_URI =
            "https://apigateway.malankabn.by/central-system/api/v1/locations/map/info?locationId=8d36c69b-92b9-471a-a5e1-53064e744028";

    public String getConnectorStatus(String connectorId) {
        try {
            var request = HttpRequest.newBuilder().uri(URI.create(MALANKA_URI)).build();
            var responseBody = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body();
            return extractConnectorStatusById(responseBody, connectorId);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractConnectorStatusById(String body, String connectorId) throws JsonProcessingException {
        var connectors = new ObjectMapper().readTree(body)
                .path("devices").get(0)
                .path("connectors");

        return StreamSupport.stream(connectors.spliterator(), false)
                .filter(c -> connectorId.equals(c.path("connectorId").asText()))
                .map(c -> c.path("status").asText())
                .findFirst().orElse(null);
    }
}
