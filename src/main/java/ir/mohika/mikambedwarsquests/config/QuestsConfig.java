package ir.mohika.mikambedwarsquests.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import ir.mohika.mikambedwarsquests.quest.QuestEvent;
import ir.mohika.mikambedwarsquests.quest.QuestType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@Configuration
@Getter
@NoArgsConstructor
public class QuestsConfig {
  @Nullable private static QuestsConfig instance = null;

  public static @NotNull QuestsConfig instance() throws IllegalStateException {
    if (instance == null) {
      throw new IllegalStateException("Tried to access QuestsConfig before init");
    }

    return instance;
  }

  @Comment({
    "DO NOT EDIT THIS!",
    "This only exists as a reference, because configlib doesn't adds comments when instances are stored inside a collection."
  })
  private Quest questTemplate = new Quest();

  @Comment({
    "Add your quests here. For reference look at the questTemplate at the top of file",
    "NOTE: key should be unique"
  })
  private Map<String, Quest> quests = Map.of("bed-removal-co", new Quest());

  public static void load(File dataFolder) throws IOException {
    instance = ConfigManager.load("quests", dataFolder, QuestsConfig.class);
  }

  @Configuration
  @Getter
  @NoArgsConstructor
  public static class Quest {
    @Comment("DAILY, WEEKLY, CHALLENGE")
    private QuestType type = QuestType.WEEKLY;

    private QuestObjective objective = new QuestObjective();

    @Comment("Quest item showed in quests gui")
    private QuestItem guiItem = new QuestItem();

    @Comment({
      "A list of commands to execute when quest is completed",
      "Placeholders:",
      "-- {player}: player's name"
    })
    private List<String> rewards =
        List.of("bw addon cosmetics currency deposit coins 100 {player}");

    @Comment({
      "A list of messages player receives when quest is completed",
      "Placeholders:",
      "-- {player}: player's name",
      "-- {quest}: quest name"
    })
    private List<String> rewardMessages = List.of("&7You received 100 coins from {quest}");
  }

  @Configuration
  @Getter
  @NoArgsConstructor
  public static class QuestObjective {
    @Comment(
        "BED_BREAK, BED_DEFEND, WIN_GAME, PLAY_GAME, KILL, FINAL_KILL, GENERATOR_PICKUP, BUY_FROM_SHOP, BUY_UPGRADE, USE_SPECIAL_ITEM")
    private QuestEvent event = QuestEvent.BED_BREAK;

    @Comment("Only used in GENERATOR_PICKUP")
    private String itemMaterial = Material.DIAMOND.name();

    private int goal = 25;
  }

  @Configuration
  @Getter
  @NoArgsConstructor
  public static class QuestItem {
    @Comment("NOTE: Item name is also used for Quest name placeholder")
    private Config.Item item =
        new Config.Item(
            Material.PAPER,
            "&7Bed Removal Co.",
            List.of("", "&7Break &a25 &7beds", "", "&fRewards&8:", "&a+100 &7Coins", ""),
            (short) 0);
  }
}
