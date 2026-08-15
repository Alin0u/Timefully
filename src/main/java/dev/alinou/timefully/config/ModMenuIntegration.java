package dev.alinou.timefully.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.alinou.timefully.hud.RepositionScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Cloth is optional, so the class that touches it is only reached
        // once the loader confirms it is installed.
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return ClothConfigScreenFactory::create;
        }
        return FallbackConfigScreen::new;
    }

    /** Opens the drag screen, used by the keybind. */
    public static void openRepositionScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(new RepositionScreen(client.currentScreen));
    }
}
