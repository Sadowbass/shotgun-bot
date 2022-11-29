package momc.bot;

import momc.bot.handlers.ShotgunHandler;
import momc.bot.holder.HandlerHolder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DispatcherListenerAdapter extends ListenerAdapter {

    private final Map<CommandType, Map<InteractionType, List<ShotgunHandler>>> handlerMap;

    public DispatcherListenerAdapter() throws IOException {
        this.handlerMap = HandlerHolder.getHandlers();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String[] splitedName = event.getName().split("-");
        getHandler(splitedName[0], "slash").forEach(shotgunHandler -> shotgunHandler.handle(event));
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String[] splitedName = event.getButton().getId().split("-");
        getHandler(splitedName[0], splitedName[1]).forEach(shotgunHandler -> shotgunHandler.handle(event));
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        String[] splitedName = event.getInteraction().getComponentId().split("-");
        getHandler(splitedName[0], splitedName[1]).forEach(shotgunHandler -> shotgunHandler.handle(event));
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        String[] splitedName = event.getInteraction().getModalId().split("-");
        getHandler(splitedName[0], splitedName[1]).forEach(shotgunHandler -> shotgunHandler.handle(event));
    }

    private List<ShotgunHandler> getHandler(String commandType, String interactionType) {
        CommandType commandKey = CommandType.valueOf(commandType.toUpperCase());
        InteractionType interactionKey = InteractionType.getInteractionType(interactionType);

        return handlerMap.get(commandKey).get(interactionKey);
    }
}
