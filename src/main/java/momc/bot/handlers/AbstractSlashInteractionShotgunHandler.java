package momc.bot.handlers;

import momc.bot.CommandType;
import momc.bot.InteractionType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class AbstractSlashInteractionShotgunHandler extends AbstractInteractionShotgunHandler<SlashCommandInteractionEvent> {

    protected AbstractSlashInteractionShotgunHandler(CommandType commandType) {
        super(commandType, InteractionType.SLASH);
    }

    @Override
    public void handle(GenericEvent event) {
        if (isAvailableInteraction(event, SlashCommandInteractionEvent.class)) {
            handleInteraction((SlashCommandInteractionEvent) event);
        }
    }
}
