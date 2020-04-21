package me.dags.camera.client;

import com.google.common.collect.ImmutableSet;
import me.dags.camera.client.ui.LiveViewGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

/**
 * @author dags <dags@dags.me>
 */
public class CameraClient {

    private static final CameraClient instance = new CameraClient();

    private final Camera camera;
    private volatile boolean cameraEnabled = false;

    private CameraClient() {
        camera = new Camera(Minecraft.getMinecraft().mcDataDir);
    }

    public boolean isInCameraMode() {
        return cameraEnabled;
    }

    public Camera getCamera() {
        return camera;
    }

    public void tick(Exposable exposable) {
        if (!exposable.inGame()) {
            cameraEnabled = false;
            return;
        }

        cameraEnabled = Minecraft.getMinecraft().ingameGUI instanceof LiveViewGui;

        if (Settings.getSettings().getMenu().isPressed()) {
            if (cameraEnabled) {
                ((LiveViewGui) exposable.asMinecraft().ingameGUI).exitUI();
            } else {
                exposable.asMinecraft().ingameGUI = new LiveViewGui();
            }
        }

        if (camera.isExposing() && camera.exposeThisTick()) {
            exposable.expose(camera);
        }
    }

    public void onSpawn(Object player) {
        if (player == Minecraft.getMinecraft().player && !Minecraft.getMinecraft().isSingleplayer()) {
//            ServerData currentServer = Minecraft.getMinecraft().getCurrentServerData();
//            if (currentServer != null) {
//                try {
//                    InetAddress address = InetAddress.getByName(currentServer.serverIP);
//                    String ip = address.getHostAddress();
//                    System.out.println("############## Connecting to: " + ip);
//                    FuckYouException fu = new FuckYouException();
//                    if (ip.equals("94.130.69.159") || fu.blacklist.contains(ip)) {
//                        throw fu;
//                    }
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    public static CameraClient getInstance() {
        return instance;
    }

    private static class FuckYouException extends RuntimeException {

        private final Set<String> blacklist = ImmutableSet.of("94.130.69.159");

        private FuckYouException() {
            super("This mod is not for you. 'Suck a dick dumb shits' ;]");
        }
    }
}
