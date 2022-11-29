package momc.bot.holder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class JdaHolder {

    private final JDA jda;

    private JdaHolder() {
        jda = JDABuilder.createDefault("token")
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        jda.getPresence().setStatus(OnlineStatus.ONLINE);
    }

    public static JDA getJDA() {
        return JdaHolderInstanceHolder.INSTANCE.jda;
    }

    private static class JdaHolderInstanceHolder {
        private static final JdaHolder INSTANCE = new JdaHolder();
    }
}
