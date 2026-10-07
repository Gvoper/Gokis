package gvoper.gokis.network;

import dev.architectury.networking.NetworkManager;
import gvoper.gokis.GokiSkills;
import gvoper.gokis.client.GokiSkillsClient;
import gvoper.gokis.network.payloads.*;
import gvoper.gokis.skill.SkillHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

/** Client side of the network: asks the server to change skills and applies what it sends back. */
public final class GokiNetworkClient {
    private GokiNetworkClient() {}

    public static void sendSkillDowngrade(Identifier location) {
        NetworkManager.sendToServer(new C2SSkillDowngradePayload(location));
    }

    public static void sendSkillUpgrade(Identifier location) {
        NetworkManager.sendToServer(new C2SSkillUpgradePayload(location));
    }

    public static void sendSkillFastDowngrade(Identifier location) {
        NetworkManager.sendToServer(new C2SSkillFastDowngradePayload(location));
    }

    public static void sendSkillFastUpgrade(Identifier location) {
        NetworkManager.sendToServer(new C2SSkillFastUpgradePayload(location));
    }

    public static void sendSkillToggle(Identifier location) {
        NetworkManager.sendToServer(new C2SSkillTogglePayload(location));
    }

    public static void sendConfigRequest() {
        NetworkManager.sendToServer(new C2SConfigRequestPayload());
    }

    public static void sendSkillInfoRequest() {
        NetworkManager.sendToServer(new C2SSkillInfoRequestPayload());
    }

    public static void handleConfigSync(S2CConfigSyncPayload payload) {
        GokiSkills.setServerConfig(payload.config());
    }

    public static void handleSkillInfoSync(S2CSkillInfoSyncPayload payload) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            SkillHelper.setSkillInfo(player, payload.info());
            GokiSkillsClient.lastPlayerInfoUpdated = Util.getMillis();
        }
    }
}
