package com.akoltochihin.evbot;

import static com.akoltochihin.evbot.Commands.START_POLLING_RODINA;
import static com.akoltochihin.evbot.Commands.START_POLLING_YAKUBOVSKOGO;
import static com.akoltochihin.evbot.Commands.STOP_POLLING;

public class Responses {

    public static final String AVAILABLE = "🟢 Charger is Available";

    public static final String POLLING_ERROR_ALREADY_STARTED = "⚠\uFE0F Polling is already started";
    public static final String POLLING_ERROR_NOT_STARTED = "⚠\uFE0F No active polling";
    public static final String POLLING_STOPPED = "✅ Polling is stopped";
    public static final String POLLING_STARTED = """
            Polling is started
            Current status: 
            """;

    public static final String MENU = """
            %s
            
            %s
            
            %s
            """.formatted(START_POLLING_RODINA, START_POLLING_YAKUBOVSKOGO, STOP_POLLING);

}
