package momc.bot.listener_adapter;

import lombok.Getter;

@Getter
public enum CommandType {
    REPORT("report"), HISTORY("history");

    private final String commandString;

    CommandType(String commandString) {
        this.commandString = commandString;
    }
}
