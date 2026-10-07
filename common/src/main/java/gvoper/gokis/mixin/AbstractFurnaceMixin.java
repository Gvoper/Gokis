package gvoper.gokis.mixin;

import gvoper.gokis.skill.SkillHelper;
import gvoper.gokis.skill.SkillInfo;
import gvoper.gokis.skill.Skills;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Furnace finesse: the furnace smelts faster while a player with the skill is around it.
 *
 * <p>The extra progress is added on top of the vanilla tick, so nothing in the vanilla cook loop is
 * replaced — the furnace just advances further per tick.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceMixin {

    /** How close a player has to stand for the furnace to speed up. */
    private static final double GOKIS_RANGE = 8.0;

    @Accessor("cookingTimer")
    protected abstract int gokis$cookingTimer();

    @Accessor("cookingTimer")
    protected abstract void gokis$setCookingTimer(int value);

    @Inject(method = "serverTick", at = @At("TAIL"))
    private static void gokis$furnaceFinesse(ServerLevel level, BlockPos pos, BlockState state,
                                             AbstractFurnaceBlockEntity entity, CallbackInfo ci) {
        AbstractFurnaceMixin self = (AbstractFurnaceMixin) (Object) entity;
        if (self.gokis$cookingTimer() <= 0) return; // nothing is being cooked right now
        double bonus = gokis$nearestBonus(level, pos);
        if (bonus <= 0) return;
        self.gokis$setCookingTimer(self.gokis$cookingTimer() + (int) Math.round(bonus));
    }

    private static double gokis$nearestBonus(ServerLevel level, BlockPos pos) {
        double best = 0;
        double bestDistance = GOKIS_RANGE * GOKIS_RANGE;
        for (ServerPlayer player : level.players()) {
            double distance = player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (distance > bestDistance) continue;
            SkillInfo info = SkillHelper.getInfo(player);
            if (!info.isEnabled(Skills.FURNACE_FINESSE)) continue;
            double bonus = info.getBonusValue(Skills.FURNACE_FINESSE);
            if (bonus > best) {
                best = bonus;
                bestDistance = distance;
            }
        }
        return best;
    }
}
