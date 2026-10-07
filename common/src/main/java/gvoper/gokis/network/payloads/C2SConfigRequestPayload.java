package gvoper.gokis.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static gvoper.gokis.GokiSkills.resource;

public record C2SConfigRequestPayload() implements CustomPacketPayload {
    public static final Type<C2SConfigRequestPayload> TYPE = new Type<>(resource("config_request"));
    public static final StreamCodec<ByteBuf, C2SConfigRequestPayload> STREAM_CODEC = StreamCodec.unit(new C2SConfigRequestPayload());

    @Override
    public @NotNull Type<C2SConfigRequestPayload> type() {
        return TYPE;
    }
}
