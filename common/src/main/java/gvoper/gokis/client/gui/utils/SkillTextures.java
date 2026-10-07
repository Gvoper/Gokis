package gvoper.gokis.client.gui.utils;

import net.minecraft.resources.Identifier;

import static gvoper.gokis.GokiSkills.resource;

public class SkillTextures {
    /** The classic skill icon sheets: 10 rows of icons, four columns per state. */
    public static final Identifier RPG_ICONS = resource("textures/rpg_icons.png");
    public static final Identifier RPG_ICONS_2 = resource("textures/rpg_icons_2.png");

    private static final SkillTexture.Builder DEFAULT_FRAME_BUILDER = new SkillTexture.Builder()
            .setHoverImage(resource("textures/gui/frame/hover.png"))
            .setMaxLevelImage(resource("textures/gui/frame/max_level.png"))
            .setOperationImage(resource("textures/gui/frame/operation.png"))
            .setOperationHoverImage(resource("textures/gui/frame/operation_hover.png"))
            .setTextureSize(26);
    public static final SkillTexture DEFAULT_OVERLAY = new SkillTexture.Builder()
            .setDefaultImage(resource("textures/gui/overlay/default.png"))
            .setMaxLevelImage(resource("textures/gui/overlay/max_level.png"))
            .setOperationImage(resource("textures/gui/overlay/operation.png"))
            .setTextureSize(24)
            .build();

    /**
     * The star the original max level frame carries, on its own so the skills without a sheet can show it
     * even while a modifier swaps the frame. Same virtual size as the frames, so it lands where it used to.
     */
    public static final SkillTexture MAX_LEVEL_STAR = new SkillTexture.Builder()
            .setDefaultImage(resource("textures/gui/overlay/max_star.png"))
            .setTextureSize(26)
            .build();

    public static SkillTexture getFrame(FrameColor color) {
        return DEFAULT_FRAME_BUILDER
                .setDefaultImage(resource("textures/gui/frame/" + color.name().toLowerCase() + ".png")).build();
    }

    public enum FrameColor {
        BLACK,
        BLUE,
        BROWN,
        CYAN,
        GRAY,
        GREEN,
        LIGHT_BLUE,
        LIGHT_GRAY,
        LIME,
        MAGENTA,
        ORANGE,
        PINK,
        PURPLE,
        RED,
        WHITE,
        YELLOW,
        RAINBOW
    }
}
