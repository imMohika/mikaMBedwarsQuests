package ir.mohika.mikambedwarsquests.quest;

import de.marcely.bedwars.api.message.Message;
import ir.mohika.mikambedwarsquests.config.Config;
import ir.mohika.mikambedwarsquests.config.QuestsConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

import static ir.mohika.mikambedwarsquests.MikaMBedwarsQuests.plugin;

@AllArgsConstructor
@Getter
public class Quest {
  private final String uniqueId;
  private final String name;
  private final QuestType type;
  private final QuestObjective objective;
  private final QuestItem guiItem;
  private final List<String> rewards;
  private final List<String> rewardMessages;

  public static Quest from(Map.Entry<String, QuestsConfig.Quest> entry) {
    return from(entry.getKey(), entry.getValue());
  }

  public static Quest from(String uniqueId, QuestsConfig.Quest config) {
    return new Quest(
        uniqueId,
        config.guiItem().item().name(),
        config.type(),
        QuestObjective.from(config.objective()),
        QuestItem.from(config.guiItem()),
        List.copyOf(config.rewards()),
        List.copyOf(config.rewardMessages()));
  }

  public void giveReward(Player player) {
    if (Bukkit.isPrimaryThread()) {
      giveRewardsUnsafe(player);
    } else {
      Bukkit.getScheduler().runTask(plugin(), () -> giveRewardsUnsafe(player));
    }
  }

  private void giveRewardsUnsafe(Player player) {
    for (String reward : rewards) {
      String cmd = Message.build(reward).placeholder("player", player.getName()).done();
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    for (String rewardMessage : rewardMessages) {
      Message.build(rewardMessage)
          .placeholder("player", player.getName())
          .placeholder("quest", name)
          .send(player);
    }
  }

  public record QuestObjective(QuestEvent event, String itemMaterial, int goal) {
    public static QuestObjective from(QuestsConfig.QuestObjective config) {
      return new QuestObjective(config.event(), config.itemMaterial(), config.goal());
    }
  }

  public record QuestItem(Config.Item item) {
    public static QuestItem from(QuestsConfig.QuestItem config) {
      return new QuestItem(config.item());
    }
  }
}
