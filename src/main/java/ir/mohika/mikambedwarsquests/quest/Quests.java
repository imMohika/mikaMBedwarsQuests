package ir.mohika.mikambedwarsquests.quest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class Quests {
  private static final Map<String, Quest> quests = new HashMap<>();

  public static void register(Quest quest) {
    quests.put(quest.uniqueId(), quest);
  }

  public static Optional<Quest> get(String uniqueId) {
    return Optional.ofNullable(quests.get(uniqueId));
  }

  public static Stream<Quest> all() {
    return quests.values().stream();
  }

  public static int count() {
    return quests.size();
  }
}
