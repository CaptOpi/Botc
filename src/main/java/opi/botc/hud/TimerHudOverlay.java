package opi.botc.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class TimerHudOverlay implements HudRenderCallback {

    private static String timerEnd = "";
    private static double timerStart = -1;
    private static double timerEndDuration = -1;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        if (timerEnd.isEmpty()) {
            return;
        } else if (timerStart == -1) {
            timerStart = Util.getMeasuringTimeMs() / 1000;
        }
        timerEndDuration = Util.getMeasuringTimeMs() / 1000 - timerStart;
        if (timerEndDuration > 3) {
            timerEnd = "";
            timerStart = -1;
            timerEndDuration = -1;
            return;
        }
        Text timerText = Text.literal("Return To Your Seats")
                .styled(style -> style.withBold(true).withItalic(true).withColor(Formatting.RED));

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        MatrixStack matrixStack = drawContext.getMatrices();
        matrixStack.push();
        
        matrixStack.scale(2.0f, 2.0f, 1.0f);
        
        int x = screenWidth / 2 - textRenderer.getWidth(timerText) / 2;
        int y = screenHeight / 2 - textRenderer.fontHeight / 2;

        drawContext.drawTextWithShadow(textRenderer, timerText, x, y, 0xFFFFFF);

        matrixStack.pop(); 
    }

    public static void setTimerEnd(String end) {
        timerEnd = end;
    }
}
