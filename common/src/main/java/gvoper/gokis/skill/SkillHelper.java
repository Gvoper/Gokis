package gvoper.gokis.skill;

import gvoper.gokis.misc.GokiData;
import gvoper.gokis.misc.GokiUtils;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

/** Access to the skill data of players. */
public final class SkillHelper {
    private SkillHelper() {}

    /** The skill info of a player, or null when it has not been created yet. */
    public static SkillInfo getInfoOrNull(Player player) {
        return GokiData.getInfoOrNull(player);
    }

    /** The skill info of a player, creating an empty one if needed. */
    public static SkillInfo getInfo(Player player) {
        return GokiData.getInfo(player);
    }

    /** The bonus of one skill of a player, without reaching through the whole skill info. */
    public static double bonus(Player player, Skill skill) {
        return getInfo(player).getBonusValue(skill);
    }

    public static void setSkillInfo(Player player, SkillInfo skillInfo) {
        GokiData.setInfo(player, skillInfo);
    }

    public static int getTotalXp(Player player) {
        return GokiUtils.getTotalXpNeededForLevel(player.experienceLevel)
                + Mth.floor(player.experienceProgress * GokiUtils.getXpNeededForNextLevel(player.experienceLevel));
    }

    /**
     * Calculate the cost of upgrading / downgrading skill
     *
     * @param skill   skill
     * @param level   current skill level
     * @param xp      current experience points
     * @param upgrade is upgrade / downgrade
     * @param fast    is fast upgrade / downgrade
     * @return [addLevel, addXp]
     */
    public static int[] calcOperation(Skill skill, int level, int xp, boolean upgrade, boolean fast) {
        int addXp = 0;
        int addLevel = 0;
        if (upgrade) {
            if (fast) {
                while (level + addLevel < skill.getMaxLevel()) {
                    int thisCost = skill.calcCost(level + addLevel);
                    if (-addXp + thisCost > xp) break;
                    addLevel++;
                    addXp -= thisCost;
                }
                return new int[]{addLevel, addXp};
            } else {
                addXp = skill.calcCost(level);
                if (addXp > xp || level + 1 > skill.getMaxLevel()) return new int[]{0, 0};
                else return new int[]{1, -addXp};
            }
        } else {
            if (fast) {
                while (level + addLevel > skill.getMinLevel()) {
                    addXp += skill.calcReturn(level + addLevel);
                    addLevel--;
                }
                return new int[]{addLevel, addXp};
            } else {
                if (level - 1 < skill.getMinLevel()) {
                    return new int[]{0, 0};
                } else {
                    addXp = skill.calcReturn(level);
                    return new int[]{-1, addXp};
                }
            }
        }
    }

    /** Applies an upgrade / downgrade on the server, spending or returning experience points. */
    public static void updateSkill(ServerPlayer player, Identifier location, boolean upgrade, boolean fast) {
        Skill skill = SkillRegistry.getSkill(location);
        if (skill == null || !skill.isEnabled()) return; // unknown or disabled skill
        SkillInfo info = getInfo(player);

        int level = info.getLevel(skill);
        int[] result = calcOperation(skill, level, getTotalXp(player), upgrade, fast);

        info.setLevel(skill, level + result[0]);
        GokiData.markDirty(player);
        SkillHooks.bypassExperienceBoost = true;
        try {
            player.giveExperiencePoints(result[1]);
        } finally {
            SkillHooks.bypassExperienceBoost = false;
        }
        player.connection.send(new ClientboundSetExperiencePacket(
                player.experienceProgress,
                player.totalExperience,
                player.experienceLevel
        ));
        SkillHooks.updateAttribute(player, info, skill);
        SkillHooks.sync(player, info);
    }
}
