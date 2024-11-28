package ir.mohika.mikambedwarsquests.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ir.mohika.mikambedwarsquests.gui.ItemBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("FieldMayBeFinal")
@Configuration
@Getter
@NoArgsConstructor
public class Config {
  @Nullable private static Config instance = null;

  public static @NotNull Config instance() throws IllegalStateException {
    if (instance == null) {
      throw new IllegalStateException("Tried to access Config before init");
    }

    return instance;
  }

  public static void load(File dataFolder) throws IOException {
    instance = ConfigManager.load("config", dataFolder, Config.class);
  }

  private BedBreakSettings bedBreakSettings = new BedBreakSettings();
  private KillSettings killSettings = new KillSettings();
  private BedDefendSettings bedDefendSettings = new BedDefendSettings();

  @Configuration
  @Getter
  @NoArgsConstructor
  public static class BedBreakSettings {
    @Comment({
      "When true only the player who destroyed the bed will be rewarded",
      "Otherwise all players who are in same team will be rewarded"
    })
    private boolean onlyDestroyer = true;
  }

  @Configuration
  @Getter
  @NoArgsConstructor
  public static class KillSettings {
    @Comment({
      "When true don't reward KILL when its FINAL_KILL",
      "Otherwise final kill will result in rewards both for KILL and FINAL_KILL"
    })
    private boolean notFinalKill = false;
  }

  @Configuration
  @Getter
  @NoArgsConstructor
  public static class BedDefendSettings {
    private int radius = 15;
  }

  public record Item(Material material, String name, List<String> lore) {
    public ItemStack getItemStack() {
      return ItemBuilder.builder().material(material).name(name).lores(lore).build().itemStack();
    }
  }
}
