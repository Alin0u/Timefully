package dev.alinou.timefully;

import dev.alinou.timefully.config.TimefullyConfig;
import dev.alinou.timefully.hud.RepositionScreen;
import dev.alinou.timefully.hud.TimefullyHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timefully implements ClientModInitializer {

    public static final String MODID = "timefully";
    public static final Logger LOGGER = LoggerFactory.getLogger("Timefully");

    private static KeyBinding repositionKey;

    @Override
    public void onInitializeClient() {
        TimefullyConfig.init();

        repositionKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.timefully.reposition",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.categories.timefully"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (repositionKey.wasPressed()) {
                client.setScreen(new RepositionScreen(client.currentScreen));
            }
        });

        HudRenderCallback.EVENT.register(new TimefullyHud());
        LOGGER.info("Timefully loaded");
    }
}
