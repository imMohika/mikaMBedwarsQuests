package ir.mohika.mikambedwarsquests.events;

import de.marcely.bedwars.api.event.player.PlayerPickupDropEvent;
import ir.mohika.mikambedwarsquests.quest.PlayerQuests;
import ir.mohika.mikambedwarsquests.quest.QuestEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class GeneratorPickupListener implements Listener {

  @EventHandler
  public void PickGeneratorItemEvent(PlayerPickupDropEvent event) {
    if (!event.isFromSpawner()) {
      return;
    }

    Player player = event.getPlayer();
    ItemStack itemStack = event.getItem().getItemStack();
    Material itemType = itemStack.getType();
    int itemAmount = itemStack.getAmount();

    PlayerQuests.from(
        player,
        playerQuests ->
            playerQuests
                .updateQuests(
                    pq -> pq.eventEquals(QuestEvent.GENERATOR_PICKUP) && pq.itemEquals(itemType),
                    itemAmount)
                .save());
  }
}
