package gvoper.gokis.mixin;

import com.mojang.logging.LogUtils;
import gvoper.gokis.GokiSkills;
import gvoper.gokis.skill.SkillHelper;
import gvoper.gokis.skill.SkillInfo;
import gvoper.gokis.skill.Skills;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Climbing and swimming speeds are hardcoded in vanilla and there is no event for them.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Inject(method = "handleRelativeFrictionAndCalculateMovement", at = @At("RETURN"), cancellable = true)
    private void gokis$climbBonus(Vec3 input, float friction, CallbackInfoReturnable<Vec3> cir) {
        Vec3 movement = cir.getReturnValue();
        if (movement == null || !(((Object) this) instanceof Player player)) return;

        LivingEntity self = (LivingEntity) (Object) this;
        boolean climbing = self.onClimbable()
                || (player.getInBlockState().is(Blocks.POWDER_SNOW) && PowderSnowBlock.canEntityWalkOnPowderSnow(player));
        if (!climbing || !(player.horizontalCollision || self.isJumping())) return;

        SkillInfo info = SkillHelper.getInfo(player);
        double bonus = info.getBonusValue(Skills.CLIMBING);
        Vec3 velocity = player.getDeltaMovement();
        if (bonus > 0 && velocity.y > 0) {
            cir.setReturnValue(new Vec3(movement.x, 0.2 * (1 + bonus), movement.z));
        }
    }

    /**
     * The swim bonus. Vanilla applies the water drag just before this call
     * ({@code getDeltaMovement().multiply(f1, 0.8, f1)}) and the result of this method becomes the
     * new delta movement, so scaling it here is what actually changes the swimming speed.
     * A multiplier on the input acceleration inside {@code travelInWater} only touched half of the
     * maths and was barely felt.
     *
     * <p>The multiplier is capped twice: by {@link Skills#swimSpeedBonus} and by the drag, because a
     * factor above {@code 1 / drag} makes {@code drag * factor >= 1} and the swim runs away instead of
     * settling (checked numerically — an uncapped 1.25 reaches 4 blocks/tick within 200 ticks).
     */
    @Inject(method = "getFluidFallingAdjustedMovement", at = @At("RETURN"), cancellable = true)
    private void gokis$swimBonus(double gravity, boolean isFalling, Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        if (!(((Object) this) instanceof Player player) || !player.isInWater()) return;
        SkillInfo info = SkillHelper.getInfo(player);
        // sprinting keeps the water drag at 0.9, walking/swimming at 0.8
        double drag = player.isSprinting() ? 0.9 : 0.8;
        double bonus = Math.min(Skills.swimSpeedBonus(info.getBonusValue(Skills.SWIMMING)), 0.95 / drag - 1);
        if (GokiSkills.DEBUG_SWIM && player.tickCount % 40 == 0) {
            LOGGER.info("[gokis] swim: level={} raw={} bonus={} side={}",
                    info.getLevel(Skills.SWIMMING), info.getBonusValue(Skills.SWIMMING), bonus,
                    player.level().isClientSide() ? "client" : "server");
        }
        if (bonus > 0) {
            cir.setReturnValue(cir.getReturnValue().multiply(1 + bonus, 1 + bonus, 1 + bonus));
        }
    }
}
