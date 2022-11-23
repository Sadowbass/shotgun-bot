package momc.bot.service;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface ButtonInteractionService {

    void doInteraction(ButtonInteractionEvent event);
}
