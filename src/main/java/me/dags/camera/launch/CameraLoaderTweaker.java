package me.dags.camera.launch;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class CameraLoaderTweaker implements ITweaker {

    @Override
    public void acceptOptions(List<String> list, File gameDir, File assetsDir, String profile) {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader launchClassLoader) {
        Launcher.launch();
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
