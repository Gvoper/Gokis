package gvoper.gokis;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import gvoper.gokis.config.CommonConfig;
import gvoper.gokis.config.ConfigUtils;
import gvoper.gokis.network.GokiNetwork;
import gvoper.gokis.skill.SkillHooks;
import gvoper.gokis.skill.SkillRegistry;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

/** Entry point of the mod. Holds the shared state: the config and the skill registry. */
public final class GokiSkills {
    public static final String MOD_ID = "gokis";

    /** Temporary diagnostics switch for the swimming bonus. */
    public static final boolean DEBUG_SWIM = true;

    /** Config read from disk. Authoritative on the server, fallback on the client. */
    public static CommonConfig config;

    /** Config received from the server. Only ever set on the client, null until the first sync. */
    private static volatile CommonConfig serverConfig;

    private static final Logger LOGGER = LogUtils.getLogger();

    private static boolean initialized = false;

    /** Called by both platform entry points. */
    public static void init() {
        if (initialized) return;
        initialized = true;
        SkillRegistry.init(); // skills have to exist before the config is read
        config = ConfigUtils.readConfig(MOD_ID + "-common", CommonConfig.class);
        logLoadedSkills();
        GokiNetwork.register();
        registerEvents();
    }

    /** The shared hooks of the mod, wired to the Architectury event bus. */
    private static void registerEvents() {
        PlayerEvent.BREAK_SPEED.register(SkillHooks::onBreakSpeed);
        EntityEvent.LIVING_FALL.register(SkillHooks::onFall);
        EntityEvent.LIVING_DEATH.register(SkillHooks::onDeath);
        TickEvent.PLAYER_POST.register(SkillHooks::onPlayerTick);
        PlayerEvent.PLAYER_JOIN.register(SkillHooks::onPlayerLogin);
        PlayerEvent.PLAYER_RESPAWN.register((player, conqueredEnd, reason) -> SkillHooks.onPlayerRespawn(player));
    }

    /** The config the client should use: the one from the server if it is known, ours otherwise. */
    public static CommonConfig getConfig() {
        CommonConfig synced = serverConfig;
        return synced != null ? synced : config;
    }

    public static CommonConfig getServerConfig() {
        return serverConfig;
    }

    public static void setServerConfig(CommonConfig config) {
        serverConfig = config;
    }

    public static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private static void logLoadedSkills() {
        StringBuilder sb = new StringBuilder("Loaded skills: ");
        SkillRegistry.getSkills().forEach(skill -> sb.append(skill.getLocation()).append(", "));
        if (sb.length() > 2) sb.delete(sb.length() - 2, sb.length());
        LOGGER.info(sb.toString());
    }
}
