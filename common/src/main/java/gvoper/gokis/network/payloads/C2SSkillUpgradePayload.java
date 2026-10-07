package gvoper.gokis.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import static gvoper.gokis.GokiSkills.resource;

public record C2SSkillUpgradePayload(Identifier location) implements CustomPacketPayload {
    public static final Type<C2SSkillUpgradePayload> TYPE = new Type<>(resource("skill_upgrade"));
    public static final StreamCodec<ByteBuf, C2SSkillUpgradePayload> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            C2SSkillUpgradePayload::location,
            C2SSkillUpgradePayload::new
    );

    @Override
    public @NotNull Type<C2SSkillUpgradePayload> type() {
        return TYPE;
    }
}
