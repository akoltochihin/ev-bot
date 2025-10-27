package com.akoltochihin.evbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.akoltochihin.evbot.Commands.START_POLLING;
import static com.akoltochihin.evbot.Commands.STOP_POLLING;
import static java.util.concurrent.TimeUnit.MINUTES;

public class EvBot implements LongPollingSingleThreadUpdateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EvBot.class);

    private final TelegramClient telegramClient;
    private final MalankaService malankaService;
    private final Map<Long, ExecutorService> executors;

    public EvBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
        malankaService = new MalankaService();
        executors = new ConcurrentHashMap<>();
    }

    @Override
    public void consume(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message message = update.getMessage();
                var chatId = message.getChatId();
                var responseMessage = handleCommand(message.getText(), chatId);
                if (responseMessage != null) {
                    sendMessage(chatId, responseMessage);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing update", e);
            sendMessage(update.getMessage().getChatId(), "❌ Sorry, an error occurred while processing your request.");
        }
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build();
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.warn("Failed to send message {} to chatId {}", sendMessage, chatId, e);
        }
    }

    private String handleCommand(String command, Long chatId) {
        return switch (command.toLowerCase()) {
            case START_POLLING -> startPolling(chatId);
            case STOP_POLLING -> stopPolling(chatId);
            default -> Responses.MENU;
        };
    }

    private String stopPolling(Long chatId) {
        var executor = executors.get(chatId);
        if (executor == null) {
            return Responses.POLLING_ERROR_NOT_STARTED;
        }

        executor.shutdown();
        executors.remove(chatId);
        return Responses.POLLING_STOPPED;
    }

    private String startPolling(Long chatId) {
        if (executors.containsKey(chatId)) {
            return Responses.POLLING_ERROR_ALREADY_STARTED;
        }

        var executor = Executors.newScheduledThreadPool(1);
        executors.put(chatId, executor);
        executor.scheduleWithFixedDelay(() -> {
//            var status = MockApi.getStatus();
            var status = malankaService.getConnectorStatus("ed83e8c4-bbed-4e23-81df-454830472f35");
            if ("Available".equals(status)) {
                sendMessage(chatId, Responses.AVAILABLE);
                executor.shutdown();
                executors.remove(chatId);
            }
        }, 0, 1, MINUTES);
        return null;
    }
}
