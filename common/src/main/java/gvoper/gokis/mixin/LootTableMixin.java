package gvoper.gokis.mixin;

import gvoper.gokis.misc.GokiLootModifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * The loot hook of the mod. The NeoForge original was an {@code IGlobalLootModifier}; the vanilla
 * method that hands the rolled stacks to the caller is the point where both platforms meet, so the
 * modifier logic runs here instead.
 *
 * <p>Vanilla body: {@code getRandomItemsRaw(context, createStackSplitter(level, consumer))}.
 */
@Mixin(LootTable.class)
public abstract class LootTableMixin {
    @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"), cancellable = true)
    private void gokis$modifyLoot(LootContext context, Consumer<ItemStack> consumer, CallbackInfo ci) {
        if (!context.hasParameter(LootContextParams.BLOCK_STATE)) return;
        if (!(context.getOptional(LootContextParams.THIS_ENTITY) instanceof ServerPlayer)) return;

        ObjectArrayList<ItemStack> loot = new ObjectArrayList<>();
        ((LootTable) (Object) this).getRandomItemsRaw(context, loot::add);
        GokiLootModifier.apply(loot, context);

        Consumer<ItemStack> splitter = LootTable.createStackSplitter(context.getLevel(), consumer);
        for (ItemStack stack : loot) {
            splitter.accept(stack);
        }
        ci.cancel();
    }
}
