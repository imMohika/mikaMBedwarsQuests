package ir.mohika.mikambedwarsquests.events;

import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaBedBreakEvent;
import ir.mohika.mikambedwarsquests.config.Config;
import ir.mohika.mikambedwarsquests.quest.PlayerQuests;
import ir.mohika.mikambedwarsquests.quest.QuestEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BedBreakListener implements Listener {
  @EventHandler
  public void onBedBreak(ArenaBedBreakEvent event) {
    if (!event.isPlayerCaused() || event.getPlayer() == null) {
      return;
    }

    Set<Player> members = new HashSet<>();

    Player destroyer = event.getPlayer();
    members.add(destroyer);

    @Nullable Team playerTeam = event.getArena().getPlayerTeam(destroyer);
    if (playerTeam != null && !Config.instance().bedBreakSettings().onlyDestroyer()) {
      members.addAll(event.getArena().getPlayersInTeam(playerTeam));
    }

    members.forEach(
        member ->
            PlayerQuests.from(
                member,
                playerQuests ->
                    playerQuests
                        .updateQuests(pq -> pq.eventEquals(QuestEvent.BED_BREAK), 1)
                        .save()));
  }
}
