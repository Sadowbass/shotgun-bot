package momc.bot.handlers;

import momc.bot.CommandType;
import momc.bot.InteractionType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;

public abstract class AbstractEntitySelectInteractionShotgunBotHandler extends AbstractInteractionShotgunHandler<EntitySelectInteractionEvent> {


    protected AbstractEntitySelectInteractionShotgunBotHandler(CommandType commandType) {
        super(commandType, InteractionType.ENTITY_SELECT);
    }

    @Override
    public void handle(GenericEvent event) {
        if (isAvailableInteraction(event, EntitySelectInteractionEvent.class)) {
            handleInteraction((EntitySelectInteractionEvent) event);
        }
    }
}
