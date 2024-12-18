package ir.mohika.mikambedwarsquests.gui;

import static ir.mohika.mikambedwarsquests.MikaMBedwarsQuests.plugin;

import de.marcely.bedwars.tools.gui.ClickListener;
import de.marcely.bedwars.tools.gui.GUIItem;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import ir.mohika.mikambedwarsquests.config.Config;
import ir.mohika.mikambedwarsquests.config.GuiConfig;
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

    addItems();
  }

  public void show() {
    if (Bukkit.isPrimaryThread()) {
      open(player);
    } else {
      Bukkit.getScheduler().runTask(plugin(), () -> open(player));
    }
  }

  private void addItems() {
    GuiConfig.instance()
        .items()
        .forEach(
            item -> {
              switch (item) {
                case GuiConfig.QuestGuiItem questItem -> addQuestItem(questItem);
                case GuiConfig.GuiItem guiItem ->
                    guiItem
                        .slots()
                        .forEach(
                            slot -> setItem(guiItem.item().itemStack(), slot.slot(getWidth())));
                case GuiConfig.GuiCloseButton closeButton ->
                    setItem(
                        closeButton.item().itemStack(),
                        closeButton.slot().slot(getWidth()),
                        (player, leftClick, shiftClick) -> player.closeInventory());
                case GuiConfig.GuiBorderItem border -> addBorder(border);
                case GuiConfig.GuiFillItem fill ->
                    fillSpace(new GUIItem(fill.item().itemStack(), ClickListener.Silent.INSTANCE));
                default -> {}
              }
            });
  }

  private void addBorder(GuiConfig.GuiBorderItem border) {
    GUIItem guiItem = new GUIItem(border.item().itemStack(), ClickListener.Silent.INSTANCE);
    List<Integer> slots = new ArrayList<>();
    // top & bottom
    for (int x = 0; x < 9; x++) {
      slots.add(calcSlot(x, 0));
      slots.add(calcSlot(x, getHeight() - 1));
    }

    //     left & right
    for (int y = 0; y < getHeight(); y++) {
      slots.add(calcSlot(0, y));
      slots.add(calcSlot(8, y));
    }

    slots.forEach(slot -> setItemIfEmpty(guiItem, slot));
  }

  private void addQuestItem(GuiConfig.QuestGuiItem questItem) {
    Optional<Quest> quest = Quests.get(questItem.questId());
    if (quest.isEmpty()) {
      plugin()
          .getLogger()
          .warning(
              "There's a gui item with quest id of '"
                  + questItem.questId()
                  + "' but no quests with this id exists");
      return;
    }

    setItem(createQuestItem(quest.get()), questItem.slot().slot(getWidth()));
  }

  private GUIItem createQuestItem(Quest quest) {
    Optional<PlayerQuest> pqOptional = playerQuests.getQuest(quest.uniqueId());
    boolean inProgress = pqOptional.isPresent();

    Config.Item itemConfig = quest.guiItem().item();

    List<String> lore = new ArrayList<>();
    if (itemConfig.lore() != null && !itemConfig.lore().isEmpty()) {
      lore.addAll(itemConfig.lore());
    }

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

  private void setItemIfEmpty(GUIItem guiItem, int slot) {
    if (getItem(slot) != null) {
      return;
    }

    setItem(guiItem, slot);
  }
}
