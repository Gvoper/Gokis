package gvoper.gokis.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import gvoper.gokis.GokiSkills;
import gvoper.gokis.client.GokiSkillsClient;
import gvoper.gokis.client.gui.components.SkillButton;
import gvoper.gokis.skill.Skill;
import gvoper.gokis.skill.SkillHelper;
import gvoper.gokis.skill.SkillRegistry;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;

/** The skills menu, opened with the mod key binding. */
public class SkillsMenuScreen extends Screen {
    private static final Component LOADING = Component.translatable("gui.gokis.loading.menu");
    private static final Component HELP = System.getProperty("os.name").toLowerCase().contains("mac")
            ? Component.translatable("gui.gokis.help.macos")
            : Component.translatable("gui.gokis.help");

    /** Skill icons are laid out in rows like a skill tree. */
    public static final int COLUMNS = 6;
    public static final int HORIZONTAL_SPACING = 32;
    public static final int VERTICAL_SPACING = 36;

    public static int playerXp = 0;

    private final Screen parent;
    private long lastUpdated = -1;
    private boolean loaded = false;

    public SkillsMenuScreen(Screen parent) {
        super(Component.translatable("gui.gokis.title"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(parent);
    }

    @Override
    protected void init() {
        if (loaded) onLoaded(); // the widgets were cleared, build them again
    }

    /** Lays every registered skill out in a grid, in the order they were registered. */
    protected void onLoaded() {
        List<Skill> skills = List.copyOf(SkillRegistry.getSkills());
        int rows = (skills.size() + COLUMNS - 1) / COLUMNS;
        int gridWidth = COLUMNS * HORIZONTAL_SPACING;
        int gridHeight = rows * VERTICAL_SPACING;
        int xStart = (this.width - gridWidth) / 2 + (HORIZONTAL_SPACING - SkillButton.DEFAULT_WIDTH) / 2;
        int yStart = (this.height - gridHeight) / 2;
        for (int i = 0; i < skills.size(); i++) {
            int x = xStart + (i % COLUMNS) * HORIZONTAL_SPACING;
            int y = yStart + (i / COLUMNS) * VERTICAL_SPACING;
            addRenderableWidget(new SkillButton(x, y, skills.get(i)));
        }
    }

    @Override
    public void tick() {
        super.tick();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!loaded && GokiSkills.getServerConfig() != null && SkillHelper.getInfoOrNull(player) != null) {
            loaded = true;
            onLoaded();
        }
        playerXp = SkillHelper.getTotalXp(player);
        if (GokiSkillsClient.lastPlayerInfoUpdated > lastUpdated) {
            lastUpdated = GokiSkillsClient.lastPlayerInfoUpdated;
            for (GuiEventListener child : children()) {
                if (child instanceof SkillButton button) button.updateLevel();
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        SkillButton.hasControlDown = InputConstants.isKeyDown(InputConstants.KEY_LCONTROL)
                || InputConstants.isKeyDown(InputConstants.KEY_RCONTROL);
        SkillButton.hasShiftDown = InputConstants.isKeyDown(InputConstants.KEY_LSHIFT)
                || InputConstants.isKeyDown(InputConstants.KEY_RSHIFT);
        SkillButton.hasAltDown = InputConstants.isKeyDown(InputConstants.KEY_LALT)
                || InputConstants.isKeyDown(InputConstants.KEY_RALT);

        if (!loaded) {
            String dots = LoadingDotsText.get(Util.getMillis());
            graphics.centeredText(font, dots, width / 2, height / 2 - 6, 8421504);
            graphics.centeredText(font, LOADING, width / 2, height / 2 + 6, 16777215);
        } else {
            super.extractRenderState(graphics, mouseX, mouseY, partialTick);
            Component[] helpLines = Arrays.stream(HELP.getString().split("\n")).map(Component::literal).toArray(Component[]::new);
            for (int k = 0; k < helpLines.length; k++) {
                graphics.text(font, helpLines[k], 5, height - font.lineHeight * (helpLines.length - k) - 4, 16777215);
            }
            Component xp = Component.translatable("gui.gokis.xp", playerXp);
            graphics.text(font, xp, width - font.width(xp) - 4, height - font.lineHeight - 4, 16777215);
        }

        graphics.centeredText(font, title, width / 2, 15, 16777215);
    }
}
