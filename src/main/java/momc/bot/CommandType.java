package momc.bot;

import lombok.Getter;

@Getter
public enum CommandType {
    REPORT("report"), HISTORY("history"), UNKNOWN("unknown");

    private final String commandString;

    CommandType(String commandString) {
        this.commandString = commandString;
    }

    public boolean isAvailableCommand(String command) {
        return command.toLowerCase().startsWith(this.commandString);
    }
}
