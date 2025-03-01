package opi.botc.networking.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import opi.botc.networking.NetworkMessages;


public record RenderTimerEnd(String end) implements CustomPayload {

    public static final CustomPayload.Id<RenderTimerEnd> ID = new CustomPayload.Id<>(NetworkMessages.TIMER_END);

     public static final PacketCodec<RegistryByteBuf, RenderTimerEnd> CODEC = PacketCodec.tuple(
        PacketCodecs.STRING, payload -> payload.end(),
        RenderTimerEnd::new
    );

    @Override
    public CustomPayload.Id<RenderTimerEnd> getId() {
        return ID;
    }

}

