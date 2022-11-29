package momc.bot;

import momc.bot.holder.JdaHolder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.io.IOException;

public class BotMain {

    public static JDA jda;

    public static void main(String[] args) throws InterruptedException, IOException {
        jda = JdaHolder.getJDA();
        jda.awaitStatus(JDA.Status.CONNECTED);

        jda.addEventListener(new DispatcherListenerAdapter());

        jda.upsertCommand("report-setting", "유저들이 건의하는 채널을 설정합니다")
                .addOption(OptionType.CHANNEL, "report", "유저들이 건의하는 채널", true)
                .addOption(OptionType.CHANNEL, "admin", "운영진들이 확인하는 채널", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
                .queue();


        jda.upsertCommand("history-show", "명령어들의 가장 최근 변경이력을 확인합니다")
                .addOption(OptionType.STRING, "name", "확인할 명령어를 입력하세요", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
                .queue();
    }
}
