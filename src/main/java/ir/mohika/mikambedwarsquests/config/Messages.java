package ir.mohika.mikambedwarsquests.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.marcely.bedwars.api.message.Message;
import java.io.File;
import java.io.IOException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@Getter
@Configuration
@NoArgsConstructor
public class Messages {
  @Nullable private static Messages instance;

  public static @NotNull Messages instance() throws IllegalStateException {
    if (instance == null) {
      throw new IllegalStateException("Tried to access Messages before init");
    }

    return instance;
  }

  public static void load(File dataFolder) throws IOException {
    instance = ConfigManager.load("messages", dataFolder, Messages.class);
  }

  private GuiMessages gui = new GuiMessages();

  @Configuration
  @NoArgsConstructor
  public static class GuiMessages {
    @Comment({"Placeholders:", "-- {player}: player's name"})
    private String title = "Quests";

    public String title(Player player) {
      return Message.build(title).placeholder("player", player.getName()).done(player);
    }

    @Comment({"Placeholders:", "-- {quest}: quest name"})
    private String untrack = "&8> &3Click to untrack {quest}";

    public String untrack(String questName, Player player) {
      return Message.build(untrack).placeholder("quest", questName).done(player);
    }

    @Comment({"Placeholders:", "-- {quest}: quest name"})
    private String track = "&8> &3Click to track {quest}";

    public String track(String questName, Player player) {
      return Message.build(track).placeholder("quest", questName).done(player);
    }

    @Comment({
      "Placeholders:",
      "-- {current}: player's current progress",
      "-- {goal}: quest goal",
      "-- {percent}: progress percent"
    })
    private String progress = "&aIn Progress: &7{current}&8/&7{goal}";

    public String progress(int current, int goal, Player player) {
      double percent = ((double) current / goal) * 100;
      return Message.build(progress)
          .placeholder("current", current)
          .placeholder("goal", goal)
          .placeholder("percent", String.format("%.2f%%", percent))
          .done(player);
    }
  }
}
