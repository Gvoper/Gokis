package gvoper.gokis.mixin;

import gvoper.gokis.skill.SkillHooks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Leaper and jump boost. Architectury has no jump event, and the vanilla delta movement is written
 * inside {@code jumpFromGround}, so the bonus goes in at the end of that method.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityJumpMixin {
    @Inject(method = "jumpFromGround", at = @At("RETURN"))
    private void gokis$jumpBoost(CallbackInfo ci) {
        if (((Object) this) instanceof Player player) {
            SkillHooks.onJump(player);
        }
    }
}
