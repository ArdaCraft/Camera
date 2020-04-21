package me.dags.camera.client.mixin;

import me.dags.camera.client.Camera;
import me.dags.camera.client.CameraClient;
import me.dags.camera.client.Exposable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author dags <dags@dags.me>
 */
@Mixin(Minecraft.class)
public class MixinMinecraft implements Exposable {

    @Shadow
    public EntityRenderer entityRenderer;
    @Shadow
    private Framebuffer framebufferMc;
    @Shadow
    public GameSettings gameSettings;
    @Shadow
    public int displayWidth;
    @Shadow
    public int displayHeight;

    @Inject(method = "runTick()V", at = @At(value = "RETURN"))
    private void runTickTail(CallbackInfo callbackInfo) {
        CameraClient.getInstance().tick(this);
    }

    @Override
    public void expose(Camera camera) {
        try {
            camera.storeSettings(framebufferMc, displayWidth, displayHeight, gameSettings.hideGUI);
            gameSettings.hideGUI = true;
            displayWidth = camera.getImageWidth();
            displayHeight = camera.getImageHeight();
            framebufferMc = camera.getFrameBuffer();
            framebufferMc.bindFramebuffer(true);
            entityRenderer.updateCameraAndRender(0L, 0L);
            camera.recordFrame(framebufferMc);
        } finally {
            displayWidth = camera.getOriginalWidth();
            displayHeight = camera.getOriginalHeight();
            framebufferMc = camera.getOriginalFrameBuffer();
            gameSettings.hideGUI = camera.getHideGui();
        }
    }
}
