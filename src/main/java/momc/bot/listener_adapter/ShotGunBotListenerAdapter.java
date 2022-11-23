package momc.bot.listener_adapter;

import momc.bot.ModifiedData;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class ShotGunBotListenerAdapter extends ListenerAdapter {

    protected final CommandType type;

    protected ShotGunBotListenerAdapter(CommandType type) {
        this.type = type;
    }

    public CommandType getType() {
        return type;
    }

    protected boolean isAvailableCommand(String commandName) {
        return this.type.getCommandString().equals(commandName);
    }

    public abstract boolean isSupportHistory();

    public abstract ModifiedData history(Guild guild);
}
