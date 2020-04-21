package me.dags.camera.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * @author dags <dags@dags.me>
 */
public class Resolution extends Element {

    private transient final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    private transient int left = 1;
    private transient int top = 1;
    private transient boolean active = false;

    private Slider width = new Slider();
    private Slider height = new Slider();

    public Resolution() {
        width.setValue((1 / 96F) * 16 * 4);
        height.setValue((1 / 96F) * 9 * 4);
    }

    public int getWidth() {
        return Math.max(256, Math.round(width.getValue() * 96) * 120);
    }

    public int getHeight() {
        return Math.max(256, Math.round(height.getValue() * 96) * 120);
    }

    @Override
    public void draw(ScaledResolution resolution, int mx, int my) {
        int centerX = resolution.getScaledWidth() / 2;
        int centerY = resolution.getScaledHeight() / 2;

        float scale = getScale(resolution.getScaledWidth(), resolution.getScaledHeight());
        int width = getScaledWidth(scale);
        int height = getScaledHeight(scale);
        int left = centerX - (width / 2);
        int right = centerX + (width / 2);
        int top = centerY - (height / 2);
        int bottom = centerY + (height / 2);

        int width3 = width / 3;
        int height3 = height / 3;

        fontRenderer.drawStringWithShadow(getLabel(), 1, 1, 0xFFFFFF);

        if (active) {
            this.width.setPos(this.left,this.top + 10).setSize(55, 8);
            this.height.setPos(this.left,this.top + 10 + 10).setSize(55, 8);
            this.width.draw(resolution, mx, my);
            this.height.draw(resolution, mx, my);
        }

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(1F);
        GlStateManager.color(1F, 1F, 1F, 0.4F);
        drawLine(left, top + height3, right, top + height3);
        drawLine(left, bottom - height3, right, bottom - height3);
        drawLine(left + width3, top, left + width3, bottom);
        drawLine(right - width3, top, right - width3, bottom);

        GlStateManager.glLineWidth(3F);
        drawLine(left, top, right, top);
        drawLine(left, bottom, right, bottom);
        drawLine(left, top, left, bottom);
        drawLine(right, top, right, bottom);
    }

    @Override
    public boolean mousePress(int mx, int my, int button) {
        boolean result = false;
        if (active) {
            width.mousePress(mx, my, button);
            height.mousePress(mx, my, button);
        }

        if (!rateLimited() && contains(mx, my, left, top, left + fontRenderer.getStringWidth(getLabel()), top + 10)) {
            active = !active;
            result = true;
        }

        return result;
    }

    @Override
    public void mouseRelease(int button) {
        if (active) {
            width.mouseRelease(button);
            height.mouseRelease(button);
        }
    }

    private String getLabel() {
        return String.format("%sx%s", getWidth(), getHeight());
    }

    private int getScaledWidth(float scale) {
        return Math.round(getWidth() * scale);
    }

    private int getScaledHeight(float scale) {
        return Math.round(getHeight() * scale);
    }

    private float getScale(int windowWidth, int windorHeight) {
        float scaleX = windowWidth / (float) getWidth();
        float scaleY = windorHeight / (float) getHeight();
        return Math.min(scaleX, scaleY);
    }

    private static void drawLine(int x1, int y1, int x2, int y2) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        buffer.pos(x1, y1, 0).endVertex();
        buffer.pos(x2, y2, 0).endVertex();
        tessellator.draw();
    }
}
