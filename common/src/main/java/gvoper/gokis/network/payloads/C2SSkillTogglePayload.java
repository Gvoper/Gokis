package gvoper.gokis.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import static gvoper.gokis.GokiSkills.resource;

public record C2SSkillTogglePayload(Identifier location) implements CustomPacketPayload {
    public static final Type<C2SSkillTogglePayload> TYPE = new Type<>(resource("skill_toggle"));
    public static final StreamCodec<ByteBuf, C2SSkillTogglePayload> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            C2SSkillTogglePayload::location,
            C2SSkillTogglePayload::new
    );

    @Override
    public @NotNull Type<C2SSkillTogglePayload> type() {
        return TYPE;
    }
}
