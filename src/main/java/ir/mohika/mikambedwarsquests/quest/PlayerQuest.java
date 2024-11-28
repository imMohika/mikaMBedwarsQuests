package ir.mohika.mikambedwarsquests.quest;

import org.bukkit.Material;

import java.util.List;

public record PlayerQuest(Quest quest, int value) {
  public boolean eventEquals(QuestEvent event) {
    return event == quest.objective().event();
  }

  public boolean eventIn(List<QuestEvent> events) {
    return events.contains(quest.objective().event());
  }

  public boolean itemEquals(Material material) {
    if (!eventEquals(QuestEvent.GENERATOR_PICKUP)) {
      throw new IllegalStateException(
          "Quest.itemMaterial can only be used when Quest.event is GENERATOR_PICKUP");
    }
    return material.name().equalsIgnoreCase(quest.objective().itemMaterial());
  }
}
