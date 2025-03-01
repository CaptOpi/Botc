package opi.botc;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import opi.botc.hud.RoleHudOverlay;
import opi.botc.hud.TimerHudOverlay;
import opi.botc.networking.NetworkMessages;

public class BloodOfTheClocktowerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new RoleHudOverlay());
        NetworkMessages.registerClient();
        HudRenderCallback.EVENT.register(new TimerHudOverlay());
    }
}