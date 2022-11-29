package momc.bot.handlers.impl;

import momc.bot.CommandType;
import momc.bot.GuildData;
import momc.bot.ModifiedData;
import momc.bot.ReportChannels;
import momc.bot.annotation.Handler;
import momc.bot.handlers.AbstractSlashInteractionShotgunHandler;
import momc.bot.holder.GuildDataHolder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.time.LocalDateTime;

@Handler
public class ReportSlashInteractionHandler extends AbstractSlashInteractionShotgunHandler {

    private static final String OPTION_REPORT = "report";
    private static final String OPTION_ADMIN = "admin";

    public ReportSlashInteractionHandler() {
        super(CommandType.REPORT);
    }

    @Override
    public void handleInteraction(SlashCommandInteractionEvent event) {
        GuildChannelUnion reportChannel = event.getOption(OPTION_REPORT).getAsChannel();
        GuildChannelUnion adminChannel = event.getOption(OPTION_ADMIN).getAsChannel();

        if (isNotAvailableChannels(reportChannel, adminChannel)) {
            event.reply("건의채널과 운영진채널은 모두 텍스트 채널이어야 합니다")
                    .setEphemeral(true).queue();
            return;
        }

        setReportChannelData(event, reportChannel, adminChannel);
        EntitySelectMenu roles =
                EntitySelectMenu.create("report-entity", EntitySelectMenu.SelectTarget.ROLE)
                        .setRequiredRange(1, 5)
                        .build();

        MessageCreateData reply = new MessageCreateBuilder()
                .addContent("멘션으로 알릴 운영진 권한을 선택해 주세요. (최소 1개 최대 5개 권한에 설정가능)")
                .addActionRow(roles)
                .build();

        event.reply(reply).setEphemeral(true).queue();
    }

    private boolean isNotAvailableChannels(GuildChannelUnion reportChannel, GuildChannelUnion adminChannel) {
        return !(reportChannel instanceof TextChannel && adminChannel instanceof TextChannel);
    }

    private void setReportChannelData(SlashCommandInteractionEvent event, GuildChannelUnion reportChannel, GuildChannelUnion adminChannel) {
        GuildData guildData = GuildDataHolder.getGuildData(event.getGuild().getId());
        ReportChannels reportChannels = guildData.getReportChannels();
        reportChannels.setModifiedData(new ModifiedData(event.getMember(), LocalDateTime.now()));
        reportChannels.setReportChannel(reportChannel.asTextChannel());
        reportChannels.setAdminChannel(adminChannel.asTextChannel());
    }
}
