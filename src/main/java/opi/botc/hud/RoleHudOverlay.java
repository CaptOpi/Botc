package opi.botc.hud;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import opi.botc.BloodOfTheClocktower;

public class RoleHudOverlay implements HudRenderCallback {
    private static final Map<Identifier, NativeImage> cachedImages = new HashMap<>();
    private static String currentRole = "";
    private int lastClientWidth = -1;
    private int lastClientHeight = -1;
    private int scaledWidth;
    private int scaledHeight;
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient clientInstance = MinecraftClient.getInstance();
        ResourceManager resourceManager = clientInstance.getResourceManager();
        Identifier textureIdentifier = Identifier.of(BloodOfTheClocktower.MOD_ID, "textures/hud/" + currentRole + ".png");

        NativeImage nativeImage = cachedImages.get(textureIdentifier);
        if(currentRole.isEmpty()) {
            return;
        }
        if (nativeImage == null) {
            try {
                Resource resource = resourceManager.getResource(textureIdentifier)
                        .orElseThrow(() -> new IOException("Resource not found"));
                try (InputStream inputStream = resource.getInputStream()) {
                    nativeImage = NativeImage.read(inputStream);
                    cachedImages.put(textureIdentifier, nativeImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        if (nativeImage == null) {
            return;
        }

        int originalWidth = nativeImage.getWidth();
        int originalHeight = nativeImage.getHeight();
        
        int clientWidth = clientInstance.getWindow().getScaledWidth();
        int clientHeight = clientInstance.getWindow().getScaledHeight();

        int physicalWidth = clientInstance.getWindow().getWidth();
        int physicalHeight = clientInstance.getWindow().getHeight();

        if (physicalWidth < 1000 || physicalHeight < 600) {
            return;
        }

        int paddingX = 0;
        int paddingY = 0;

        int x = paddingX;
        int y = paddingY;
        if (clientWidth != lastClientWidth || clientHeight != lastClientHeight) {
            lastClientWidth = clientWidth;
            lastClientHeight = clientHeight;
            
            double maxWidth = clientWidth * 0.4;
            double maxHeight = clientHeight * 0.4;
            double scaleFactor = Math.min(1.0, Math.min(maxWidth / originalWidth, maxHeight / originalHeight));

            scaledWidth = (int) (originalWidth * scaleFactor);
            scaledHeight = (int) (originalHeight * scaleFactor);
        }

        drawContext.drawTexture(textureIdentifier, x, y, 0, 0, scaledWidth, scaledHeight, scaledWidth, scaledHeight);
    }

    public static void setCurrentRole(String role) {
        currentRole = role;
    }
    public static String getCurrentRole() {
        return currentRole;
    }

}