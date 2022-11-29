package momc.bot.handlers;

import lombok.RequiredArgsConstructor;
import momc.bot.CommandType;
import momc.bot.InteractionType;
import net.dv8tion.jda.api.events.GenericEvent;

@RequiredArgsConstructor
public abstract class AbstractInteractionShotgunHandler<T extends GenericEvent> implements InteractionTypeHandler<T> {

    private final CommandType commandType;
    private final InteractionType interactionType;

    @Override
    public boolean isAvailableInteraction(GenericEvent event, Class<T> clazz) {
        return clazz.isInstance(event);
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }

    @Override
    public InteractionType getInteractionType() {
        return interactionType;
    }
}
