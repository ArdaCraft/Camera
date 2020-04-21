package me.dags.camera.client.ui;

import net.minecraft.client.gui.ScaledResolution;

/**
 * @author dags <dags@dags.me>
 */
public class Slider extends Element {

    private static final int TRACK_COLOR = 0x77000000;
    private static final int SLIDER_COLOR = 0x77FFFFFF;
    private static final int SLIDER_DEF_COLOR = 0x55FFFFFF;
    private static final int SLIDER_GRABBED_COLOR = 0xFFFFFFFF;
    private static final int SLIDER_PADDING = 1;
    private static final int SLIDER_SIZE = 5;

    private transient int left, top, width, height;
    private transient int sLeft, sTop, sRight, sBottom;
    private transient boolean grabbed = false;
    private transient boolean vertical = false;
    private transient String label = "";

    private float defaultVal;
    private float value;

    public Slider() {
        value = 0.5F;
        defaultVal = -1F;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public float getValue() {
        return value;
    }

    public Slider setDefault(float value) {
        this.defaultVal = value;
        return this;
    }

    public Slider setValue(float value) {
        this.value = value;
        return this;
    }

    public Slider setLabel(String label) {
        this.label = label;
        return this;
    }

    public Slider setVertical(boolean vertical) {
        this.vertical = vertical;
        return this;
    }

    public Slider setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Slider setPos(int left, int top) {
        this.left = left;
        this.top = top;
        return this;
    }

    @Override
    public boolean mousePress(int mx, int my, int button) {
        if (!rateLimited() && contains(mx, my, sLeft, sTop, sRight, sBottom)) {
            grabbed = true;
        }
        return grabbed;
    }

    @Override
    public void mouseRelease(int button) {
        grabbed = false;
    }

    @Override
    public void draw(ScaledResolution resolution, int mx, int my) {
        drawTrack(mx, my);
        drawDefault();
        drawSlider(mx, my);
        drawLabel(resolution);
    }

    private void drawTrack(int mx, int my) {
         drawRect(left, top, left + width, top + height, TRACK_COLOR);
    }

    private void drawDefault() {
        if (defaultVal >= 0F) {
            int l = vertical ? left - 1 : left + Math.round(defaultVal * width) - 1;
            int t = vertical ? top + Math.round(defaultVal * height) - 1 : top - 1;
            int r = vertical ? left + width + 1 : l + 3;
            int b = vertical ? t + 3 : top + height + 1;
            drawRect(l, t, r, b, TRACK_COLOR);
        }
    }

    private void drawSlider(int mx, int my) {
        if (grabbed) {
            if (vertical) {
                updateValue(my, top, height);
            } else {
                updateValue(mx, left, width);
            }
        }

        sLeft = vertical ? left - SLIDER_PADDING : left + Math.round(value * width) - (SLIDER_SIZE / 2);
        sTop = vertical ? (top + height) - Math.round(value * height) - (SLIDER_SIZE / 2) : top - SLIDER_PADDING;
        sRight = vertical ? left + width + SLIDER_PADDING : sLeft + SLIDER_SIZE;
        sBottom = vertical ? sTop + SLIDER_SIZE : top + height + SLIDER_PADDING;
        drawRect(sLeft, sTop, sRight, sBottom, grabbed ? SLIDER_GRABBED_COLOR : SLIDER_COLOR);
    }

    private void drawLabel(ScaledResolution resolution) {
        if (label.isEmpty()) {
            return;
        }
        int x = left + (width / 2);
        int y = top - 10;
        drawLabel(resolution, label, x, y);
    }

    private void updateValue(int mouse, int min, int size) {
        if (mouse < min) {
            value = vertical ? 1F : 0F;
            return;
        }
        if (mouse > min + size) {
            value = vertical ? 0F : 1F;
            return;
        }

        if (vertical) {
            value = (float) (size - (mouse - min)) / (float) size;
        } else {
            value = (float) (mouse - min) / (float) size;
        }
    }
}
