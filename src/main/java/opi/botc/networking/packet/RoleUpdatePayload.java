package opi.botc.networking.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import opi.botc.networking.NetworkMessages;

public record RoleUpdatePayload(String name) implements CustomPayload {

    public static final CustomPayload.Id<RoleUpdatePayload> ID = new CustomPayload.Id<>(NetworkMessages.ROLE_UPDATE);

     public static final PacketCodec<RegistryByteBuf, RoleUpdatePayload> CODEC = PacketCodec.tuple(
        PacketCodecs.STRING, payload -> payload.name(),
        RoleUpdatePayload::new
    );

    @Override
    public CustomPayload.Id<RoleUpdatePayload> getId() {
        return ID;
    }

}
