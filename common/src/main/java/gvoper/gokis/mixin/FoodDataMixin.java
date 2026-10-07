package gvoper.gokis.mixin;

import gvoper.gokis.skill.SkillHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Slow hunger, part two: the exhaustion the body spends on regeneration.
 *
 * <p>Vanilla does not route that through {@code Player.causeFoodExhaustion} — {@code FoodData.tick}
 * calls its own {@code addExhaustion} directly, twice (once while the hidden saturation lasts, once
 * from the food bar), so a mixin on the player method alone would leave the hidden bar untouched.
 *
 * <p>{@code @ModifyArg} cannot take the owner's arguments, so the player is remembered at the head
 * of {@code tick} and used by the argument modifier.
 */
@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @Unique
    private ServerPlayer gokis$player;

    @Inject(method = "tick", at = @At("HEAD"))
    private void gokis$rememberPlayer(ServerPlayer player, CallbackInfo ci) {
        this.gokis$player = player;
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"), index = 0)
    private float gokis$slowHungerRegeneration(float exhaustion) {
        if (this.gokis$player == null) return exhaustion;
        return (float) SkillHooks.slowHunger(this.gokis$player, exhaustion);
    }
}
