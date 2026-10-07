package gvoper.gokis.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gvoper.gokis.GokiSkills;
import gvoper.gokis.misc.GokiUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Per player skill data: the level of every skill and the skills the player switched off. */
public class SkillInfo {
    private static final int SCHEMA_VERSION = 1;

    /** Saved as a compound of "skill id -> level", same shape as the original mod used. */
    private static final Codec<Map<String, Integer>> LEVELS_CODEC = Codec.unboundedMap(Codec.STRING, ExtraCodecs.NON_NEGATIVE_INT);

    public static final MapCodec<SkillInfo> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LEVELS_CODEC.optionalFieldOf("levels", Map.of()).forGetter(SkillInfo::getLevels),
            Codec.STRING.listOf().optionalFieldOf("disabled", List.of()).forGetter(info -> List.copyOf(info.getDisabled())),
            Codec.INT.optionalFieldOf("schema", SCHEMA_VERSION).forGetter(info -> SCHEMA_VERSION)
    ).apply(instance, (levels, disabled, schema) -> new SkillInfo(levels, Set.copyOf(disabled))));

    public static final StreamCodec<RegistryFriendlyByteBuf, SkillInfo> STREAM_CODEC = StreamCodec.of(
            (buf, info) -> {
                buf.writeVarInt(info.levels.size());
                info.levels.forEach((key, value) -> {
                    buf.writeUtf(key);
                    buf.writeVarInt(value);
                });
                buf.writeVarInt(info.disabled.size());
                info.disabled.forEach(buf::writeUtf);
            },
            buf -> {
                int levelsSize = buf.readVarInt();
                Map<String, Integer> levels = new LinkedHashMap<>(levelsSize);
                for (int i = 0; i < levelsSize; i++) {
                    levels.put(buf.readUtf(), buf.readVarInt());
                }
                int disabledSize = buf.readVarInt();
                Set<String> disabled = new HashSet<>(disabledSize);
                for (int i = 0; i < disabledSize; i++) {
                    disabled.add(buf.readUtf());
                }
                return new SkillInfo(levels, disabled);
            }
    );

    private final Map<String, Integer> levels;
    private final Set<String> disabled;

    public SkillInfo() {
        this(Map.of(), Set.of());
    }

    public SkillInfo(Map<String, Integer> levels, Set<String> disabled) {
        this.levels = levels instanceof LinkedHashMap<String, Integer> ? levels : new LinkedHashMap<>(levels);
        this.disabled = disabled instanceof HashSet<String> ? disabled : new HashSet<>(disabled);
    }

    public Map<String, Integer> getLevels() {
        return levels;
    }

    public Set<String> getDisabled() {
        return disabled;
    }

    public int getLevel(Skill skill) {
        return levels.getOrDefault(skill.getLocation().toString(), skill.getDefaultLevel());
    }

    public int getLevel(Identifier location) {
        return getLevel(SkillRegistry.getSkill(location));
    }

    public void setLevel(Skill skill, int level) {
        levels.put(skill.getLocation().toString(), level);
    }

    @Nullable
    public Double getBonus(Skill skill) {
        return skill.calcBonus(isEnabled(skill) ? getLevel(skill) : skill.getDefaultLevel());
    }

    @Nullable
    public Double getBonus(Identifier location) {
        return getBonus(SkillRegistry.getSkill(location));
    }

    /** The bonus a skill grants right now: 0 when the skill is off or has no bonus at all. */
    public double getBonusValue(Skill skill) {
        if (!isEnabled(skill)) return 0.0;
        Double bonus = skill.calcBonus(getLevel(skill));
        return bonus == null ? 0.0 : bonus;
    }

    public boolean isEnabled(Skill skill) {
        return skill.isEnabled() && !disabled.contains(skill.getLocation().toString());
    }

    public boolean isEnabled(Identifier location) {
        return isEnabled(SkillRegistry.getSkill(location));
    }

    public void toggle(Skill skill) {
        String key = skill.getLocation().toString();
        if (isEnabled(skill)) {
            disabled.add(key);
        } else {
            disabled.remove(key);
        }
    }

    /** Rolls the "lose levels" penalty for every skill. Returns true if anything changed. */
    public boolean onDeath() {
        boolean changed = false;
        if (!GokiSkills.getConfig().lostLevelOnDeath.enabled) return false;
        for (Map.Entry<String, Integer> entry : levels.entrySet()) {
            if (Math.random() >= GokiSkills.getConfig().lostLevelOnDeath.chance) continue;
            Identifier location = Identifier.tryParse(entry.getKey());
            if (location == null || SkillRegistry.getSkill(location) == null) continue;
            Skill skill = SkillRegistry.getSkill(location);
            int lostLevel = Math.min(
                    GokiUtils.randomInt(
                            GokiSkills.getConfig().lostLevelOnDeath.minLevel,
                            GokiSkills.getConfig().lostLevelOnDeath.maxLevel + 1
                    ),
                    entry.getValue() - skill.getMinLevel()
            );
            if (lostLevel > 0) {
                entry.setValue(entry.getValue() - lostLevel);
                changed = true;
            }
        }
        return changed;
    }

}
