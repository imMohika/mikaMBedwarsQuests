package ir.mohika.mikambedwarsquests.events;

import de.marcely.bedwars.api.event.player.PlayerQuitArenaEvent;
import ir.mohika.mikambedwarsquests.quest.PlayerQuests;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuitArenaListener implements Listener {
  @EventHandler
  public void onPlayerQuitArena(PlayerQuitArenaEvent event) {
    Player player = event.getPlayer();
    PlayerQuests.from(player, playerQuests -> playerQuests.resetChallenges().save());
  }
}
