package gvoper.gokis.skill;

import gvoper.gokis.config.ConfigUtils;
import gvoper.gokis.config.GokiSkillConfig;
import gvoper.gokis.GokiSkills;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/** A skill that can be unlocked and upgraded with experience points. */
public interface ISkill {
    Identifier getLocation();

    /** Category used to group skills into rows in the menu. */
    Identifier getCategory();

    boolean isEnabled();

    int getMaxLevel();

    int getDefaultLevel();

    int getMinLevel();

    int calcCost(int level); // curr level -> cost

    int calcReturn(int level); // curr level -> return

    @Nullable
    Double calcBonus(int level); // curr level -> bonus

    Component getName();

    Component getDescription(int level, @Nullable Double bonus);

    Class<? extends GokiSkillConfig> getConfigClass();

    GokiSkillConfig getDefaultConfig();

    @SuppressWarnings("unchecked")
    default <T extends GokiSkillConfig> T getConfig() {
        return (T) ConfigUtils.fromJsonObject(
                GokiSkills.getConfig().skills.get(getLocation().toString()),
                getConfigClass()
        );
    }
}
