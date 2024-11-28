package ir.mohika.mikambedwarsquests.quest;

import static ir.mohika.mikambedwarsquests.MikaMBedwarsQuests.logger;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ir.mohika.mikambedwarsquests.config.QuestsConfig;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record PlayerQuestsData(Map<String, PlayerQuest> quests, Map<String, Long> cooldowns) {
  public static class PlayerQuestsDataAdapter
      implements JsonSerializer<PlayerQuestsData>, JsonDeserializer<PlayerQuestsData> {

    @Override
    public JsonElement serialize(
        PlayerQuestsData playerQuestsData,
        Type type,
        JsonSerializationContext jsonSerializationContext) {
      JsonObject jsonObject = new JsonObject();

      JsonObject progressJson = new JsonObject();
      playerQuestsData.quests.forEach(
          (key, playerQuest) -> progressJson.addProperty(key, playerQuest.value()));
      jsonObject.add("progress", progressJson);

      JsonObject cooldownsJson = new JsonObject();
      playerQuestsData.cooldowns().forEach(cooldownsJson::addProperty);
      jsonObject.add("cooldowns", cooldownsJson);

      return jsonObject;
    }

    @Override
    public PlayerQuestsData deserialize(
        JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      JsonObject jsonObject = json.getAsJsonObject();

      Map<String, PlayerQuest> quests = new HashMap<>();
      if (jsonObject.has("progress") && jsonObject.get("progress").isJsonObject()) {
        JsonObject progressJson = jsonObject.getAsJsonObject("progress");
        progressJson
            .entrySet()
            .forEach(
                entry -> {
                  String questId = entry.getKey();
                  int value;
                  if (entry.getValue().isJsonPrimitive()) {
                    value = entry.getValue().getAsInt();
                  } else {
                    logger()
                        .warning(
                            "Trying to deserialize quest with id of "
                                + questId
                                + " but its value is not set. Setting it to 0.");
                    value = 0;
                  }

                  Optional<Quest> quest =
                      Optional.ofNullable(
                          Quests.get(questId)
                              .orElseGet(
                                  () -> {
                                    QuestsConfig.Quest questConfig =
                                        QuestsConfig.instance().quests().get(questId);
                                    if (questConfig == null) {
                                      return null;
                                    }
                                    return Quest.from(questId, questConfig);
                                  }));

                  if (quest.isEmpty()) {
                    logger()
                        .warning(
                            "Trying to deserialize quest with id of "
                                + questId
                                + " but it doesn't exists in quests config");
                    return;
                  }

                  quests.put(questId, new PlayerQuest(quest.get(), value));
                });
      }

      Map<String, Long> cooldowns;
      if (jsonObject.has("cooldowns") && jsonObject.get("cooldowns").isJsonObject()) {
        JsonObject cooldownsJson = jsonObject.getAsJsonObject("cooldowns");
        cooldowns =
            context.deserialize(cooldownsJson, new TypeToken<Map<String, Long>>() {}.getType());
      } else {
        cooldowns = new HashMap<>();
      }

      return new PlayerQuestsData(quests, cooldowns);
    }
  }
}
