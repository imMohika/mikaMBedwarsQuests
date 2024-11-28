package ir.mohika.mikambedwarsquests.quest;

import static ir.mohika.mikambedwarsquests.MikaMBedwarsQuests.logger;

import com.google.gson.*;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ir.mohika.mikambedwarsquests.config.Messages;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class PlayerQuests {
  private final Player player;
  private final Map<String, PlayerQuest> quests;
  private final Map<String, Long> cooldowns;

  private static final Gson gson =
      new GsonBuilder()
          .registerTypeAdapter(
              PlayerQuestsData.class, new PlayerQuestsData.PlayerQuestsDataAdapter())
          .create();

  public static void from(Player player, Consumer<PlayerQuests> callback) {
    PlayerDataAPI.get()
        .getProperties(
            player.getUniqueId(),
            playerProperties -> {
              final Optional<String> json = playerProperties.get("mikaquests");

              if (json.isEmpty()) {
                callback.accept(new PlayerQuests(player, new HashMap<>(), new HashMap<>()));
                return;
              }

              PlayerQuestsData playerQuestsData = gson.fromJson(json.get(), PlayerQuestsData.class);

              callback.accept(
                  new PlayerQuests(
                      player, playerQuestsData.quests(), playerQuestsData.cooldowns()));
            });
  }

  public void save() {
    String json = gson.toJson(new PlayerQuestsData(quests, cooldowns));
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

    Map<String, Long> updatedCooldowns;
    @Nullable Long cooldown = cooldowns.get(quest.uniqueId());
    if (cooldown != null && quest.type() != QuestType.CHALLENGE) {
      if (System.currentTimeMillis() < cooldown) {
        String message =
            switch (quest.type()) {
              case DAILY -> Messages.instance().dailyQuestAlreadyCompleted(quest.name(), player);
              case WEEKLY -> Messages.instance().weeklyQuestAlreadyCompleted(quest.name(), player);
              case CHALLENGE -> throw new IllegalStateException("how?");
            };
        Message.build(message).send(player);
        return this;
      } else {
        updatedCooldowns = new HashMap<>(cooldowns);
        updatedCooldowns.remove(quest.uniqueId());
      }
    } else {
      updatedCooldowns = cooldowns;
    }

    Map<String, PlayerQuest> updatedQuests = questsDeepCopy();
    updatedQuests.put(quest.uniqueId(), new PlayerQuest(quest, 0));

    return new PlayerQuests(player, updatedQuests, updatedCooldowns);
  }

  public PlayerQuests removeQuest(String questId) {
    Map<String, PlayerQuest> updatedQuests = questsDeepCopy();
    updatedQuests.remove(questId);
    return new PlayerQuests(player, updatedQuests, cooldowns);
  }

  public PlayerQuests updateQuests(Predicate<PlayerQuest> condition, int incBy) {
    Map<String, PlayerQuest> updatedQuests = new HashMap<>();
    Map<String, Long> updatedCooldowns = new HashMap<>(cooldowns);
    quests.forEach(
        (key, playerQuest) -> {
          int newValue = playerQuest.value();
          if (condition.test(playerQuest)) {
            newValue += incBy;
            if (newValue >= playerQuest.quest().objective().goal()) {
              playerQuest.quest().giveReward(player);

              @Nullable
              TemporalAmount plus =
                  switch (playerQuest.quest().type()) {
                    case DAILY -> Period.ofDays(1);
                    case WEEKLY -> Period.ofWeeks(1);
                    case CHALLENGE -> null;
                  };

              if (plus != null) {
                updatedCooldowns.put(key, Instant.now().plus(plus).toEpochMilli());
              }
              return;
            }
          }

          updatedQuests.put(key, new PlayerQuest(playerQuest.quest(), newValue));
        });
    return new PlayerQuests(player, updatedQuests, updatedCooldowns);
  }

  public PlayerQuests resetChallenges() {
    Map<String, PlayerQuest> updatedQuests = new HashMap<>();
    quests.forEach(
        (key, playerQuest) -> {
          int newValue = playerQuest.value();
          if (playerQuest.quest().type() == QuestType.CHALLENGE) {
            newValue = 0;
          }

          updatedQuests.put(key, new PlayerQuest(playerQuest.quest(), newValue));
        });
    return new PlayerQuests(player, updatedQuests, cooldowns);
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
