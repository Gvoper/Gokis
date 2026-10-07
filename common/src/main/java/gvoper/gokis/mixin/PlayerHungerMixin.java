package gvoper.gokis.mixin;

import gvoper.gokis.skill.SkillHooks;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Slow hunger, part one: the exhaustion the world pushes in (running, jumping, mining, food).
 * The part the body spends on its own regeneration lives in {@code FoodDataMixin}.
 */
@Mixin(Player.class)
public abstract class PlayerHungerMixin {
    @ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float gokis$slowHunger(float exhaustion) {
        return (float) SkillHooks.slowHunger((Player) (Object) this, exhaustion);
    }
}
