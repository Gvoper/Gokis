package gvoper.gokis.network.payloads;

import gvoper.gokis.skill.SkillInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static gvoper.gokis.GokiSkills.resource;

public record S2CSkillInfoSyncPayload(SkillInfo info) implements CustomPacketPayload {
    public static final Type<S2CSkillInfoSyncPayload> TYPE = new Type<>(resource("skill_info_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSkillInfoSyncPayload> STREAM_CODEC = StreamCodec.composite(
            SkillInfo.STREAM_CODEC,
            S2CSkillInfoSyncPayload::info,
            S2CSkillInfoSyncPayload::new
    );

    @Override
    public @NotNull Type<S2CSkillInfoSyncPayload> type() {
        return TYPE;
    }
}
