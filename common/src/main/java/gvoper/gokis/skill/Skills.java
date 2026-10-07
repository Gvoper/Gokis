package gvoper.gokis.skill;

import gvoper.gokis.GokiSkills;
import gvoper.gokis.client.gui.utils.SkillTexture;
import gvoper.gokis.client.gui.utils.SkillTextures;
import gvoper.gokis.misc.GokiUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static gvoper.gokis.GokiSkills.resource;

public class Skills {
    private static final Identifier abilityCategory = resource("1_ability");
    private static final Identifier breakingCategory = resource("3_breaking");
    private static final Identifier professionCategory = resource("2_profession");
    private static final Identifier protectionCategory = resource("4_protection");

    public static final Skill CLIMBING = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.ORANGE))
            .setImageID(11)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/ladder.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/acacia_planks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.climbing.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.climbing.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final Skill FORTUNE = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RAINBOW))
            .setImageID(16)
            .setMaxLevel(3)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/goki.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/stone.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.fortune.name"))
            .setDescription((level, bonus) -> Component.translatable("skill.gokis.fortune.desc"))
            .setCalcCost(level -> 25 * Math.pow(level, 2) + 25 * level + 100)
            .build();

    public static final Skill HEALTH = new Skill.Builder()
            .setCategory(abilityCategory)
            .setMaxLevel(40)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RED))
            .setImageID(21)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/instant_health.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/crimson_planks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.health.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.health.desc",
                            GokiUtils.doubleToString(bonus, 0)
                    )
            )
            .setCalcCost(level -> Math.pow(level, 2) + 48 + level)
            .setCalcBonus(Double::valueOf)
            .build();

    public static final Skill LEAPER = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.WHITE))
            .setImageID(8)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/leaper.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/birch_planks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.leaper.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.leaper.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final Skill SWIMMING = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.LIGHT_BLUE))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/item/tropical_fish.png"))
                            .setDisabledImage(resource("textures/gui/icon/swimming_disabled.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/background/water.png"))
                            .setDisabledImage(resource("textures/gui/background/water_disabled.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.swimming.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.swimming.desc",
                            GokiUtils.doubleToString(swimSpeedShown(bonus), 2),
                            GokiUtils.doubleToString(bonus * 25, 2)
                    )
            )
            .build();

    public static final Skill JUMP_BOOST = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.WHITE))
            .setImageID(9)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/mob_effect/jump_boost.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/birch_planks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.jump_boost.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.jump_boost.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final Skill CHOPPING = new Skill.Builder()
            .setCategory(breakingCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.BROWN))
            .setImageID(2)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/item/diamond_axe.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/oak_log.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.chopping.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.chopping.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();
    public static final Skill DIGGING = new Skill.Builder()
            .setCategory(breakingCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.LIGHT_GRAY))
            .setImageID(1)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/item/diamond_shovel.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/gravel.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.digging.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.digging.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();
    public static final Skill HARVESTING = new Skill.Builder()
            .setCategory(breakingCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.YELLOW))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/item/diamond_hoe.png"))
                            .setDisabledImage(resource("textures/gui/icon/harvesting_disabled.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/hay_block_side.png"))
                            .setDisabledImage(resource("textures/gui/background/hay_block_side_disabled.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.harvesting.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.harvesting.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();
    public static final Skill MINING = new Skill.Builder()
            .setCategory(breakingCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.GRAY))
            .setImageID(0)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/item/diamond_pickaxe.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/stone.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.mining.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.mining.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();
    public static final Skill SHEARING = new Skill.Builder()
            .setCategory(breakingCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.YELLOW))
            .setImageID(3)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/item/shears.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/yellow_wool.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.shearing.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.shearing.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final Skill ALCHEMY = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.YELLOW))
            .setImageID(17)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/item/raw_iron.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/gold_block.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.alchemy.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.alchemy.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final Skill ARCHER = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RED))
            .setImageID(14)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/item/bow_pulling_0.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/background/archer.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.archer.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.archer.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final Skill BOXING = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RED))
            .setImageID(12)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/boxing.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/bricks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.boxing.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.boxing.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final Skill FENCING = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.PINK))
            .setImageID(13)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/item/iron_sword.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/cherry_planks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.fencing.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.fencing.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final Skill NINJA = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.BLACK))
            .setImageID(19)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/ninja.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/chiseled_polished_blackstone.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.ninja.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.ninja.desc",
                            GokiUtils.doubleToString(bonus * 100, 2),
                            GokiUtils.doubleToString(bonus * 25, 2)
                    )
            )
            .build();

    public static final Skill ONE_HIT = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RED))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/sickle.png"))
                            .setDisabledImage(resource("textures/gui/icon/one_hit_disabled.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/red_terracotta.png"))
                            .setDisabledImage(resource("textures/gui/background/red_terracotta_disabled.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.one_hit.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.one_hit.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2),
                            GokiUtils.doubleToString(Math.min(bonus * 40, 100), 2)
                    )
            )
            .setCalcBonus((level) -> 0.01 * level)
            .build();

    public static final Skill REAPER = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RED))
            .setImageID(15)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/sickle.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/soul_sand.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.reaper.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.reaper.desc",
                            GokiUtils.doubleToString(bonus * 100, 2),
                            reaperMaxHealth()
                    )
            )
            .setCalcBonus((level) -> Math.min(Math.pow(Math.max(level, 0), 1.0768D) * 0.0025D, 1.0D))
            .build();

    public static final Skill MINING_MAGICIAN = new Skill.Builder()
            .setCategory(breakingCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.LIGHT_BLUE))
            .setImageID(20)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/item/diamond.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/diamond_block.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.mining_magician.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.mining_magician.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .setCalcBonus((level) -> Math.min(0.02D * level, 0.5D))
            .setCalcCost((level) -> 15.0 + 2.5 * level * (level + 1))
            .build();

    public static final Skill BLAST_PROTECTION = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RED))
            .setImageID(6)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/creeper.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/tnt_side.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.blast_protection.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.blast_protection.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2)
                    )
            )
            .setCalcBonus(level -> 0.026 * level)
            .build();

    public static final Skill DODGE = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.WHITE))
            .setImageID(22)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/wind_charged.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/pale_oak_planks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.dodge.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.dodge.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2)
                    )
            )
            .setCalcBonus(level -> 0.006 * level)
            .build();

    public static final Skill ENDOTHERMY = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.LIGHT_BLUE))
            .setImageID(5)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/mob_effect/fire_resistance.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/ice.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.endothermy.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.endothermy.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2)
                    )
            )
            .setCalcBonus(level -> 0.026 * level)
            .build();

    public static final Skill FEATHER_FALLING = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.WHITE))
            .setImageID(7)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/item/feather.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/sand.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.feather_falling.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.feather_falling.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2)
                    )
            )
            .setCalcBonus(level -> 0.026 * level)
            .build();

    public static final Skill KNOCKBACK_RESISTANCE = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.WHITE))
            .setImageID(18)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/mob_effect/absorption.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/iron_block.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.knockback_resistence.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.knockback_resistence.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2)
                    )
            )
            .setCalcBonus(level -> 0.013 * level)
            .build();

    public static final Skill PROTECTION = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.GRAY))
            .setImageID(4)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/mob_effect/resistance.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/netherite_block.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.protection.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.protection.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2)
                    )
            )
            .setCalcBonus(level -> 0.008 * level)
            .build();

    /** The health limit below which the reaper can instantly kill a mob. */
    public static int reaperMaxHealth() {
        return GokiSkills.getConfig() == null ? 20 : GokiSkills.getConfig().reaperMaxHealth;
    }

    /** Above this the water drag no longer holds the player back and the swim turns into a runaway. */
    // ------------------------------------------------------------ professions

    /** Smelts faster: the furnace speeds up while a player with this skill is nearby. */
    public static final Skill FURNACE_FINESSE = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.ORANGE))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/furnace_front_on.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/furnace_top.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.furnace_finesse.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.furnace_finesse.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    /** More experience from everything that grants experience. */
    public static final Skill EXPERIENCE_BOOST = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.LIME))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/item/experience_bottle.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(Identifier.withDefaultNamespace("textures/block/bookshelf.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokis.experience_boost.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokis.experience_boost.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    // ------------------------------------------------------------ new in this port

    /** Less magic damage: potions, dragon breath, wither and poison. */
    public static final Skill MAGIC_RESISTANCE = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.PURPLE))
            .setIcon(texture("textures/item/dragon_breath.png"))
            .setBackground(texture("textures/block/purpur_block.png"))
            .setName(Component.translatable("skill.gokis.magic_resistance.name"))
            .setDescription((level, bonus) -> Component.translatable(
                    "skill.gokis.magic_resistance.desc", GokiUtils.doubleToString(bonus * 50, 2)))
            .build();

    /** Faster on foot, up to double speed. */
    public static final Skill MOVEMENT_SPEED = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.WHITE))
            .setIcon(texture("textures/item/iron_boots.png"))
            .setBackground(texture("textures/block/stone_bricks.png"))
            .setName(Component.translatable("skill.gokis.movement_speed.name"))
            .setDescription((level, bonus) -> Component.translatable(
                    "skill.gokis.movement_speed.desc", GokiUtils.doubleToString(bonus * 100, 2)))
            .build();

    /** Reflects a part of the damage taken back to the attacker. */
    public static final Skill THORNS = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RED))
            .setIcon(texture("textures/item/pufferfish.png"))
            .setBackground(texture("textures/block/prismarine_bricks.png"))
            .setMaxLevel(30)
            .setCalcBonus(level -> level * 0.01)
            .setName(Component.translatable("skill.gokis.thorns.name"))
            .setDescription((level, bonus) -> Component.translatable(
                    "skill.gokis.thorns.desc", GokiUtils.doubleToString(bonus * 100, 2)))
            .build();

    /** Hunger builds up slower. */
    public static final Skill SLOW_HUNGER = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.BROWN))
            .setIcon(texture("textures/item/bread.png"))
            .setBackground(texture("textures/block/hay_block_side.png"))
            .setName(Component.translatable("skill.gokis.slow_hunger.name"))
            .setDescription((level, bonus) -> Component.translatable(
                    "skill.gokis.slow_hunger.desc", GokiUtils.doubleToString(bonus * 50, 2)))
            .build();

    /** Shortcut for a vanilla picture used as an icon or a background of a skill. */
    static SkillTexture texture(String path) {
        return new SkillTexture.Builder()
                .setDefaultImage(Identifier.withDefaultNamespace(path))
                .setTextureSize(16)
                .build();
    }

    private static final double SWIM_SPEED_CAP = 0.45;
    /** The percentage the capped swim speed is shown as in the tooltip. */
    private static final double SWIM_SPEED_CAP_SHOWN = 50.0;

    /** The swim speed bonus (fraction) a raw skill bonus grants, capped at {@link #SWIM_SPEED_CAP}. */
    public static double swimSpeedBonus(double rawBonus) {
        return Math.min(rawBonus * 0.5, SWIM_SPEED_CAP);
    }

    /** The swim speed percentage of the tooltip: the cap is displayed as {@link #SWIM_SPEED_CAP_SHOWN}. */
    public static double swimSpeedShown(double rawBonus) {
        return swimSpeedBonus(rawBonus) / SWIM_SPEED_CAP * SWIM_SPEED_CAP_SHOWN;
    }

    public static void bootstrap() {
        // ability
        register("climbing", CLIMBING);
        register("swimming", SWIMMING);
        register("health", HEALTH);
        register("leaper", LEAPER);
        register("jump_boost", JUMP_BOOST);

        // fortune and magic
        register("fortune", FORTUNE);
        register("mining_magician", MINING_MAGICIAN);

        // breaking
        register("chopping", CHOPPING);
        register("digging", DIGGING);
        register("harvesting", HARVESTING);
        register("mining", MINING);
        register("shearing", SHEARING);

        // profession
        register("archer", ARCHER);
        register("boxing", BOXING);
        register("fencing", FENCING);
        register("ninja", NINJA);
        register("one_hit", ONE_HIT);
        register("reaper", REAPER);

        // protection
        register("blast_protection", BLAST_PROTECTION);
        register("dodge", DODGE);
        register("endothermy", ENDOTHERMY);
        register("feather_falling", FEATHER_FALLING);
        register("knockback_resistence", KNOCKBACK_RESISTANCE);
        register("protection", PROTECTION);

        // new in this port
        register("furnace_finesse", FURNACE_FINESSE);
        register("experience_boost", EXPERIENCE_BOOST);
        register("magic_resistance", MAGIC_RESISTANCE);
        register("movement_speed", MOVEMENT_SPEED);
        register("thorns", THORNS);
        register("slow_hunger", SLOW_HUNGER);
    }

    private static void register(String path, Skill skill) {
        SkillRegistry.register(path, skill);
    }
}
