package momc.bot.handlers.impl;

import momc.bot.CommandType;
import momc.bot.ReportChannels;
import momc.bot.annotation.Handler;
import momc.bot.handlers.AbstractModalInteractionShotgunHandler;
import momc.bot.holder.GuildDataHolder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import static momc.bot.ContentConst.BLOCK_BACKTICK;
import static momc.bot.ContentConst.LINE_BREAK;

@Handler
public class ReportModalInteractionHandler extends AbstractModalInteractionShotgunHandler {

    public ReportModalInteractionHandler() {
        super(CommandType.REPORT);
    }

    @Override
    public void handleInteraction(ModalInteractionEvent event) {
        String modalCommand = event.getModalId().split("-")[2];
        ReportChannels reportChannels = GuildDataHolder.getGuildData(event.getGuild().getId()).getReportChannels();
        MessageCreateData mcd;

        if (modalCommand.equals("suggestion")) {
            mcd = createSuggestionMessage(event, reportChannels);
        } else {
            mcd = createReportMessage(event, reportChannels);
        }

        TextChannel adminChannel = reportChannels.getAdminChannel();
        adminChannel.sendMessage(mcd).queue();
        event.reply("등록이 완료되었습니다").setEphemeral(true).queue();
    }

    private MessageCreateData createSuggestionMessage(ModalInteractionEvent event, ReportChannels reportChannels) {
        return createMessage(event, reportChannels, "님이 건의/문의사항이 등록되었습니다.");
    }

    private MessageCreateData createReportMessage(ModalInteractionEvent event, ReportChannels reportChannels) {
        return createMessage(event, reportChannels, "님이 신고를 접수하였습니다.");
    }

    private MessageCreateData createMessage(ModalInteractionEvent event, ReportChannels reportChannels, String messageType) {
        return createDefaultMessage(reportChannels)
                .addContent(LINE_BREAK)
                .addContent(event.getMember().getAsMention())
                .addContent(messageType)
                .addContent(LINE_BREAK)
                .addContent(BLOCK_BACKTICK)
                .addContent(getReport(event))
                .addContent(BLOCK_BACKTICK)
                .addActionRow(Button.primary("report-button-solve", "처리완료"))
                .build();
    }

    private MessageCreateBuilder createDefaultMessage(ReportChannels reportChannels) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        reportChannels.getAdminRoles().forEach(role -> builder.addContent(role.getAsMention()).addContent(" "));

        return builder;
    }

    private String getReport(ModalInteractionEvent event) {
        return event.getValue("text-input").getAsString();
    }
}
