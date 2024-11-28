package ir.mohika.mikambedwarsquests.events;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerUseSpecialItemEvent;
import ir.mohika.mikambedwarsquests.quest.PlayerQuests;
import ir.mohika.mikambedwarsquests.quest.QuestEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SpecialItemListener implements Listener {
  @EventHandler(priority = EventPriority.MONITOR)
  public void onSpecialItemUse(PlayerUseSpecialItemEvent event) {
    Arena arena = event.getArena();
    if (arena == null) {
      return;
    }

    if (event.isCancelled()) {
      return;
    }

    Player player = event.getPlayer();
    PlayerQuests.from(
        player,
        playerQuests ->
            playerQuests.updateQuests(pq -> pq.eventEquals(QuestEvent.USE_SPECIAL_ITEM), 1).save());
  }
}
