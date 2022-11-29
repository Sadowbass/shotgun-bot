package momc.bot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuildData {

    private String guildId;
    private ReportChannels reportChannels;

    public GuildData(String guildId) {
        this.guildId = guildId;
        this.reportChannels = new ReportChannels();
    }
}