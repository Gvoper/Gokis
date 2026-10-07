package gvoper.gokis.mixin;

import gvoper.gokis.skill.SkillHooks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Swimming slows down the air consumption. The NeoForge original used {@code LivingBreatheEvent};
 * vanilla only drains the air in {@code decreaseAirSupply}, so the amount is rewritten there.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityAirMixin {
    @Inject(method = "decreaseAirSupply", at = @At("RETURN"), cancellable = true)
    private void gokis$swimBreath(int airSupply, CallbackInfoReturnable<Integer> cir) {
        if (!(((Object) this) instanceof Player player)) return;
        int consumed = airSupply - cir.getReturnValue();
        if (consumed <= 0) return;
        int reduced = SkillHooks.consumedAir(player, consumed);
        if (reduced != consumed) {
            cir.setReturnValue(airSupply - reduced);
        }
    }
}
