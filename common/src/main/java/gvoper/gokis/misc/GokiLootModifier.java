package gvoper.gokis.misc;

import gvoper.gokis.GokiSkills;
import gvoper.gokis.skill.SkillHelper;
import gvoper.gokis.skill.SkillInfo;
import gvoper.gokis.skill.Skills;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Optional;

/**
 * The loot side of the mod: the fortune skill rolls an extra treasure table for the block that
 * was broken, the lucky miner doubles the drops of an ore.
 *
 * <p>The NeoForge original was an {@code IGlobalLootModifier}; here the same method is called from
 * {@code LootTableMixin} on both platforms.
 */
public class GokiLootModifier {

    private GokiLootModifier() {}

    public static void apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!context.hasParameter(LootContextParams.BLOCK_STATE) || !context.hasParameter(LootContextParams.THIS_ENTITY))
            return;
        if (!(context.getOptional(LootContextParams.THIS_ENTITY) instanceof ServerPlayer player)) return;

        BlockState state = context.getOptional(LootContextParams.BLOCK_STATE);
        SkillInfo info = SkillHelper.getInfo(player);

        // fortune: roll the treasure table of the block, if there is one
        if (info.isEnabled(Skills.FORTUNE) && info.getLevel(Skills.FORTUNE) > 0) {
            treasureTable(state.getBlock()).ifPresent(table -> context.getResolver()
                    .lookupOrThrow(Registries.LOOT_TABLE)
                    .get(table)
                    .ifPresent(holder -> holder.value().getRandomItemsRaw(
                            context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add))));
        }

        // lucky miner: one roll per block, the whole drop of the ore is doubled. The ore already went
        // through the tool, so a fortune pickaxe that rolled 3 now drops 6.
        double chance = info.getBonusValue(Skills.MINING_MAGICIAN);
        if (chance > 0 && state.is(GokiTags.MAGICIAN_ORE) && context.getRandom().nextDouble() < chance) {
            for (int i = 0; i < generatedLoot.size(); i++) {
                ItemStack drop = generatedLoot.get(i);
                generatedLoot.set(i, drop.copyWithCount(drop.getCount() * 2));
            }
        }
    }

    /** The key of the treasure table of a block: gokis:treasure_finder/&lt;block&gt;. */
    public static Optional<ResourceKey<LootTable>> treasureTable(Block block) {
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
        if (blockId == null || blockId.equals(BuiltInRegistries.BLOCK.getDefaultKey())) return Optional.empty();
        return Optional.of(ResourceKey.create(Registries.LOOT_TABLE,
                Identifier.fromNamespaceAndPath(GokiSkills.MOD_ID, "treasure_finder/" + blockId.getPath())));
    }
}
