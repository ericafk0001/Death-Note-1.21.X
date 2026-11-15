package dev.deathnote.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public final class DeathNoteHud {
    private static final Text TITLE = Text.literal("Your name was written");
    private static final Text SUBTITLE = Text.literal("Death Note claims another soul...");
    private static final int TITLE_COLOR = 0xAA0000;
    private static final int SUBTITLE_COLOR = 0xFF5555;
    private static final int TITLE_MAX_SCALE = 4;
    private static final int SUBTITLE_MAX_SCALE = 2;
    private static final int TOTAL_TICKS = 100;
    private static final int FADE_IN_TICKS = 20;
    private static final int FADE_OUT_TICKS = 20;
    private static final float LINE_GAP = 8.0F;
    // Keep the notification inside the scaled viewport. This matters at large
    // GUI scales, where the viewport can be only a few hundred pixels wide.
    private static final float SCREEN_MARGIN = 12.0F;
    private static final float SHADOW_MARGIN = 2.0F;

    private static int remainingTicks;

    private DeathNoteHud() {
    }

    public static void show() {
        remainingTicks = TOTAL_TICKS;
    }

    public static void tick() {
        if (remainingTicks > 0) {
            remainingTicks--;
        }
    }

    public static void render(DrawContext context) {
        if (remainingTicks <= 0) {
            return;
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int elapsed = TOTAL_TICKS - remainingTicks;
        int alpha;
        if (elapsed < FADE_IN_TICKS) {
            alpha = 255 * elapsed / FADE_IN_TICKS;
        } else if (remainingTicks < FADE_OUT_TICKS) {
            alpha = 255 * remainingTicks / FADE_OUT_TICKS;
        } else {
            alpha = 255;
        }

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        float titleScale = fitScale(textRenderer, TITLE, screenWidth, screenHeight, TITLE_MAX_SCALE);
        float subtitleScale = fitScale(textRenderer, SUBTITLE, screenWidth, screenHeight, SUBTITLE_MAX_SCALE);
        float titleHeight = textRenderer.fontHeight * titleScale;
        float subtitleHeight = textRenderer.fontHeight * subtitleScale;
        float totalHeight = titleHeight + LINE_GAP + subtitleHeight;
        float availableHeight = Math.max(1.0F, screenHeight - (SCREEN_MARGIN * 2.0F));
        if (totalHeight > availableHeight) {
            float heightScale = availableHeight / totalHeight;
            titleScale *= heightScale;
            subtitleScale *= heightScale;
            titleHeight *= heightScale;
            subtitleHeight *= heightScale;
            totalHeight = titleHeight + LINE_GAP + subtitleHeight;
        }
        float centerX = screenWidth / 2.0F;
        float top = Math.max(SCREEN_MARGIN, (screenHeight - totalHeight) / 2.0F);

        drawScaled(context, textRenderer, TITLE, titleScale, centerX, top,
                withAlpha(TITLE_COLOR, alpha));
        drawScaled(context, textRenderer, SUBTITLE, subtitleScale, centerX, top + titleHeight + LINE_GAP,
                withAlpha(SUBTITLE_COLOR, alpha));
    }

    private static float fitScale(TextRenderer textRenderer, Text text, int screenWidth,
                                  int screenHeight, float maxScale) {
        float textWidth = Math.max(1.0F, textRenderer.getWidth(text));
        float textHeight = Math.max(1.0F, textRenderer.fontHeight);
        float availableWidth = Math.max(1.0F, screenWidth - (SCREEN_MARGIN * 2.0F) - SHADOW_MARGIN);
        float availableHeight = Math.max(1.0F, screenHeight - (SCREEN_MARGIN * 2.0F));

        // Fit against both axes. Width is normally the limiting factor, but
        // fitting height prevents clipping on unusually short high-scale GUIs.
        float widthScale = availableWidth / textWidth;
        float heightScale = availableHeight / textHeight;
        return Math.max(0.1F, Math.min(maxScale, Math.min(widthScale, heightScale)));
    }

    private static int withAlpha(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }

    private static void drawScaled(DrawContext context, TextRenderer textRenderer, Text text,
                                   float scale, float centerX, float top, int color) {
        //? if >=1.21.6 {
        /*context.getMatrices().pushMatrix();
        context.getMatrices().translate(centerX, top);
        context.getMatrices().scale(scale, scale);
        context.drawCenteredTextWithShadow(textRenderer, text, 0, 0, color);
        context.getMatrices().popMatrix();
        *///?} else {
        context.getMatrices().push();
        context.getMatrices().translate(centerX, top, 0.0);
        context.getMatrices().scale(scale, scale, 1.0F);
        context.drawCenteredTextWithShadow(textRenderer, text, 0, 0, color);
        context.getMatrices().pop();
        //?}
    }
}
