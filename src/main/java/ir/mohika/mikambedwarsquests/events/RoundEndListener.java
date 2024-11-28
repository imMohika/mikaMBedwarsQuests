package ir.mohika.mikambedwarsquests.events;

import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import ir.mohika.mikambedwarsquests.quest.PlayerQuests;
import ir.mohika.mikambedwarsquests.quest.QuestEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RoundEndListener implements Listener {
  @EventHandler
  public void onRoundEnd(RoundEndEvent event) {
    event
        .getWinners()
        .forEach(
            player ->
                PlayerQuests.from(
                    player,
                    playerQuests ->
                        playerQuests
                            .updateQuests(
                                pq ->
                                    pq.eventEquals(QuestEvent.WIN_GAME)
                                        || pq.eventEquals(QuestEvent.PLAY_GAME),
                                1)
                            .save()));

    event
        .getLosers()
        .forEach(
            player ->
                PlayerQuests.from(
                    player,
                    playerQuests ->
                        playerQuests
                            .updateQuests(pq -> pq.eventEquals(QuestEvent.PLAY_GAME), 1)
                            .save()));
  }
}
