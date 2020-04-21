package me.dags.camera.client.mixin;

import me.dags.camera.client.CameraClient;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author dags <dags@dags.me>
 */
@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {

    @Inject(method = "preparePlayerToSpawn()V", at = @At("HEAD"))
    private void preparePlayerToSpawn(CallbackInfo ci) {
        Object instance = this;
        if (instance.getClass() == EntityPlayerSP.class) {
            CameraClient.getInstance().onSpawn(this);
        }
    }
}
