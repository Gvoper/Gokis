package gvoper.gokis.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import static gvoper.gokis.GokiSkills.resource;

public record C2SSkillFastDowngradePayload(Identifier location) implements CustomPacketPayload {
    public static final Type<C2SSkillFastDowngradePayload> TYPE = new Type<>(resource("skill_fast_downgrade"));
    public static final StreamCodec<ByteBuf, C2SSkillFastDowngradePayload> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            C2SSkillFastDowngradePayload::location,
            C2SSkillFastDowngradePayload::new
    );

    @Override
    public @NotNull Type<C2SSkillFastDowngradePayload> type() {
        return TYPE;
    }
}
