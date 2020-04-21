package me.dags.camera.client.ui;

import me.dags.camera.client.CameraClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author dags <dags@dags.me>
 */
public class Screenshots extends Element {

    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    private String label = "<->";
    private int left, top, width = fontRenderer.getStringWidth(label), height = 10;

    @Override
    public void draw(ScaledResolution resolution, int mx, int my) {
        left = resolution.getScaledWidth() - width - 1;
        top = resolution.getScaledHeight() - height - 1;
        fontRenderer.drawString(label, left, top, 0xFFFFFF);
    }

    @Override
    public boolean mousePress(int mx, int my, int button) {
        if (!rateLimited() && contains(mx, my, left, top, left + width, top + height)) {
            CameraClient.getInstance().getCamera().openScreenshots();
            return true;
        }
        return false;
    }
}
