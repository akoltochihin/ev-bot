package com.akoltochihin.evbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.StreamSupport;

public class MalankaService {

    private static final String BOT_ONRENDER_URI = "https://ev-bot.onrender.com/";
    private static final String MALANKA_URI = "https://apigateway.malankabn.by/central-system/api/v1/locations/map/info?locationId=";

    public String getChargerStatus(Chargers charger) {
        try {
            sendKeepAliveRequest();
            var responseBody = getChargersByLocationId(charger.locationId).body();
            return extractConnectorStatusById(responseBody, charger.type);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractConnectorStatusById(String body, String typeEn) throws JsonProcessingException {
        var connectors = new ObjectMapper().readTree(body)
                .path("devices").get(0)
                .path("connectors");

        return StreamSupport.stream(connectors.spliterator(), false)
                .filter(c -> typeEn.equals(c.path("typeEn").asText()))
                .map(c -> c.path("status").asText())
                .findFirst().orElse(null);
    }

    private HttpResponse<String> getChargersByLocationId(String locationId) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder().uri(URI.create(MALANKA_URI + locationId)).build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void sendKeepAliveRequest() throws IOException, InterruptedException {
        HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder().uri(URI.create(BOT_ONRENDER_URI)).build(),
                        HttpResponse.BodyHandlers.ofString()
                );
    }
}
