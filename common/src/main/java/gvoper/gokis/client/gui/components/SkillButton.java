package gvoper.gokis.client.gui.components;

import gvoper.gokis.client.gui.screens.SkillsMenuScreen;
import gvoper.gokis.client.gui.utils.SkillTexture;
import gvoper.gokis.client.gui.utils.SkillTextures;
import gvoper.gokis.network.GokiNetworkClient;
import gvoper.gokis.skill.Skill;
import gvoper.gokis.skill.SkillHelper;
import gvoper.gokis.skill.SkillInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

/** One skill in the menu: shows its icon, level and what a click would cost. */
public class SkillButton extends Button {
    public static final int DEFAULT_WIDTH = 24;
    public static final int DEFAULT_HEIGHT = 24;
    public static final int DEFAULT_ICON_PADDING = 4;

    /** The icon sheets hold 48x48 pictures in a 512x512 texture. */
    private static final int SHEET_SIZE = 512;
    private static final int ICON_CELL = 48;
    /** Columns of a row: plain, hovered / usable, disabled, max level. */
    private static final int INACTIVE = 0;
    private static final int ACTIVATED = 1;
    private static final int DISABLED = 2;
    private static final int MAXIMUM = 3;

    private static final Component LOADING = Component.translatable("gui.gokis.loading.skill");
    private static final Component DISABLED_LABEL = Component.translatable("gui.gokis.disabled");
    private static final Component NO_DOWNGRADE = Component.translatable("gui.gokis.downgrade.no")
            .withStyle(Style.EMPTY.withColor(ChatFormatting.RED));
    private static final Component DOWNGRADE = Component.translatable("gui.gokis.downgrade")
            .withStyle(Style.EMPTY.withColor(-13658630));
    private static final Component NO_UPGRADE = Component.translatable("gui.gokis.upgrade.no")
            .withStyle(Style.EMPTY.withColor(ChatFormatting.RED));
    private static final Component UPGRADE = Component.translatable("gui.gokis.upgrade")
            .withStyle(Style.EMPTY.withColor(-11535825));

    /** Set by the menu every frame, buttons only render. */
    public static boolean hasControlDown = false;
    public static boolean hasShiftDown = false;
    public static boolean hasAltDown = false;

    private final Skill skill;
    private boolean waitForUpdate = false;
    public int level = 0;
    public boolean enabled = true;

    public SkillButton(int x, int y, Skill skill) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, CommonComponents.EMPTY, b -> {}, DEFAULT_NARRATION);
        this.skill = skill;
        updateLevel();
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (hasAltDown) {
            waitForUpdate = true;
            GokiNetworkClient.sendSkillToggle(skill.getLocation());
            return;
        }
        int[] result = SkillHelper.calcOperation(skill, level, SkillsMenuScreen.playerXp, !hasControlDown, hasShiftDown);
        if (waitForUpdate || result[0] == 0) return;
        if (hasControlDown && hasShiftDown) {
            if (level > skill.getMinLevel()) {
                waitForUpdate = true;
                GokiNetworkClient.sendSkillFastDowngrade(skill.getLocation());
            }
        } else if (hasControlDown) {
            if (level > skill.getMinLevel()) {
                waitForUpdate = true;
                GokiNetworkClient.sendSkillDowngrade(skill.getLocation());
            }
        } else if (hasShiftDown) {
            if (level < skill.getMaxLevel()) {
                waitForUpdate = true;
                GokiNetworkClient.sendSkillFastUpgrade(skill.getLocation());
            }
        } else {
            if (level < skill.getMaxLevel()) {
                waitForUpdate = true;
                GokiNetworkClient.sendSkillUpgrade(skill.getLocation());
            }
        }
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = isHovered();
        boolean maxLevel = level >= skill.getMaxLevel();
        boolean operation = hasControlDown || hasShiftDown || hasAltDown;
        int state = iconState(hovered, maxLevel);
        boolean sheetIcon = skill.getImageID() >= 0;
        // a skill without a sheet has no grey column, so that state is drawn by hand
        boolean disabled = !sheetIcon && state == DISABLED;
        // the sheets hide the glow behind their cell, so the glow goes off while they are greyed out
        boolean glow = operation && !disabled;

        // the background never follows a held modifier: the golden max level look only shows while no
        // modifier is held, and a held modifier never turns it blue — only the frame changes
        // only Ctrl (downgrade) drops the golden background; Shift keeps it, so a maxed out skill
        // still reads as maxed out while fast upgrading
        boolean backgroundMaxLevel = maxLevel && !hasControlDown && !skill.hasStaticBackground();
        blitState(graphics, skill.getBackground(), getX(), getY(), width, height, hovered, backgroundMaxLevel, false, disabled);
        blitState(graphics, skill.getOverlay(), getX(), getY(), width, height, hovered, backgroundMaxLevel, false, disabled);
        if (sheetIcon) {
            blitSheetIcon(graphics, state);
        } else {
            blitState(graphics, skill.getIcon(),
                    getX() + DEFAULT_ICON_PADDING, getY() + DEFAULT_ICON_PADDING,
                    width - DEFAULT_ICON_PADDING * 2, height - DEFAULT_ICON_PADDING * 2,
                    hovered, maxLevel, false, disabled);
            // the star of the original max level frame, on its own so a held modifier cannot take it away
            if (!sheetIcon && state == MAXIMUM) {
                blitState(graphics, SkillTextures.MAX_LEVEL_STAR,
                        getX() - 1, getY() - 1, width + 2, height + 2, false, false, false, false);
            }
        }
        blit(graphics, skill.getFrame(), getX() - 1, getY() - 1, width + 2, height + 2, hovered, maxLevel, operation);

        Component label = waitForUpdate ? LOADING
                : (!enabled ? DISABLED_LABEL
                : (maxLevel ? Component.literal("*" + level + "*") : Component.literal(String.valueOf(level))));
        graphics.centeredText(
                Minecraft.getInstance().font,
                label,
                getX() + width / 2,
                getY() + height + 3,
                !waitForUpdate && maxLevel ? 16763904 : 16777215
        );

        renderTooltip(graphics, mouseX, mouseY, maxLevel);
    }

    /** Which of the four pictures of the icon to show, like the original skill sheets do. */
    private int iconState(boolean hovered, boolean maxLevel) {
        int state = INACTIVE;
        if (hovered) state = ACTIVATED;
        int cost = skill.calcCost(level);
        if (!hasControlDown && SkillsMenuScreen.playerXp < cost) state = INACTIVE;
        if (maxLevel) state = MAXIMUM;
        if (hasControlDown) state = level > skill.getMinLevel() ? ACTIVATED : DISABLED;
        if (!enabled) state = DISABLED;
        return state;
    }

    private void blitSheetIcon(GuiGraphicsExtractor graphics, int state) {
        int imageID = skill.getImageID();
        Identifier sheet = imageID >= 20 ? SkillTextures.RPG_ICONS_2 : SkillTextures.RPG_ICONS;
        int u = (imageID % 20 / 10) * ICON_CELL * 4 + state * ICON_CELL;
        int v = (imageID % 10) * ICON_CELL;
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                sheet,
                getX(), getY(),
                (float) u, (float) v,
                DEFAULT_WIDTH, DEFAULT_HEIGHT,
                ICON_CELL, ICON_CELL,
                SHEET_SIZE, SHEET_SIZE
        );
    }

    private static void blit(GuiGraphicsExtractor graphics, SkillTexture texture, int x, int y, int width, int height,
                             boolean hover, boolean maxLevel, boolean operation) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture.getItem(hover, maxLevel, operation),
                x, y,
                0.0F, 0.0F,
                width, height,
                texture.getTextureWidth(), texture.getTextureHeight()
        );
    }

    /**
     * Draws the picture of a texture that belongs to the state of the button: the greyed out one while the
     * skill cannot be used, otherwise the one the state of the icon sheets would pick.
     */
    private static void blitState(GuiGraphicsExtractor graphics, SkillTexture texture, int x, int y, int width, int height,
                                  boolean hover, boolean maxLevel, boolean operation, boolean disabled) {
        Identifier image = disabled ? texture.getDisabledItem() : texture.getItem(hover, maxLevel, operation);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                image,
                x, y,
                0.0F, 0.0F,
                width, height,
                texture.getTextureWidth(), texture.getTextureHeight()
        );
        // the icon sheets carry a grey column of their own; pictures taken from vanilla textures do not,
        // so the greyed out state is tinted by hand instead of silently looking normal
        if (disabled && texture.getDisabledItem() == texture.getDefaultItem()) {
            graphics.fill(x, y, x + width, y + height, 0xA0606060);
        }
    }

    private void renderTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean maxLevel) {
        if (!isHovered()) return;
        Component click = null;
        Component cost = null;
        int[] result = SkillHelper.calcOperation(skill, level, SkillsMenuScreen.playerXp, !hasControlDown, hasShiftDown);

        if (hasAltDown) {
            click = (enabled ? Component.translatable("gui.gokis.toggle.off") : Component.translatable("gui.gokis.toggle.on"))
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
        } else if (hasControlDown) {
            if (result[0] == 0) {
                click = NO_DOWNGRADE;
            } else if (hasShiftDown) {
                click = Component.translatable("gui.gokis.downgrade.fast", -result[0])
                        .withStyle(Style.EMPTY.withColor(-13658630));
                cost = Component.translatable("gui.gokis.return", result[1])
                        .withStyle(Style.EMPTY.withColor(-8405510));
            } else {
                click = DOWNGRADE;
                cost = Component.translatable("gui.gokis.return", result[1])
                        .withStyle(Style.EMPTY.withColor(-8405510));
            }
        } else if (!maxLevel) {
            if (result[0] == 0) {
                click = NO_UPGRADE;
            } else if (hasShiftDown) {
                click = Component.translatable("gui.gokis.upgrade.fast", result[0])
                        .withStyle(Style.EMPTY.withColor(-11535825));
                cost = Component.translatable("gui.gokis.cost", -result[1])
                        .withStyle(Style.EMPTY.withColor(-6291570));
            } else {
                click = UPGRADE;
                cost = Component.translatable("gui.gokis.cost", -result[1])
                        .withStyle(Style.EMPTY.withColor(-6291570));
            }
        }

        Font font = Minecraft.getInstance().font;
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(skill.getName().copy()
                .append(Component.literal(" "))
                .append(maxLevel
                        ? Component.translatable("gui.gokis.max_level")
                                .withStyle(Style.EMPTY.withColor(enabled ? -9145 : 11184810))
                        : Component.literal("Lv" + level))
                .withStyle(Style.EMPTY.withColor(enabled ? (maxLevel ? -13312 : 16777215) : 11184810)));
        tooltip.add(skill.getDescription(level, skill.calcBonus(level)).copy()
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        if (click != null) tooltip.add(click);
        if (cost != null) tooltip.add(cost);

        graphics.setComponentTooltipForNextFrame(font, tooltip, mouseX, mouseY);
    }

    public void updateLevel() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        SkillInfo info = SkillHelper.getInfo(player);
        level = info.getLevel(skill);
        enabled = info.isEnabled(skill);
        waitForUpdate = false;
    }
}
