package com.akoltochihin.evbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class EvBotApplication {

    private static final Logger logger = LoggerFactory.getLogger(EvBotApplication.class);
    private static final String token = System.getenv("BOT_TOKEN");

    public static void main(String[] args) {
        if (token == null) {
            logger.error("Failed to register the Bot: token is null");
            System.exit(1);
        }
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
}
