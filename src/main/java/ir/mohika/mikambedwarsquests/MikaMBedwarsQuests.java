package ir.mohika.mikambedwarsquests;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.BedwarsAddon;
import ir.mohika.mikambedwarsquests.config.Config;
import ir.mohika.mikambedwarsquests.config.Messages;
import ir.mohika.mikambedwarsquests.config.QuestsConfig;
import ir.mohika.mikambedwarsquests.quest.Quest;
import ir.mohika.mikambedwarsquests.quest.Quests;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class MikaMBedwarsQuests extends JavaPlugin {
  private static MikaMBedwarsQuests instance;
  private static Logger logger;
  @Getter private QuestsAddon addon;

  @Override
  public void onEnable() {
    instance = this;
    logger = getLogger();

    if (!checkMBedwars()) return;
    if (!registerAddon()) return;

    BedwarsAPI.onReady(
        () -> {
          try {
            logger.info("Loading config...");
            Config.load(addon.getDataFolder());
            logger.info("Loaded config!");

            logger.info("Loading messages...");
            Messages.load(addon.getDataFolder());
            logger.info("Loaded messages!");

            logger.info("Loading quests config...");
            QuestsConfig.load(addon.getDataFolder());
            logger.info("Loaded quests config!");
          } catch (IOException e) {
            getLogger().warning("Failed to load configs");
            e.printStackTrace();

            Bukkit.getPluginManager().disablePlugin(this);
          }

          logger.info("Loading quests...");
          QuestsConfig.instance()
              .quests()
              .forEach((key, value) -> Quests.register(Quest.from(key, value)));
          logger.info("Loaded " + Quests.count() + " quests!");

          addon.registerAddon();
        });
  }

  @Override
  public void onDisable() {
    if (addon != null) {
      addon.unregisterAddon();
    }
  }

  private boolean checkMBedwars() {
    final int supportedAPIVersion = 114;
    final String supportedVersionName = "5.4.15";

    try {
      Class<?> apiClass = Class.forName("de.marcely.bedwars.api.BedwarsAPI");
      int apiVersion = (int) apiClass.getMethod("getAPIVersion").invoke(null);

      if (apiVersion < supportedAPIVersion) throw new IllegalStateException();
    } catch (Exception e) {
      getLogger()
          .severe(
              "Sorry, your installed version of MBedwars is not supported. Please install at least v"
                  + supportedVersionName);
      Bukkit.getPluginManager().disablePlugin(this);
      return false;
    }

    return true;
  }

  private boolean registerAddon() {
    addon = new QuestsAddon(this);

    if (!addon.register()) {
      getLogger()
          .warning(
              "It seems like this addon has already been loaded. Please delete duplicates and try again.");
      Bukkit.getPluginManager().disablePlugin(this);

      return false;
    }

    return true;
  }

  public static MikaMBedwarsQuests plugin() {
    return instance;
  }

  public static Logger logger() {
    return logger;
  }
}
