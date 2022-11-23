package momc.bot.listener_adapter;

import momc.bot.ModifiedData;
import momc.bot.ReportChannels;
import momc.bot.service.ButtonInteractionService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static momc.bot.ContentConst.BLOCK_BACKTICK;
import static momc.bot.ContentConst.LINE_BREAK;

public class ReportListenerAdapter extends ShotGunBotListenerAdapter {

    private final JDA jda;
    private final ButtonInteractionService buttonInteractionService;
    private final Map<Guild, ReportChannels> guildReportChannelsMap;
    private final String reportOption = "report";
    private final String adminOption = "admin";
    private boolean init;

    public ReportListenerAdapter(JDA jda, ButtonInteractionService buttonInteractionService) {
        super(CommandType.REPORT);
        this.jda = jda;
        this.buttonInteractionService = buttonInteractionService;
        this.guildReportChannelsMap = new HashMap<>();
        this.init = false;
        init();
    }

    public void init() {
        if (!jda.getStatus().isInit() || this.init) {
            return;
        }

        this.jda.getGuilds().forEach(guild -> guildReportChannelsMap.put(guild, new ReportChannels()));
        this.init = true;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (isAvailableCommand(event.getName())) {
            GuildChannelUnion reportChannel = event.getOption(this.reportOption).getAsChannel();
            GuildChannelUnion adminChannel = event.getOption(this.adminOption).getAsChannel();

            if (isNotAvailableChannels(reportChannel, adminChannel)) {
                event.reply("건의채널과 운영진채널은 모두 텍스트 채널이어야 합니다")
                        .setEphemeral(true).queue();
                return;
            }

            setReportChannelData(event, reportChannel, adminChannel);
            EntitySelectMenu roles =
                    EntitySelectMenu.create("report-admins", EntitySelectMenu.SelectTarget.ROLE)
                            .setRequiredRange(1, 5)
                            .build();

            MessageCreateData reply = new MessageCreateBuilder()
                    .addContent("멘션으로 알릴 운영진 권한을 선택해 주세요. (최소 1개 최대 5개 권한에 설정가능)")
                    .addActionRow(roles)
                    .build();

            event.reply(reply).setEphemeral(true).queue();
        }
    }

    private boolean isNotAvailableChannels(GuildChannelUnion reportChannel, GuildChannelUnion adminChannel) {
        return !(reportChannel instanceof TextChannel && adminChannel instanceof TextChannel);
    }

    private void setReportChannelData(SlashCommandInteractionEvent event, GuildChannelUnion reportChannel, GuildChannelUnion adminChannel) {
        ReportChannels reportChannels = guildReportChannelsMap.get(event.getGuild());
        reportChannels.setModifiedData(new ModifiedData(event.getMember(), LocalDateTime.now()));
        reportChannels.setReportChannel(reportChannel.asTextChannel());
        reportChannels.setAdminChannel(adminChannel.asTextChannel());
    }

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        if (event.getInteraction().getComponentId().startsWith(this.type.getCommandString())) {
            Guild guild = event.getGuild();
            List<Role> roles = getRoles(event);

            ReportChannels reportChannels = guildReportChannelsMap.get(guild);
            reportChannels.setAdminRoles(roles);

            settingReportChannel(event.getGuild());
            event.reply(createSuccessResponse(reportChannels.getReportChannel(), reportChannels.getAdminChannel(), roles))
                    .setEphemeral(true)
                    .queue();
        }
    }

    @NotNull
    private List<Role> getRoles(EntitySelectInteractionEvent event) {
        return event.getValues().stream()
                .map(Role.class::cast)
                .collect(Collectors.toList());
    }

    private MessageCreateData createSuccessResponse(TextChannel reportChannel, TextChannel adminChannel, List<Role> roles) {
        return new MessageCreateBuilder()
                .addContent("건의 채널 : " + reportChannel)
                .addContent(LINE_BREAK)
                .addContent("운영진 채널 : " + adminChannel)
                .addContent(LINE_BREAK)
                .addContent("운영진 권한 : " + roles)
                .addContent("로 설정되었습니다.")
                .build();
    }

    private void settingReportChannel(Guild guild) {
        ReportChannels reportChannels = guildReportChannelsMap.get(guild);
        TextChannel reportChannel = reportChannels.getReportChannel();
        clearReportChannel(reportChannel);

        MessageCreateData reportNotice = createReportNotice();
        reportChannel.sendMessage(reportNotice).queue();
    }

    private void clearReportChannel(TextChannel reportChannel) {
        List<Message> messages = reportChannel.getHistory().retrievePast(99).complete();

        while (!messages.isEmpty()) {
            if (messages.size() == 1) {
                messages.get(0).delete().queue();
                break;
            } else {
                reportChannel.deleteMessages(messages).queue();
                messages = reportChannel.getHistory().retrievePast(99).complete();
            }
        }
    }

    @NotNull
    private MessageCreateData createReportNotice() {
        return new MessageCreateBuilder()
                .addContent(BLOCK_BACKTICK)
                .addContent("길드 운영진에게 건의/문의와 신고를 할 수 있습니다.")
                .addContent(LINE_BREAK)
                .addContent(BLOCK_BACKTICK)
                .addActionRow(
                        Button.primary("report-suggestion", "건의/문의"),
                        Button.danger("report-report", "신고")
                )
                .build();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getButton().getId().startsWith(this.type.getCommandString())) {
            buttonInteractionService.doInteraction(event);
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();
        if (modalId.startsWith(this.type.getCommandString())) {
            Guild guild = event.getGuild();

            ReportChannels reportChannels = guildReportChannelsMap.get(guild);
            TextChannel adminChannel = reportChannels.getAdminChannel();
            String modalCommand = modalId.split("-")[1];

            MessageCreateData mcd;
            if (modalCommand.equals("suggestion")) {
                mcd = createSuggestionMessage(event, reportChannels);
            } else {
                mcd = createReportMessage(event, reportChannels);
            }
            adminChannel.sendMessage(mcd).queue();
            event.reply("등록이 완료되었습니다").setEphemeral(true).queue();
        }
    }

    private MessageCreateData createSuggestionMessage(ModalInteractionEvent event, ReportChannels reportChannels) {
        return createMessage(event, reportChannels, "님이 건의/문의사항이 등록되었습니다.");
    }

    private MessageCreateData createReportMessage(ModalInteractionEvent event, ReportChannels reportChannels) {
        return createMessage(event, reportChannels, "님이 신고를 접수하였습니다.");
    }

    @NotNull
    private MessageCreateData createMessage(ModalInteractionEvent event, ReportChannels reportChannels, String messageType) {
        return createDefaultMessage(reportChannels)
                .addContent(LINE_BREAK)
                .addContent(event.getMember().getAsMention())
                .addContent(messageType)
                .addContent(LINE_BREAK)
                .addContent(BLOCK_BACKTICK)
                .addContent(getReport(event))
                .addContent(BLOCK_BACKTICK)
                .addActionRow(Button.primary("report-solve", "처리완료"))
                .build();
    }

    private MessageCreateBuilder createDefaultMessage(ReportChannels reportChannels) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        reportChannels.getAdminRoles().forEach(role -> builder.addContent(role.getAsMention()));

        return builder;
    }

    private String getReport(ModalInteractionEvent event) {
        return event.getValue("text-input").getAsString();
    }

    @Override
    public boolean isSupportHistory() {
        return true;
    }

    @Override
    public ModifiedData history(Guild guild) {
        return guildReportChannelsMap.getOrDefault(guild, new ReportChannels()).getModifiedData();
    }
}
