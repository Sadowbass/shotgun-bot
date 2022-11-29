package momc.bot.handlers.impl;

import momc.bot.CommandType;
import momc.bot.ContentConst;
import momc.bot.annotation.Handler;
import momc.bot.handlers.AbstractButtonInteractionShotgunHandler;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

@Handler
public class ReportButtonInteractionHandler extends AbstractButtonInteractionShotgunHandler {

    private static final String BUTTON_SUGGESTION = "suggestion";
    private static final String BUTTON_REPORT = "report";
    private static final String BUTTON_SOLVE = "solve";


    public ReportButtonInteractionHandler() {
        super(CommandType.REPORT);
    }

    @Override
    public void handleInteraction(ButtonInteractionEvent event) {
        String selectedCommand = event.getButton().getId().split("-")[2];

        if (BUTTON_SOLVE.equals(selectedCommand)) {
            solveButtonInteraction(event);
        } else {
            doModalInteraction(event, selectedCommand);
        }
    }

    private void doModalInteraction(ButtonInteractionEvent event, String selectedCommand) {
        Modal modal;
        if (BUTTON_SUGGESTION.equals(selectedCommand)) {
            modal = Modal.create("report-modal-suggestion", "건의/문의")
                    .addActionRow(TextInput.create("text-input", "건의/문의 사항을 작성해 주세요", TextInputStyle.PARAGRAPH)
                            .build())
                    .build();
        } else {
            modal = Modal.create("report-modal-report", "신고")
                    .addActionRow(TextInput.create("text-input", "신고할 내용을 작성해 주세요", TextInputStyle.PARAGRAPH)
                            .build())
                    .build();
        }
        event.replyModal(modal).queue();
    }

    private void solveButtonInteraction(ButtonInteractionEvent event) {
        String contentRaw = event.getMessage().getContentRaw();
        String effectiveName = event.getMember().getEffectiveName();

        MessageEditData build = new MessageEditBuilder()
                .setContent(createSolveMessage(contentRaw, effectiveName))
                .build();

        event.editMessage(build).queue(interactionHook -> event.editButton(null).queue());
    }

    @NotNull
    private String createSolveMessage(String contentRaw, String effectiveName) {
        return contentRaw + ContentConst.LINE_BREAK +
                effectiveName + " 님이 처리하였습니다";
    }
}
