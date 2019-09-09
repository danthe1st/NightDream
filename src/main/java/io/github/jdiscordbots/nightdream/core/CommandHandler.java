package io.github.jdiscordbots.nightdream.core;

import java.util.Collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.github.jdiscordbots.nightdream.commands.Command;
import io.github.jdiscordbots.nightdream.core.CommandParser.CommandContainer;
import io.github.jdiscordbots.nightdream.logging.LogType;
import io.github.jdiscordbots.nightdream.logging.NDLogger;
import io.github.jdiscordbots.nightdream.util.BotData;
import net.dv8tion.jda.api.EmbedBuilder;

import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * executed by a listener when Message sent which begins with the Bot prefix
 * @author Daniel Schmid
 */
public class CommandHandler {
	private static final Map<String, Command> commands = new HashMap<>();
	
	private static final NDLogger LOG=NDLogger.getLogger("CommandSystem");
	
	private static final LevenshteinDistance distCalculator=LevenshteinDistance.getDefaultInstance();
	
	private CommandHandler() {
		//no instantiation
	}
	
	public static Map<String, Command> getCommands() {
		return Collections.unmodifiableMap(commands);
	}
	static void addCommand(String name,Command cmd) {
		commands.put(name, cmd);
	}
	/**
	 * loads Command and executes it
	 * @param cmd the Command as {@link CommandContainer}
	 */
	public static void handleCommand(final CommandParser.CommandContainer cmd) {
		if (commands.containsKey(cmd.invoke.toLowerCase())) {
			boolean save = commands.get(cmd.invoke.toLowerCase()).allowExecute(cmd.args, cmd.event);
			
			if (save) {
				try {
					commands.get(cmd.invoke.toLowerCase()).action(cmd.args, cmd.event);
				} catch (RuntimeException e) {
					LOG.log(LogType.WARN,"An exception while executing the command "+cmd.event.getMessage().getContentRaw(),e);
					save = false;
				}
			}
			commands.get(cmd.invoke.toLowerCase()).executed(save, cmd.event);
		} else {
			EmbedBuilder builder=new EmbedBuilder();
			builder.setColor(0x212121);
			
			String fieldText=findSimilarCommand(cmd.invoke);
			if(fieldText==null) {
				fieldText="Try `"+BotData.getPrefix(cmd.event.getGuild())+"help` for a List of Commands";
			}else {
				fieldText="Did you mean `"+BotData.getPrefix(cmd.event.getGuild())+fieldText+"`?";
			}
			builder.addField("<:IconProvide:553870022125027329> It seems that this command does not exist",
					fieldText, false);
			cmd.event.getChannel().sendMessage(builder.build()).queue();
		}
	}
	public static String findSimilarCommand(String cmd) {
		Set<String> candidates = commands.keySet();
		Map<String, Integer> cache = new ConcurrentHashMap<>();
		Optional<String> min = candidates.parallelStream()
		    .map(String::trim)
		    .filter(s -> !s.equalsIgnoreCase(cmd))
		    .min((a, b) -> Integer.compare(
		      cache.computeIfAbsent(a, k -> distCalculator.apply(cmd, k)),
		      cache.computeIfAbsent(b, k -> distCalculator.apply(cmd, k))));
		return min.isPresent()?min.get():null;
	}
}
