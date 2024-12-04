package ir.mohika.mikambedwarsquests.command;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.command.CommandsCollection;
import de.marcely.bedwars.api.command.SubCommand;
import ir.mohika.mikambedwarsquests.command.commands.QuestsCommand;
import ir.mohika.mikambedwarsquests.command.commands.TrackCommand;
import ir.mohika.mikambedwarsquests.command.commands.UntrackCommand;

public class CommandManager {
  private static CommandsCollection commandsCollection;

  public static void register() {
    CommandsCollection root =
        BedwarsAPI.getRootCommandsCollection().addCommandsCollection("quests");
    if (root == null) {
      throw new RuntimeException("Couldn't register commands collection");
    }
    root.setHandler(new QuestsCommand());

    final SubCommand track = root.addCommand("track");
    if (track == null) {
      throw new RuntimeException("Couldn't register track command");
    }
    track.setPermission("mbedwars.cmd.quests.track");
    track.setUsage("/bw quests track <quest-id> <player>");
    track.setHandler(new TrackCommand());

    final SubCommand untrack = root.addCommand("untrack");
    if (untrack == null) {
      throw new RuntimeException("Couldn't register untrack command");
    }
    untrack.setPermission("mbedwars.cmd.quests.untrack");
    untrack.setUsage("/bw quests untrack <quest-id> <player>");
    untrack.setHandler(new UntrackCommand());

    commandsCollection = root;
  }

  public static void unregister() {
    if (commandsCollection != null) {
      BedwarsAPI.getRootCommandsCollection().removeCommand(commandsCollection);
    }
  }
}
