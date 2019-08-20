package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.core.NightDream;
import io.github.bynoobiyt.nightdream.util.BotData;
import io.github.bynoobiyt.nightdream.util.TextToGraphics;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.Year;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@BotCommand("info")
public class Info implements Command {

    private static int getUsers(JDA jda) {
        int count=0;
        for (User user : jda.getUsers()) {
        	if (!user.isBot()) {
        		count++;
        	}
		}
        return count;
    }

    private static int getBots(JDA jda) {
    	int count=0;
        for (User user : jda.getUsers()) {
        	if (user.isBot()) {
        		count++;
        	}
		}
        return count;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent event) {
    	final JDA jda=event.getJDA();
        if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_EXT_EMOJI)) {
        	TextToGraphics.sendTextAsImage(event.getChannel(), "info.png", String.format(
        			"Bot Info\n"
        			+ "\tIn %s guilds, serving %s users and %s bots.\n"
                    + "\tThis instance is owned by " + Stream.of(BotData.getAdminIDs()).map(id -> jda.retrieveUserById(id).complete().getAsTag()).collect(Collectors.joining(" and ")) + ".\n"
                    + "\tJDA v4.0.0_39\n"
                    + "\tLogo Font: Avenir Next LT Pro / (c) Linotype\n"
                    + "\t(c) dan1st and Gehasstes %s, Release %s.\n "
                    + "\tThis is a copy of Daydream (https://gitlab.com/botstudio/daydream/) by SP46", event.getJDA().getGuilds().size(), getUsers(jda), getBots(jda), Year.now().getValue(), NightDream.VERSION
        			), event.getAuthor().getAsMention());
        }
    }

    @Override
    public String help() {
        return "Displays bot information";
    }
}
