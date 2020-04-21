package me.dags.camera.client.mixin;

import me.dags.camera.client.CameraClient;
import me.dags.camera.client.Settings;
import net.minecraft.client.renderer.ViewFrustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author dags <dags@dags.me>
 */
@Mixin(ViewFrustum.class)
public abstract class MixinViewFrustum {

    @Inject(method = "updateChunkPositions", at = @At("HEAD"), cancellable = true)
    public void updateChunkPositionsHead(double x, double y, CallbackInfo ci) {
        if (CameraClient.getInstance().isInCameraMode() && Settings.getSettings().isFreeCamOn()) {
            ci.cancel();
        }
    }
}
