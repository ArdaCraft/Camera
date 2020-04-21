package me.dags.camera.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author dags <dags@dags.me>
 */
public abstract class Element extends Gui {

    private long lastUpdate = 0L;

    abstract public void draw(ScaledResolution resolution, int mx, int my);

    public void drawLabel(ScaledResolution resolution, String label, int x, int y) {
        FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
        int halfWidth = renderer.getStringWidth(label) / 2;
        int xOffset = Math.max((x + halfWidth) - resolution.getScaledWidth(), 0);
        renderer.drawStringWithShadow(label, x - halfWidth - xOffset, y, 0xFFFFFF);
    }

    public boolean mousePress(int mx, int my, int button) {
        return false;
    }

    public void mouseRelease(int button) {

    }

    public boolean contains(int mx, int my, int left, int top, int right, int bottom) {
        return mx >= left && mx <= right && my >= top && my <= bottom;
    }

    public boolean rateLimited() {
        long time = System.currentTimeMillis();
        if (time - lastUpdate > 200L) {
            lastUpdate = time;
            return false;
        }
        return true;
    }
}
