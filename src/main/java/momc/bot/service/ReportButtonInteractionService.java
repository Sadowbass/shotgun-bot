package momc.bot.service;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class ReportButtonInteractionService implements ButtonInteractionService {

    @Override
    public void doInteraction(ButtonInteractionEvent event) {
        Button button = event.getButton();
        String selectedCommand = button.getId().split("-")[1];

        Modal modal;
        if (selectedCommand.equals("suggestion")) {
            modal = Modal.create("report-suggestion-modal", "건의/문의")
                    .addActionRow(TextInput.create("text-input", "건의/문의 사항을 작성해 주세요", TextInputStyle.PARAGRAPH)
                            .build())
                    .build();
        } else if (selectedCommand.equals("report")) {
            modal = Modal.create("report-report-modal", "신고")
                    .addActionRow(TextInput.create("text-input", "신고할 내용을 작성해 주세요", TextInputStyle.PARAGRAPH)
                            .build())
                    .build();
        } else {
            event.reply("처리중 에러가 발생하였습니다.").setEphemeral(true).queue();
            throw new UnsupportedOperationException("버튼 아이디가 잘못되었습니다");
        }

        event.replyModal(modal).queue();
    }
}
