package me.dags.camera.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

/**
 * @author dags <dags@dags.me>
 */
public class Frame {

    private final ByteBuffer color;
    private final int width;
    private final int height;

    public Frame(int width, int height, int bpp) {
        this.color = BufferUtils.createByteBuffer(width * height * bpp);
        this.width = width;
        this.height = height;
    }

    public byte getColor(int index) {
        return color.get(index);
    }

    public void read(Framebuffer fbo) {
        GlStateManager.glPixelStorei(3333, 1);
        GlStateManager.glPixelStorei(3317, 1);
        GlStateManager.bindTexture(fbo.framebufferTexture);
        GL11.glGetTexImage(3553, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, color);
    }
}
