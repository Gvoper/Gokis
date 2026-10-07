package gvoper.gokis.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.block.Block;

import static gvoper.gokis.GokiSkills.resource;

public class GokiTags {
    public static final TagKey<DamageType> CAN_DODGE = TagKey.create(Registries.DAMAGE_TYPE, resource("can_dodge"));
    public static final TagKey<DamageType> CAN_PROTECT = TagKey.create(Registries.DAMAGE_TYPE, resource("can_protect"));
    /** Potions, dragon breath, wither and poison — the damage the magic resistance skill covers. */
    public static final TagKey<DamageType> MAGIC_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, resource("magic_damage"));

    /** Blocks the lucky miner doubles the drops of. */
    public static final TagKey<Block> MAGICIAN_ORE = TagKey.create(Registries.BLOCK, resource("magician_ore"));
}
