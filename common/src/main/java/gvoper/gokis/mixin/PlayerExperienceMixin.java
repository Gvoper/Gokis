package gvoper.gokis.mixin;

import gvoper.gokis.skill.SkillHelper;
import gvoper.gokis.skill.SkillHooks;
import gvoper.gokis.skill.SkillInfo;
import gvoper.gokis.skill.Skills;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Experience boost: every experience point a player receives is multiplied.
 *
 * <p>The spend/return of the skill menu itself goes through {@code giveExperiencePoints} too, so it
 * is marked with {@link SkillHooks#bypassExperienceBoost} — otherwise buying levels would cost less
 * than it returns and the skill would print experience.
 */
@Mixin(Player.class)
public abstract class PlayerExperienceMixin {
    @ModifyVariable(method = "giveExperiencePoints", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int gokis$experienceBoost(int amount) {
        if (amount <= 0 || SkillHooks.bypassExperienceBoost) return amount;
        SkillInfo info = SkillHelper.getInfo((Player) (Object) this);
        if (!info.isEnabled(Skills.EXPERIENCE_BOOST)) return amount;
        double bonus = info.getBonusValue(Skills.EXPERIENCE_BOOST);
        if (bonus <= 0) return amount;
        return (int) Math.round(amount * (1 + bonus));
    }
}
