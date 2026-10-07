package gvoper.gokis.skill;

import gvoper.gokis.GokiSkills;
import gvoper.gokis.client.gui.utils.SkillTexture;
import gvoper.gokis.client.gui.utils.SkillTextures;
import gvoper.gokis.config.GokiSkillConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Skill implements ISkill {
    public static final Function<Integer, Double> DEFAULT_CALC_COST = (level) -> Math.pow(level, 1.6) + 6 + level;

    public static final BiFunction<Integer, Function<Integer, Double>, Double> DEFAULT_CALC_RETURN = (level, calcCost) -> calcCost.apply(level - 1);

    public static final Function<Integer, Double> DEFAULT_CALC_BONUS = (level) -> 0.04 * level;

    private Identifier location;
    private final Identifier category;
    private final Function<Integer, Double> calcCost;
    private final BiFunction<Integer, Function<Integer, Double>, Double> calcReturn;
    @Nullable
    private final Function<Integer, Double> calcBonus;
    private final SkillTexture icon;
    private final SkillTexture frame;
    private final SkillTexture overlay;
    private final SkillTexture background;
    /** Keeps the plain background in every state, no golden max level look at all. */
    private final boolean staticBackground;
    private final int imageID;
    private final Component name;
    private final SkillDescription description;
    private final Class<? extends GokiSkillConfig> configClass;
    private final GokiSkillConfig defaultConfig;

    public Skill(
            Identifier category,
            Function<Integer, Double> calcCost,
            BiFunction<Integer, Function<Integer, Double>, Double> calcReturn,
            @Nullable Function<Integer, Double> calcBonus,
            SkillTexture icon,
            SkillTexture frame,
            SkillTexture overlay,
            SkillTexture background,
            boolean staticBackground,
            int imageID,
            Component name,
            SkillDescription description,
            Class<? extends GokiSkillConfig> configClass,
            GokiSkillConfig defaultConfig
    ) {
        this.category = category;
        this.calcCost = calcCost;
        this.calcReturn = calcReturn;
        this.calcBonus = calcBonus;
        this.icon = icon;
        this.frame = frame;
        this.overlay = overlay;
        this.background = background;
        this.staticBackground = staticBackground;
        this.imageID = imageID;
        this.name = name;
        this.description = description;
        this.configClass = configClass;
        this.defaultConfig = defaultConfig;
    }

    /** Set once by the registry that owns this skill. */
    public void setLocation(Identifier location) {
        this.location = location;
    }

    @Override
    public Identifier getLocation() {
        return location;
    }

    @Override
    public boolean isEnabled() {
        return GokiSkills.getConfig() != null && getConfig().enabled;
    }

    @Override
    public Identifier getCategory() {
        return category;
    }

    @Override
    public int getMaxLevel() {
        return getConfig().maxLevel;
    }

    @Override
    public int getDefaultLevel() {
        return getConfig().defaultLevel;
    }

    @Override
    public int getMinLevel() {
        return getConfig().minLevel;
    }

    @Override
    public int calcCost(int level) {
        return Math.toIntExact(Math.round(calcCost.apply(level) * getConfig().costMultiplier));
    }

    @Override
    public int calcReturn(int level) {
        return Math.toIntExact(Math.round(calcReturn.apply(level, calcCost) * getConfig().downgradeReturnFactor));
    }

    @Nullable
    @Override
    public Double calcBonus(int level) {
        if (calcBonus == null) return null;
        return calcBonus.apply(level) * getConfig().bonusMultiplier;
    }

    public SkillTexture getIcon() {
        return icon;
    }

    /** Index of the icon in the rpg icon sheets, or -1 when the skill has its own icon texture. */
    public int getImageID() {
        return imageID;
    }

    public SkillTexture getFrame() {
        return frame;
    }

    public SkillTexture getOverlay() {
        return overlay;
    }

    public boolean hasStaticBackground() {
        return staticBackground;
    }

    public SkillTexture getBackground() {
        return background;
    }

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public Component getDescription(int level, @Nullable Double bonus) {
        return description.getDescription(level, bonus);
    }

    @Override
    public Class<? extends GokiSkillConfig> getConfigClass() {
        return configClass;
    }

    @Override
    public GokiSkillConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        return Objects.equals(getLocation(), ((Skill) obj).getLocation());
    }

    @Override
    public int hashCode() {
        return getLocation().hashCode();
    }

    public interface SkillDescription {
        Component getDescription(int level, @Nullable Double bonus);
    }

    public static class Builder {
        private Identifier category;
        private int maxLevel = 25;
        private int defaultLevel = 0;
        private int minLevel = 0;
        private Function<Integer, Double> calcCost = DEFAULT_CALC_COST;
        private BiFunction<Integer, Function<Integer, Double>, Double> calcReturn = DEFAULT_CALC_RETURN;
        @Nullable
        private Function<Integer, Double> calcBonus = DEFAULT_CALC_BONUS;
        private SkillTexture icon;
        private SkillTexture frame;
        private SkillTexture overlay = SkillTextures.DEFAULT_OVERLAY;
        private SkillTexture background;
        private boolean staticBackground = false;
        private int imageID = -1;
        private Component name;
        private SkillDescription description;
        private Class<? extends GokiSkillConfig> configClass = GokiSkillConfig.class;
        @Nullable
        private GokiSkillConfig defaultConfig;

        public Builder setCategory(Identifier category) {
            this.category = category;
            return this;
        }

        public Builder setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder setDefaultLevel(int defaultLevel) {
            this.defaultLevel = defaultLevel;
            return this;
        }

        public Builder setMinLevel(int minLevel) {
            this.minLevel = minLevel;
            return this;
        }

        public Builder setCalcCost(Function<Integer, Double> calcCost) {
            this.calcCost = calcCost;
            return this;
        }

        public Builder setCalcReturn(BiFunction<Integer, Function<Integer, Double>, Double> calcReturn) {
            this.calcReturn = calcReturn;
            return this;
        }

        public Builder setCalcBonus(@Nullable Function<Integer, Double> calcBonus) {
            this.calcBonus = calcBonus;
            return this;
        }

        public Builder setIcon(SkillTexture icon) {
            this.icon = icon;
            return this;
        }

        public Builder setFrame(SkillTexture frame) {
            this.frame = frame;
            return this;
        }

        public Builder setOverlay(SkillTexture overlay) {
            this.overlay = overlay;
            return this;
        }

        public Builder setBackground(SkillTexture background) {
            this.background = background;
        this.staticBackground = staticBackground;
            return this;
        }

        public Builder setStaticBackground(boolean staticBackground) {
            this.staticBackground = staticBackground;
            return this;
        }

        public Builder setImageID(int imageID) {
            this.imageID = imageID;
            return this;
        }

        public Builder setName(Component name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(SkillDescription description) {
            this.description = description;
            return this;
        }

        public Builder setConfigClass(Class<? extends GokiSkillConfig> configClass) {
            this.configClass = configClass;
            return this;
        }

        public Skill build() {
            if (category == null) throw new IllegalStateException("Category must be set");
            if (icon == null) throw new IllegalStateException("Icon must be set");
            if (frame == null) throw new IllegalStateException("Frame must be set");
            if (background == null) throw new IllegalStateException("Background must be set");
            if (name == null) throw new IllegalStateException("Name must be set");
            if (description == null) throw new IllegalStateException("Description must be set");
            if (configClass == null) throw new IllegalStateException("Config class must be set");
            GokiSkillConfig defaultConfig = this.defaultConfig;
            if (defaultConfig == null) {
                try {
                    defaultConfig = configClass.getConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException("Default config must be set", e);
                }
            }
            defaultConfig.defaultLevel = defaultLevel;
            defaultConfig.maxLevel = maxLevel;
            defaultConfig.minLevel = minLevel;
            return new Skill(
                    category,
                    calcCost,
                    calcReturn,
                    calcBonus,
                    icon,
                    frame,
                    overlay,
                    background,
                    staticBackground,
                    imageID,
                    name,
                    description,
                    configClass,
                    defaultConfig
            );
        }
    }
}
