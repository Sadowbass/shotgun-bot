package momc.bot.listener_adapter;

import momc.bot.ContentConst;
import momc.bot.ModifiedData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HistoryListenerAdapter extends ShotGunBotListenerAdapter {

    private final JDA jda;
    private final String commandNameOption = "name";

    public HistoryListenerAdapter(JDA jda) {
        super(CommandType.HISTORY);
        this.jda = jda;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (isAvailableCommand(event.getName())) {
            handleAdapterHistory(event);
        }
    }

    private void handleAdapterHistory(SlashCommandInteractionEvent event) {
        OptionMapping option = event.getOption(this.commandNameOption);
        List<Object> registeredListeners = jda.getRegisteredListeners();
        Optional<ShotGunBotListenerAdapter> supportedListener = findSupportedListener(option, registeredListeners);

        if (supportedListener.isEmpty()) {
            responseUnsupportedCommand(event, registeredListeners);
        } else {
            ShotGunBotListenerAdapter adapter = supportedListener.get();
            ModifiedData history = adapter.history(event.getGuild());
            if (Objects.isNull(history)) {
                event.reply("변경 내역이 없습니다.").setEphemeral(true).queue();
            } else {
                event.reply(history.toString()).setEphemeral(true).queue();
            }
        }
    }

    private Optional<ShotGunBotListenerAdapter> findSupportedListener(OptionMapping option, List<Object> registeredListeners) {
        return registeredListeners.stream()
                .filter(ShotGunBotListenerAdapter.class::isInstance)
                .map(ShotGunBotListenerAdapter.class::cast)
                .filter(o -> o.isSupportHistory() && o.isAvailableCommand(option.getAsString()))
                .findFirst();
//        return registeredListeners.stream()
//                .filter(o -> {
//                    if (o instanceof ShotGunBotListenerAdapter) {
//                        ShotGunBotListenerAdapter adapter = (ShotGunBotListenerAdapter) o;
//                        return adapter.isAvailableCommand(option.getAsString());
//                    } else {
//                        return false;
//                    }
//                }).findFirst();
    }

    private void responseUnsupportedCommand(SlashCommandInteractionEvent event, List<Object> registeredListeners) {
        MessageCreateBuilder builder = new MessageCreateBuilder()
                .addContent("해당 명령어는 내역조회가 되지 않습니다.")
                .addContent(ContentConst.LINE_BREAK)
                .addContent("지원되는 명령어는 다음과 같습니다.")
                .addContent(ContentConst.LINE_BREAK)
                .addContent("```");

        String supportedCommands = createSupportedCommands(registeredListeners);

        MessageCreateData mcd = builder.addContent(supportedCommands)
                .addContent("```")
                .build();

        event.reply(mcd).setEphemeral(true).queue();
    }

    private String createSupportedCommands(List<Object> registeredListeners) {
        StringBuilder sb = new StringBuilder();

        registeredListeners.stream()
                .filter(ShotGunBotListenerAdapter.class::isInstance)
                .map(ShotGunBotListenerAdapter.class::cast)
                .filter(ShotGunBotListenerAdapter::isSupportHistory)
                .forEach(adapter -> sb.append(adapter.type.getCommandString()).append(", "));

        return sb.substring(0, sb.length() - 2);
    }

    @Override
    public boolean isSupportHistory() {
        return false;
    }

    @Override
    public ModifiedData history(Guild guild) {
        throw new UnsupportedOperationException("history는 변경이력 조회가 불가능한 명령어입니다");
    }
}
