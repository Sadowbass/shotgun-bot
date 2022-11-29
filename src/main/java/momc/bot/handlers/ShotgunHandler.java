package momc.bot.handlers;

import momc.bot.CommandType;
import momc.bot.InteractionType;
import net.dv8tion.jda.api.events.GenericEvent;

public interface ShotgunHandler {

    void handle(GenericEvent event);

    CommandType getCommandType();

    InteractionType getInteractionType();
}
