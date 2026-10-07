package gvoper.gokis.config;

import com.google.gson.JsonObject;
import gvoper.gokis.skill.SkillRegistry;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CommonConfig implements GokiConfig {
    public LostLevelOnDeath lostLevelOnDeath = new LostLevelOnDeath();
    /** Mobs with at most this much max health can be reaped by the reaper skill. */
    public int reaperMaxHealth = 20;
    public Map<String, JsonObject> skills = SkillRegistry.getDefaultConfigs();

    public static class LostLevelOnDeath {
        public boolean enabled = false;
        public double chance = 0.5;
        public int minLevel = 1;
        public int maxLevel = 1;
    }

    @Override
    public void validatePostLoad() throws ConfigException {
        if (lostLevelOnDeath.chance < 0.0 || lostLevelOnDeath.chance > 1.0)
            throw new ConfigException("Lost level on death chance must be between 0.0 and 1.0");
        if (lostLevelOnDeath.minLevel < 0 || lostLevelOnDeath.maxLevel < 0)
            throw new ConfigException("Lost level on death levels cannot be negative");
        if (reaperMaxHealth < 1)
            throw new ConfigException("Reaper max health must be positive");
        if (lostLevelOnDeath.minLevel > lostLevelOnDeath.maxLevel)
            throw new ConfigException("Lost level on death min level cannot be greater than max level");
        skills = new HashMap<>(skills);
        SkillRegistry.getDefaultConfigs().forEach((key, value) -> this.skills.putIfAbsent(key, value));
        skills.forEach((key, value) -> {
            try {
                var skill = SkillRegistry.getSkill(Identifier.tryParse(key));
                GokiSkillConfig config = ConfigUtils.fromJsonObject(value, skill.getConfigClass());
                GokiSkillConfig defaultConfig = skill.getDefaultConfig();

                // v1.0.0 configs have no per-skill levels
                if (!value.has("minLevel")) {
                    boolean enabled = config.enabled;
                    config = defaultConfig;
                    config.enabled = enabled;
                } else {
                    // The level range describes the shape of the skill, not its balance: it comes from
                    // the code. An old config file would otherwise freeze the old range and the same
                    // skill would level up differently on two installs.
                    config.minLevel = defaultConfig.minLevel;
                    config.defaultLevel = defaultConfig.defaultLevel;
                    config.maxLevel = defaultConfig.maxLevel;
                }

                config.validatePostLoad();
                skills.put(key, ConfigUtils.toJsonObject(config));
            } catch (ConfigException e) {
                throw new ConfigException("Invalid skill config for " + key + ": " + e.getMessage());
            }
        });
        skills = Map.copyOf(skills);
    }
}
