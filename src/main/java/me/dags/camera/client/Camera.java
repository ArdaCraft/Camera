package me.dags.camera.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author dags <dags@dags.me>
 */
public class Camera {

    private static final SoundEvent SHUTTER = new SoundEvent(new ResourceLocation("minecraft", "ui.button.click"));
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    private static final Logger logger = LogManager.getLogger("Camera");

    private final File dir;
    private final ExecutorService service;

    // state
    private long lastExposure = 0L;
    private long exposureStart = 0L;
    private boolean exposing = false;

    // camera settings
    private long interval = -1L;
    private long exposure = -1L;
    private int imageWidth = 0;
    private int imageHeight = 0;
    private Framebuffer framebuffer;
    private ImmutableList.Builder<Frame> frameBuilder;

    // screen settings
    private int originalWidth = 0;
    private int originalHeight = 0;
    private boolean hideGui = false;
    private Framebuffer originalFrameBuffer;

    public Camera(File gameDir) {
        dir = new File(gameDir, "screenshots");
        service = Executors.newSingleThreadExecutor();
    }

    public void takePicture() {
        Exposable exposable = (Exposable) Minecraft.getMinecraft();

        Settings settings = Settings.getSettings();
        int width = Settings.getSettings().getImageWidth();
        int height = Settings.getSettings().getImageHeight();
        long exposure = settings.getExposureMS();

        Camera camera = CameraClient.getInstance().getCamera();
        camera.setDimensions(width, height);

        if (exposure == 0) {
            camera.openShutter();
            exposable.expose(camera);
            camera.closeShutter();
        } else {
            camera.setInterval(20, TimeUnit.MILLISECONDS);
            camera.setExposure(5000, TimeUnit.MILLISECONDS);
            camera.openShutter();
            exposable.expose(camera);
        }
    }

    public void recordFrame(Framebuffer framebuffer) {
        Frame frame = new Frame(imageWidth, imageHeight, FrameWriter.BYTES_PER_PIXEL);
        frame.read(framebuffer);
        frameBuilder.add(frame);
        lastExposure = System.currentTimeMillis();
        playShutter(0.05F, 2F, 0);
    }

    public void openShutter() {
        playShutter(0.1F, 2F, 1);
        framebuffer = new Framebuffer(imageWidth, imageHeight, true);
        frameBuilder = ImmutableList.builder();
        exposureStart = System.currentTimeMillis();
        lastExposure = 0L;
        exposing = true;
        logger.info("Shutter: open");
    }

    public void closeShutter() {
        try {
            playShutter(0.2F, 1.65F, 1);
            File output = getTimestampedPNGFileForDirectory(dir);
            List<Frame> frames = frameBuilder.build();
            int width = framebuffer.framebufferWidth;
            int height = framebuffer.framebufferHeight;
            service.submit(new FrameWriter(output, frames, width, height));
        } finally {
            exposing = false;
            framebuffer.deleteFramebuffer();
            framebuffer = null;
            frameBuilder = null;
            logger.info("Shutter: closed");
            scheduleMessage(new TextComponentString("Saving..."));
        }
    }

    public boolean exposeThisTick() {
        long time = System.currentTimeMillis();

        if (exposure > 0) {
            if (time - exposureStart > exposure) {
                closeShutter();
                return false;
            }
        }

        if (interval > 0) {
            if (time - lastExposure < interval) {
                return false;
            }
        }

        return true;
    }

    public void storeSettings(Framebuffer buffer, int width, int height, boolean hide) {
        originalFrameBuffer = buffer;
        originalWidth = width;
        originalHeight = height;
        hideGui = hide;
    }

    public void setDimensions(int width, int height) {
        imageWidth = width;
        imageHeight = height;
    }

    public void setInterval(long interval, TimeUnit timeUnit) {
        this.interval = timeUnit.toMillis(interval);
    }

    public void setExposure(long exposure, TimeUnit timeUnit) {
        this.exposure = timeUnit.toMillis(exposure);
    }

    public Framebuffer getFrameBuffer() {
        return framebuffer;
    }

    public Framebuffer getOriginalFrameBuffer() {
        return originalFrameBuffer;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }

    public boolean getHideGui() {
        return hideGui;
    }

    public boolean isExposing() {
        return exposing;
    }

    public void openScreenshots() {
        try {
            Desktop.getDesktop().open(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playShutter(float volume, float pitch, int delay) {
        BlockPos pos = Minecraft.getMinecraft().player.getPosition();
        PositionedSoundRecord record = new PositionedSoundRecord(SHUTTER, SoundCategory.PLAYERS, volume, pitch, pos);
        if (delay == 0) {
            Minecraft.getMinecraft().getSoundHandler().playSound(record);
        } else {
            Minecraft.getMinecraft().getSoundHandler().playDelayedSound(record, delay);
        }
    }

    private static File getTimestampedPNGFileForDirectory(File dir) {
        String s = DATE_FORMAT.format(new Date());
        int i = 1;

        while (true) {
            File file = new File(dir, s + (i == 1 ? "" : "_" + i) + ".png");
            if (!file.exists()) {
                return file;
            }
            ++i;
        }
    }

    public static void scheduleMessage(ITextComponent text) {
        Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().player.sendMessage(text));
    }
}