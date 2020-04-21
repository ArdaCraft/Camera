package me.dags.camera.client;

import net.minecraft.client.Minecraft;

/**
 * @author dags <dags@dags.me>
 */
public interface Exposable {

    void expose(Camera camera);

    default boolean inGame() {
        return asMinecraft().player != null;
    }

    default Minecraft asMinecraft() {
        return (Minecraft) this;
    }
}
