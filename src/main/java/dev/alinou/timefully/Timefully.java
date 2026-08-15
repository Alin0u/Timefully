package dev.alinou.timefully;

import dev.alinou.timefully.hud.TimefullyHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timefully implements ClientModInitializer {

    public static final String MODID = "timefully";
    public static final Logger LOGGER = LoggerFactory.getLogger("Timefully");

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new TimefullyHud());
        LOGGER.info("Timefully loaded");
    }
}
