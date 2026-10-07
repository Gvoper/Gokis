package gvoper.gokis.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import gvoper.gokis.GokiSkills;
import gvoper.gokis.network.payloads.*;
import gvoper.gokis.skill.Skill;
import gvoper.gokis.skill.SkillHelper;
import gvoper.gokis.skill.SkillHooks;
import gvoper.gokis.skill.SkillInfo;
import gvoper.gokis.skill.SkillRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/** Registers and sends the packets of the mod. Handlers run on the main thread. */
public final class GokiNetwork {
    private GokiNetwork() {}

    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
                C2SSkillDowngradePayload.TYPE, C2SSkillDowngradePayload.STREAM_CODEC,
                (payload, context) -> context.queue(() -> updateSkill(context.getPlayer(), payload.location(), false, false)));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
                C2SSkillUpgradePayload.TYPE, C2SSkillUpgradePayload.STREAM_CODEC,
                (payload, context) -> context.queue(() -> updateSkill(context.getPlayer(), payload.location(), true, false)));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
                C2SSkillFastDowngradePayload.TYPE, C2SSkillFastDowngradePayload.STREAM_CODEC,
                (payload, context) -> context.queue(() -> updateSkill(context.getPlayer(), payload.location(), false, true)));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
                C2SSkillFastUpgradePayload.TYPE, C2SSkillFastUpgradePayload.STREAM_CODEC,
                (payload, context) -> context.queue(() -> updateSkill(context.getPlayer(), payload.location(), true, true)));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
                C2SSkillTogglePayload.TYPE, C2SSkillTogglePayload.STREAM_CODEC,
                (payload, context) -> context.queue(() -> toggleSkill(context.getPlayer(), payload.location())));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
                C2SConfigRequestPayload.TYPE, C2SConfigRequestPayload.STREAM_CODEC,
                (payload, context) -> context.queue(() -> sendConfigSync(context.getPlayer())));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
                C2SSkillInfoRequestPayload.TYPE, C2SSkillInfoRequestPayload.STREAM_CODEC,
                (payload, context) -> context.queue(() -> {
                    if (context.getPlayer() instanceof ServerPlayer player) {
                        sendSkillInfoSync(player, SkillHelper.getInfo(player));
                    }
                }));

        // the client handlers of these two live in GokiSkillsClient, so the whole call is env guarded
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                    S2CConfigSyncPayload.TYPE, S2CConfigSyncPayload.STREAM_CODEC,
                    (payload, context) -> context.queue(() -> GokiNetworkClient.handleConfigSync(payload)));
            NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                    S2CSkillInfoSyncPayload.TYPE, S2CSkillInfoSyncPayload.STREAM_CODEC,
                    (payload, context) -> context.queue(() -> GokiNetworkClient.handleSkillInfoSync(payload)));
        });
    }

    public static void sendConfigSync(Player player) {
        if (player instanceof ServerPlayer serverPlayer && GokiSkills.config != null) {
            NetworkManager.sendToPlayer(serverPlayer, new S2CConfigSyncPayload(GokiSkills.config));
        }
    }

    public static void sendSkillInfoSync(ServerPlayer player, SkillInfo info) {
        NetworkManager.sendToPlayer(player, new S2CSkillInfoSyncPayload(info));
    }

    private static void updateSkill(Player player, Identifier location, boolean upgrade, boolean fast) {
        if (player instanceof ServerPlayer serverPlayer) {
            SkillHelper.updateSkill(serverPlayer, location, upgrade, fast);
        }
    }

    private static void toggleSkill(Player player, Identifier location) {
        if (player instanceof ServerPlayer serverPlayer) {
            SkillInfo info = SkillHelper.getInfo(serverPlayer);
            Skill skill = SkillRegistry.getSkill(location);
            if (skill == null) return;
            info.toggle(skill);
            SkillHooks.updateAttribute(serverPlayer, info, skill);
            sendSkillInfoSync(serverPlayer, info);
        }
    }
}
