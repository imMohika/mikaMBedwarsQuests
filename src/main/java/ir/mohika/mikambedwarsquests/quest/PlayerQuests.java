package ir.mohika.mikambedwarsquests.quest;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.marcely.bedwars.api.player.PlayerDataAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import static ir.mohika.mikambedwarsquests.MikaMBedwarsQuests.logger;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerQuests {
  private final Player player;
  private final Map<String, PlayerQuest> quests;
  private static final Gson gson =
      new GsonBuilder()
          .registerTypeAdapter(PlayerQuest.class, new PlayerQuest.PlayerQuestAdapter())
          .create();

  public static void from(Player player, Consumer<PlayerQuests> callback) {
    PlayerDataAPI.get()
        .getProperties(
            player.getUniqueId(),
            playerProperties -> {
              final Optional<String> json = playerProperties.get("mikaquests");

              if (json.isEmpty()) {
                callback.accept(new PlayerQuests(player, new HashMap<>()));
                return;
              }

              Map<String, PlayerQuest> playerQuests =
                  gson.fromJson(json.get(), new TypeToken<Map<String, PlayerQuest>>() {}.getType());

              callback.accept(new PlayerQuests(player, playerQuests));
            });
  }

  public void save() {
    String json = gson.toJson(quests);
    PlayerDataAPI.get()
        .getProperties(
            player.getUniqueId(), playerProperties -> playerProperties.set("mikaquests", json));
  }

  public PlayerQuests addQuest(Quest quest) {
    if (quests.containsKey(quest.uniqueId())) {
      // todo)) remove this after enough testing
      logger()
          .severe(
              "Tried to add duplicate quest id of "
                  + quest.uniqueId()
                  + " for "
                  + player.getName());
      return this;
    }

    Map<String, PlayerQuest> updatedQuests = questsDeepCopy();
    updatedQuests.put(quest.uniqueId(), new PlayerQuest(quest, 0));
    return new PlayerQuests(player, updatedQuests);
  }

  public PlayerQuests removeQuest(String questId) {
    Map<String, PlayerQuest> updatedQuests = questsDeepCopy();
    updatedQuests.remove(questId);
    return new PlayerQuests(player, updatedQuests);
  }

  public PlayerQuests updateQuests(Predicate<PlayerQuest> condition, int incBy) {
    Map<String, PlayerQuest> updatedQuests = new HashMap<>();
    quests.forEach(
        (key, playerQuest) -> {
          int newValue = playerQuest.value();
          if (condition.test(playerQuest)) {
            newValue += incBy;
            if (newValue >= playerQuest.quest().objective().goal()) {
              playerQuest.quest().giveReward(player);
              return;
            }
          }

          updatedQuests.put(key, new PlayerQuest(playerQuest.quest(), newValue));
        });
    return new PlayerQuests(player, updatedQuests);
  }

  public Optional<PlayerQuest> getQuest(String uniqueId) {
    return Optional.ofNullable(quests.get(uniqueId));
  }

  private Map<String, PlayerQuest> questsDeepCopy() {
    return quests.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                e -> new PlayerQuest(e.getValue().quest(), e.getValue().value())));
  }

  public boolean hasQuest(String questId) {
    return quests.containsKey(questId);
  }
}
