package gvoper.gokis.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gvoper.gokis.GokiSkills;
import gvoper.gokis.skill.SkillInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per player skill data. The NeoForge original kept it in an {@code AttachmentType} with
 * {@code copyOnDeath()}; on Architectury the same job is done by a {@link SavedData} keyed by the
 * player UUID, which survives death and dimension changes for the same reason.
 *
 * <p>26.x saves data through a Codec, so the map goes in as-is with {@link SkillInfo#MAP_CODEC}.
 */
public final class GokiData extends SavedData {

    private static final Codec<Map<UUID, SkillInfo>> PLAYERS_CODEC = Codec.unboundedMap(
            Codec.STRING.xmap(UUID::fromString, UUID::toString),
            SkillInfo.MAP_CODEC.codec()
    );

    public static final Codec<GokiData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PLAYERS_CODEC.optionalFieldOf("players", Map.of()).forGetter(data -> data.players)
    ).apply(instance, GokiData::new));

    public static final SavedDataType<GokiData> TYPE = new SavedDataType<GokiData>(
            GokiSkills.resource("skill_data"), GokiData::new, CODEC, DataFixTypes.LEVEL);

    /** The client has no saved data of its own: what the server sends lives here. */
    private static final Map<UUID, SkillInfo> CLIENT_CACHE = new HashMap<>();

    private final Map<UUID, SkillInfo> players = new HashMap<>();

    public GokiData() {}

    public GokiData(Map<UUID, SkillInfo> players) {
        this.players.putAll(players);
    }

    public static GokiData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    /** The skill info of a player, or null when it has not been created yet. */
    @Nullable
    public static SkillInfo getInfoOrNull(Player player) {
        if (player.level().isClientSide()) {
            return CLIENT_CACHE.get(player.getUUID());
        }
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.level().getServer() != null) {
            return get(serverPlayer.level().getServer()).players.get(player.getUUID());
        }
        return null;
    }

    /** The skill info of a player, creating an empty one if needed. */
    public static SkillInfo getInfo(Player player) {
        SkillInfo info = getInfoOrNull(player);
        if (info != null) return info;
        info = new SkillInfo();
        setInfo(player, info);
        return info;
    }

    public static void setInfo(Player player, SkillInfo info) {
        if (player.level().isClientSide()) {
            CLIENT_CACHE.put(player.getUUID(), info);
            return;
        }
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.level().getServer() != null) {
            GokiData data = get(serverPlayer.level().getServer());
            data.players.put(player.getUUID(), info);
            data.setDirty();
        }
    }

    /**
     * Marks the skill data as changed so that the next save actually writes it to disk.
     *
     * <p>Needed because 26.x only writes SavedData that reports {@code isDirty()} (see
     * {@code SavedDataStorage#collectDirtyTagsToSave}). Mutating a SkillInfo in place does not mark
     * anything by itself, so every write path has to call this.
     */
    public static void markDirty(Player player) {
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.level().getServer() != null) {
            get(serverPlayer.level().getServer()).setDirty();
        }
    }

    /** Called when the client leaves a world: the synced data is not valid anywhere else. */
    public static void clearClientCache() {
        CLIENT_CACHE.clear();
    }
}
