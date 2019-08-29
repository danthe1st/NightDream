/*
 * Copyright (c) danthe1st and byNoobiYT 2019.
 * File: BugReport.java
 * Project: NightDream
 * Licenced under Boost Software License 1.0
 */

package io.github.bynoobiyt.nightdream.commands;

import io.github.bynoobiyt.nightdream.util.BotData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

@BotCommand("bugreport")
public class BugReport implements Command {

	private static final Logger LOG=LoggerFactory.getLogger(BugReport.class);
	private static final String DISABLED_INVALID_CHAN="Bug report command is disabled. To enable it, please insert a valid channel id into NightDream.properties.";
	
	@Override
	public void action(String[] args, GuildMessageReceivedEvent event) {
		int latestBugId = BotData.getBugID();

		int thisId = latestBugId + 1;

		BotData.setBugID(thisId);
		StringBuilder sb = new StringBuilder();
		for (String string : args) {
			sb.append(string).append(" ");
		}
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.white).setTitle("New Bug").setDescription(sb.toString())
				.setFooter(event.getAuthor().getAsTag() + " | Bug ID " + thisId);

		event.getJDA().getTextChannelById(BotData.getBugReportChannel()).sendMessage(eb.build()).queue();

		event.getChannel().sendMessage("Send with ID " + thisId).queue();
	}

	@Override
	public boolean allowExecute(String[] args, GuildMessageReceivedEvent event) {
		if (BotData.getBugReportChannel() == null) {
			BotData.setBugReportChannel("");
			LOG.warn(DISABLED_INVALID_CHAN);
			return false;
		}
		try {
			if(event.getJDA().getTextChannelById(BotData.getBugReportChannel())==null) {
				LOG.warn(DISABLED_INVALID_CHAN);
			}
		} catch (NumberFormatException e) {
			LOG.warn(DISABLED_INVALID_CHAN);
			return false;
		}
		return true;

	}

	@Override
	public String help() {
		return "Files a bug report";
	}
}
