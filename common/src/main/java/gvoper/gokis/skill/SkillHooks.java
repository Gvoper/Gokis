package gvoper.gokis.skill;

import dev.architectury.event.EventResult;
import dev.architectury.utils.value.DoubleValue;
import dev.architectury.utils.value.FloatValue;
import gvoper.gokis.GokiSkills;
import gvoper.gokis.misc.GokiData;
import gvoper.gokis.misc.GokiTags;
import gvoper.gokis.network.GokiNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.block.state.BlockState;

/** Where the skills meet the game: break speed, damage, movement and attributes. */
public final class SkillHooks {
    public static final Identifier HEALTH_SKILL_MODIFIER = GokiSkills.resource("health_skill_modifier");
    public static final Identifier KNOCKBACK_RESISTANCE_SKILL_MODIFIER = GokiSkills.resource("knockback_resistance_skill_modifier");
    public static final Identifier NINJA_SKILL_MODIFIER = GokiSkills.resource("ninja_skill_modifier");
    public static final Identifier SPEED_SKILL_MODIFIER = GokiSkills.resource("speed_skill_modifier");

    /** Set while the skill menu pays experience in or out, so the xp boost does not apply. */
    public static boolean bypassExperienceBoost = false;

    private SkillHooks() {}

    // ---------------------------------------------------------------- breaking

    public static EventResult onBreakSpeed(Player player, BlockState state, net.minecraft.core.BlockPos pos, FloatValue speed) {
        SkillInfo info = SkillHelper.getInfo(player);
        ItemStack item = player.getMainHandItem();
        double bonus = 1.0;
        if (info.isEnabled(Skills.CHOPPING) && item.is(ItemTags.AXES) && state.is(BlockTags.MINEABLE_WITH_AXE)) {
            bonus += info.getBonusValue(Skills.CHOPPING);
        } else if (info.isEnabled(Skills.DIGGING) && item.is(ItemTags.SHOVELS) && state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            bonus += info.getBonusValue(Skills.DIGGING);
        } else if (info.isEnabled(Skills.HARVESTING) && item.is(ItemTags.HOES) && state.is(BlockTags.MINEABLE_WITH_HOE)) {
            bonus += info.getBonusValue(Skills.HARVESTING);
        } else if (info.isEnabled(Skills.MINING) && item.is(ItemTags.PICKAXES) && state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            bonus += info.getBonusValue(Skills.MINING);
        } else if (info.isEnabled(Skills.SHEARING) && item.is(Items.SHEARS) && item.getDestroySpeed(state) != 1) {
            bonus += info.getBonusValue(Skills.SHEARING);
        }
        if (bonus != 1.0) speed.accept((float) (speed.get() * bonus));
        return EventResult.pass();
    }

    // ----------------------------------------------------------------- damage

    /**
     * The damage side of the skills. The NeoForge original used {@code LivingIncomingDamageEvent},
     * which can rewrite the amount; the Architectury equivalent only interrupts, so the amount is
     * rewritten in {@code LivingEntityDamageMixin} instead and this method is called from there.
     *
     * @return true when the hit should be cancelled, the new amount is written back into {@code amount}
     */
    public static boolean onIncomingDamage(LivingEntity entity, DamageSource source, float[] amount) {
        // profession: bonus damage dealt by a player
        if (source.getEntity() instanceof ServerPlayer player) {
            SkillInfo info = SkillHelper.getInfo(player);
            ItemStack item = player.getMainHandItem();

            if (info.isEnabled(Skills.ONE_HIT)) {
                double bonus = info.getBonusValue(Skills.ONE_HIT);
                if (entity.getHealth() < entity.getMaxHealth() * 0.4 * bonus && Math.random() < bonus) {
                    entity.setHealth(0.0F);
                    entity.die(source);
                    actionBar(player, Component.translatable("skill.gokis.one_hit.message")
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                    player.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 1.0F, 1.0F);
                    return true;
                }
            }
            if (info.isEnabled(Skills.REAPER) && entity.getMaxHealth() <= Skills.reaperMaxHealth()) {
                double chance = info.getBonusValue(Skills.REAPER);
                if (player.isCrouching()) chance *= 1 + info.getBonusValue(Skills.NINJA);
                if (Math.random() < chance) {
                    entity.setHealth(0.0F);
                    entity.die(source);
                    actionBar(player, Component.translatable("skill.gokis.reaper.message")
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                    player.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 1.0F, 1.0F);
                    return true;
                }
            }
            if (info.isEnabled(Skills.NINJA) && player.isCrouching()) {
                amount[0] *= (float) (1 + info.getBonusValue(Skills.NINJA) * 0.25);
            }
            if (info.isEnabled(Skills.ARCHER) && item.getItem() instanceof ProjectileWeaponItem) {
                amount[0] *= (float) (1 + info.getBonusValue(Skills.ARCHER));
            } else if (info.isEnabled(Skills.BOXING) && item.isEmpty()) {
                amount[0] *= (float) (1 + info.getBonusValue(Skills.BOXING));
            } else if (info.isEnabled(Skills.FENCING) && item.is(ItemTags.SWORDS)) {
                amount[0] *= (float) (1 + info.getBonusValue(Skills.FENCING));
            }
        }

        // protection: less damage taken by a player
        if (entity instanceof ServerPlayer player && !player.isCreative()) {
            SkillInfo info = SkillHelper.getInfo(player);
            if (info.isEnabled(Skills.DODGE) && source.is(GokiTags.CAN_DODGE)) {
                if (Math.random() < info.getBonusValue(Skills.DODGE)) {
                    actionBar(player, Component.translatable("skill.gokis.dodge.message")
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
                    player.playSound(SoundEvents.PLAYER_ATTACK_NODAMAGE, 1.0F, 1.0F);
                    player.setInvulnerableTime(20);
                    return true;
                }
            }
            if (info.isEnabled(Skills.THORNS) && source.getEntity() instanceof LivingEntity attacker
                    && attacker != player && !attacker.isDeadOrDying()) {
                double reflect = info.getBonusValue(Skills.THORNS);
                if (reflect > 0 && player.level() instanceof ServerLevel serverLevel) {
                    // guaranteed, no roll: a share of what was taken goes straight back
                    attacker.hurtServer(serverLevel, source, (float) (amount[0] * reflect));
                }
            }
            if (info.isEnabled(Skills.MAGIC_RESISTANCE) && source.is(GokiTags.MAGIC_DAMAGE)) {
                amount[0] = (float) (amount[0] * (1 - Math.min(info.getBonusValue(Skills.MAGIC_RESISTANCE), 1.0) * 0.5));
            } else if (info.isEnabled(Skills.BLAST_PROTECTION) && source.is(DamageTypeTags.IS_EXPLOSION)) {
                amount[0] = (float) (amount[0] * (1 - info.getBonusValue(Skills.BLAST_PROTECTION)));
            } else if (info.isEnabled(Skills.ENDOTHERMY) && (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_FREEZING))) {
                amount[0] = (float) (amount[0] * (1 - info.getBonusValue(Skills.ENDOTHERMY)));
            } else if (info.isEnabled(Skills.FEATHER_FALLING) && source.is(DamageTypeTags.IS_FALL)) {
                amount[0] = (float) (amount[0] * (1 - info.getBonusValue(Skills.FEATHER_FALLING)));
            } else if (info.isEnabled(Skills.PROTECTION) && source.is(GokiTags.CAN_PROTECT)) {
                amount[0] = (float) (amount[0] * (1 - info.getBonusValue(Skills.PROTECTION)));
            }
        }
        return false;
    }

    /**
     * Hunger of a player builds up slower. Covers both paths: what the world pushes in and what the
     * body spends on regeneration — the hidden saturation bar and the visible food bar alike.
     */
    public static double slowHunger(Player player, double exhaustion) {
        if (exhaustion <= 0) return exhaustion;
        SkillInfo info = SkillHelper.getInfo(player);
        if (!info.isEnabled(Skills.SLOW_HUNGER)) return exhaustion;
        double bonus = Math.min(info.getBonusValue(Skills.SLOW_HUNGER), 1.0);
        return bonus > 0 ? exhaustion * (1 - bonus * 0.5) : exhaustion;
    }

    /** The air a player consumes while swimming, called from {@code LivingEntityAirMixin}. */
    /**
     * Air a player spends while swimming. The vanilla drain is one point per call, so scaling it and
     * rounding back to an int would eat the whole bonus — the saving is rolled instead: with a chance
     * equal to the bonus the point is not spent at all.
     */
    public static int consumedAir(Player player, int consumed) {
        SkillInfo info = SkillHelper.getInfo(player);
        if (!info.isEnabled(Skills.SWIMMING)) return consumed;
        double bonus = Math.min(info.getBonusValue(Skills.SWIMMING) * 0.25, 0.5);
        if (bonus <= 0) return consumed;
        if (player.getRandom().nextDouble() < bonus) return 0;
        return consumed;
    }

    public static EventResult onFall(LivingEntity entity, DoubleValue distance, FloatValue multiplier) {
        if (entity instanceof Player player) {
            SkillInfo info = SkillHelper.getInfo(player);
            if (info.isEnabled(Skills.JUMP_BOOST)) {
                distance.accept(distance.get() - 3.5 * info.getBonusValue(Skills.JUMP_BOOST));
            }
        }
        return EventResult.pass();
    }

    public static EventResult onDeath(LivingEntity entity, DamageSource source) {
        if (entity instanceof ServerPlayer player) {
            SkillInfo info = SkillHelper.getInfoOrNull(player);
            if (info != null && info.onDeath()) {
                GokiData.markDirty(player);
                sync(player, info);
            }
        }
        return EventResult.pass();
    }

    // -------------------------------------------------------------- abilities

    /** Called from {@code LivingEntityJumpMixin} — vanilla has no jump event on Architectury. */
    public static void onJump(Player player) {
        SkillInfo info = SkillHelper.getInfo(player);
        double jumpBoostBonus = info.isEnabled(Skills.JUMP_BOOST) ? info.getBonusValue(Skills.JUMP_BOOST) : 0.0;
        double leaperBonus = info.isEnabled(Skills.LEAPER) ? info.getBonusValue(Skills.LEAPER) : 0.0;
        if (jumpBoostBonus != 0.0 || leaperBonus != 0.0) {
            player.setDeltaMovement(player.getDeltaMovement()
                    .multiply(leaperBonus + 1, jumpBoostBonus + 1, leaperBonus + 1));
        }
    }

    /** The ninja speed bonus only applies while sneaking, so it is refreshed every tick. */
    public static void onPlayerTick(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            updateAttribute(serverPlayer, SkillHelper.getInfo(serverPlayer), Skills.NINJA);
        }
    }

    // ---------------------------------------------------------- player states

    public static void onPlayerLogin(ServerPlayer player) {
        SkillInfo info = SkillHelper.getInfo(player);
        updateAttribute(player, info, Skills.KNOCKBACK_RESISTANCE);
        float maxHealthBefore = player.getMaxHealth();
        updateAttribute(player, info, Skills.HEALTH);
        // health is capped at the old maximum while the player is loaded, give it back
        if (player.getMaxHealth() > maxHealthBefore && player.getHealth() >= maxHealthBefore) {
            player.setHealth(player.getMaxHealth());
        }
        updateAttribute(player, info, Skills.NINJA);
        updateAttribute(player, info, Skills.MOVEMENT_SPEED);
        sync(player, info);
        GokiNetwork.sendConfigSync(player);
    }

    public static void onPlayerRespawn(ServerPlayer player) {
        SkillInfo info = SkillHelper.getInfo(player);
        updateAttribute(player, info, Skills.KNOCKBACK_RESISTANCE);
        updateAttribute(player, info, Skills.HEALTH);
        updateAttribute(player, info, Skills.NINJA);
        updateAttribute(player, info, Skills.MOVEMENT_SPEED);
        player.setHealth(player.getMaxHealth());
        sync(player, info);
    }

    // ------------------------------------------------------------- attributes

    /** Applies the attribute modifier of a skill, or removes it when the skill is off. */
    public static void updateAttribute(ServerPlayer player, SkillInfo info, Skill skill) {
        if (skill == Skills.KNOCKBACK_RESISTANCE) {
            applyModifier(player, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_SKILL_MODIFIER,
                    AttributeModifier.Operation.ADD_VALUE, info.getBonusValue(Skills.KNOCKBACK_RESISTANCE));
        } else if (skill == Skills.HEALTH) {
            applyModifier(player, Attributes.MAX_HEALTH, HEALTH_SKILL_MODIFIER,
                    AttributeModifier.Operation.ADD_VALUE, info.getBonusValue(Skills.HEALTH));
        } else if (skill == Skills.NINJA) {
            applyModifier(player, Attributes.MOVEMENT_SPEED, NINJA_SKILL_MODIFIER,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                    player.isShiftKeyDown() ? info.getBonusValue(Skills.NINJA) : 0.0);
        } else if (skill == Skills.MOVEMENT_SPEED) {
            applyModifier(player, Attributes.MOVEMENT_SPEED, SPEED_SKILL_MODIFIER,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                    info.getBonusValue(Skills.MOVEMENT_SPEED));
        }
    }

    /** Adds, updates or removes the modifier of one attribute, keyed by our own id. */
    private static void applyModifier(ServerPlayer player, Holder<Attribute> attribute, Identifier location,
                                      AttributeModifier.Operation operation, double value) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        AttributeModifier oldModifier = instance.getModifier(location);
        if (value > 0) {
            if (oldModifier == null || oldModifier.amount() != value) {
                instance.removeModifier(location);
                instance.addTransientModifier(new AttributeModifier(location, value, operation));
            }
        } else if (oldModifier != null) {
            instance.removeModifier(location);
        }
    }

    /** Sends the skill data of a player back to that player. */
    public static void sync(ServerPlayer player, SkillInfo info) {
        GokiNetwork.sendSkillInfoSync(player, info);
    }

    private static void actionBar(ServerPlayer player, Component message) {
        player.connection.send(new ClientboundSetActionBarTextPacket(message));
    }
}
