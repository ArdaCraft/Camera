package me.dags.camera.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.util.function.Consumer;

/**
 * @author dags <dags@dags.me>
 */
public class ToggleText extends Element {

    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    private final transient int width;
    private final transient int height = 10;
    private transient int left = 0;
    private transient int top = 0;

    private final String format;
    private final String on;
    private final String off;

    private Consumer<Boolean> callback = b -> {};
    private boolean enabled = false;

    public ToggleText(String format, String on, String off) {
        this.format = format;
        this.on = on;
        this.off = off;
        this.width = Math.max(fontRenderer.getStringWidth(String.format(format, on)), fontRenderer.getStringWidth(String.format(format, off)));
    }

    public boolean getValue() {
        return enabled;
    }

    public ToggleText setValue(boolean value) {
        this.enabled = value;
        return this;
    }

    public ToggleText setPos(int left, int top) {
        this.left = left;
        this.top = top;
        return this;
    }

    public ToggleText setCallback(Consumer<Boolean> callback) {
        this.callback = callback;
        return this;
    }

    public int getWidth() {
        return fontRenderer.getStringWidth(getLabel());
    }

    @Override
    public void draw(ScaledResolution resolution, int mx, int my) {
        fontRenderer.drawStringWithShadow(getLabel(), left, top, 0xFFFFFF);
    }

    @Override
    public boolean mousePress(int mx, int my, int button) {
        if (!rateLimited() && contains(mx, my, left, top, left + width, top + height)) {
            enabled = !enabled;
            callback.accept(enabled);
            return true;
        }
        return false;
    }

    public String getLabel() {
        return String.format(format, enabled ? on : off);
    }
}
