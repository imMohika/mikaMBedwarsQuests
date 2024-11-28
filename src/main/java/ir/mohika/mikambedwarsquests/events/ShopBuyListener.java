package ir.mohika.mikambedwarsquests.events;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import ir.mohika.mikambedwarsquests.quest.PlayerQuests;
import ir.mohika.mikambedwarsquests.quest.QuestEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ShopBuyListener implements Listener {
  @EventHandler(priority = EventPriority.MONITOR)
  public void onShopBuy(PlayerBuyInShopEvent event) {
    if (event.getArena() == null) return;

    if (!event.getProblems().isEmpty()) {
      return;
    }

    int multiplier = event.getMultiplier();
    Player player = event.getPlayer();

    PlayerQuests.from(
        player,
        playerQuests ->
            playerQuests
                .updateQuests(pq -> pq.eventEquals(QuestEvent.BUY_FROM_SHOP), multiplier)
                .save());
  }
}
