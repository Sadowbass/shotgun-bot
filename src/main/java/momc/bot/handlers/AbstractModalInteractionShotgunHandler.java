package momc.bot.handlers;

import momc.bot.CommandType;
import momc.bot.InteractionType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public abstract class AbstractModalInteractionShotgunHandler extends AbstractInteractionShotgunHandler<ModalInteractionEvent> {

    protected AbstractModalInteractionShotgunHandler(CommandType commandType) {
        super(commandType, InteractionType.MODAL);
    }

    @Override
    public void handle(GenericEvent event) {
        if (isAvailableInteraction(event, ModalInteractionEvent.class)) {
            handleInteraction((ModalInteractionEvent) event);
        }
    }
}
