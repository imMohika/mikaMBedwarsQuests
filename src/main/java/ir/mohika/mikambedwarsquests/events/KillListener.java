package ir.mohika.mikambedwarsquests.events;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.player.PlayerKillPlayerEvent;
import de.marcely.bedwars.tools.location.XYZD;
import ir.mohika.mikambedwarsquests.config.Config;
import ir.mohika.mikambedwarsquests.quest.PlayerQuests;
import ir.mohika.mikambedwarsquests.quest.QuestEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class KillListener implements Listener {
  @EventHandler
  public void onKill(PlayerKillPlayerEvent event) {
    Player killer = event.getKiller();
    if (killer == null) {
      return;
    }
    kill(event, killer);
    bedDefend(event, killer);
  }

  private static void kill(PlayerKillPlayerEvent event, Player killer) {
    var questEvents = new ArrayList<QuestEvent>();

    if (event.isFatalDeath()) {
      questEvents.add(QuestEvent.FINAL_KILL);
      if (!Config.instance().killSettings().notFinalKill()) {
        questEvents.add(QuestEvent.KILL);
      }
    } else {
      questEvents.add(QuestEvent.KILL);
    }

    PlayerQuests.from(
        killer, playerQuests -> playerQuests.updateQuests(pq -> pq.eventIn(questEvents), 1).save());
  }

  private void bedDefend(PlayerKillPlayerEvent event, Player killer) {
    Arena arena = event.getArena();
    Team killerTeam = arena.getPlayerTeam(killer);
    if (killerTeam == null) {
      return;
    }

    XYZD bedLocation = arena.getBedLocation(killerTeam);
    if (bedLocation == null) {
      return;
    }

    Player damaged = event.getDamaged();
    if (bedLocation.distance(damaged.getLocation())
        >= Config.instance().bedDefendSettings().radius()) {
      return;
    }

    PlayerQuests.from(
        killer,
        playerQuests ->
            playerQuests.updateQuests(pq -> pq.eventEquals(QuestEvent.BED_DEFEND), 1).save());
  }
}
