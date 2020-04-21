package me.dags.camera.launch;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

/**
 * @author dags <dags@dags.me>
 */
class Launcher {

    private static final Logger logger = LogManager.getLogger("NoClipLauncher");

    static void launch() {
        logger.info("Attempting to launch Camera mod");
        init();
        addConfiguration("mixin.camera.client.json");
        if (fmlIsPresent()) {
            setObfuscationContext("searge");
        }
    }

    private static void init() {
        logger.info("Initializing mixin bootstrap");
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
    }

    private static void addConfiguration(String mixin) {
        logger.info("Adding mixin configuration: {}", mixin);
        Mixins.addConfiguration(mixin);
    }

    private static void setObfuscationContext(String content) {
        logger.info("Setting obfuscation context: {}", content);
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext(content);
    }

    private static boolean fmlIsPresent() {
        for (IClassTransformer transformer : net.minecraft.launchwrapper.Launch.classLoader.getTransformers()) {
            if (transformer.getClass().getName().contains("fml")) {
                logger.info("Detected Forge");
                return true;
            }
        }
        return false;
    }
}
