package opi.botc.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import opi.botc.BloodOfTheClocktower;
import opi.botc.hud.RoleHudOverlay;
import opi.botc.hud.TimerHudOverlay;
import opi.botc.networking.packet.RenderTimerEnd;
import opi.botc.networking.packet.RoleUpdatePayload;

public class NetworkMessages {
    public static final Identifier ROLE_UPDATE = Identifier.of(BloodOfTheClocktower.MOD_ID, "role_update");
    public static final Identifier ROLE_REQUEST = Identifier.of(BloodOfTheClocktower.MOD_ID, "role_request");
    public static final Identifier TIMER_END = Identifier.of(BloodOfTheClocktower.MOD_ID, "timer_end");

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(RoleUpdatePayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                RoleHudOverlay.setCurrentRole(payload.name());
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(RenderTimerEnd.ID, (payload, context) -> {
            context.client().execute(() -> {
                TimerHudOverlay.setTimerEnd(payload.end());
            });
        });
    }
}
