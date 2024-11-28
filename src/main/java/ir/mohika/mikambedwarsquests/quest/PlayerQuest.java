package ir.mohika.mikambedwarsquests.quest;

import com.google.gson.*;
import ir.mohika.mikambedwarsquests.config.QuestsConfig;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static ir.mohika.mikambedwarsquests.MikaMBedwarsQuests.logger;

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

  public static class PlayerQuestAdapter
      implements JsonSerializer<PlayerQuest>, JsonDeserializer<PlayerQuest> {

    @Override
    public JsonElement serialize(
        PlayerQuest playerQuest, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      json.addProperty("quest", playerQuest.quest.uniqueId());
      json.addProperty("value", playerQuest.value);
      return json;
    }

    @Override
    public PlayerQuest deserialize(
        JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      JsonObject jsonObject = json.getAsJsonObject();
      String questId = jsonObject.get("quest").getAsString();
      int value;
      if (jsonObject.get("value") != null) {
        value = jsonObject.get("value").getAsInt();
      } else {
        logger()
            .warning(
                "Trying to deserialize quest with id of "
                    + questId
                    + " but its value is not set. Setting it to 0.");
        value = 0;
      }

      Optional<Quest> quest = Quests.get(questId);
      if (quest.isPresent()) {
        return new PlayerQuest(quest.get(), value);
      }

      QuestsConfig.Quest questConfig = QuestsConfig.instance().quests().get(questId);
      if (questConfig != null) {
        return new PlayerQuest(Quest.from(questId, questConfig), value);
      }

      logger()
          .warning(
              "Trying to deserialize quest with id of "
                  + questId
                  + " but it doesn't exists in quests config");
      // todo)) better handling
      return null;
    }
  }
}
