package me.dags.camera.client.mixin;

import me.dags.camera.client.CameraClient;
import me.dags.camera.client.Settings;
import net.minecraft.world.biome.BiomeProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author dags <dags@dags.me>
 */
@Mixin(BiomeProvider.class)
public class MixinBiomeProvider {

    @Inject(method = "getTemperatureAtHeight", at = @At("HEAD"), cancellable = true)
    private void getTemperatureAtHeightHead(CallbackInfoReturnable<Float> cir) {
        if (CameraClient.getInstance().isInCameraMode() && Settings.getSettings().isWinter()) {
            cir.setReturnValue(0F);
        }
    }
}
