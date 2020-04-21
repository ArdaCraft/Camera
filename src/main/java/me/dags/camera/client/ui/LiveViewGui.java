package me.dags.camera.client.ui;

import me.dags.camera.client.CameraClient;
import me.dags.camera.client.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.util.Arrays;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class LiveViewGui extends GuiIngame {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Settings settings = Settings.getSettings();
    private final MouseHelper mouse = new MouseHelper();
    private final List<Element> elements;
    private final GuiIngame originalUI;
    private final long originalTime;
    private final float originalFOV;

    private boolean hasMouse = false;
    private ScaledResolution resolution;
    private int mouseRawX = Integer.MIN_VALUE, mouseRawY, mouseX, mouseY;

    public LiveViewGui() {
        super(Minecraft.getMinecraft());
        originalUI = mc.ingameGUI;
        originalTime = mc.world.getWorldTime();
        originalFOV = mc.gameSettings.fovSetting;
        elements = Arrays.asList(settings.getImageSize(), settings.getTime(), settings.getRain(), settings.getZoom(), settings.getFreecam(), settings.getPrecipitation(), new Screenshots());
    }

    @Override
    public GuiNewChat getChatGUI() {
        return originalUI.getChatGUI();
    }

    @Override
    public void renderGameOverlay(float ticks) {
        mc.entityRenderer.setupOverlayRendering();
        resolution = new ScaledResolution(mc);
        update();
        drawChat();
        elements.forEach(s -> s.draw(resolution, mouseX, mouseY));
    }

    private void drawChat() {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, (float)(resolution.getScaledHeight() - 60), 0.0F);
        getChatGUI().drawChat(getUpdateCounter());
        GlStateManager.popMatrix();
    }

    private void update() {
        int wWidth = resolution.getScaledWidth();
        int wHeight = resolution.getScaledHeight();
        int xCenter = wWidth / 2;
        int yCenter = wHeight / 2;

        if (hasMouse) {
            if (mc.inGameHasFocus) {
                mc.setIngameNotInFocus();
                Mouse.setCursorPosition(mouseRawX, mouseRawY);
            }
        }

        handleKeyboard();
        if (hasMouse) {
            handleMouse();
        }

        updateConditions(wWidth, wHeight, xCenter, yCenter);
        updateZoom(wWidth, wHeight, xCenter, yCenter);
        updateMisc(wWidth, wHeight, xCenter, yCenter);

        mc.gameSettings.fovSetting = settings.getZoomFOV();
    }

    private void updateConditions(int windowWidth, int windowHeight, int xCenter, int yCenter) {
        float scale = 0.8F;
        int width = Math.round(windowWidth * scale);
        int height = 10;
        int pad = 5;

        settings.getTime().setSize(width, height);
        settings.getTime().setPos(xCenter - (width / 2), windowHeight - height - pad);

        settings.getRain().setSize(width, height);
        settings.getRain().setPos(xCenter - (width / 2), windowHeight - (height * 4) + (pad / 2));
    }

    private void updateZoom(int windowWidth, int windowHeight, int xCenter, int yCenter) {
        float scale = 0.6F;
        int height = Math.round(scale * windowHeight);
        int width = 10;
        int pad = 5;
        settings.getZoom().setSize(width, height);
        settings.getZoom().setPos(windowWidth - width - pad, yCenter - (height / 2));
    }

    private void updateMisc(int windowWidth, int windowHeight, int xCenter, int yCenter) {
        settings.getFreecam().setPos(windowWidth - settings.getFreecam().getWidth() - 1, 1);
        settings.getPrecipitation().setPos(xCenter - (settings.getPrecipitation().getWidth() / 2), settings.getRain().getTop() - 10);
    }

    private void handleKeyboard() {
        if (settings.getModifier().isKeyDown()) {
            if (!hasMouse) {
                hasMouse = true;
                mc.setIngameNotInFocus();
                if (mouseRawX != Integer.MIN_VALUE) {
                    Mouse.setCursorPosition(mouseRawX, mouseRawY);
                }
            }
        } else if (hasMouse) {
            hasMouse = false;
            mc.setIngameFocus();
        }

        if (settings.getExpose().isPressed()) {
            CameraClient.getInstance().getCamera().takePicture();
        }
    }

    private void handleMouse() {
        mouseRawX = Mouse.getX();
        mouseRawY = Mouse.getY();
        mouseX = mouseRawX / resolution.getScaleFactor();
        mouseY = resolution.getScaledHeight() - (mouseRawY / resolution.getScaleFactor());

        int pressed = mouse.getPressedButton();
        if (pressed != -1) {
            mousePress(pressed);
        }

        int released = mouse.getReleasedButton();
        if (released != -1) {
            mouseRelease(released);
        }
    }

    public void exitUI() {
        mc.world.setWorldTime(originalTime);
        mc.gameSettings.fovSetting = originalFOV;
        mc.ingameGUI = originalUI;
    }

    private void mousePress(int button) {
        for (Element element : elements) {
            if (element.mousePress(mouseX, mouseY, button)) {
                break;
            }
        }
    }

    private void mouseRelease(int button) {
        elements.forEach(s -> s.mouseRelease(button));
    }
}
