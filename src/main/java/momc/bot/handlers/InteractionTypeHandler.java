package momc.bot.handlers;

import net.dv8tion.jda.api.events.GenericEvent;

public interface InteractionTypeHandler<T extends GenericEvent> extends ShotgunHandler {

    void handleInteraction(T event);

    boolean isAvailableInteraction(GenericEvent event, Class<T> clazz);
}
