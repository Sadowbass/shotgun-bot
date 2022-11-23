package momc.bot;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TestListenerAdapter extends ListenerAdapter {

    @Override
    public void onGenericEvent(GenericEvent event) {
        System.out.println("TestListenerAdapter.onGenericEvent");
        System.out.println(event.getClass());
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        System.out.println(event.getRawData());
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        System.out.println(event.getMember());
    }


}
