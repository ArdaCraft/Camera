package me.dags.camera.launch;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class CameraTweaker implements ITweaker {

    private final List<String> args = new ArrayList<>();

    @Override
    public void acceptOptions(List<String> list, File gameDir, File assetsDir, String profile) {
        args.addAll(list);
        if (!args.contains("--version") && profile != null) {
            this.args.add("--version");
            this.args.add(profile);
        }
        if (!args.contains("--assetsDir") && assetsDir != null) {
            this.args.add("--assetsDir");
            this.args.add(assetsDir.getPath());
        }
        if (!args.contains("--gameDir") && gameDir != null) {
            this.args.add("--gameDir");
            this.args.add(gameDir.getPath());
        }
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
        return args.toArray(new String[args.size()]);
    }
}
