package ir.mohika.mikambedwarsquests.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.Polymorphic;
import de.exlll.configlib.PolymorphicTypes;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@Getter
@Configuration
@NoArgsConstructor
public class GuiConfig {
  @Nullable private static GuiConfig instance;

  public static @NotNull GuiConfig instance() throws IllegalStateException {
    if (instance == null) {
      throw new IllegalStateException("Tried to access gui config before init");
    }

    return instance;
  }

  public static void load(File dataFolder) throws IOException {
    instance = ConfigManager.load("gui", dataFolder, GuiConfig.class);
  }

  @Comment({"Placeholders:", "-- {player}: player's name"})
  private String title = "&lQuests";

  @Comment("Number of gui rows (min 1 and max 9).")
  private int height = 9;

  List<BaseGuiItem> items =
      List.of(
          new QuestGuiItem("bed-removal-co", new GuiSlot(5, 2)),
          new GuiItem(
              new Config.Item(
                  Material.NETHER_STAR,
                  "&7Stats",
                  List.of(
                      "",
                      "&7Daily: <daily>",
                      "&7Weekly: <weekly>",
                      "&7Challenges: <challenged>",
                      "&7Total: <total>"),
                  (short) 0),
              List.of(new GuiSlot(4))),
          new GuiCloseButton(
              new Config.Item(Material.BARRIER, "&c&lClose Menu", null, (short) 0),
              new GuiSlot(9, 6)),
          new GuiBorderItem(new Config.Item(Material.STAINED_GLASS_PANE, " ", null, (short) 7)),
          new GuiFillItem(new Config.Item(Material.STAINED_GLASS_PANE, " ", null, (short) 8)));

  @Configuration
  @Polymorphic
  @PolymorphicTypes({
    @PolymorphicTypes.Type(type = QuestGuiItem.class, alias = "quest"),
    @PolymorphicTypes.Type(type = GuiItem.class, alias = "item"),
    @PolymorphicTypes.Type(type = GuiCloseButton.class, alias = "close"),
    @PolymorphicTypes.Type(type = GuiBorderItem.class, alias = "border"),
    @PolymorphicTypes.Type(type = GuiFillItem.class, alias = "fill"),
  })
  public interface BaseGuiItem {}

  public record QuestGuiItem(String questId, GuiSlot slot) implements BaseGuiItem {}

  public record GuiItem(Config.Item item, List<GuiSlot> slots) implements BaseGuiItem {}

  public record GuiCloseButton(Config.Item item, GuiSlot slot) implements BaseGuiItem {}

  public record GuiBorderItem(Config.Item item) implements BaseGuiItem {}

  public record GuiFillItem(Config.Item item) implements BaseGuiItem {}

  @Configuration
  @NoArgsConstructor
  public static class GuiSlot {
    @Comment("From 1 to 9")
    @Nullable
    public Integer x;

    @Comment("From 1 to 6")
    @Nullable
    public Integer y;

    @Nullable public Integer slot;

    public GuiSlot(int slot) {
      this.slot = slot;
    }

    public GuiSlot(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public int slot(int width) {
      if (slot != null) {
        return slot;
      }

      if (x == null || y == null) {
        throw new IllegalStateException("Either slot or both x and y must be set.");
      }

      return (y - 1) * width + (x - 1);
    }
  }
}
