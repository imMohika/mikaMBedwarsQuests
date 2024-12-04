package ir.mohika.mikambedwarsquests;

import static ir.mohika.mikambedwarsquests.MikaMBedwarsQuests.plugin;

import de.marcely.bedwars.api.BedwarsAddon;
import ir.mohika.mikambedwarsquests.command.CommandManager;
import ir.mohika.mikambedwarsquests.events.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class QuestsAddon extends BedwarsAddon {

  /**
   * @param plugin The plugin that constructs it
   */
  public QuestsAddon(Plugin plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "mikaMBedwarsQuests";
  }

  public void registerAddon() {
    CommandManager.register();
    registerEvents();
  }

  private void registerEvents() {
    PluginManager manager = plugin().getServer().getPluginManager();

    manager.registerEvents(new BedBreakListener(), plugin()); // for BED_BREAK
    manager.registerEvents(new GeneratorPickupListener(), plugin()); // for GENERATOR_PICKUP
    manager.registerEvents(new KillListener(), plugin()); // for KILL, FINAL_KILL and BED_DEFEND
    manager.registerEvents(new RoundEndListener(), plugin()); // for PLAY_GAME and WIN_GAME
    manager.registerEvents(new ShopBuyListener(), plugin()); // for BUY_FROM_SHOP
    manager.registerEvents(new SpecialItemListener(), plugin()); // for USE_SPECIAL_ITEM
    manager.registerEvents(new UpgradeBuyListener(), plugin()); // for BUY_UPGRADE

    manager.registerEvents(new QuitArenaListener(), plugin()); // for resetting challenge progress
  }

  public void unregisterAddon() {
    CommandManager.unregister();
  }
}
