package me.dags.camera.client;

import net.minecraft.util.text.TextComponentString;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author dags <dags@dags.me>
 */
public class FrameWriter implements Runnable {

    public static final int BYTES_PER_PIXEL = 3;

    private final File out;
    private final int width;
    private final int height;
    private final List<Frame> frames;

    public FrameWriter(File out, List<Frame> frames, int width, int height) {
        this.out = out;
        this.width = width;
        this.height = height;
        this.frames = frames;
    }

    @Override
    public void run() {
        if (frames.isEmpty()) {
            return;
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        if (frames.size() == 1) {
            FrameWriter.write(frames.iterator().next(), image, width, height);
        } else {
            FrameWriter.blend(frames, image, width, height);
        }

        try {
            out.getParentFile().mkdirs();
            ImageIO.write(image, "png", out);
            Camera.scheduleMessage(new TextComponentString("Saved screenshot: " + out.getName()));
        } catch (IOException e) {
            e.printStackTrace();
            Camera.scheduleMessage(new TextComponentString("Screenshot failed :("));
        }
    }

    public static void write(Frame frame, BufferedImage image, int width, int height) {
        float contrast = 0.1F;
        float factor = 1F + contrast;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = ((y * width) + x) * BYTES_PER_PIXEL;
                int r = frame.getColor(index) & 0xFF;
                int g = frame.getColor(index + 1) & 0xFF;
                int b = frame.getColor(index + 2) & 0xFF;

                int color = r;
                color = (color << 8) + g;
                color = (color << 8) + b;
                image.setRGB(x, height - (y + 1), color);
            }
        }

        image.getGraphics().drawString("" + contrast, 20, 50);
    }

    public static void blend(Collection<Frame> frames, BufferedImage image, int width, int height) {
        if (frames.isEmpty()) {
            return;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = ((y * width) + x) * BYTES_PER_PIXEL;
                int r = 0, g = 0, b = 0, c = 0;

                for (Frame frame : frames) {
                    c++;
                    r += frame.getColor(index) & 0xFF;
                    g += frame.getColor(index + 1) & 0xFF;
                    b += frame.getColor(index + 2) & 0xFF;
                }

                int rgb = (r / c);
                rgb = (rgb << 8) + (g / c);
                rgb = (rgb << 8) + (b / c);

                image.setRGB(x, height - (y + 1), rgb);
            }
        }
    }
}
