package momc.bot.handlers;

import momc.bot.CommandType;
import momc.bot.InteractionType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class AbstractButtonInteractionShotgunHandler extends AbstractInteractionShotgunHandler<ButtonInteractionEvent> {

    protected AbstractButtonInteractionShotgunHandler(CommandType commandType) {
        super(commandType, InteractionType.BUTTON);
    }

    @Override
    public void handle(GenericEvent event) {
        if (isAvailableInteraction(event, ButtonInteractionEvent.class)) {
            handleInteraction((ButtonInteractionEvent) event);
        }
    }
}
