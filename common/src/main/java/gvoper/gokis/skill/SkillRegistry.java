package gvoper.gokis.skill;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import gvoper.gokis.config.ConfigUtils;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static gvoper.gokis.GokiSkills.resource;

/** Holds every skill of the mod, keyed by its id. Ids are stable: they are used in configs and saves. */
public final class SkillRegistry {
    private static final Map<Identifier, Skill> SKILLS = new LinkedHashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean initialized = false;

    private SkillRegistry() {}

    public static synchronized void init() {
        if (initialized) return;
        initialized = true;
        Skills.bootstrap();
    }

    public static Skill register(String path, Skill skill) {
        Identifier location = resource(path);
        skill.setLocation(location);
        SKILLS.put(location, skill);
        return skill;
    }

    public static Map<String, JsonObject> getDefaultConfigs() {
        Map<String, JsonObject> configs = new HashMap<>();
        SKILLS.forEach((location, skill) -> {
            try {
                configs.put(location.toString(), ConfigUtils.toJsonObject(skill.getDefaultConfig()));
            } catch (Exception e) {
                LOGGER.warn("Error creating config for skill {}", location, e);
            }
        });
        return Map.copyOf(configs);
    }

    public static Skill getSkill(Identifier location) {
        Skill skill = SKILLS.get(location);
        if (skill == null) throw new NoSuchElementException("Unknown skill: " + location);
        return skill;
    }

    public static Collection<Skill> getSkills() {
        return SKILLS.values();
    }

    /** Skills grouped by category and sorted, ready to be laid out by the menu. */
    public static List<List<Skill>> getSortedSkills() {
        Map<Identifier, List<Skill>> categories = new LinkedHashMap<>();
        getSkills().forEach(skill -> categories.computeIfAbsent(skill.getCategory(), key -> new ArrayList<>()).add(skill));
        List<List<Skill>> sorted = new ArrayList<>();
        categories.entrySet().stream()
                .sorted((e1, e2) -> compareIdentifier(e1.getKey(), e2.getKey()))
                .forEach(entry -> {
                    List<Skill> skills = entry.getValue().stream()
                            .sorted((s1, s2) -> compareIdentifier(s1.getLocation(), s2.getLocation()))
                            .filter(ISkill::isEnabled)
                            .toList();
                    if (!skills.isEmpty()) sorted.add(skills);
                });
        return sorted;
    }

    /** Own ids come first, then plain alphabetical order. */
    public static int compareIdentifier(Identifier id1, Identifier id2) {
        boolean isGoki1 = id1.getNamespace().equals(gvoper.gokis.GokiSkills.MOD_ID);
        boolean isGoki2 = id2.getNamespace().equals(gvoper.gokis.GokiSkills.MOD_ID);
        if (isGoki1 && !isGoki2) return -1;
        if (!isGoki1 && isGoki2) return 1;
        int compare = id1.compareTo(id2);
        return compare != 0 ? compare : id1.getPath().compareTo(id2.getPath());
    }
}
