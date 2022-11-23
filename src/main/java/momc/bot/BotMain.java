package momc.bot;

import momc.bot.listener_adapter.HistoryListenerAdapter;
import momc.bot.listener_adapter.ReportListenerAdapter;
import momc.bot.service.ReportButtonInteractionService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.List;

public class BotMain {

    public static JDA jda;
    private static final List<ListenerAdapter> listenerAdapters = new ArrayList<>();
    private static final String LINE_BREAK = " \n";

    public static void main(String[] args) throws InterruptedException {
        jda = JDABuilder.createDefault("MTA0MTk3MTk1NjA3MDc0ODIwMA.Gif3Pv.piudu4gBNFv6zzXYBavNNZ8yNUKiHD0Sim4pXA")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();

        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        jda.awaitStatus(JDA.Status.CONNECTED);

        jda.addEventListener(new ReportListenerAdapter(jda, new ReportButtonInteractionService()));
        jda.addEventListener(new HistoryListenerAdapter(jda));

        jda.upsertCommand("report", "유저들이 건의하는 채널을 설정합니다")
                .addOption(OptionType.CHANNEL, "report", "유저들이 건의하는 채널", true)
                .addOption(OptionType.CHANNEL, "admin", "운영진들이 확인하는 채널", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
                .queue();

        jda.upsertCommand("history", "명령어들의 가장 최근 변경이력을 확인합니다")
                .addOption(OptionType.STRING, "name", "확인할 명령어를 입력하세요", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
                .queue();
    }

    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String selectOption = event.getSelectedOptions().get(0).getValue();

        Modal anonymousModal = Modal.create("anonymous-modal", "익명 제보")
                .addActionRow(TextInput.create("report-input", "건의사항 작성", TextInputStyle.PARAGRAPH).build())
                .build();

        Modal namedModal = Modal.create("named-modal", "기명 제보")
                .addActionRow(TextInput.create("report-input", "건의사항 작성", TextInputStyle.PARAGRAPH).build())
                .build();

        ModalCallbackAction modalCallbackAction;

        if (selectOption.equals("named")) {
            modalCallbackAction = event.replyModal(namedModal);
        } else {
            modalCallbackAction = event.replyModal(anonymousModal);
        }

        modalCallbackAction.queue();
    }

    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();
        TextChannel reportListChannel = event.getGuild().getTextChannelsByName("건의목록", true).get(0);
        String reportContent = event.getValues().get(0).getAsString();

        Role adminRole = event.getGuild().getRoles().stream().filter(role -> role.getName().equals("운영진"))
                .findFirst().orElseThrow(RuntimeException::new);

        String userId = event.getUser().getId();
        Member member = event.getGuild().getMemberById(userId);

        if (modalId.equals("named-modal")) {
            MessageCreateData namedMcd = new MessageCreateBuilder()
                    .addContent(adminRole.getAsMention())
                    .addContent(LINE_BREAK)
                    .addContent(member.getEffectiveName() + " : " + userId)
                    .addContent(LINE_BREAK)
                    .addContent(reportContent)
                    .build();

            reportListChannel.sendMessage(namedMcd).queue();
        } else {
            MessageCreateData anonymousMcd = new MessageCreateBuilder()
                    .addContent(adminRole.getAsMention())
                    .addContent(LINE_BREAK)
                    .addContent("익명제보")
                    .addContent(LINE_BREAK)
                    .addContent(reportContent)
                    .build();

            reportListChannel.sendMessage(anonymousMcd).queue();
        }

        event.reply("제보 등록이 완료되었습니다.").setEphemeral(true).queue();
    }
}
