package ir.mohika.mikambedwarsquests.config;

import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurationStore;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
  private static final Map<String, LoadedConfig> loadedConfigs = new HashMap<>();
  private static final YamlConfigurationProperties properties =
      YamlConfigurationProperties.newBuilder().build();

  public static <T> T load(String name, File dataPath, Class<T> clazz) throws IOException {
    return load(name, dataPath.toPath(), clazz);
  }

  public static <T> T load(String name, Path dataPath, Class<T> clazz) throws IOException {
    YamlConfigurationStore<T> store = new YamlConfigurationStore<>(clazz, properties);
    Files.createDirectories(dataPath);

    Path configPath = dataPath.resolve(name + ".yml");

    if (!configPath.toFile().exists()) {
      T configInstance = createDefaultInstance(clazz);
      store.save(configInstance, configPath);
      T config = store.load(configPath);
      loadedConfigs.put(name, new LoadedConfig(store, configPath));
      return config;
    }

    T config = store.update(configPath);
    loadedConfigs.put(name, new LoadedConfig(store, configPath));
    return config;
  }

  public static <T> YamlConfigurationStore<T> getStore(String fileName) {
    @SuppressWarnings("unchecked")
    YamlConfigurationStore<T> store =
        (YamlConfigurationStore<T>) loadedConfigs.get(fileName).store();
    return store;
  }

  private static <T> T createDefaultInstance(Class<T> configClass) {
    try {
      return configClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException(
          "Unable to create default instance of " + configClass.getName(), e);
    }
  }

  public record LoadedConfig(YamlConfigurationStore<?> store, Path path) {}
}
