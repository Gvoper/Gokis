package gvoper.gokis.config;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import dev.architectury.platform.Platform;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConfigUtils {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    return fieldAttributes.getAnnotation(GsonIgnore.class) != null;
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            })
            .create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Path configPath(String fileName) {
        return Platform.getConfigFolder().resolve(fileName + ".json");
    }

    public static <T extends GokiConfig> T readConfig(String fileName, Class<T> configClass) {
        Path path = configPath(fileName);
        T config = null;
        try {
            config = configClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.error("Failed to create config file: {}.json, using default config", fileName, e);
        }
        if (Files.exists(path)) {
            try {
                config = deserialize(Files.readString(path), configClass);
            } catch (Exception e) {
                LOGGER.error("Failed to read config file: {}.json, using default config", fileName, e);
            }
        }
        try {
            config.validatePostLoad();
        } catch (ConfigException e) {
            LOGGER.error("Failed to validate config file: {}.json", fileName, e);
            throw e;
        }
        saveConfig(fileName, config);
        return config;
    }

    public static <T extends GokiConfig> void saveConfig(String fileName, T config) {
        try {
            Path path = configPath(fileName);
            Files.createDirectories(path.getParent());
            Files.writeString(path, serialize(config), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            LOGGER.error("Failed to save config file: {}.json", fileName, e);
        }
    }

    public static JsonObject toJsonObject(Object object) {
        return GSON.toJsonTree(object).getAsJsonObject();
    }

    public static <T> T fromJsonObject(JsonObject jsonObject, Class<T> clazz) {
        return GSON.fromJson(jsonObject, clazz);
    }

    public static String serialize(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, T> streamCodecOf(Class<T> clazz) {
        return ByteBufCodecs.STRING_UTF8.<RegistryFriendlyByteBuf>cast().map(
                str -> deserialize(str, clazz),
                ConfigUtils::serialize
        );
    }
}
