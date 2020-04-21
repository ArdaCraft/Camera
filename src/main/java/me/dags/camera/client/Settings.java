package me.dags.camera.client;

import me.dags.camera.client.ui.Resolution;
import me.dags.camera.client.ui.Slider;
import me.dags.camera.client.ui.ToggleText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

/**
 * @author dags <dags@dags.me>
 */
public class Settings {

    private static final Settings instance = new Settings();

    private KeyBinding menu = new KeyBinding("Open Camera", Keyboard.KEY_K, "Camera");
    private KeyBinding expose = new KeyBinding("Release Shutter", Keyboard.KEY_L, "Camera");
    private KeyBinding modifier = new KeyBinding("Mouse Focus", Keyboard.KEY_BACKSLASH, "Camera");

    private KeyBinding previous = new KeyBinding("Previous Shader", Keyboard.KEY_LBRACKET, "Camera");
    private KeyBinding next = new KeyBinding("Next Shader", Keyboard.KEY_RBRACKET, "Camera");

    private ToggleText freecam = new ToggleText("Freecam: %s", "On", "Off");
    private ToggleText precipitation = new ToggleText("%s", "Snow", "Rain");
    private Resolution imageSize = new Resolution();
    private Slider exposure = new Slider();
    private Slider time = new Slider();
    private Slider zoom = new Slider();
    private Slider rain = new Slider();

    public Settings() {
        time.setLabel("Time").setDefault(0.5F).setValue(0.5F);
        exposure.setLabel("Exposure").setValue(0F);
        zoom.setLabel("Zoom").setDefault(defaultZoom(70F)).setValue(0.5F).setVertical(true);
        precipitation.setCallback(b -> Minecraft.getMinecraft().renderGlobal.loadRenderers());
        rain.setValue(0F);
        freecam.setValue(false);
        registerKeyBinding(menu);
        registerKeyBinding(expose);
        registerKeyBinding(modifier);
        registerKeyBinding(next);
        registerKeyBinding(previous);
    }

    public KeyBinding getNext() {
        return next;
    }

    public KeyBinding getPrevious() {
        return previous;
    }

    public KeyBinding getExpose() {
        return expose;
    }

    public KeyBinding getMenu() {
        return menu;
    }

    public KeyBinding getModifier() {
        return modifier;
    }

    public ToggleText getPrecipitation() {
        return precipitation;
    }

    public ToggleText getFreecam() {
        return freecam;
    }

    public Resolution getImageSize() {
        return imageSize;
    }

    public Slider getTime() {
        return time;
    }

    public Slider getZoom() {
        return zoom;
    }

    public Slider getRain() {
        return rain;
    }

    public boolean isWinter() {
        return precipitation.getValue();
    }

    public boolean isFreeCamOn() {
        return freecam.getValue();
    }

    public int getImageWidth() {
        return imageSize.getWidth();
    }

    public int getImageHeight() {
        return imageSize.getHeight();
    }

    public long getExposureMS() {
        return Math.round(exposure.getValue() * 10000);
    }

    public long getTimeTicks() {
        return Math.round(time.getValue() * 24000);
    }

    public float getRainStrength() {
        return 2 * rain.getValue();
    }

    public float getZoomFOV() {
        float min = 1F, max = 155F, range = max - min;
        return max - Math.round(zoom.getValue() * range);
    }

    public static Settings getSettings() {
        return instance;
    }

    public static void registerKeyBinding(KeyBinding key) {
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.add(Minecraft.getMinecraft().gameSettings.keyBindings, key);
    }

    private static float defaultZoom(float fov) {
        float min = 1F, max = 155F, range = max - min;
        return fov / range;
    }
}
