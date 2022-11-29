package momc.bot.holder;

import momc.bot.GuildData;

import java.util.HashMap;
import java.util.Map;

public class GuildDataHolder {

    private final Map<String, GuildData> guildDataMap;

    private GuildDataHolder() {
        this.guildDataMap = new HashMap<>();
        init();
    }

    private void init() {
        JdaHolder.getJDA().getGuilds().forEach(guild -> guildDataMap.put(guild.getId(), new GuildData(guild.getId())));
    }

    public static Map<String, GuildData> getAllGuildDatas() {
        return GuildDataHolderInstanceHolder.INSTANCE.guildDataMap;
    }

    public static GuildData getGuildData(String guildId) {
        return GuildDataHolderInstanceHolder.INSTANCE.guildDataMap.get(guildId);
    }

    public static void addGuildData(GuildData guildData) {
        GuildDataHolderInstanceHolder.INSTANCE.guildDataMap.put(guildData.getGuildId(), guildData);
    }

    private static class GuildDataHolderInstanceHolder {
        private static final GuildDataHolder INSTANCE = new GuildDataHolder();
    }
}
