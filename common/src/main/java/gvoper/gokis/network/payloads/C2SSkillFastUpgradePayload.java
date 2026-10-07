package gvoper.gokis.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import static gvoper.gokis.GokiSkills.resource;

public record C2SSkillFastUpgradePayload(Identifier location) implements CustomPacketPayload {
    public static final Type<C2SSkillFastUpgradePayload> TYPE = new Type<>(resource("skill_fast_upgrade"));
    public static final StreamCodec<ByteBuf, C2SSkillFastUpgradePayload> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            C2SSkillFastUpgradePayload::location,
            C2SSkillFastUpgradePayload::new
    );

    @Override
    public @NotNull Type<C2SSkillFastUpgradePayload> type() {
        return TYPE;
    }
}
