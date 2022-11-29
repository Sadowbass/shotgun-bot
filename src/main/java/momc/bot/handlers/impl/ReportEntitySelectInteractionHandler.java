package momc.bot.handlers.impl;

import momc.bot.CommandType;
import momc.bot.GuildData;
import momc.bot.ReportChannels;
import momc.bot.annotation.Handler;
import momc.bot.handlers.AbstractEntitySelectInteractionShotgunBotHandler;
import momc.bot.holder.GuildDataHolder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.List;
import java.util.stream.Collectors;

import static momc.bot.ContentConst.BLOCK_BACKTICK;
import static momc.bot.ContentConst.LINE_BREAK;

@Handler
public class ReportEntitySelectInteractionHandler extends AbstractEntitySelectInteractionShotgunBotHandler {

    public ReportEntitySelectInteractionHandler() {
        super(CommandType.REPORT);
    }

    @Override
    public void handleInteraction(EntitySelectInteractionEvent event) {
        Guild guild = event.getGuild();
        List<Role> roles = getRoles(event);

        GuildData guildData = GuildDataHolder.getGuildData(guild.getId());

        ReportChannels reportChannels = guildData.getReportChannels();
        reportChannels.setAdminRoles(roles);

        settingReportChannel(reportChannels);
        event.reply(createSuccessResponse(reportChannels.getReportChannel(), reportChannels.getAdminChannel(), roles))
                .setEphemeral(true)
                .queue();
    }

    private List<Role> getRoles(EntitySelectInteractionEvent event) {
        return event.getValues().stream()
                .map(Role.class::cast)
                .collect(Collectors.toList());
    }

    private void settingReportChannel(ReportChannels reportChannels) {
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

    private MessageCreateData createReportNotice() {
        return new MessageCreateBuilder()
                .addContent(BLOCK_BACKTICK)
                .addContent("길드 운영진에게 건의/문의와 신고를 할 수 있습니다.")
                .addContent(LINE_BREAK)
                .addContent(BLOCK_BACKTICK)
                .addActionRow(
                        Button.primary("report-button-suggestion", "건의/문의"),
                        Button.danger("report-button-report", "신고")
                )
                .build();
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
}
