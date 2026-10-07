package gvoper.gokis.network.payloads;

import gvoper.gokis.config.CommonConfig;
import gvoper.gokis.config.ConfigUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static gvoper.gokis.GokiSkills.resource;

public record S2CConfigSyncPayload(CommonConfig config) implements CustomPacketPayload {
    public static final Type<S2CConfigSyncPayload> TYPE = new Type<>(resource("config_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CConfigSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ConfigUtils.streamCodecOf(CommonConfig.class),
            S2CConfigSyncPayload::config,
            S2CConfigSyncPayload::new
    );

    @Override
    public @NotNull Type<S2CConfigSyncPayload> type() {
        return TYPE;
    }
}
