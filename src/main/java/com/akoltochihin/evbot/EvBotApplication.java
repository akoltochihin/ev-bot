package com.akoltochihin.evbot;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import java.io.IOException;
import java.net.InetSocketAddress;

public class EvBotApplication {

    private static final Logger logger = LoggerFactory.getLogger(EvBotApplication.class);
    private static final String token = System.getenv("BOT_TOKEN");

    public static void main(String[] args) throws IOException {
        if (token == null) {
            logger.error("Failed to register the Bot: token is null");
            System.exit(1);
        }
        startFakeServer();
        try (var botApp = new TelegramBotsLongPollingApplication()) {
            botApp.registerBot(token, new EvBot(token));
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            logger.info("Application interrupted, shutting down...");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Failed to register the Bot:", e);
        }
    }

    private static void startFakeServer() throws IOException {
        var server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", exchange -> {
            var content = "<html><body><h1>Service is up and running</h1></body></html>";
            exchange.sendResponseHeaders(200, content.length());
            try (var os = exchange.getResponseBody()) {
                os.write(content.getBytes());
            }
        });

        server.setExecutor(null);
        server.start();
    }
}
