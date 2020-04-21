package me.dags.camera.client.mixin;

import me.dags.camera.client.CameraClient;
import me.dags.camera.client.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author dags <dags@dags.me>
 */
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Final
    @Shadow
    private Minecraft mc;

    @Inject(method = "updateCameraAndRender", at = @At("HEAD"))
    private void updateCameraAndRenderHead(CallbackInfo callbackInfo) {
        if (CameraClient.getInstance().isInCameraMode()) {
            mc.world.setWorldTime(Settings.getSettings().getTimeTicks());
            mc.world.setRainStrength(Settings.getSettings().getRainStrength());
        }
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true)
    private void addRainParticlesHead(CallbackInfo callbackInfo) {
        if (CameraClient.getInstance().isInCameraMode() && Settings.getSettings().isWinter()) {
            callbackInfo.cancel();
        }
    }
}
