package me.dags.camera.client.mixin;

import me.dags.camera.client.CameraClient;
import me.dags.camera.client.Settings;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author dags <dags@dags.me>
 */
@Mixin(Frustum.class)
public class MixinFrustum {

    @Inject(method = "isBoxInFrustum", at = @At("HEAD"), cancellable = true)
    private void isBoxInFrustumHead(CallbackInfoReturnable<Boolean> ci) {
        if (CameraClient.getInstance().isInCameraMode() && Settings.getSettings().isFreeCamOn()) {
            ci.setReturnValue(true);
        }
    }
}
