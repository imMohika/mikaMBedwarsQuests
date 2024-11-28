package ir.mohika.mikambedwarsquests.events;

import de.marcely.bedwars.api.event.player.PlayerBuyUpgradeEvent;
import ir.mohika.mikambedwarsquests.quest.PlayerQuests;
import ir.mohika.mikambedwarsquests.quest.QuestEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class UpgradeBuyListener implements Listener {
  @EventHandler(priority = EventPriority.MONITOR)
  public void onUpgradeBuy(PlayerBuyUpgradeEvent event) {
    if (event.getArena() == null) {
      return;
    }

    if (!event.getProblems().isEmpty()) {
      return;
    }

    Player player = event.getPlayer();
    PlayerQuests.from(
        player,
        playerQuests ->
            playerQuests.updateQuests(pq -> pq.eventEquals(QuestEvent.BUY_UPGRADE), 1).save());
  }
}
