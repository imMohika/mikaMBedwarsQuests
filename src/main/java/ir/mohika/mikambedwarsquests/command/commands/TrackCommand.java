package ir.mohika.mikambedwarsquests.command.commands;

import de.marcely.bedwars.api.command.CommandHandler;
import de.marcely.bedwars.api.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static ir.mohika.mikambedwarsquests.MikaMBedwarsQuests.plugin;

public class TrackCommand implements CommandHandler {
  @Override
  public Plugin getPlugin() {
    return plugin();
  }

  @Override
  public void onRegister(SubCommand subCommand) {}

  @Override
  public void onFire(CommandSender sender, String fullUsage, String[] args) {
    // todo))
  }

  @Override
  public @Nullable List<String> onAutocomplete(CommandSender sender, String[] args) {
    return List.of();
  }
}
