package ir.mohika.mikambedwarsquests.gui;

import static ir.mohika.mikambedwarsquests.MikaMBedwarsQuests.plugin;

import de.marcely.bedwars.tools.gui.GUIItem;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import ir.mohika.mikambedwarsquests.config.Config;
import ir.mohika.mikambedwarsquests.config.Messages;
import ir.mohika.mikambedwarsquests.quest.PlayerQuest;
import ir.mohika.mikambedwarsquests.quest.PlayerQuests;
import ir.mohika.mikambedwarsquests.quest.Quest;
import ir.mohika.mikambedwarsquests.quest.Quests;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Builder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class QuestGui extends ChestGUI {
  private final Player player;
  private final PlayerQuests playerQuests;

  @Builder
  public QuestGui(Player player, PlayerQuests quests) {
    super(6, Messages.instance().gui().title(player));
    this.player = player;
    this.playerQuests = quests;

    addQuests();
  }

  public void show() {
    if (Bukkit.isPrimaryThread()) {
      open(player);
    } else {
      Bukkit.getScheduler().runTask(plugin(), () -> open(player));
    }
  }

  private void addQuests() {
    Quests.all().forEach(quest -> setItem(createQuestItem(quest), quest.guiItem().slot()));
  }

  private GUIItem createQuestItem(Quest quest) {
    Optional<PlayerQuest> pqOptional = playerQuests.getQuest(quest.uniqueId());
    boolean inProgress = pqOptional.isPresent();

    Config.Item itemConfig = quest.guiItem().item();
    List<String> lore = new ArrayList<>(itemConfig.lore());
    if (inProgress) {
      PlayerQuest playerQuest = pqOptional.get();

      lore.add(Messages.instance().gui().untrack(quest.name(), player));
      lore.add("");
      lore.add(
          Messages.instance()
              .gui()
              .progress(playerQuest.value(), quest.objective().goal(), player));
    } else {
      lore.add(Messages.instance().gui().track(quest.name(), player));
    }

    return new GUIItem(
        ItemBuilder.builder()
            .material(itemConfig.material())
            .name(itemConfig.name())
            .lores(lore)
            .build()
            .itemStack(),
        (player, leftClick, shiftClick) -> {
          if (inProgress) {
            playerQuests.removeQuest(quest.uniqueId()).save();
          } else {
            playerQuests.addQuest(quest).save();
          }

          player.closeInventory();
        });
  }
}
