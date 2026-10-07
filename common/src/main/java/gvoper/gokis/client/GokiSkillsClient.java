package gvoper.gokis.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import gvoper.gokis.GokiSkills;
import gvoper.gokis.client.gui.screens.SkillsMenuScreen;
import gvoper.gokis.misc.GokiData;
import gvoper.gokis.network.GokiNetworkClient;
import gvoper.gokis.skill.SkillHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

/** Everything the mod does on the client: the key binding, the menu and the data the server sends. */
public final class GokiSkillsClient {
    public static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(GokiSkills.MOD_ID, "category"));
    public static final KeyMapping OPEN_MENU_KEY =
            new KeyMapping("key.gokis.open", InputConstants.KEY_Y, CATEGORY);

    /** Time the client last received its skill data, used to refresh the menu. */
    public static long lastPlayerInfoUpdated = 0;

    private static long nextSendTime = 0;

    private GokiSkillsClient() {}

    /** Called by the platform client entry points. */
    public static void init() {
        ClientTickEvent.CLIENT_PRE.register(GokiSkillsClient::onClientTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> onLoggingOut());
    }

    private static void onClientTick(Minecraft client) {
        if (client.level == null) return;

        if (client.gui.screen() == null && OPEN_MENU_KEY.consumeClick()) {
            client.gui.setScreen(new SkillsMenuScreen(null));
        }

        long now = Util.getMillis();
        if (now > nextSendTime) {
            // ask again until the server answered, the menu needs both to open
            if (GokiSkills.getServerConfig() == null) GokiNetworkClient.sendConfigRequest();
            if (SkillHelper.getInfoOrNull(client.player) == null) GokiNetworkClient.sendSkillInfoRequest();
            nextSendTime = now + 5000;
        }
    }

    private static void onLoggingOut() {
        GokiSkills.setServerConfig(null);
        GokiData.clearClientCache();
        lastPlayerInfoUpdated = 0;
        nextSendTime = 0;
    }
}
