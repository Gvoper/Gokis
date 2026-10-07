package gvoper.gokis.mixin;

import gvoper.gokis.skill.SkillHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Damage of the skills. The NeoForge original listened to {@code LivingIncomingDamageEvent}, which
 * can rewrite the amount and cancel the hit; Architectury's hurt event can only interrupt, so the
 * same logic runs here: the amount is rewritten first, the cancellation is applied right after.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageMixin {
    @Unique
    private boolean gokis$cancelled;

    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float gokis$modifyDamage(float amount, ServerLevel level, DamageSource source) {
        float[] box = new float[]{amount};
        this.gokis$cancelled = SkillHooks.onIncomingDamage((LivingEntity) (Object) this, source, box);
        return box[0];
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void gokis$cancelHurt(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.gokis$cancelled) {
            this.gokis$cancelled = false;
            cir.setReturnValue(false);
        }
    }
}
