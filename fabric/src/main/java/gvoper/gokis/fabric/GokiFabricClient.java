package gvoper.gokis.fabric;

import gvoper.gokis.client.GokiSkillsClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

@Environment(EnvType.CLIENT)
public final class GokiFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GokiSkillsClient.init();
        KeyMappingHelper.registerKeyMapping(GokiSkillsClient.OPEN_MENU_KEY);
    }
}
