package me.dags.camera.client.ui;

import org.lwjgl.input.Mouse;

/**
 * @author dags <dags@dags.me>
 */
public class MouseHelper {

    private int button = -1;

    public int getPressedButton() {
        if (button == -1) {
            if (Mouse.isButtonDown(0)) {
                button = 0;
            }
        }
        return button;
    }

    public int getReleasedButton() {
        int released = -1;
        if (button != -1 && !Mouse.isButtonDown(button)) {
            released = button;
            button = -1;
        }
        return released;
    }
}
