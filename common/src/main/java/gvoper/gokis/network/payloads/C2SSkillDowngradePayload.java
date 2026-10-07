package gvoper.gokis.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import static gvoper.gokis.GokiSkills.resource;

public record C2SSkillDowngradePayload(Identifier location) implements CustomPacketPayload {
    public static final Type<C2SSkillDowngradePayload> TYPE = new Type<>(resource("skill_downgrade"));
    public static final StreamCodec<ByteBuf, C2SSkillDowngradePayload> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            C2SSkillDowngradePayload::location,
            C2SSkillDowngradePayload::new
    );

    @Override
    public @NotNull Type<C2SSkillDowngradePayload> type() {
        return TYPE;
    }
}
