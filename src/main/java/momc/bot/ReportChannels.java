package momc.bot;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReportChannels {

    private TextChannel reportChannel;
    private TextChannel adminChannel;
    private List<Role> adminRoles = new ArrayList<>();
    private ModifiedData modifiedData;
}
